LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libblake2b
LOCAL_CFLAGS    += -std=gnu99
LOCAL_SRC_FILES := black2b-ref.c
include $(BUILD_STATIC_LIBRARY)

LOCAL_MODULE    := libcuckoo
LOCAL_CFLAGS    += -std=gnu99
LOCAL_SRC_FILES := cuckoo.c
include $(BUILD_STATIC_LIBRARY)

LOCAL_MODULE    := libopencl
LOCAL_CFLAGS    += -std=gnu99
LOCAL_SRC_FILES := openCL_phone.c
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := cuckoo-cycle

LOCAL_SRC_FILES := cuckoo_main.cpp lean_miner.cpp mean_miner.cpp momentomatum.cpp \
                   simple_miner.cpp tomato_miner.cpp

LOCAL_STATIC_LIBRARIES    := libblake2b libcuckoo libopencl
LOCAL_CPPFLAGS  := -std=c++11 -fexceptions -frtti
LOCAL_LDLIBS    := -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings

include $(BUILD_SHARED_LIBRARY)
