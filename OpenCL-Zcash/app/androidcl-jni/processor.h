#ifndef __JNI_H__
#define __JNI_H__
#ifdef __cplusplus

#include <android/bitmap.h>
#include <android/log.h>

#define app_name "JNIProcessor"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, app_name, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, app_name, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, app_name, __VA_ARGS__))
extern "C" {
#endif

#define DECLARE_NOPARAMS(returnType,fullClassName,func) \
JNIEXPORT returnType JNICALL Java_##fullClassName##_##func(JNIEnv *env, jclass clazz);

#define DECLARE_WITHPARAMS(returnType,fullClassName,func,...) \
JNIEXPORT returnType JNICALL Java_##fullClassName##_##func(JNIEnv *env, jclass clazz,__VA_ARGS__);

DECLARE_NOPARAMS(jboolean, so_opencl_LiveFeatureActivity,compileKernels)

DECLARE_WITHPARAMS(void, so_opencl_CameraPreview,runfilter,jobject outBmp, jbyteArray inData, jint width, jint height, jint choice)

#ifdef __cplusplus
}
#endif

#endif //__JNI_H__
