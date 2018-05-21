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
#include <string.h>
#include "App.h"
#include "common/log/AndroidLog.h"

jobject jcallbackObj;
JNIEnv* jenv;

char* intToChar(int a) {
    //int 32位
    char *b = new char[32];
    int i = 0;
    int flag = 1;
    if (a < 0) {
        b[i++] = '-';
        a = 0 -a ;
        flag = -1;
    }
    while(a) {
        b[i++] = a % 10 + '0';
        a /=10;
    }
    b[i] = '\0';
    int n = strlen(b);
    char c;
    int j = 0;

    if (flag == -1) {
        j = 1;
    }
    int k = 0;
    for(; j < n / 2; j++, k++) {
        c = b[j];
        b[j] = b[n - k - 1];
        b[n - k - 1] = c;
    }
    return b;
}

extern "C" {
    JNIEXPORT void JNICALL Java_waterhole_miner_monero_NewXmr_startMine(JNIEnv *env, jobject thiz, jint threads, jint cpu_uses, jobject callback) {
        /**
         * test: ./xmrig --api-port 556 -o pool.monero.hashvault.pro:3333 -u 46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY -p worker1:651043704@qq.com -k
         */
         jenv = env;
         jcallbackObj = callback;

         int argc = 14;
         int threadCounts = (int) threads;
         int cpuUses = (int) cpu_uses;
         char *argv[] = {
            (char*)"./xmrig",
            (char*)"--api-port", (char*)"556",
            (char*)"-o", (char*)"xmr.waterhole.xyz:3333",
            (char*)"-u", (char*)"46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY",
            (char*)"-p", (char*)"worker1:651043704@qq.com",
            (char*)"--thread", intToChar(threadCounts),
            (char*)"--max-cpu-usage", intToChar(cpuUses),
            (char*) "-k"};
         App app(argc, argv);
         app.exec();
    }
}

int assertCallOnJava() {
    if (jenv == NULL || jcallbackObj == NULL) {
        return 0;
    }
    return 1;
}

void onConnectPoolBegin() {
    if (assertCallOnJava()) {
       jclass jcallback = jenv->GetObjectClass(jcallbackObj);
       jmethodID mid = jenv->GetMethodID(jcallback, "onConnectPoolBegin", "()V");
       jenv->CallVoidMethod(jcallbackObj, mid);
       jenv->DeleteLocalRef(jcallback);
    }
}

void onConnectPoolSuccess() {
    if (assertCallOnJava()) {
       jclass jcallback = jenv->GetObjectClass(jcallbackObj);
       jmethodID mid = jenv->GetMethodID(jcallback, "onConnectPoolSuccess", "()V");
       jenv->CallVoidMethod(jcallbackObj, mid);
       jenv->DeleteLocalRef(jcallback);
    }
}

void onPoolDisconnect(const char* error) {
    if (assertCallOnJava()) {
        jclass jcallback = jenv->GetObjectClass(jcallbackObj);
        jmethodID mid = jenv->GetMethodID(jcallback, "onPoolDisconnect", "(Ljava/lang/String;)V");
        jstring newerror = jenv->NewStringUTF(error);
        jenv->CallVoidMethod(jcallbackObj, mid, newerror);
        jenv->DeleteLocalRef(newerror);
        jenv->DeleteLocalRef(jcallback);
    }
}

void onMessageFromPool(const char* message) {
    if (assertCallOnJava()) {
        jclass jcallback = jenv->GetObjectClass(jcallbackObj);
        jmethodID mid = jenv->GetMethodID(jcallback, "onMessageFromPool", "(Ljava/lang/String;)V");
        jstring newmessage = jenv->NewStringUTF(message);
        jenv->CallVoidMethod(jcallbackObj, mid, newmessage);
        jenv->DeleteLocalRef(newmessage);
        jenv->DeleteLocalRef(jcallback);
    }
}

void onMiningStatus(const double speed) {
    if (assertCallOnJava()) {
        jclass jcallback = jenv->GetObjectClass(jcallbackObj);
        jmethodID mid = jenv->GetMethodID(jcallback, "onMiningStatus", "(D)V");
        jenv->CallVoidMethod(jcallbackObj, mid, speed);
        jenv->DeleteLocalRef(jcallback);
    }
}

