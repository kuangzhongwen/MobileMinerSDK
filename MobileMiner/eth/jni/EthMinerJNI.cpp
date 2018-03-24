#include <jni.h>

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/trim_all.hpp>
#include <boost/optional.hpp>

#include <libethcore/Exceptions.h>
#include <libdevcore/SHA3.h>
#include <libethcore/EthashAux.h>
#include <libethcore/Farm.h>
#include <CLMiner.h>

JNIEXPORT void JNICALL Java_waterhole_miner_eth_MineService_startJNIMine(JNIEnv *env, jobject thiz, jobject callback) {
    /**
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
    Farm f;
    f.setSealers(sealers);
    */
}

JNIEXPORT void JNICALL Java_waterhole_miner_eth_MineService_stopJNIMine(JNIEnv *env, jobject thiz) {

}