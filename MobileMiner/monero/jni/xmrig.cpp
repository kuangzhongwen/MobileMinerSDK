/* XMRig
 * Copyright 2010      Jeff Garzik <jgarzik@pobox.com>
 * Copyright 2012-2014 pooler      <pooler@litecoinpool.org>
 * Copyright 2014      Lucas Jones <https://github.com/lucasjones>
 * Copyright 2014-2016 Wolf9466    <https://github.com/OhGodAPet>
 * Copyright 2016      Jay D Dee   <jayddee246@gmail.com>
 * Copyright 2016-2017 XMRig       <support@xmrig.com>
 *
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

#include <jni.h>
#include "App.h"
#include "common/log/AndroidLog.h"

extern "C" {
    JNIEXPORT void JNICALL Java_waterhole_miner_monero_NewXmrMinerActivity_startMine(JNIEnv *env, jobject thiz) {
        /**
         * test: ./xmrig --api-port 556 -o pool.monero.hashvault.pro:3333 -u 46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY -p worker1:651043704@qq.com -k
         */
         int argc = 12;
         char *argv[] = {
            (char*)"./xmrig",
            (char*)"--api-port", (char*)"556",
            (char*)"-o", (char*)"pool.monero.hashvault.pro:3333",
            (char*)"-u", (char*)"46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY",
            (char*)"-p", (char*)"worker1:651043704@qq.com",
            (char*)"--thread", (char*)"4",
            (char*) "-k"};
         App app(argc, argv);
         app.exec();
    }
}

