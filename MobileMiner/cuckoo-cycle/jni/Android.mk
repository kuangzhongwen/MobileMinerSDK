LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libblake2b
LOCAL_CFLAGS    += -std=gnu99
LOCAL_SRC_FILES := black2b-ref.c

include $(BUILD_SHARED_LIBRARY)
