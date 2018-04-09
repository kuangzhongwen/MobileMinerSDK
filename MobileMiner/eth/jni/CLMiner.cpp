/// OpenCL miner implementation.
///
/// @file
/// @copyright GNU General Public License

#include <jni.h>

#include "CLMiner.h"
#include "Utility.h"
#include <libethash/internal.h>
#include "CLMiner_kernel_stable.h"
#include "CLMiner_kernel_experimental.h"

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/trim_all.hpp>
#include <boost/optional.hpp>

#include <libethcore/Exceptions.h>
#include <libdevcore/SHA3.h>
#include <libethcore/EthashAux.h>
#include <libethcore/Farm.h>

#include "android_log.h"

using namespace dev;
using namespace eth;

namespace dev
{
namespace eth
{

unsigned CLMiner::s_workgroupSize = CLMiner::c_defaultLocalWorkSize;
unsigned CLMiner::s_initialGlobalWorkSize = CLMiner::c_defaultGlobalWorkSizeMultiplier * CLMiner::c_defaultLocalWorkSize;
unsigned CLMiner::s_threadsPerHash = 8;
CLKernelName CLMiner::s_clKernelName = CLMiner::c_defaultKernelName;

constexpr size_t c_maxSearchResults = 1;

struct CLChannel: public LogChannel
{
	static const char* name() { return EthOrange " cl"; }
	static const int verbosity = 2;
	static const bool debug = false;
};
struct CLSwitchChannel: public LogChannel
{
	static const char* name() { return EthOrange " cl"; }
	static const int verbosity = 6;
	static const bool debug = false;
};
#define cllog clog(CLChannel)
#define clswitchlog clog(CLSwitchChannel)
#define ETHCL_LOG(_contents) cllog << _contents

/**
 * Returns the name of a numerical cl_int error
 * Takes constants from CL/cl.h and returns them in a readable format
 */
static const char *strClError(cl_int err) {

	switch (err) {
	case CL_SUCCESS:
		return "CL_SUCCESS";
	case CL_DEVICE_NOT_FOUND:
		return "CL_DEVICE_NOT_FOUND";
	case CL_DEVICE_NOT_AVAILABLE:
		return "CL_DEVICE_NOT_AVAILABLE";
	case CL_COMPILER_NOT_AVAILABLE:
		return "CL_COMPILER_NOT_AVAILABLE";
	case CL_MEM_OBJECT_ALLOCATION_FAILURE:
		return "CL_MEM_OBJECT_ALLOCATION_FAILURE";
	case CL_OUT_OF_RESOURCES:
		return "CL_OUT_OF_RESOURCES";
	case CL_OUT_OF_HOST_MEMORY:
		return "CL_OUT_OF_HOST_MEMORY";
	case CL_PROFILING_INFO_NOT_AVAILABLE:
		return "CL_PROFILING_INFO_NOT_AVAILABLE";
	case CL_MEM_COPY_OVERLAP:
		return "CL_MEM_COPY_OVERLAP";
	case CL_IMAGE_FORMAT_MISMATCH:
		return "CL_IMAGE_FORMAT_MISMATCH";
	case CL_IMAGE_FORMAT_NOT_SUPPORTED:
		return "CL_IMAGE_FORMAT_NOT_SUPPORTED";
	case CL_BUILD_PROGRAM_FAILURE:
		return "CL_BUILD_PROGRAM_FAILURE";
	case CL_MAP_FAILURE:
		return "CL_MAP_FAILURE";
	case CL_MISALIGNED_SUB_BUFFER_OFFSET:
		return "CL_MISALIGNED_SUB_BUFFER_OFFSET";
	case CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST:
		return "CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST";

#ifdef CL_VERSION_1_2
	case CL_COMPILE_PROGRAM_FAILURE:
		return "CL_COMPILE_PROGRAM_FAILURE";
	case CL_LINKER_NOT_AVAILABLE:
		return "CL_LINKER_NOT_AVAILABLE";
	case CL_LINK_PROGRAM_FAILURE:
		return "CL_LINK_PROGRAM_FAILURE";
	case CL_DEVICE_PARTITION_FAILED:
		return "CL_DEVICE_PARTITION_FAILED";
	case CL_KERNEL_ARG_INFO_NOT_AVAILABLE:
		return "CL_KERNEL_ARG_INFO_NOT_AVAILABLE";
#endif // CL_VERSION_1_2

	case CL_INVALID_VALUE:
		return "CL_INVALID_VALUE";
	case CL_INVALID_DEVICE_TYPE:
		return "CL_INVALID_DEVICE_TYPE";
	case CL_INVALID_PLATFORM:
		return "CL_INVALID_PLATFORM";
	case CL_INVALID_DEVICE:
		return "CL_INVALID_DEVICE";
	case CL_INVALID_CONTEXT:
		return "CL_INVALID_CONTEXT";
	case CL_INVALID_QUEUE_PROPERTIES:
		return "CL_INVALID_QUEUE_PROPERTIES";
	case CL_INVALID_COMMAND_QUEUE:
		return "CL_INVALID_COMMAND_QUEUE";
	case CL_INVALID_HOST_PTR:
		return "CL_INVALID_HOST_PTR";
	case CL_INVALID_MEM_OBJECT:
		return "CL_INVALID_MEM_OBJECT";
	case CL_INVALID_IMAGE_FORMAT_DESCRIPTOR:
		return "CL_INVALID_IMAGE_FORMAT_DESCRIPTOR";
	case CL_INVALID_IMAGE_SIZE:
		return "CL_INVALID_IMAGE_SIZE";
	case CL_INVALID_SAMPLER:
		return "CL_INVALID_SAMPLER";
	case CL_INVALID_BINARY:
		return "CL_INVALID_BINARY";
	case CL_INVALID_BUILD_OPTIONS:
		return "CL_INVALID_BUILD_OPTIONS";
	case CL_INVALID_PROGRAM:
		return "CL_INVALID_PROGRAM";
	case CL_INVALID_PROGRAM_EXECUTABLE:
		return "CL_INVALID_PROGRAM_EXECUTABLE";
	case CL_INVALID_KERNEL_NAME:
		return "CL_INVALID_KERNEL_NAME";
	case CL_INVALID_KERNEL_DEFINITION:
		return "CL_INVALID_KERNEL_DEFINITION";
	case CL_INVALID_KERNEL:
		return "CL_INVALID_KERNEL";
	case CL_INVALID_ARG_INDEX:
		return "CL_INVALID_ARG_INDEX";
	case CL_INVALID_ARG_VALUE:
		return "CL_INVALID_ARG_VALUE";
	case CL_INVALID_ARG_SIZE:
		return "CL_INVALID_ARG_SIZE";
	case CL_INVALID_KERNEL_ARGS:
		return "CL_INVALID_KERNEL_ARGS";
	case CL_INVALID_WORK_DIMENSION:
		return "CL_INVALID_WORK_DIMENSION";
	case CL_INVALID_WORK_GROUP_SIZE:
		return "CL_INVALID_WORK_GROUP_SIZE";
	case CL_INVALID_WORK_ITEM_SIZE:
		return "CL_INVALID_WORK_ITEM_SIZE";
	case CL_INVALID_GLOBAL_OFFSET:
		return "CL_INVALID_GLOBAL_OFFSET";
	case CL_INVALID_EVENT_WAIT_LIST:
		return "CL_INVALID_EVENT_WAIT_LIST";
	case CL_INVALID_EVENT:
		return "CL_INVALID_EVENT";
	case CL_INVALID_OPERATION:
		return "CL_INVALID_OPERATION";
	case CL_INVALID_GL_OBJECT:
		return "CL_INVALID_GL_OBJECT";
	case CL_INVALID_BUFFER_SIZE:
		return "CL_INVALID_BUFFER_SIZE";
	case CL_INVALID_MIP_LEVEL:
		return "CL_INVALID_MIP_LEVEL";
	case CL_INVALID_GLOBAL_WORK_SIZE:
		return "CL_INVALID_GLOBAL_WORK_SIZE";
	case CL_INVALID_PROPERTY:
		return "CL_INVALID_PROPERTY";

#ifdef CL_VERSION_1_2
	case CL_INVALID_IMAGE_DESCRIPTOR:
		return "CL_INVALID_IMAGE_DESCRIPTOR";
	case CL_INVALID_COMPILER_OPTIONS:
		return "CL_INVALID_COMPILER_OPTIONS";
	case CL_INVALID_LINKER_OPTIONS:
		return "CL_INVALID_LINKER_OPTIONS";
	case CL_INVALID_DEVICE_PARTITION_COUNT:
		return "CL_INVALID_DEVICE_PARTITION_COUNT";
#endif // CL_VERSION_1_2

#ifdef CL_VERSION_2_0
	case CL_INVALID_PIPE_SIZE:
		return "CL_INVALID_PIPE_SIZE";
	case CL_INVALID_DEVICE_QUEUE:
		return "CL_INVALID_DEVICE_QUEUE";
#endif // CL_VERSION_2_0

#ifdef CL_VERSION_2_2
	case CL_INVALID_SPEC_ID:
		return "CL_INVALID_SPEC_ID";
	case CL_MAX_SIZE_RESTRICTION_EXCEEDED:
		return "CL_MAX_SIZE_RESTRICTION_EXCEEDED";
#endif // CL_VERSION_2_2
	}

	return "Unknown CL error encountered";
}

/**
 * Prints cl::Errors in a uniform way
 * @param msg text prepending the error message
 * @param clerr cl:Error object
 *
 * Prints errors in the format:
 *      msg: what(), string err() (numeric err())
 */
static std::string ethCLErrorHelper(const char *msg, cl::Error const &clerr) {
	std::ostringstream osstream;
	osstream << msg << ": " << clerr.what() << ": " << strClError(clerr.err())
	         << " (" << clerr.err() << ")";
	return osstream.str();
}

namespace
{

void addDefinition(string& _source, char const* _id, unsigned _value)
{
	char buf[256];
	fprintf(buf, "#define %s %uu\n", _id, _value);
	_source.insert(_source.begin(), buf, buf + strlen(buf));
}

std::vector<cl::Platform> getPlatforms()
{
	vector<cl::Platform> platforms;
	try
	{
		cl::Platform::get(&platforms);
	}
	catch(cl::Error const& err)
	{
#if defined(CL_PLATFORM_NOT_FOUND_KHR)
		if (err.err() == CL_PLATFORM_NOT_FOUND_KHR)
			cwarn << "No OpenCL platforms found";
		else
#endif
			throw err;
	}
	return platforms;
}

std::vector<cl::Device> getDevices(std::vector<cl::Platform> const& _platforms, unsigned _platformId)
{
	vector<cl::Device> devices;
	size_t platform_num = min<size_t>(_platformId, _platforms.size() - 1);
	try
	{
		_platforms[platform_num].getDevices(
			CL_DEVICE_TYPE_GPU | CL_DEVICE_TYPE_ACCELERATOR,
			&devices
		);
	}
	catch (cl::Error const& err)
	{
		LOGD("error %d", err.err());
		// if simply no devices found return empty vector
		if (err.err() != CL_DEVICE_NOT_FOUND)
			throw err;
	}
	return devices;
}

}

}
}

unsigned CLMiner::s_platformId = 0;
unsigned CLMiner::s_numInstances = 0;
vector<int> CLMiner::s_devices(MAX_MINERS, -1);

CLMiner::CLMiner(FarmFace& _farm, unsigned _index):
	Miner("cl-", _farm, _index)
{}

CLMiner::~CLMiner()
{
	stopWorking();
	kick_miner();
}

void CLMiner::workLoop()
{
	// Memory for zero-ing buffers. Cannot be static because crashes on macOS.
	uint32_t const c_zero = 0;

	uint64_t startNonce = 0;

	// The work package currently processed by GPU.
	WorkPackage current;
	current.header = h256{1u};
	current.seed = h256{1u};

	try {
		while (true)
		{
			const WorkPackage w = work();

			if (current.header != w.header)
			{
				// New work received. Update GPU data.
				/**
				if (!w)
				{
					LOGD("%s", "No work. Pause for 3 s.");
					std::this_thread::sleep_for(std::chrono::seconds(3));
					continue;
				}

				//cllog << "New work: header" << w.header << "target" << w.boundary.hex();
                */
				// printf("New seed %d", w.seed);
				// todo test
                h256 seed = FixedHash<32>("7bb6f14a940828054edc1aa9ec9f31e274e036a730709926e1f2900225e2f745");
                h256 boundary = FixedHash<32>("000000007fffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                h256 header = FixedHash<32>("a0c4bf7550bd54b486106c897684c9556db6e131404c89ff5a1cef456162391c");
                init(seed);

				// Upper 64 bits of the boundary.
				const uint64_t target = (uint64_t)(u64)((u256)boundary >> 192);
				assert(target > 0);

				// Update header constant buffer.
				m_queue.enqueueWriteBuffer(m_header, CL_FALSE, 0, header.size, header.data());
				m_queue.enqueueWriteBuffer(m_searchBuffer, CL_FALSE, 0, sizeof(c_zero), &c_zero);

				m_searchKernel.setArg(0, m_searchBuffer);  // Supply output buffer to kernel.
				m_searchKernel.setArg(4, target);

				// FIXME: This logic should be move out of here.
				if (w.exSizeBits >= 0)
				{
					// This can support up to 2^c_log2MaxMiners devices.
					startNonce = w.startNonce | ((uint64_t)index << (64 - LOG2_MAX_MINERS - w.exSizeBits));
				}
				else
					startNonce = get_start_nonce();

                auto costSeconds = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::high_resolution_clock::now() - workSwitchStart).count();
				LOGD("Switch time %lld ms.", costSeconds);
			}
			LOGD("%s", "0000000");
			// Read results.
			// TODO: could use pinned host pointer instead.
			uint32_t results[c_maxSearchResults + 1];
			m_queue.enqueueReadBuffer(m_searchBuffer, CL_TRUE, 0, sizeof(results), &results);
			LOGD("%s", "1111111");
			uint64_t nonce = 0;
			if (results[0] > 0)
			{
				LOGD("%s", "2222222");
				// Ignore results except the first one.
				nonce = current.startNonce + results[1];
				// Reset search buffer if any solution found.
				m_queue.enqueueWriteBuffer(m_searchBuffer, CL_FALSE, 0, sizeof(c_zero), &c_zero);
			    LOGD("%s", "3333333");
			}
			LOGD("%s", "4444444");
			// Run the kernel.
			m_searchKernel.setArg(3, startNonce);
		    LOGD("%s", "5555555");
			m_queue.enqueueNDRangeKernel(m_searchKernel, cl::NullRange, m_globalWorkSize, m_workgroupSize);
			LOGD("%s", "enqueueNDRangeKernel");
			// Report results while the kernel is running.
			// It takes some time because ethash must be re-evaluated on CPU.
			if (nonce != 0) {
				LOGD("%s", "nonce != 0");
				Result r = EthashAux::eval(current.seed, current.header, nonce);
				if (r.value < current.boundary) {
					LOGD("%s", "find solution");
					farm.submitProof(Solution{nonce, r.mixHash, current, current.header != w.header});
				} else {
					farm.failedSolution();
					LOGD("%s", "FAILURE: failed solution !");
				}
			}

			current = w;        // kernel now processing newest work
			current.startNonce = startNonce;
			// Increase start nonce for following kernel execution.
			startNonce += m_globalWorkSize;

			// Report hash count
			addHashCount(m_globalWorkSize);
		}

		// Make sure the last buffer write has finished --
		// it reads local variable.
		m_queue.finish();
	}
	catch (cl::Error const& _e)
	{
		cwarn << ethCLErrorHelper("OpenCL Error", _e);
		LOGD("%d", _e.err());
	}
}

void CLMiner::kick_miner() {}

unsigned CLMiner::getNumDevices()
{
	vector<cl::Platform> platforms = getPlatforms();
	if (platforms.empty())
		return 0;

	vector<cl::Device> devices = getDevices(platforms, s_platformId);
	if (devices.empty())
	{
		cwarn << "No OpenCL devices found.";
		return 0;
	}
	return devices.size();
}

void CLMiner::listDevices()
{
	string outString ="\nListing OpenCL devices.\nFORMAT: [platformID] [deviceID] deviceName\n";
	unsigned int i = 0;

	vector<cl::Platform> platforms = getPlatforms();
	if (platforms.empty())
		return;
	for (unsigned j = 0; j < platforms.size(); ++j)
	{
		i = 0;
		vector<cl::Device> devices = getDevices(platforms, j);
		for (auto const& device: devices)
		{
			outString += "[" + to_string(j) + "] [" + to_string(i) + "] " + device.getInfo<CL_DEVICE_NAME>() + "\n";
			outString += "\tCL_DEVICE_TYPE: ";
			switch (device.getInfo<CL_DEVICE_TYPE>())
			{
			case CL_DEVICE_TYPE_CPU:
				outString += "CPU\n";
				break;
			case CL_DEVICE_TYPE_GPU:
				outString += "GPU\n";
				break;
			case CL_DEVICE_TYPE_ACCELERATOR:
				outString += "ACCELERATOR\n";
				break;
			default:
				outString += "DEFAULT\n";
				break;
			}
			outString += "\tCL_DEVICE_GLOBAL_MEM_SIZE: " + to_string(device.getInfo<CL_DEVICE_GLOBAL_MEM_SIZE>()) + "\n";
			outString += "\tCL_DEVICE_MAX_MEM_ALLOC_SIZE: " + to_string(device.getInfo<CL_DEVICE_MAX_MEM_ALLOC_SIZE>()) + "\n";
			outString += "\tCL_DEVICE_MAX_WORK_GROUP_SIZE: " + to_string(device.getInfo<CL_DEVICE_MAX_WORK_GROUP_SIZE>()) + "\n";
			++i;
		}
	}
	LOGD("listDevices : %s", outString.c_str());
	std::cout << outString;
}

bool CLMiner::configureGPU(
	unsigned _localWorkSize,
	unsigned _globalWorkSizeMultiplier,
	unsigned _platformId,
	uint64_t _currentBlock,
	unsigned _dagLoadMode,
	unsigned _dagCreateDevice,
	bool _exit
)
{
	s_dagLoadMode = _dagLoadMode;
	s_dagCreateDevice = _dagCreateDevice;
	s_exit = _exit;

	s_platformId = _platformId;

	_localWorkSize = ((_localWorkSize + 7) / 8) * 8;
	s_workgroupSize = _localWorkSize;
	s_initialGlobalWorkSize = _globalWorkSizeMultiplier * _localWorkSize;
	uint64_t dagSize = ethash_get_datasize(_currentBlock);

	listDevices();

	vector<cl::Platform> platforms = getPlatforms();
	if (platforms.empty())
		return false;
	if (_platformId >= platforms.size())
		return false;
    LOGD("_platformId %d", _platformId);
	vector<cl::Device> devices = getDevices(platforms, _platformId);
	for (auto const& device: devices)
	{
		cl_ulong result = 0;
		device.getInfo(CL_DEVICE_GLOBAL_MEM_SIZE, &result);
		if (result >= dagSize)
		{
			cnote <<
				"Found suitable OpenCL device [" << device.getInfo<CL_DEVICE_NAME>()
												 << "] with " << result << " bytes of GPU memory";
			return true;
		}

		cnote <<
			"OpenCL device " << device.getInfo<CL_DEVICE_NAME>()
							 << " has insufficient GPU memory." << result <<
							 " bytes of memory found < " << dagSize << " bytes of memory required";
	}

	cout << "No GPU device with sufficient memory was found. Can't GPU mine. Remove the -G argument" << endl;
	return false;
}

bool CLMiner::init(const h256& seed)
{
	EthashAux::LightType light = EthashAux::light(seed);
	// get all platforms
	try
	{
		vector<cl::Platform> platforms = getPlatforms();
		LOGD("%s", "getPlatforms");
		if (platforms.empty()) {
		    LOGD("%s", "getPlatforms empty");
			return false;
        }
		// use selected platform
		unsigned platformIdx = min<unsigned>(s_platformId, platforms.size() - 1);

		string platformName = platforms[platformIdx].getInfo<CL_PLATFORM_NAME>();
		LOGD("platformName %s", platformName.c_str());

		int platformId = OPENCL_PLATFORM_UNKNOWN;
		{
			// this mutex prevents race conditions when calling the adl wrapper since it is apparently not thread safe
			static std::mutex mtx;
			std::lock_guard<std::mutex> lock(mtx);

			if (platformName == "NVIDIA CUDA")
			{
			    LOGD("platformName: %s", "NVIDIA CUDA");
				platformId = OPENCL_PLATFORM_NVIDIA;
				m_hwmoninfo.deviceType = HwMonitorInfoType::NVIDIA;
				m_hwmoninfo.indexSource = HwMonitorIndexSource::OPENCL;
			}
			else if (platformName == "AMD Accelerated Parallel Processing")
			{
			    LOGD("platformName: %s", "AMD Accelerated Parallel Processing");
				platformId = OPENCL_PLATFORM_AMD;
				m_hwmoninfo.deviceType = HwMonitorInfoType::AMD;
				m_hwmoninfo.indexSource = HwMonitorIndexSource::OPENCL;
			}
			else if (platformName == "Clover")
			{
			    LOGD("platformName: %s", "Clover");
				platformId = OPENCL_PLATFORM_CLOVER;
			}
		}

		// get GPU device of the default platform
		vector<cl::Device> devices = getDevices(platforms, platformIdx);
		if (devices.empty())
		{
		    LOGD("%s", "No OpenCL devices found.");
			return false;
		}

		// use selected device
		int idx = index % devices.size();
		unsigned deviceId = s_devices[idx] > -1 ? s_devices[idx] : index;
		m_hwmoninfo.deviceIndex = deviceId % devices.size();
		cl::Device& device = devices[deviceId % devices.size()];
		string device_version = device.getInfo<CL_DEVICE_VERSION>();
        LOGD("device_version %s", device_version.c_str());
		string clVer = device_version.substr(7, 3);

		if (clVer == "1.0" || clVer == "1.1")
		{
			if (platformId == OPENCL_PLATFORM_CLOVER)
			{
			    LOGD("%s", "not supported, but platform Clover might work nevertheless. USE AT OWN RISK!");
			}
			else
			{
			    LOGD("%s", "not supported - minimum required version is 1.2");
				return false;
			}
		}

		int computeCapability = 0;
		if (platformId == OPENCL_PLATFORM_NVIDIA) {
			cl_uint computeCapabilityMajor;
			cl_uint computeCapabilityMinor;
			clGetDeviceInfo(device(), CL_DEVICE_COMPUTE_CAPABILITY_MAJOR_NV, sizeof(cl_uint), &computeCapabilityMajor, NULL);
			clGetDeviceInfo(device(), CL_DEVICE_COMPUTE_CAPABILITY_MINOR_NV, sizeof(cl_uint), &computeCapabilityMinor, NULL);

			computeCapability = computeCapabilityMajor * 10 + computeCapabilityMinor;
			int maxregs = computeCapability >= 35 ? 72 : 63;
			LOGD("-cl-nv-maxrregcount=%d", maxregs);
		}
		else {
			LOGD("-cl-nv-maxrregcount=%s", "platformId != OPENCL_PLATFORM_NVIDIA");
		}
		// create context
		m_context = cl::Context(vector<cl::Device>(&device, &device + 1));
		m_queue = cl::CommandQueue(m_context, device);

		// make sure that global work size is evenly divisible by the local workgroup size
		m_workgroupSize = s_workgroupSize;
		m_globalWorkSize = s_initialGlobalWorkSize;
		if (m_globalWorkSize % m_workgroupSize != 0)
			m_globalWorkSize = ((m_globalWorkSize / m_workgroupSize) + 1) * m_workgroupSize;
		// todo test dagSize
		uint64_t dagSize = 73739904U;
		uint32_t dagSize128 = (unsigned)(dagSize / ETHASH_MIX_BYTES);
		uint32_t lightSize64 = (unsigned)(light->data().size() / sizeof(node));

		// patch source code
		// note: The kernels here are simply compiled version of the respective .cl kernels
		// into a byte array by bin2h.cmake. There is no need to load the file by hand in runtime
		// See libethash-cl/CMakeLists.txt: add_custom_command()
		// TODO: Just use C++ raw string literal.
		string code;

		if ( s_clKernelName == CLKernelName::Experimental ) {
			LOGD("%s", "OpenCL kernel: Experimental kernel");
			code = string(CLMiner_kernel_experimental, CLMiner_kernel_experimental + sizeof(CLMiner_kernel_experimental));
		}
		else { //if(s_clKernelName == CLKernelName::Stable)
            LOGD("%s", "OpenCL kernel: Stable kernel");
			//CLMiner_kernel_stable.cl will do a #undef THREADS_PER_HASH
			if(s_threadsPerHash != 8) {
			    LOGD("%s", "The current stable OpenCL kernel only supports exactly 8 threads. Thread parameter will be ignored.");
			}

			code = string(CLMiner_kernel_stable, CLMiner_kernel_stable + sizeof(CLMiner_kernel_stable));
		}

		/**
		 * linux config
		 *
		 * #define GROUP_SIZE 128
         * #define DAG_SIZE 19988477
         * #define LIGHT_SIZE 624607
         * #define ACCESSES 64
         * #define MAX_OUTPUTS 1
         * #define PLATFORM 0
         * #define COMPUTE 0
         * #define THREADS_PER_HASH 8
		 */
		addDefinition(code, "GROUP_SIZE", m_workgroupSize);
		addDefinition(code, "DAG_SIZE", dagSize128);
		addDefinition(code, "LIGHT_SIZE", lightSize64);
		addDefinition(code, "ACCESSES", ETHASH_ACCESSES);
		addDefinition(code, "MAX_OUTPUTS", c_maxSearchResults);
		addDefinition(code, "PLATFORM", platformId);
		addDefinition(code, "COMPUTE", computeCapability);
		addDefinition(code, "THREADS_PER_HASH", s_threadsPerHash);

		// create miner OpenCL program
		// replace '?#?'?#?'?#?'?#?'?#?'?#?'?#?'?#?#
		code = code.substr(32, code.size() - 32);
		// LOGD("data = %s", code.data());
		cl::Program::Sources sources{{code.data(), code.size()}};
		cl::Program program(m_context, sources);
		try
		{
		    // options
			program.build({device}, "");
			LOGD("%s", "Build info success");
		}
		catch (cl::Error const& error)
		{
			LOGD("%s", "Build info error");
			// CL_INVALID_BUILD_OPTIONS
			LOGD("%d", error.err());
			LOGD("%s", program.getBuildInfo<CL_PROGRAM_BUILD_LOG>(device).c_str());
			return false;
		}

		//check whether the current dag fits in memory everytime we recreate the DAG
		cl_ulong result = 0;
		device.getInfo(CL_DEVICE_GLOBAL_MEM_SIZE, &result);
		LOGD("GPU memory %lld, dagSize %lld", result, dagSize);
		if (result < dagSize)
		{
			LOGD("%s", "OpenCL device has insufficient GPU memory. bytes of memory found bytes of memory required");
			return false;
		}

		// create buffer for dag
		try
		{
		    LOGD("Creating light cache buffer = %d", light->data().size());
			m_light = cl::Buffer(m_context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, light->data().size());
			LOGD("Creating DAG buffer = %lld", dagSize);
			m_dag = cl::Buffer(m_context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, dagSize);
			LOGD("%s", "Loading kernels");
			m_searchKernel = cl::Kernel(program, "ethash_search");
			m_dagKernel = cl::Kernel(program, "ethash_calculate_dag_item");
			LOGD("%s", "Writing light cache buffer");
			m_queue.enqueueWriteBuffer(m_light, CL_TRUE, 0, light->data().size(), light->data().data());
		}
		catch (cl::Error const& err)
		{
		    LOGD("%s", "Creating buffer failed");
		    LOGD("%d", err.err());
			return false;
		}
		// create buffer for header
		LOGD("%s", "Creating buffer for header.");
		m_header = cl::Buffer(m_context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, 32);

		m_searchKernel.setArg(1, m_header);
		m_searchKernel.setArg(2, m_dag);
		m_searchKernel.setArg(5, ~0u);  // Pass this to stop the compiler unrolling the loops.

		// create mining buffers
		LOGD("%s", "Creating mining buffer");
		m_searchBuffer = cl::Buffer(m_context, CL_MEM_WRITE_ONLY, (c_maxSearchResults + 1) * sizeof(uint32_t));

		uint32_t const work = (uint32_t)(dagSize / sizeof(node));
		uint32_t fullRuns = work / m_globalWorkSize;
		uint32_t const restWork = work % m_globalWorkSize;
		if (restWork > 0) fullRuns++;

		m_dagKernel.setArg(1, m_light);
		m_dagKernel.setArg(2, m_dag);
		m_dagKernel.setArg(3, ~0u);

		auto startDAG = std::chrono::steady_clock::now();
		for (uint32_t i = 0; i < fullRuns; i++)
		{
			m_dagKernel.setArg(0, i * m_globalWorkSize);
			m_queue.enqueueNDRangeKernel(m_dagKernel, cl::NullRange, m_globalWorkSize, m_workgroupSize);
			m_queue.finish();
		}
		auto endDAG = std::chrono::steady_clock::now();

		auto dagTime = std::chrono::duration_cast<std::chrono::milliseconds>(endDAG-startDAG);
		float gb = (float)dagSize / (1024 * 1024 * 1024);
		LOGD("%s", "GB of DAG data generated in ms.");
	}
	catch (cl::Error const& err)
	{
	    LOGD("%s", "OpenCL init failed");
		if(s_exit)
			exit(1);
		return false;
	}
	return true;
}

extern "C" {
    JNIEXPORT void JNICALL Java_waterhole_miner_eth_MineService_startJNIMine(JNIEnv *env, jobject thiz, jobject callback) {
    	unsigned m_openclSelectedKernel = 0;  ///< A numeric value for the selected OpenCL kernel
    	unsigned m_openclDeviceCount = 1;
    	vector<unsigned> m_openclDevices = vector<unsigned>(MAX_MINERS, -1);
    	m_openclDevices[0] = 0;
    	unsigned m_openclThreadsPerHash = 8;
    	unsigned m_globalWorkSizeMultiplier = CLMiner::c_defaultGlobalWorkSizeMultiplier;
    	unsigned m_localWorkSize = CLMiner::c_defaultLocalWorkSize;
    	unsigned m_openclPlatform = 0;
    	unsigned m_dagLoadMode = 0;
    	unsigned m_dagCreateDevice = 0;

    	bool m_exit = false;

        CLMiner::setDevices(m_openclDevices, m_openclDeviceCount);
        unsigned m_miningThreads = m_openclDeviceCount;

    	CLMiner::setCLKernel(m_openclSelectedKernel);
    	CLMiner::setThreadsPerHash(m_openclThreadsPerHash);

    	if (!CLMiner::configureGPU(m_localWorkSize, m_globalWorkSizeMultiplier,
    	    m_openclPlatform, 0, m_dagLoadMode, m_dagCreateDevice, m_exit)) {
    				exit(1);
    	}
    	CLMiner::setNumInstances(m_miningThreads);

        map<string, Farm::SealerDescriptor> sealers;
        sealers["opencl"] = Farm::SealerDescriptor {
            &CLMiner::instances, [](FarmFace& _farm, unsigned _index) {
                       return new CLMiner(_farm, _index);
                  }
            };
        Farm m_farm;
        m_farm.setSealers(sealers);
        if (!m_farm.isMining()) {
			LOGD("%s", "Spinning up miners...");
			m_farm.start("opencl", false);
		}
    }

    JNIEXPORT void JNICALL Java_waterhole_miner_eth_MineService_stopJNIMine(JNIEnv *env, jobject thiz) {

    }
}
