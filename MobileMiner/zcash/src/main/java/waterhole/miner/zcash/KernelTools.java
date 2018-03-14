package waterhole.miner.zcash;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.annotation.ExcuteOnAsyn;

import static waterhole.commonlibs.utils.LogUtils.printStackTrace;
import static android.content.Context.MODE_PRIVATE;
import static waterhole.commonlibs.utils.IOUtils.closeSafely;
import static waterhole.commonlibs.utils.Preconditions.checkOnChildThread;

/**
 * Kernel工具类.
 *
 * @author kzw on 2018/03/12.
 */
final class KernelTools {

    private static final String KERNEL_FILENAME = "kernel.cl";
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
