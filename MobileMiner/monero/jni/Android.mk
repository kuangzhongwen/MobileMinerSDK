LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

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


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-uv
LOCAL_EXPORT_C_INCLUDES    := $(LOCAL_PATH)/3rdparty/uv
LOCAL_SRC_FILES    := $(LOCAL_PATH)/3rdparty/uv/libuv.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-crypto
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/crypto/

LOCAL_SRC_FILES    := \
                    ./crypto/c_blake256.c \
                    ./crypto/c_groestl.c \
                    ./crypto/c_jh.c \
                    ./crypto/c_keccak.c
LOCAL_CFLAGS  += -std=gnu99 -Os
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := lib-cryptoNight
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/crypto/

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
    LOCAL_CPPFLAGS := -std=c++11 -DHAVE_NEON -mfloat-abi=softfp -mfpu=neon -march=armv7-a
    LOCAL_SRC_FILES := ./crypto/CryptoNight.cpp
endif

LOCAL_STATIC_LIBRARIES := lib-crypto
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := monero-miner

LOCAL_CPPFLAGS := -std=c++11 -Ofast -s -funroll-loops -fvariable-expansion-in-unroller \
            -ftree-loop-if-convert-stores -fmerge-all-constants -fbranch-target-load-optimize2 \
            -Wall -fno-exceptions -fno-rtti -llog -Wextra

LOCAL_C_INCLUDES    := $(LOCAL_PATH)/3rdparty/rapidjson \
                       $(LOCAL_PATH)/3rdparty/rapidjson/error \
                       $(LOCAL_PATH)/3rdparty/rapidjson/internal \
                       $(LOCAL_PATH)/3rdparty/rapidjson/msinttypes \
                       $(LOCAL_PATH)/api/ \
                       $(LOCAL_PATH)/interfaces/ \
                       $(LOCAL_PATH)/log/ \
                       $(LOCAL_PATH)/net/ \
                       $(LOCAL_PATH)/workers/

LOCAL_SRC_FILES    := ./api/Api.cpp \
                      ./api/ApiState.cpp \
                      ./api/NetworkState.cpp\
                      ./log/ConsoleLog.cpp \
                      ./log/FileLog.cpp \
                      ./log/Log.cpp \
                      ./log/SysLog.cpp \
                      ./net/strategies/DonateStrategy.cpp \
                      ./net/strategies/FailoverStrategy.cpp \
                      ./net/strategies/SinglePoolStrategy.cpp \
                      ./net/Client.cpp \
                      ./net/Job.cpp \
                      ./net/Network.cpp \
                      ./net/SubmitResult.cpp \
                      ./net/Url.cpp \
                      ./workers/DoubleWorker.cpp \
                      ./workers/Handle.cpp \
                      ./workers/Hashrate.cpp \
                      ./workers/SingleWorker.cpp \
                      ./workers/Worker.cpp \
                      ./workers/Workers.cpp \
                      ./App.cpp \
                      ./Console.cpp \
                      ./Cpu.cpp \
                      ./Cpu_arm.cpp \
                      ./Mem.cpp \
                      ./Options.cpp \
                      ./Platform.cpp \
                      ./Summary.cpp \
                      ./xmrig.cpp

LOCAL_STATIC_LIBRARIES := lib-cpuid lib-uv lib-crypto lib-cryptoNight
include $(BUILD_SHARED_LIBRARY)
