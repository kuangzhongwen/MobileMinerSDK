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
        指定要使用的装置类別。目前可以使用的类別包括：
        CL_DEVICE_TYPE_CPU：使用CPU装置
        CL_DEVICE_TYPE_GPU：使用显示晶片装置
        CL_DEVICE_TYPE_ACCELERATOR：特定的OpenC 加速装置，例如 CELL
        CL_DEVICE_TYPE_DEFAULT：系统预设的OpenCL装置
        CL_DEVICE_TYPE_ALL：所有系统计中的OpenCL装置