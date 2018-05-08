LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

######################################## third party library ########################################
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


# microhttpd
include $(CLEAR_VARS)

LIB_ROOT_ABS :=  $(LOCAL_PATH)/3rdparty/libmicrohttpd
LOCAL_CFLAGS := -DHAVE_CONFIG_H

LOCAL_MODULE := lib-microhttpd

LOCAL_SRC_FILES := \
        $(LIB_ROOT_ABS)/src/microhttpd/base64.c \
        $(LIB_ROOT_ABS)/src/microhttpd/basicauth.c \
        $(LIB_ROOT_ABS)/src/microhttpd/connection.c \
        $(LIB_ROOT_ABS)/src/microhttpd/daemon.c \
        $(LIB_ROOT_ABS)/src/microhttpd/digestauth.c \
        $(LIB_ROOT_ABS)/src/microhttpd/internal.c \
        $(LIB_ROOT_ABS)/src/microhttpd/md5.c \
        $(LIB_ROOT_ABS)/src/microhttpd/memorypool.c \
        $(LIB_ROOT_ABS)/src/microhttpd/postprocessor.c \
        $(LIB_ROOT_ABS)/src/microhttpd/reason_phrase.c \
        $(LIB_ROOT_ABS)/src/microhttpd/response.c \
        $(LIB_ROOT_ABS)/src/microhttpd/tsearch.c

LOCAL_C_INCLUDES += \
        $(LIB_ROOT_ABS) \
        $(LIB_ROOT_ABS)/src/include \
        $(LIB_ROOT_ABS)/src/microhttpd

LOCAL_CFLAGS  += -std=gnu99
include $(BUILD_STATIC_LIBRARY)


######################################## monero logic ########################################
