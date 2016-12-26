#include <jni.h>  
#include <string.h>
#include <android/log.h>
#include <CL/cl.h>

#include"OpenCL_phone.h"

extern const unsigned int CL_INFO_ARR_LEN = 200;

void OpenCL_Init(cl_device_id *device, JNIEnv *env, jobject thiz);

#ifdef __cplusplus
extern "C" {
#endif

void Java_so_opencl_MainActivity_openclTest(JNIEnv *env, jobject thiz) {
    cl_device_id device;
	OpenCL_Init(&device, env, thiz);
}
#ifdef __cplusplus
}
#endif

/** char数组转jstring */
jstring charTojstring(JNIEnv* env, const char* str) {
	 jsize len = strlen(str);
     jclass strClazz = env -> FindClass("java/lang/String");
     jmethodID mid = env -> GetMethodID(strClazz, "<init>", "([BLjava/lang/String;)V");
     jbyteArray barr = env -> NewByteArray(len);

     env -> SetByteArrayRegion(barr, 0, len, (jbyte*) str);
     jstring strenCode = env -> NewStringUTF("UTF-8");

     return (jstring) env -> NewObject(strClazz, mid, barr, strenCode);
}

void checkErr(cl_int err,int num) {
	if (CL_SUCCESS != err) {
	    LOGD ("%s", "OpenCL error");
	}
}

/** 初始化OpenCL */
void OpenCL_Init(cl_device_id *device, JNIEnv* env, jobject thiz) {
	cl_int err;
	cl_uint num_platform;
	cl_uint num_device;
	cl_platform_id *platform;
	cl_device_id *devices;

    char platformName[CL_INFO_ARR_LEN];
    char deviceName[CL_INFO_ARR_LEN];

	int loadedCL = load_Func();

	err = rclGetPlatformIDs(0, 0, &num_platform);
	checkErr(err, 1);
	platform = (cl_platform_id*) malloc(sizeof(cl_platform_id) *num_platform);
	err = rclGetPlatformIDs(num_platform, platform, NULL);
#if 1
	for (int i = 0; i < num_platform; i++) {
		err = rclGetPlatformInfo(platform[i], CL_PLATFORM_NAME, CL_INFO_ARR_LEN, platformName, NULL);
	}
	LOGD ("%s", platformName);
#endif
	checkErr(err,1);
	err = rclGetDeviceIDs(platform[0], CL_DEVICE_TYPE_ALL, 0, NULL,&num_device);
	devices = (cl_device_id*) malloc(sizeof(cl_device_id) *num_device);
	err = rclGetDeviceIDs(platform[0], CL_DEVICE_TYPE_ALL, num_device, devices, NULL);
	*device = devices[0];
#if 1
	err = rclGetDeviceInfo(devices[0], CL_DEVICE_NAME, CL_INFO_ARR_LEN, deviceName, NULL);
	LOGD ("%s", deviceName);
#endif
	free(platform);
	free(devices);

    // 调用java函数更新UI
	jclass clazz = (env) -> FindClass("so/opencl/MainActivity");
    if (clazz == NULL) {
        return;
    }

    jmethodID method = (env) -> GetMethodID(clazz, "setOpenCLInfo", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    if (method == NULL) {
        return;
    }
    jstring soString = charTojstring(env, getCLLocation());
    jstring platformString = charTojstring(env, platformName);
    jstring nameString = charTojstring(env, deviceName);
    // 调用java方法
    (env) -> CallVoidMethod(thiz, method, soString, platformString, nameString);
}