package waterhole.miner.zcash;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import waterhole.miner.core.utils.LogUtils;
import waterhole.miner.zcash.utils.GsonUtil;

/**
 * 矿池通信器
 *
 * @author huwwds on 2018/03/19
 */
public class MinerPoolCommunicator {

    private MineService mineService;

    MinerPoolCommunicator(MineService service) {
        this.mineService = service;
    }

    enum MinerState {
        SENT_SUBSCRIBE("SENT_SUBSCRIBE"),
        DISCONNECTED("DISCONNECTED"),
        CONNECTING("CONNECTING"),
        AUTHORIZED("AUTHORIZED"),
        SENT_EXTRANONCE_SUBSCRIBE("SENT_EXTRANONCE_SUBSCRIBE"),
        SENT_AUTHORIZE("SENT_AUTHORIZE");

        String val;

        MinerState(String val) {
            this.val = val;
        }
    }

    private MinerState minerState;

    private static final String TAG = MinerPoolCommunicator.class.getClass().getSimpleName();

    private static final ThreadLocal<Socket> threadConnect = new ThreadLocal<Socket>();

    private static final String HOST = "zec-cn.waterhole.xyz";

    private static final int PORT = 3329;

    private static Socket client;

    private OutputStream outStr = null;

    private InputStream inStr = null;

    private int stId;

    private String jobId;

    private String job;

    private int exceptedId;

    private byte[] nonceLeftPart;

    private byte[] target;

    private byte[] zcashNoncelessHeader;

    private int stAccepted = 0;

    private boolean running = false;

    private boolean mining = false;

    private boolean stHadJob;

    /**
     * 接收
     */
    private Thread tRecv = new Thread(new RecvThread());
    /**
     * 心跳
     */
    private Thread tKeep = new Thread(new KeepThread());

    public void connect() throws IOException {
        client = threadConnect.get();
        if (client == null) {
            client = new Socket(HOST, PORT);
            threadConnect.set(client);
            tKeep.start();
            System.out.println("========链接开始！========");
        }
        outStr = client.getOutputStream();
        inStr = client.getInputStream();
    }

    public void disconnect() {
        try {
            exceptedId = 0;
            running = false;
            ZcashMiner.instance().stopMine();
            outStr.close();
            inStr.close();
            client.close();
        } catch (IOException e) {
            LogUtils.printStackTrace(e);
        }
    }

    private class KeepThread implements Runnable {
        public void run() {
            try {
                System.out.println("=====================开始发送心跳包==============");
                while (running) {
                    try {
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("发送心跳数据包");
                    outStr.write("send heart beat data package !".getBytes());
                }
            } catch (IOException e) {
                LogUtils.printStackTrace(e);
            }
        }
    }

    private class RecvThread implements Runnable {
        public void run() {
            try {
                System.out.println("==============开始接收数据===============");
                while (running) {
                    byte[] b = new byte[4 * 1024];
                    int r = inStr.read(b);
                    if (r > -1) {
                        String str = new String(b, 0, r);
                        System.out.println(str);
                        receivedMsg(str);
                    }
                }
            } catch (Exception e) {
                LogUtils.printStackTrace(e);
            }
        }
    }

    private class SendThread implements Runnable {
        @Override
        public void run() {
            try {
                OutputStreamWriter osw = new OutputStreamWriter(outStr);
                StringBuffer sb = new StringBuffer();
                sb.append(
                        "{\"method\": \"mining.subscribe\", \"params\": [\"silentarmy\", null, \"us1-zcash.flypool.org\", \"3333\"], \"id\": 1}\n");
                osw.write(sb.toString());
                osw.flush();
                System.out.println("注册矿机>>>>>");

                Thread.sleep(3000);
                sb = new StringBuffer();
                sb.append(
                        "{\"method\": \"mining.authorize\", \"params\": [\"t1cVviFvgJinQ4w3C2m2CfRxgP5DnHYaoFC\", \"\"], \"id\": 3}\n");
                osw.write(sb.toString());
                osw.flush();
                System.out.println("验证矿机>>>>");
            } catch (Exception e) {
                LogUtils.printStackTrace(e);
            }
        }
    }

    public void doSend(String msg) {
        try {
            LogUtils.info(TAG, "doSend >>>" + msg);
            OutputStreamWriter osw = new OutputStreamWriter(outStr);
            osw.write(msg + "\n");
            osw.flush();
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    public void receivedMsg(String msg) {
        LogUtils.info(TAG, "receivedMsg >>>" + msg);
        try {
            String[] jsons = msg.split("\n");
            for (String json : jsons) {
                String tosend = processMsg(json);
                doSend(tosend);
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
        LogUtils.error("huwwds", "received msg>>>>>" + msg);
    }

    private String processMsg(String json) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (!jsonObject.has("id")) {
                throw new Exception("'id' field is missing");
            }
            if (jsonObject.has("result")) {
                if (jsonObject.has("error") && jsonObject.get("error") == null) {
                    LogUtils.error(TAG, "Stratum server returned an error :" + jsonObject.get("error"));
                    return null;
                }
                if (!jsonObject.getString("id").equals(String.valueOf(exceptedId))) {
                    LogUtils.error(TAG, "Stratum server returned wrong id: " + jsonObject.getString("id"));
                }
                exceptedId = 0;
                if (minerState == MinerState.SENT_SUBSCRIBE) {
                    setNonceLeftpart(jsonObject.getJSONArray("result").getString(1));
                    minerState = MinerState.SENT_AUTHORIZE;
                    return stratumMsg("mining.authorize", new JobEntity());
                } else if (minerState == MinerState.SENT_EXTRANONCE_SUBSCRIBE) {
                    minerState = MinerState.SENT_AUTHORIZE;
                    return stratumMsg("mining.authorize", new JobEntity());
                } else if (minerState == MinerState.SENT_AUTHORIZE) {
                    if (jsonObject.get("result") == null)
                        throw new Exception("mining.authorize failed");
                    minerState = MinerState.AUTHORIZED;
                    updateMiningJob();
                } else if (minerState == MinerState.AUTHORIZED) {
                    LogUtils.info(TAG, "Stratum server accepted a share");
                    stAccepted += 1;
                } else {
                    throw new RuntimeException("unknown state : " + minerState);
                }
            } else if (jsonObject.has("method")) {
                if (jsonObject.getString("method").equals("mining.set_target")) {
                    setTarget(jsonObject.getJSONArray("params").getString(0));
                } else if (jsonObject.getString("method").equals("mining.set_extranonce")) {
                    setNonceLeftpart(jsonObject.getJSONArray("params").getString(0));
                } else if (jsonObject.getString("method").equals("mining.notify")) {
                    JSONArray params = jsonObject.getJSONArray("params");
                    setNewJob(params.getString(0), params.getString(1), params.getString(2), params.getString(3), params.getString(4), params.getString(5), params.getString(6), params.getBoolean(7));
                    updateMiningJob();
                } else if (jsonObject.getString("method").equals("client.reconnect")) {
                    LogUtils.info(TAG, "Stratum server forcing a reconnection");
                } else {
                    throw new Exception("Unimplemented method:" + jsonObject.getString("method"));
                }
            } else {
                throw new Exception("Message is neither a result nor a method call");
            }
        } catch (Exception e) {
            LogUtils.error(TAG, "Stratum: invalid msg from server: >>>>" + json);
        }
        return null;
    }

    private void setNewJob(String job_id, String nversion, String hash_prev_block, String hash_merkle_root, String hash_reserved, String ntime, String nbits, boolean clean_jobs) throws Exception {
        LogUtils.info(TAG, "Received job : " + job_id);
        if (!clean_jobs) {
            LogUtils.info(TAG, "Ignoring job " + job_id + " (clean_jobs=False)");
            return;
        }
        this.jobId = job_id;
        if (!nversion.equals("04000000"))
            throw new Exception("Invalid version: " + nversion);
        if (!hash_prev_block.matches("^[0-9a-fA-F]{64}$"))
            throw new Exception("Invalid hashPrevBlock: " + hash_prev_block);
        if (!hash_merkle_root.matches("[0-9a-fA-F]{64}$"))
            throw new Exception("Invalid hashMerkleRoot: " + hash_merkle_root);
        if (!hash_reserved.equals("0000000000000000000000000000000000000000000000000000000000000000"))
            throw new Exception("Invalid hashReserved: " + hash_reserved);
        if (!ntime.matches("^[0-9a-fA-F]{8}$"))
            throw new Exception("Invalid nTime: " + ntime);
        if (!nbits.matches("^[0-9a-fA-F]{8}$"))
            throw new Exception("Invalid nBits: " + nbits);
        zcashNoncelessHeader = hexStringToBytes(nversion + hash_prev_block + hash_merkle_root + hash_reserved + ntime + nbits);
    }

    private void setTarget(String t) throws Exception {
        LogUtils.info(TAG, "Received target : " + t);
        if (!t.matches("^[0-9a-fA-F]{64}$"))
            throw new Exception("Invalid target : " + t);
        boolean isFirstTarget = target == null;
        byte[] bytes = hexStringToBytes(t);
        byte[] buffer = new byte[bytes.length];
        for (int i = bytes.length - 1, j = 0; i >= 0; i--, j++) {
            buffer[j] = bytes[i];
        }
        target = buffer;
        if (isFirstTarget)
            updateMiningJob();
    }

    private void updateMiningJob() {
        if (nonceLeftPart == null)
            return;
        if (minerState != MinerState.AUTHORIZED)
            return;
        if (target == null)
            return;
        if (zcashNoncelessHeader == null)
            return;
        if (!mining) {
            mining = true;
            ZcashMiner.instance().startMine();
            new Thread() {
                @Override
                public void run() {
                    mineService.startJNIMine(mineService.getPackageName(), mineService.mZcashMiner.getMineCallback(), MinerPoolCommunicator.this);
                }
            }.start();
        }
        if (!stHadJob) {
            LogUtils.info(TAG, "Stratum server sent us the first job");
            stHadJob = true;
        }
        job = bytesToHexString(target) + " " + jobId + " " + bytesToHexString(zcashNoncelessHeader) + " " + bytesToHexString(nonceLeftPart);
        mineService.writeJob(job);
        LogUtils.info(TAG, "to solvers : " + job);
    }

    public void onSubmit(String jobId) {
        LogUtils.error(TAG, "huwwds>>>>>jobid>>>" + jobId);
        JobEntity jobEntity = new JobEntity();
        jobEntity.jobId = jobId;
        doSend(stratumMsg("mining.submit", jobEntity));
    }

    private String stratumMsg(String method, JobEntity jobEntity) {
        StratumEntity stratumEntity = new StratumEntity();
        if (method.equals("mining.subscribe")) {
            stratumEntity.params.add("silentarmy");
            stratumEntity.params.add(null);
            stratumEntity.params.add(HOST);
            stratumEntity.params.add(PORT + "");
        } else if (method.equals("mining.authorize")) {
            stratumEntity.params.add(jobEntity.user);
        } else if (method.equals("mining.submit")) {
            stratumEntity.params.add(jobEntity.user);
            stratumEntity.params.add(jobEntity.jobId);
            stratumEntity.params.add(jobEntity.ntime);
            stratumEntity.params.add(jobEntity.nonceRightPart);
            stratumEntity.params.add(jobEntity.sol);
        } else {
            throw new RuntimeException("unknown method : " + method);
        }
        stId = nextId();
        stratumEntity.id = stId;
        stratumEntity.method = method;
        return GsonUtil.obj2json(stratumEntity);
    }

    private void setNonceLeftpart(String hexStr) {
        nonceLeftPart = hexStringToBytes(hexStr);
        LogUtils.info(TAG, "Stratum server fixes " + nonceLeftPart.length + " bytes of the nonce");
        if (nonceLeftPart.length > 17) {
            throw new RuntimeException("'Stratum: SILENTARMY is not compatible with servers fixing the first " + nonceLeftPart.length + " bytes of the nonce");
        }
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    static class StratumEntity {
        int id;
        String method;
        List<Object> params = new ArrayList<>();
    }

    static class JobEntity {
        String user = "t1cVviFvgJinQ4w3C2m2CfRxgP5DnHYaoFC";
        String jobId;
        String ntime;
        String nonceRightPart;
        String sol;
    }

    private int nextId() {
        stId += 1;
        exceptedId = stId;
        return stId;
    }

    public void startCommunicate() {
        try {
            if (!running) {
                running = true;
                connect();
                tRecv.start();
                minerState = MinerState.SENT_SUBSCRIBE;
                doSend(stratumMsg("mining.subscribe", new JobEntity()));
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }
}