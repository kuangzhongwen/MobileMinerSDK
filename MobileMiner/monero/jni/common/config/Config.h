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
        (char*)"46Ffvb3jf7ZcVqgPjeReAfZyAk7qKm4FqMb6g6SsT6bpKAhPo9EtNKUVEdMpk62zPpB9GJt75xTD75vYHKredVB3RDHfxdY",
        (char*)"worker1:651043704@qq.com",
        "0"
        };
#endif //MOBILEMINER_CONFIG_H
