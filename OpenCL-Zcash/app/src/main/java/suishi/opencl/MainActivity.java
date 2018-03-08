package suishi.opencl;

import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author kuang
 * @since 2016-11-16
 */

public class MainActivity extends Activity {

    static {
        // System.loadLibrary("testCL");
        try {
            System.loadLibrary("silentarmy");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private native void openclTest();

    private native void silentarmyTest();

    private native void helloWorldOpenCL();

    private native void scaleImage();

    private native void sgemm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                copyFile("sgemm.cl");
                silentarmyTest();
            }
        }).start();
    }

    private void copyFile(final String f) {
        InputStream in;
        try {
            in = getResources().getAssets().open(f);
            final File of = new File(getDir("execdir", MODE_PRIVATE), f);
            final OutputStream out = new FileOutputStream(of);

            final byte b[] = new byte[65535];
            int sz;
            while ((sz = in.read(b)) > 0) {
                out.write(b, 0, sz);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
