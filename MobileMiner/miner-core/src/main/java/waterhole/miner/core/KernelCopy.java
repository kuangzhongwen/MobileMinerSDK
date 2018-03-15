package waterhole.miner.core;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterhole.miner.core.annotation.ExcuteOnAsyn;

import static android.content.Context.MODE_PRIVATE;
import static waterhole.miner.core.utils.IOUtils.closeSafely;
import static waterhole.miner.core.utils.LogUtils.printStackTrace;
import static waterhole.miner.core.utils.Preconditions.checkNotNull;
import static waterhole.miner.core.utils.Preconditions.checkOnChildThread;

/**
 * 拷贝kernel文件.
 *
 * @author kzw on 2018/03/14.
 */
public final class KernelCopy {

    private static final int BUFFER = 65535;

    private KernelCopy() {
    }

    /**
     * 拷贝kernel.cl文件到app安装目录，在jni层去读取kernel文件并构建openCL program.
     *
     * @param context 上下文对象
     * @param filename kernel文件名
     */
    @ExcuteOnAsyn
    public static void copy(Context context, String filename) {
        checkOnChildThread();
        checkNotNull(context);
        checkNotNull(filename);

        InputStream in = null;
        OutputStream out = null;
        try {
            final File of = new File(context.getDir("execdir", MODE_PRIVATE), filename);
            in = context.getResources().getAssets().open(filename);
            out = new FileOutputStream(of);
            final byte b[] = new byte[BUFFER];
            int sz;
            while ((sz = in.read(b)) > 0) {
                out.write(b, 0, sz);
            }
        } catch (IOException e) {
            printStackTrace(e);
        } finally {
            closeSafely(in);
            closeSafely(out);
        }
    }
}
