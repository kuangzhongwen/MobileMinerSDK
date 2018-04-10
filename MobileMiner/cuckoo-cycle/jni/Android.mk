LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libblake2b
LOCAL_SRC_FILES := blake2b-ref.c
LOCAL_CFLAGS += -std=gnu99

include $(BUILD_SHARED_LIBRARY)
