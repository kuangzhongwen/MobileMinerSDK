LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := cuckoo-cycle

LOCAL_SRC_FILES := cuckoo.c
LOCAL_CFLAGS += -std=gnu99
LOCAL_LDLIBS:= -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings

include $(BUILD_SHARED_LIBRARY)
