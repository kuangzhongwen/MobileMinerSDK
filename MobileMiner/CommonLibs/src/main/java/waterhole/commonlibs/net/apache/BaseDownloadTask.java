package waterhole.commonlibs.net.apache;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.ByteArrayBuffer;

import waterhole.commonlibs.utils.IOUtils;

/**
 * 抽象下载任务，利用HttpClient进行下载，可以获取下载字节数和百分比。
 *
 * @author kzw on 2016/07/06.
 * @see waterhole.commonlibs.net.apache.HttpProgressListener
 * @see #handleProgress(long, long)
 */
public abstract class BaseDownloadTask<T> implements Runnable {

    private static final int BUFFER = 4096;

    protected final T download(String url) {
        HttpResponse httpResponse;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams()
                    .setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            return getResponse(httpClient, httpResponse);
        } catch (IOException e) {
            handleResult(null);
            return null;
        }
    }

    private T getResponse(HttpClient httpClient, HttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            CountingInputStream cis = null;
            try {
                HttpEntity entity = httpResponse.getEntity();
                long contentLength = entity.getContentLength();
                cis = getCountingInputStream(entity, contentLength);

                if (contentLength < 0) {
                    contentLength = BUFFER;
                }
                final ByteArrayBuffer buffer = new ByteArrayBuffer((int) contentLength);
                final byte[] tmp = new byte[BUFFER];
                int l;
                while ((l = cis.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                byte[] byteIn = buffer.toByteArray();
                if (byteIn == null || byteIn.length == 0) {
                    return null;
                }
                return handleResult(byteIn);
            } finally {
                IOUtils.closeSafely(cis);
                shutdownConnection(httpClient);
            }
        }

        return null;
    }

    private static void shutdownConnection(HttpClient httpClient) {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * 获取Counting输入流
     */
    private CountingInputStream getCountingInputStream(HttpEntity entity, final long size)
            throws IOException {
        return new CountingInputStream(entity.getContent(), new HttpProgressListener() {
            @Override
            public void transferred(long transferedBytes) {
                handleProgress(transferedBytes, size);
            }

            @Override
            public void transferredPercent(int percent) {
            }
        });
    }

    public abstract T handleResult(byte[] result);

    public abstract void handleProgress(long transferedBytes, long size);
}
