LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libblake2b
LOCAL_SRC_FILES := blake2b-ref.c
LOCAL_CFLAGS    += -std=gnu99

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := libopencl
LOCAL_CFLAGS    += -std=gnu99
LOCAL_SRC_FILES := openCL_phone.c

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := libcuckoo
LOCAL_CFLAGS    += -std=gnu99
LOCAL_SRC_FILES := cuckoo.c
LOCAL_STATIC_LIBRARIES    := libblake2b

include $(BUILD_STATIC_LIBRARY)