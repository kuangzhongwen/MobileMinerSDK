LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := silentarmy

LOCAL_SRC_FILES := blake.c sha256.c Silentarmy.c
LOCAL_CFLAGS += -std=gnu99
LOCAL_LDLIBS:= -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings libOpenCL.so

include $(BUILD_SHARED_LIBRARY)
