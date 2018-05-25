//
// Created by kzw on 2018/5/21.
//

#ifndef MOBILEMINER_STRINGUTILS_H
#define MOBILEMINER_STRINGUTILS_H

#include <string.h>
#include <string>
#include <sstream>
#include <iostream>
#include <jni.h>
#include <stdlib.h>

char *intToChar(int a);

std::string toString(const int a);

char* jstringTostring(JNIEnv* env, jstring jstr);

#endif //MOBILEMINER_STRINGUTILS_H

