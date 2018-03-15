#ifndef  CHAR_UTILS_H
#define CHAR_UTILS_H

char* jstringTostr(JNIEnv* env, jstring jstr);

jstring strToJstring(JNIEnv* env, const char* pStr);

#endif