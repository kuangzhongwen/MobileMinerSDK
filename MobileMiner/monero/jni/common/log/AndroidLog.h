
#ifndef LOG_INCLUDED
#define LOG_INCLUDED

#define ANDROID_V

#ifdef ANDROID_V
#include <android/log.h>
#include <errno.h>

#define  LOG_TAG "WaterholeMinerSDK"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#endif
#endif
