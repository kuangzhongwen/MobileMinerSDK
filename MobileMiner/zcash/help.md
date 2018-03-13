## zcash

#### 1. 获取android手机下的cl库文件

     adb pull /vendor/lib/ /Users/kuangzhongwen/Desktop

#### 2. Zcash GPU Miner
     
     https://github.com/mbevand/silentarmy
     
#### 3. 依赖库位置

     Adreno GPU：/system/vendor/lib/libOpenCL.so
     PowerVR GPU：/system/vendor/lib/libPVROCL.so
     mali GPU：/system/vendor/lib/egl/libGLES_mali.so
     
     android N(7.0) 不能使用dlopen打开私有库