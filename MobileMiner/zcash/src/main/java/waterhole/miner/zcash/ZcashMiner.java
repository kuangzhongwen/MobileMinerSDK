package waterhole.miner.zcash;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;

import waterhole.miner.core.CommonMinerIterface;
import waterhole.miner.core.ContextWrapper;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.SocketManager;
import waterhole.miner.core.annotation.ExcuteOnAsyn;

import static android.content.Context.MODE_PRIVATE;
import static waterhole.miner.core.utils.LogUtils.printStackTrace;
import static waterhole.miner.core.utils.Preconditions.checkNotNull;
import static waterhole.miner.core.utils.Preconditions.checkOnChildThread;
import static waterhole.miner.core.utils.IOUtils.closeSafely;

/**
 * Zcash挖矿类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner implements CommonMinerIterface {

    static {
        try {
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native void startJNIMine(MineCallback callback);

    private native void stopJNIMine(MineCallback callback);

    private MineCallback mMineCallback;

    private ZcashMiner() {
    }

    public static ZcashMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static ZcashMiner instance = new ZcashMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public ZcashMiner setMineCallback(MineCallback callback) {
        mMineCallback = callback;
        return this;
    }

    @Override
    public void startMine() {
        checkOnChildThread();
        checkNotNull(mMineCallback, "MineCallback must be not Null");

        KernelTools.copyKernel();

        SocketManager socketManager = SocketManager.instance();
        socketManager.connect();
        socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                "\"zec-cn.waterhole.xyz\", \"3443\"]," +
                " \"method\": \"mining.subscribe\"}");

        startJNIMine(mMineCallback);
    }

    @Override
    public void stopMine() {
        checkOnChildThread();
        checkNotNull(mMineCallback, "MineCallback must be not Null");
        stopJNIMine(mMineCallback);
    }

    private static final class KernelTools {

        private static final String KERNEL_FILENAME = "zcash.kernel";
        private static final int BUFFER = 65535;

        private KernelTools() {
        }

        /**
         * 拷贝kernel.cl文件到app安装目录，在jni层去读取kernel文件并构建openCL program.
         */
        @ExcuteOnAsyn
        static void copyKernel() {
            checkOnChildThread();

            InputStream in = null;
            OutputStream out = null;
            Context context = ContextWrapper.getInstance().obtainContext();

            try {
                final String kernelFile = KERNEL_FILENAME;
                final File of = new File(context.getDir("execdir", MODE_PRIVATE), kernelFile);
                if (!of.exists()) {
                    in = context.getResources().getAssets().open(kernelFile);
                    out = new FileOutputStream(of);
                    final byte b[] = new byte[BUFFER];
                    int sz;
                    while ((sz = in.read(b)) > 0) {
                        out.write(b, 0, sz);
                    }
                }
            } catch (IOException e) {
                printStackTrace(e);
            } finally {
                closeSafely(in);
                closeSafely(out);
            }
        }
    }
}
