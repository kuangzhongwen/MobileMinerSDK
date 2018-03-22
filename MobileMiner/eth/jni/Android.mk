LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libboost_system
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/boost/include
LOCAL_SRC_FILES := $(LOCAL_PATH)/boost/lib/libboost_system.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-dev-core

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -frtti -lpthread
LOCAL_C_INCLUDES += ./jni/boost/include/
LOCAL_STATIC_LIBRARIES := libboost_system

LOCAL_SRC_FILES := \
        ./libdevcore/CommonData.cpp \
        ./libdevcore/FixedHash.cpp \
        ./libdevcore/Log.cpp \
        ./libdevcore/RLP.cpp \
        ./libdevcore/SHA3.cpp \
        ./libdevcore/Worker.cpp

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-hash
LOCAL_C_INCLUDES += ./jni/libethash/

LOCAL_SRC_FILES := \
        ./libethash/internal.c \
        ./libethash/sha3.c
LOCAL_CFLAGS += -std=gnu99

include $(BUILD_SHARED_LIBRARY)


