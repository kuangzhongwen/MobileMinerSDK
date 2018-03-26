LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := libboost_system
LOCAL_EXPORT_C_INCLUDES    := $(LOCAL_PATH)/boost/include
LOCAL_SRC_FILES    := $(LOCAL_PATH)/boost/lib/libboost_system.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-dev-core

LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti -lpthread
LOCAL_C_INCLUDES  += ./jni/boost/include/
LOCAL_C_INCLUDES  += ./jni/libdevcore/
LOCAL_STATIC_LIBRARIES := libboost_system

LOCAL_SRC_FILES    := \
                    ./libdevcore/CommonData.cpp \
                    ./libdevcore/FixedHash.cpp \
                    ./libdevcore/Log.cpp \
                    ./libdevcore/RLP.cpp \
                    ./libdevcore/SHA3.cpp \
                    ./libdevcore/Worker.cpp

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-hash
LOCAL_C_INCLUDES  += ./jni/libethash/

LOCAL_SRC_FILES    := \
                    ./libethash/internal.c \
                    ./libethash/sha3.c
LOCAL_CFLAGS  += -std=gnu99

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti
LOCAL_MODULE    := hwmon
LOCAL_C_INCLUDES  += ./jni/libhwmon/
LOCAL_SRC_FILES    := \
                    ./libhwmon/wrapadl.cpp \
                    ./libhwmon/wrapamdsysfs.cpp \
                    ./libhwmon/wraphelper.cpp \
                    ./libhwmon/wrapnvml.cpp

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti
LOCAL_MODULE    := eth-core
LOCAL_C_INCLUDES  += ./jni/libethcore/

LOCAL_STATIC_LIBRARIES    := eth-dev-core eth-hash hwmon
LOCAL_SRC_FILES := \
                    ./libethcore/BlockHeader.cpp \
                    ./libethcore/EthashAux.cpp \
                    ./libethcore/Miner.cpp

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := eth-miner
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti

LOCAL_STATIC_LIBRARIES    := eth-core
LOCAL_SRC_FILES    := \
               ./OpenCLPhone.cpp \
               ./CLMiner.cpp
LOCAL_LDLIBS    := -llog -Wextra -Wall \
        -Wno-deprecated-declarations -Wno-overlength-strings \
        libGLES_mali.so

include $(BUILD_SHARED_LIBRARY)

