LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := testCL
LOCAL_SRC_FILES := OpenCLTest.cpp OpenCL_phone.cpp

LOCAL_LDLIBS:= -llog
LOCAL_CPPFLAGS:=-std=c++11 -frtti

include $(BUILD_SHARED_LIBRARY)
