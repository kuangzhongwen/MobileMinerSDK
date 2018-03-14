LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := zcash-miner

LOCAL_SRC_FILES := OpenCL_phone.c blake.c sha256.c zcash_miner.c
LOCAL_CFLAGS += -std=gnu99
LOCAL_LDLIBS:= -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings

include $(BUILD_SHARED_LIBRARY)
