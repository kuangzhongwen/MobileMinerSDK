package io.waterhole.miner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import waterhole.miner.core.StateObserver;
import waterhole.miner.monero.XmrMiner;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.MathUtils.parseDoubleKeep2;
import static waterhole.miner.core.utils.LogUtils.info;

public final class MainActivity extends Activity {

    private TextView mStatusText;

    private int mCoinPosition;
    private boolean isMining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.coins_spinner);
        final List<String> datas = new ArrayList<>();
        datas.add("eth");
        datas.add("zcash");
        datas.add("menero");
        CoinAdapter adapter = new CoinAdapter(getApplicationContext());
        adapter.setDatas(datas);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCoinPosition = position;
                isMining = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mStatusText = (TextView) findViewById(R.id.status_text);
        final Button minerBtn = (Button) findViewById(R.id.miner_button);
        minerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCoinPosition) {
                    case 0:
                        minerBtn.setText("停止挖矿");
                        initEthMiner();
                        break;
                    case 1:
//                        if (isMining) {
//                            if (MineService.mMinerPoolCommunicator != null) {
//                                MineService.mMinerPoolCommunicator.disconnect();
//                            }
//                            mStatusText.setText("prepare");
//                            minerBtn.setText("开始挖矿");
//                        } else {
//                            minerBtn.setText("停止挖矿");
//                            initZcashMiner();
//                        }
//                        isMining = !isMining;
                        break;
                    case 2:
                        if (isMining) {
                            mStatusText.setText("prepare");
                            minerBtn.setText("开始挖矿");
                            XmrMiner.instance().stopMine();
                        } else {
                            minerBtn.setText("停止挖矿");
                            initMoneroMiner();
                        }
                        isMining = !isMining;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setupStatusText(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText(status);
            }
        });
    }

    private void initZcashMiner() {
//        ZcashMiner.instance().init(getApplicationContext()).setMineCallback(new MineCallback() {
//
//            @Override
//            public IBinder asBinder() {
//                return null;
//            }
//
//            @Override
//            public void onConnectPoolBegin() {
//                info(TAG, "onConnectPoolBegin");
//                setupStatusText("开始连接矿池...");
//            }
//
//            @Override
//            public void onConnectPoolSuccess() {
//                info(TAG, "onConnectPoolSuccess");
//                setupStatusText("连接矿池成功...");
//            }
//
//            @Override
//            public void onConnectPoolFail(String error) {
//                error(TAG, "onConnectPoolFail: " + error);
//                setupStatusText("连接矿池失败: " + error);
//            }
//
//            @Override
//            public void onPoolDisconnect(String error) {
//                error(TAG, "onPoolDisconnect: " + error);
//                setupStatusText("与矿池连接断开: " + error);
//            }
//
//            @Override
//            public void onMessageFromPool(String message) {
//                info(TAG, "onMessageFromPool: " + message);
//                setupStatusText("收到矿池消息: " + message);
//            }
//
//            @Override
//            public void onMiningError(String error) {
//                error(TAG, "onMiningError = " + error);
//                setupStatusText("挖矿失败，错误原因：" + error);
//            }
//
//            @Override
//            public void onMiningStatus(double speed) {
//                info(TAG, "onMiningStatus speed = " + speed);
//                setupStatusText("speed = " + speed + " sols");
//            }
//        }).startMine();
    }

    private void initEthMiner() {
//        EthMiner.instance().init(getApplicationContext()).setMineCallback(new MineCallback() {
//
//            @Override
//            public IBinder asBinder() {
//                return null;
//            }
//
//            @Override
//            public void onConnectPoolBegin() {
//                info(TAG, "onConnectPoolBegin");
//                setupStatusText("开始连接矿池...");
//            }
//
//            @Override
//            public void onConnectPoolSuccess() {
//                info(TAG, "onConnectPoolSuccess");
//                setupStatusText("连接矿池成功...");
//            }
//
//            @Override
//            public void onConnectPoolFail(String error) {
//                error(TAG, "onConnectPoolFail: " + error);
//                setupStatusText("连接矿池失败: " + error);
//            }
//
//            @Override
//            public void onPoolDisconnect(String error) {
//                error(TAG, "onPoolDisconnect: " + error);
//                setupStatusText("与矿池连接断开: " + error);
//            }
//
//            @Override
//            public void onMessageFromPool(String message) {
//                info(TAG, "onMessageFromPool: " + message);
//                setupStatusText("收到矿池消息: " + message);
//            }
//
//            @Override
//            public void onMiningError(String error) {
//                error(TAG, "onMiningError = " + error);
//                setupStatusText("挖矿失败，错误原因：" + error);
//            }
//
//            @Override
//            public void onMiningStatus(double speed) {
//                info(TAG, "onMiningStatus speed = " + speed);
//                setupStatusText("speed = " + speed + " sols");
//            }
//        }).startMine();
    }

    private void initMoneroMiner() {
        XmrMiner.instance().init(this).setStateObserver(new StateObserver() {

            @Override
            public void onConnectPoolBegin() {
                info("onConnectPoolBegin");
                setupStatusText("开始连接矿池...");
            }

            @Override
            public void onConnectPoolSuccess() {
                info("onConnectPoolSuccess");
                setupStatusText("连接矿池成功...");
            }

            @Override
            public void onConnectPoolFail(String error) {
                error("onConnectPoolFail: " + error);
                setupStatusText("连接矿池失败: " + error);
            }

            @Override
            public void onPoolDisconnect(String error) {
                error("onPoolDisconnect: " + error);
                setupStatusText("与矿池连接断开: " + error);
            }

            @Override
            public void onMessageFromPool(String message) {
                info("onMessageFromPool: " + message);
                setupStatusText("收到矿池消息: " + message);
            }

            @Override
            public void onMiningError(String error) {
                error("onMiningError = " + error);
                setupStatusText("挖矿失败，错误原因：" + error);
            }

            @Override
            public void onMiningStatus(double speed) {
                info("onMiningStatus speed = " + speed);
                setupStatusText("挖矿速度： " + parseDoubleKeep2(speed) + " H/s");
            }
        }).startMine();
    }
}
