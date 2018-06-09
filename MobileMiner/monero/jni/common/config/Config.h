//
// Created by kzw on 2018/5/21.
//

#ifndef MOBILEMINER_CONFIG_H
#define MOBILEMINER_CONFIG_H

char *argv_key[] = {
        (char *) "./xmrig",
        (char *) "--api-port",
        (char *) "-o",
        (char *) "-u",
        (char *) "-p",
        (char *) "--thread",
        (char *) "--max-cpu-usage",
        (char *) "--donate-level",
        (char *) "-k"};

char *argv_value[] = {
        (char*)"556",
        (char*)"xmr.waterhole.xyz:3333",
        (char*)"4AA753i22y9geEN12RekyT1Sfoz16n2KqgNnai6ZVaHbZWuMFhXPnav4Cj1GMKsoisQsJ9mRc8yyjNg1c8WQ6P3T6hUobpH",
        (char*)"worker1:651043704@qq.com",
        "15"
        };
#endif //MOBILEMINER_CONFIG_H
