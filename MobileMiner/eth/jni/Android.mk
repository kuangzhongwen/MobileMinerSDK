LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := libboost_system
LOCAL_EXPORT_C_INCLUDES    := $(LOCAL_PATH)/boost/include
LOCAL_SRC_FILES    := $(LOCAL_PATH)/boost/lib/libboost_system.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-dev-core

LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti -lpthread
LOCAL_C_INCLUDES  += ./jni/boost/include/
LOCAL_C_INCLUDES  += ./jni/libdevcore/
LOCAL_STATIC_LIBRARIES := libboost_system

LOCAL_SRC_FILES    := \
                    ./libdevcore/CommonData.cpp \
                    ./libdevcore/FixedHash.cpp \
                    ./libdevcore/Log.cpp \
                    ./libdevcore/RLP.cpp \
                    ./libdevcore/SHA3.cpp \
                    ./libdevcore/Worker.cpp

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-hash
LOCAL_C_INCLUDES  += ./jni/libethash/

LOCAL_SRC_FILES    := \
                    ./libethash/internal.c \
                    ./libethash/sha3.c
LOCAL_CFLAGS  += -std=gnu99

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti
LOCAL_MODULE    := hwmon
LOCAL_C_INCLUDES  += ./jni/libhwmon/
LOCAL_SRC_FILES    := \
                    ./libhwmon/wrapadl.cpp \
                    ./libhwmon/wrapamdsysfs.cpp \
                    ./libhwmon/wraphelper.cpp \
                    ./libhwmon/wrapnvml.cpp

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti
LOCAL_MODULE    := eth-core
LOCAL_C_INCLUDES  += ./jni/libethcore/

LOCAL_STATIC_LIBRARIES    := eth-dev-core eth-hash hwmon
LOCAL_SRC_FILES := \
                    ./libethcore/BlockHeader.cpp \
                    ./libethcore/EthashAux.cpp \
                    ./libethcore/Miner.cpp

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libz
LOCAL_SRC_FILES    := $(LOCAL_PATH)/curl/libz.a
LOCAL_EXPORT_C_INCLUDES    := ./jni/curl/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libssl
LOCAL_SRC_FILES    := $(LOCAL_PATH)/curl/libssl.a
LOCAL_EXPORT_C_INCLUDES    := ./jni/curl/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libcrypto
LOCAL_SRC_FILES    := $(LOCAL_PATH)/curl/libcrypto.a
LOCAL_EXPORT_C_INCLUDES    := ./jni/curl/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libcurl
LOCAL_SRC_FILES    := $(LOCAL_PATH)/curl/libcurl.a
LOCAL_EXPORT_C_INCLUDES    := ./jni/curl/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := jsonrpc

LOCAL_EXPORT_C_INCLUDES := ./jni/curl/include
LOCAL_CPPFLAGS    := -std=c++11 -Wall -Wextra -pedantic -Wredundant-decls \
                -Wshadow -O2 -Wno-long-long -Werror -ljsoncpp -lpthread

LOCAL_SRC_FILES := \
                ./jsonrpccpp/jsonrpc_client.cpp \
                ./jsonrpccpp/jsonrpc_handler.cpp \
                ./jsonrpccpp/jsonrpc_httpclient.cpp \
                ./jsonrpccpp/jsonrpc_server.cpp \
                ./jsonrpccpp/jsonrpc_tcpclient.cpp \
                ./jsonrpccpp/jsonrpc_tcpserver.cpp \
                ./jsonrpccpp/jsonrpc_udpclient.cpp \
                ./jsonrpccpp/jsonrpc_udpserver.cpp \
                ./jsonrpccpp/netstring.cpp \
                ./jsonrpccpp/networking.cpp \
                ./jsonrpccpp/system.cpp

LOCAL_STATIC_LIBRARIES := libcurl libssl libcrypto libz
LOCAL_LDLIBS    := -llog

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := pool-protocols
LOCAL_CPPFLAGS    := -Wall -std=c++11 -DANDROID -frtti -DHAVE_PTHREAD\
                  -finline-functions -ffast-math -O0
LOCAL_C_INCLUDES  += ./jni/boringssl/include/ \
                     ./jni/jsonrpccpp/
LOCAL_ARM_MODE    := arm
LOCAL_STATIC_LIBRARIES    := eth-dev-core libboost_system jsonrpc

LOCAL_SRC_FILES := \
                ./libpoolprotocols/getwork/EthGetworkClient.cpp \
                ./libpoolprotocols/stratum/EthStratumClient.cpp \
                ./libpoolprotocols/testing/SimulateClient.cpp \
                ./libpoolprotocols/PoolClient.cpp \
                ./libpoolprotocols/PoolManager.cpp

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := eth-miner
LOCAL_CPPFLAGS    := -std=c++11 -fexceptions -frtti
LOCAL_C_INCLUDES  += ./jni/libpoolprotocols/ \
                     ./jni/libpoolprotocols/getwork/ \
                     ./jni/libpoolprotocols/stratum/ \
                     ./jni/libpoolprotocols/testing/

LOCAL_STATIC_LIBRARIES    := eth-core pool-protocols
LOCAL_SRC_FILES    := \
               ./OpenCLPhone.cpp \
               ./CLMiner.cpp
LOCAL_LDLIBS    := -llog -Wextra -Wall \
        -Wno-deprecated-declarations -Wno-overlength-strings \
        libGLES_mali.so

include $(BUILD_SHARED_LIBRARY)

