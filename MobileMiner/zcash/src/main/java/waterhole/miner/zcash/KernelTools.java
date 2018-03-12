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

    @ExcuteOnAsyn
    static void copyKernel() {
        checkOnChildThread();

        InputStream in = null;
        OutputStream out = null;
        Context context = ContextWrapper.getInstance().obtainContext();

        try {
            final String kernelFile = KERNEL_FILENAME;
            in = context.getResources().getAssets().open(kernelFile);
            final File of = new File(context.getDir("execdir", MODE_PRIVATE), kernelFile);
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
