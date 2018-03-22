LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CPP_EXTENSION := .cpp
LOCAL_MODULE    := eth-dev-core

LOCAL_C_INCLUDES := ./jni/boost/include

LOCAL_CFLAGS += -std=c++11 -pthread -fexceptions -frtti
LOCAL_LDLIBS:= -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings
LOCAL_CFLAGS += -I$(LOCAL_PATH)/boost/include
LOCAL_LDLIBS += -L$(LOCAL_PATH)/boost/lib/ -lboost_system -lboost_...

LOCAL_SRC_FILES := \
        ./libdevcore/CommonData.cpp \
        ./libdevcore/FixedHash.cpp \
        ./libdevcore/Log.cpp \
        ./libdevcore/RLP.cpp \
        ./libdevcore/SHA3.cpp \
        ./libdevcore/Worker.cpp

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := eth-miner
LOCAL_SRC_FILES := EthMiner.cpp
LOCAL_STATIC_LIBRARIES := eth-dev-core
include $(BUILD_SHARED_LIBRARY)



