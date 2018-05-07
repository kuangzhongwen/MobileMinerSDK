LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# cpuid
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


# uv
include $(CLEAR_VARS)
LOCAL_MODULE    := lib-uv
LOCAL_EXPORT_C_INCLUDES    := $(LOCAL_PATH)/3rdparty/uv
LOCAL_SRC_FILES    := $(LOCAL_PATH)/3rdparty/uv/libuv.a
include $(PREBUILT_STATIC_LIBRARY)
