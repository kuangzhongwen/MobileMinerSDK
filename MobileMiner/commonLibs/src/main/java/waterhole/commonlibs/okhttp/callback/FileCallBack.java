package waterhole.commonlibs.okhttp.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import waterhole.commonlibs.okhttp.OkHttpUtils;
import waterhole.commonlibs.utils.IOUtils;

/**
 * 文件回调
 *
 * @author kzw on 2017/07/31.
 */
public abstract class FileCallBack extends Callback<File> {

    // 目标文件存储的文件夹路径
    private String mDestFileDir;
    // 目标文件存储的文件名
    private String mDestFileName;

    public FileCallBack(String destFileDir, String destFileName) {
        mDestFileDir = destFileDir;
        mDestFileName = destFileName;
    }

    @Override
    public File parseNetworkResponse(Response response, int id) throws Exception {
        return saveFile(response, id);
    }

    public File saveFile(Response response, final int id) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();

            long sum = 0;

            File dir = new File(mDestFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, mDestFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    @Override
                    public void run() {
                        inProgress(finalSum * 1.0f / total, total, id);
                    }
                });
            }
            fos.flush();
            return file;

        } finally {
            IOUtils.closeSafely(response.body());
            IOUtils.closeSafely(is);
            IOUtils.closeSafely(fos);
        }
    }
}
