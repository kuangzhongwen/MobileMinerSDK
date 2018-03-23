/// OpenCL miner implementation.
///
/// @file
/// @copyright GNU General Public License
// @update kzw on 2018/03/23

// 保证头文件只被编译一次
#pragma once

#include <libdevcore/Worker.h>
#include <libethcore/EthashAux.h>
#include <libethcore/Miner.h>
#include <CL/cl.h>
#include "OpenCLPhone.h"

// 是否使用opencl 1.2 过期api
#define CL_USE_DEPRECATED_OPENCL_1_2_APIS true
#define CL_HPP_ENABLE_EXCEPTIONS true
// 默认使用opencl 1.2编译
#define CL_HPP_CL_1_2_DEFAULT_BUILD true

// 编译opencl的版本控制
#define CL_HPP_TARGET_OPENCL_VERSION 120
#define CL_HPP_MINIMUM_OPENCL_VERSION 120

// macOS OpenCL fix:
#ifndef CL_DEVICE_COMPUTE_CAPABILITY_MAJOR_NV
#define CL_DEVICE_COMPUTE_CAPABILITY_MAJOR_NV       0x4000
#endif

#ifndef CL_DEVICE_COMPUTE_CAPABILITY_MINOR_NV
#define CL_DEVICE_COMPUTE_CAPABILITY_MINOR_NV       0x4001
#endif

// opencl 平台
#define OPENCL_PLATFORM_UNKNOWN 0
#define OPENCL_PLATFORM_NVIDIA  1
#define OPENCL_PLATFORM_AMD     2
#define OPENCL_PLATFORM_CLOVER  3


namespace dev {
    namespace eth {

        // opencl kernel名称，需要根据.cl编译生成二进制.h文件
        enum CLKernelName {
            // 稳定版
	        Stable,
	        // 实验版
	        Experimental,
        };

        /**
         * miner 实现版本 - opencl
         */
        class CLMiner: public Miner {
            public:
                /* -- default values -- */
                /// Default value of the local work size. Also known as workgroup size.
                static const unsigned c_defaultLocalWorkSize = 128;
                /// Default value of the global work size as a multiplier of the local work size
                static const unsigned c_defaultGlobalWorkSizeMultiplier = 8192;

                // 默认使用稳定版
                static const CLKernelName c_defaultKernelName = CLKernelName::Stable;

                // 构造器，传入FarmFace引入，避免拷贝
                CLMiner(FarmFace& _farm, unsigned _index);
                // 析构函数
                ~CLMiner() override;

                static unsigned instances() {
                    return s_numInstances > 0 ? s_numInstances : 1;
                }

                static unsigned getNumDevices();

                static void listDevices();

                // GPU配置函数声明
                static bool configureGPU(unsigned _localWorkSize, unsigned _globalWorkSizeMultiplier,
                		unsigned _platformId, uint64_t _currentBlock, unsigned _dagLoadMode,
                		unsigned _dagCreateDevice, bool _exit
                		);

                static void setNumInstances(unsigned _instances) {
                    s_numInstances = std::min<unsigned>(_instances, getNumDevices());
                }

                static void setThreadsPerHash(unsigned _threadsPerHash) {
                    s_threadsPerHash = _threadsPerHash;
                }

                /**
                 * 设置设备，设备的ID和设备数
                 */
                static void setDevices(const vector<unsigned> _devices, unsigned _selectedDeviceCount) {
                    for (unsigned i = 0; i < _selectedDeviceCount; ++i) {
                        s_devices[i] = _devices[i];
                    }
                }

                /**
                 * 设置opencl kernel版本，稳定版还是实验版
                 */
                static void setCLKernel(unsigned _clKernel) {
                    s_clKernelName = _clKernel == 1 ? CLKernelName::Experimental : CLKernelName::Stable;
                }

            protected:
                // 虚函数
                void kick_miner() override;



            private:
                // miner实例
                static unsigned s_numInstances;
                // 线程的每个hash数
                static unsigned s_threadsPerHash;
                // 设备id
            	static vector<int> s_devices;
            	// opencl platform id
            	static unsigned s_platformId;
            	// cl版本
            	static CLKernelName s_clKernelName;

                // cl相关变量
            	cl_context m_context;
            	cl_command_queue m_queue;
            	cl_kernel m_searchKernel;
            	cl_kernel m_dagKernel;
            	cl_mem m_dag;
            	cl_mem m_light;
            	cl_mem m_header;
            	cl_mem m_searchBuffer;
            	unsigned m_globalWorkSize = 0;
            	unsigned m_workgroupSize = 0;

            	/// The local work size for the search
            	static unsigned s_workgroupSize;
            	/// The initial global work size for the searches
            	static unsigned s_initialGlobalWorkSize;

                // 轮询work虚函数
            	void workLoop() override;

                bool init(const h256& seed);
        };
    }
}