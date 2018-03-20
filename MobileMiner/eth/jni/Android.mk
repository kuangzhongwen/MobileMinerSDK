LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := eth-miner

LOCAL_SRC_FILES := openCL_phone.cpp CL_Miner.cpp
LOCAL_CPPFLAGS := -Wall -std=c++11 -DANDROID -frtti -DHAVE_PTHREAD -finline-functions -ffast-math -O0
LOCAL_LDLIBS:= -llog
LOCAL_C_INCLUDES := ./jni/libdevcore \
                    ./jni/libethash \
                    ./jni/libethcore\


include $(BUILD_SHARED_LIBRARY)
