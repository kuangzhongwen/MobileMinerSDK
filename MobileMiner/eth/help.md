## eth

#### git地址
     
     https://github.com/ethereum-mining/ethminer
     
#### 注意事项
     
     1. 移植前需要先用交叉编译平台编译boost, 注意需要适配不同ABI平台。
     2. 需要根据bin2h.cmake将.cl转换成.h二进制文件。
     3. 源代码执行逻辑入口：../ethminer-master/ethminer/MinerAux.h。
     
#### 要求
     
     1. OpenCL版本必须为1.2及其以上，kernel内部代码有要求。
     2. OpenCL的Global Mem Size必须>=2558525056，目前eth的dag size的需求。
     3. CL device type: CL_DEVICE_TYPE_GPU  | CL_DEVICE_TYPE_ACCELERATOR