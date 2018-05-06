LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := lib-cpuid
LOCAL_C_INCLUDES  += $(LOCAL_PATH)/3rdparty/libcpuid
LOCAL_SRC_FILES    := \
                    ./3rdparty/libcpuid/cpuid_main.c \
                    ./3rdparty/libcpuid/asm-bits.c \
                    ./3rdparty/libcpuid/recog_amd.c \
                    ./3rdparty/libcpuid/recog_intel.c \
                    ./3rdparty/libcpuid/libcpuid_util.c
LOCAL_CFLAGS  += -std=gnu99 -Os
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-uv
LOCAL_EXPORT_C_INCLUDES    := $(LOCAL_PATH)/3rdparty/uv
LOCAL_SRC_FILES    := $(LOCAL_PATH)/3rdparty/uv/libuv.a
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-api
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti -lpthread
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/3rdparty/rapidjson \
                       $(LOCAL_PATH)/3rdparty/rapidjson/error \
                       $(LOCAL_PATH)/3rdparty/rapidjson/internal \
                       $(LOCAL_PATH)/3rdparty/rapidjson/msinttypes
LOCAL_SRC_FILES := \
    ./api/Api.cpp \
    ./api/ApiState.cpp \
    ./api/NetworkState.cpp

LOCAL_STATIC_LIBRARIES := lib-uv
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-crypto
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/crypto/

LOCAL_SRC_FILES    := \
                    ./crypto/c_blake256.c \
                    ./crypto/c_groestl.c \
                    ./crypto/c_jh.c \
                    ./crypto/c_keccak.c
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-cryptoNight
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/crypto/

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
    LOCAL_CPPFLAGS := -std=c++11 -DHAVE_NEON -mfloat-abi=softfp -mfpu=neon -march=armv7-a
    LOCAL_SRC_FILES := ./crypto/CryptoNight.cpp
endif

LOCAL_STATIC_LIBRARIES := lib-crypto

include $(BUILD_STATIC_LIBRARY)
