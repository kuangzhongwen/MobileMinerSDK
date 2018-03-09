LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := silentarmy

LOCAL_SRC_FILES := blake.c sha256.c Silentarmy.c

LOCAL_LDLIBS:= -llog libGLES_mali.so

LOCAL_CFLAGS += -std=c99

include $(BUILD_SHARED_LIBRARY)
