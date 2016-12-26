LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := silentarmy

LOCAL_SRC_FILES := common.cpp image.cpp sgemm.cpp

LOCAL_LDLIBS:= -llog libGLES_mali.so

LOCAL_CPPFLAGS:=-std=c++11 -frtti

include $(BUILD_SHARED_LIBRARY)
