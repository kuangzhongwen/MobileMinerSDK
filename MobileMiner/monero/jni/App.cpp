/* XMRig
 * Copyright 2010      Jeff Garzik <jgarzik@pobox.com>
 * Copyright 2012-2014 pooler      <pooler@litecoinpool.org>
 * Copyright 2014      Lucas Jones <https://github.com/lucasjones>
 * Copyright 2014-2016 Wolf9466    <https://github.com/OhGodAPet>
 * Copyright 2016      Jay D Dee   <jayddee246@gmail.com>
 * Copyright 2017-2018 XMR-Stak    <https://github.com/fireice-uk>, <https://github.com/psychocrypt>
 * Copyright 2016-2018 XMRig       <https://github.com/xmrig>, <support@xmrig.com>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


#include <stdlib.h>
#include "3rdparty/uv/uv.h"


#include "api/Api.h"
#include "App.h"
#include "common/Console.h"
#include "common/log/Log.h"
#include "common/Platform.h"
#include "core/Config.h"
#include "core/Controller.h"
#include "Cpu.h"
#include "crypto/CryptoNight.h"
#include "Mem.h"
#include "net/Network.h"
#include "Summary.h"
#include "version.h"
#include "workers/Workers.h"
#include "common/log/AndroidLog.h"
#include "StringUtils.h"
#include "common/config/Config.h"

#ifndef XMRIG_NO_HTTPD
#   include "common/api/Httpd.h"
#endif


App *App::m_self = nullptr;

App::App(int threads, int cpu_uses) :
    m_console(nullptr),
    m_httpd(nullptr)
{
   int argc = 16;
   int threadCounts = (int) threads;
   int cpuUses = (int) cpu_uses;
   char *argv[] = {argv_key[0], argv_key[1], argv_value[0],
       argv_key[2], argv_value[1], argv_key[3], argv_value[2],
       argv_key[4], argv_value[3], argv_key[5], intToChar(threadCounts),
       argv_key[6], intToChar(cpuUses), argv_key[7], argv_value[4],
       argv_key[8]};

    m_self = this;
    m_controller = new xmrig::Controller();
    if (m_controller->init(argc, argv) != 0) {
        return;
    }

    if (!m_controller->config()->isBackground()) {
        m_console = new Console(this);
    }

    uv_signal_init(uv_default_loop(), &m_sigHUP);
    uv_signal_init(uv_default_loop(), &m_sigINT);
    uv_signal_init(uv_default_loop(), &m_sigTERM);
}


App::~App()
{
    uv_tty_reset_mode();

    delete m_console;
    delete m_controller;

#   ifndef XMRIG_NO_HTTPD
    delete m_httpd;
#   endif
}


int App::exec()
{
    if (!m_controller->isReady()) {
        return 2;
    }

    uv_signal_start(&m_sigHUP,  App::onSignal, SIGHUP);
    uv_signal_start(&m_sigINT,  App::onSignal, SIGINT);
    uv_signal_start(&m_sigTERM, App::onSignal, SIGTERM);
    background();
    Mem::init(m_controller->config()->isHugePages());

    Summary::print(m_controller);

    if (m_controller->config()->isDryRun()) {
        LOGD("OK");
        release();

        return 0;
    }

#   ifndef XMRIG_NO_API
    Api::start(m_controller);
#   endif

#   ifndef XMRIG_NO_HTTPD
    m_httpd = new Httpd(
                m_controller->config()->apiPort(),
                m_controller->config()->apiToken(),
                m_controller->config()->isApiIPv6(),
                m_controller->config()->isApiRestricted()
                );

    m_httpd->start();
#   endif

    Workers::start(m_controller);

    m_controller->network()->connect();

    onConnectPoolBegin();

    const int r = uv_run(uv_default_loop(), UV_RUN_DEFAULT);
    uv_loop_close(uv_default_loop());

    release();
    return r;
}


void App::onConsoleCommand(char command)
{
    switch (command) {
    case 'h':
    case 'H':
        Workers::printHashrate(true);
        break;

    case 'p':
    case 'P':
        if (Workers::isEnabled()) {
            LOGD("%s", "paused, press 'r' to resume");
            Workers::setEnabled(false);
        }
        break;

    case 'r':
    case 'R':
        if (!Workers::isEnabled()) {
            LOGD("%s", "resumed");
            Workers::setEnabled(true);
        }
        break;

    case 3:
        LOGD("%s", "Ctrl+C received, exiting");
        close();
        break;

    default:
        break;
    }
}


void App::close()
{
    m_controller->network()->stop();
    Workers::stop();

    uv_stop(uv_default_loop());
}


void App::release()
{
}


void App::onSignal(uv_signal_t *handle, int signum)
{
    switch (signum)
    {
    case SIGHUP:
        LOGD("%s", "SIGHUP received, exiting");
        break;

    case SIGTERM:
        LOGD("%s", "SIGTERM received, exiting");
        break;

    case SIGINT:
        LOGD("%s", "SIGINT received, exiting");
        break;

    default:
        break;
    }

    uv_signal_stop(handle);
    m_self->close();
}
