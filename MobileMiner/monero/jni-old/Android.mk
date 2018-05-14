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
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/3rdparty/uv \
                       $(LOCAL_PATH)/3rdparty/uv/unix

LOCAL_SRC_FILES    := \
                      $(LOCAL_PATH)/3rdparty/uv/fs-poll.c \
                      $(LOCAL_PATH)/3rdparty/uv/inet.c \
                      $(LOCAL_PATH)/3rdparty/uv/threadpool.c \
                      $(LOCAL_PATH)/3rdparty/uv/uv-common.c \
                      $(LOCAL_PATH)/3rdparty/uv/uv-data-getter-setters.c \
                      $(LOCAL_PATH)/3rdparty/uv/version.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/android-ifaddrs.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/async.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/core.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/cygwin.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/dl.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/fs.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/getaddrinfo.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/getnameinfo.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/linux-core.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/linux-inotify.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/linux-syscalls.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/loop.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/loop-watcher.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/no-fsevents.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/no-proctitle.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/pipe.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/poll.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/posix-hrtime.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/process.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/procfs-exepath.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/proctitle.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/pthread-fixes.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/signal.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/stream.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/sysinfo-loadavg.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/sysinfo-memory.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/tcp.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/thread.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/timer.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/tty.c \
                      $(LOCAL_PATH)/3rdparty/uv/unix/udp.c

LOCAL_CFLAGS  += -std=gnu99 -Os
include $(BUILD_STATIC_LIBRARY)


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
# c crypto
include $(CLEAR_VARS)
LOCAL_MODULE    := lib-crypto
LOCAL_C_INCLUDES    := $(LOCAL_PATH)/crypto/

LOCAL_SRC_FILES    := \
                    ./crypto/c_blake256.c \
                    ./crypto/c_groestl.c \
                    ./crypto/c_jh.c \
                    ./crypto/c_keccak.c \
                    ./crypto/c_skein.c

LOCAL_CFLAGS  += -std=c11 -Wall -Wno-strict-aliasing
include $(BUILD_STATIC_LIBRARY)


# monero miner
include $(CLEAR_VARS)
LOCAL_MODULE := monero-miner

HEADERS := $(LOCAL_PATH)/

LOCAL_C_INCLUDES := $(HEADERS)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/3rdparty/libcpuid/

LOCAL_SRC_FILES := $(LOCAL_PATH)/api/Api.cpp \
                   $(LOCAL_PATH)/api/ApiState.cpp \
                   $(LOCAL_PATH)/api/Httpd.cpp \
                   $(LOCAL_PATH)/api/NetworkState.cpp \
                   $(LOCAL_PATH)/crypto/CryptoNight.cpp \
                   $(LOCAL_PATH)/log/ConsoleLog.cpp \
                   $(LOCAL_PATH)/log/FileLog.cpp \
                   $(LOCAL_PATH)/log/Log.cpp \
                   $(LOCAL_PATH)/log/SysLog.cpp \
                   $(LOCAL_PATH)/net/strategies/DonateStrategy.cpp \
                   $(LOCAL_PATH)/net/strategies/FailoverStrategy.cpp \
                   $(LOCAL_PATH)/net/strategies/SinglePoolStrategy.cpp \
                   $(LOCAL_PATH)/net/Client.cpp \
                   $(LOCAL_PATH)/net/Job.cpp \
                   $(LOCAL_PATH)/net/Network.cpp \
                   $(LOCAL_PATH)/net/SubmitResult.cpp \
                   $(LOCAL_PATH)/net/Url.cpp \
                   $(LOCAL_PATH)/workers/DoubleWorker.cpp \
                   $(LOCAL_PATH)/workers/Handle.cpp \
                   $(LOCAL_PATH)/workers/Hashrate.cpp \
                   $(LOCAL_PATH)/workers/SingleWorker.cpp \
                   $(LOCAL_PATH)/workers/Worker.cpp \
                   $(LOCAL_PATH)/workers/Workers.cpp \
                   $(LOCAL_PATH)/App.cpp \
                   $(LOCAL_PATH)/App_unix.cpp \
                   $(LOCAL_PATH)/Console.cpp \
                   $(LOCAL_PATH)/Cpu.cpp \
                   $(LOCAL_PATH)/Cpu_arm.cpp \
                   $(LOCAL_PATH)/Cpu_unix.cpp \
                   $(LOCAL_PATH)/Mem.cpp \
                   $(LOCAL_PATH)/Mem_unix.cpp \
                   $(LOCAL_PATH)/Options.cpp \
                   $(LOCAL_PATH)/Platform.cpp \
                   $(LOCAL_PATH)/Platform_unix.cpp \
                   $(LOCAL_PATH)/Summary.cpp \
                   $(LOCAL_PATH)/xmrig.cpp

LOCAL_LDLIBS:= -llog -pedantic -Wextra -Wall -Wno-deprecated-declarations -Wno-overlength-strings \
               -pthread
LOCAL_STATIC_LIBRARIES := lib-cpuid lib-uv lib-microhttpd lib-crypto

LOCAL_CPPFLAGS := -std=c++11 -Wall -fno-exceptions -fno-rtti \
                -DHAVE_NEON  -flax-vector-conversions -Wno-strict-aliasing -march=armv8-a+crypto

include $(BUILD_SHARED_LIBRARY)


