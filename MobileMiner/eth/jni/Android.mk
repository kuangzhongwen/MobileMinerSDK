LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION := .cpp
LOCAL_MODULE    := eth-dev-core
LOCAL_SRC_FILES := \
        ./libdevcore/CommonData.cpp \
        ./libdevcore/FixedHash.cpp \
        ./libdevcore/Log.cpp \
        ./libdevcore/RLP.cpp \
        ./libdevcore/SHA3.cpp \
        ./libdevcore/Worker.cpp

LOCAL_C_INCLUDES := ./jni/libdevcore

LOCAL_CFLAGS += -std=c++11 -pthread -fexceptions -frtti
LOCAL_LDLIBS:= -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings
LOCAL_CFLAGS += -I$(LOCAL_PATH)/boost/include/boost-1_49
LOCAL_LDLIBS += -L$(LOCAL_PATH)/boost/lib/ -lboost_system -lboost_...

include $(BUILD_STATIC_LIBRARY)
