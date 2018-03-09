package waterhole.commonlibs.net.apache;

import static waterhole.commonlibs.utils.FileUtils.isFileExist;
import static waterhole.commonlibs.utils.IOUtils.closeSafely;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import waterhole.commonlibs.utils.IOUtils;
import waterhole.commonlibs.utils.LogUtils;
import waterhole.commonlibs.utils.StringUtils;

/**
 * Http 上传工具，利用java原生{@link HttpURLConnection}实现。
 *
 * @author kzw on 2015/09/16
 * @see #uploadImage(HttpUploadListener, String, Bitmap, HttpProgressListener)
 * @see #uploadImage(HttpUploadListener, String, String, HttpProgressListener)
 * @see #uploadImage(String, Bitmap)
 * @see #uploadImage(String, Bitmap, HttpProgressListener)
 * @see #uploadFile(HttpUploadListener, String, File, HttpProgressListener)
 * ...
 */
public final class HttpUploader {

    private static final String TAG = "HttpUploader";

    private HttpUploader() {
    }

    /**
     * 上传图片
     *
     * @param httpUrl   上传url
     * @param imagePath 图片路径
     */
    public static String uploadImage(String httpUrl, String imagePath) {
        return uploadImage(httpUrl, imagePath, null);
    }

    /**
     * 上传图片
     *
     * @param httpUrl              上传url
     * @param imagePath            图片路径
     * @param httpProgressListener 上传进度监听
     */
    private static String uploadImage(String httpUrl, String imagePath,
                                      HttpProgressListener httpProgressListener) {
        return uploadImage(null, httpUrl, imagePath, httpProgressListener);
    }

    /**
     * 上传图片
     *
     * @param httpUploadListener   上传回调
     * @param httpUrl              上传url
     * @param imagePath            图片路径
     * @param httpProgressListener 上传进度监听
     */
    private static String uploadImage(HttpUploadListener httpUploadListener, String httpUrl,
                                      String imagePath, HttpProgressListener httpProgressListener) {
        if (TextUtils.isEmpty(httpUrl) || TextUtils.isEmpty(imagePath)) {
            return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
        }
        if (isFileExist(imagePath)) {
            try {
                File image = new File(imagePath);
                FileInputStream fis = new FileInputStream(image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] imageBytes = new byte[(int) image.length()];
                fis.read(imageBytes);
                baos.write(imageBytes);
                return upload(httpUploadListener, httpUrl, StringUtils.encodeBase64(
                        baos.toByteArray()), httpProgressListener);
            } catch (Exception e) {
                return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
            }
        }
        return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
    }

    /**
     * 上传图片
     *
     * @param httpUrl 上传url
     * @param bitmap  源bitmap
     */
    public static String uploadImage(String httpUrl, Bitmap bitmap) throws Exception {
        return uploadImage(httpUrl, bitmap, null);
    }

    /**
     * 上传图片
     *
     * @param httpUrl              上传url
     * @param bitmap               源bitmap
     * @param httpProgressListener 上传进度监听
     */
    public static String uploadImage(String httpUrl, Bitmap bitmap, HttpProgressListener
            httpProgressListener) throws Exception {
        return uploadImage(null, httpUrl, bitmap, httpProgressListener);
    }

    /**
     * 上传图片
     *
     * @param httpUploadListener   上传回调
     * @param httpUrl              上传url
     * @param bitmap               源bitmap
     * @param httpProgressListener 上传进度监听
     */
    public static String uploadImage(HttpUploadListener httpUploadListener, String httpUrl,
                                     Bitmap bitmap, HttpProgressListener httpProgressListener) throws Exception {
        if (TextUtils.isEmpty(httpUrl) || bitmap == null) {
            return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return upload(httpUploadListener, httpUrl, StringUtils.encodeBase64(baos.toByteArray()),
                httpProgressListener);
    }

    /**
     * 上传文件
     *
     * @param httpUrl 上传url
     * @param file    源文件
     */
    public static String uploadFile(String httpUrl, File file) throws Exception {
        return uploadFile(httpUrl, file, null);
    }

    /**
     * 上传文件
     *
     * @param httpUrl              上传url
     * @param file                 源文件
     * @param httpProgressListener http进度监听
     */
    private static String uploadFile(String httpUrl, File file, HttpProgressListener
            httpProgressListener) throws Exception {
        return uploadFile(null, httpUrl, file, httpProgressListener);
    }

    /**
     * 上传文件
     *
     * @param httpUploadListener   上传回调
     * @param httpUrl              上传url
     * @param file                 源文件
     * @param httpProgressListener 上传进度监听
     */
    public static String uploadFile(HttpUploadListener httpUploadListener,
                                    String httpUrl, File file, HttpProgressListener httpProgressListener) throws Exception {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            baos = new ByteArrayOutputStream();
            baos.write(fileBytes);
            baos.flush();
            return upload(httpUploadListener, httpUrl, StringUtils.encodeBase64(baos.toByteArray()),
                    httpProgressListener);
        } catch (FileNotFoundException e) {
            return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
        } catch (IOException e) {
            return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
        } catch (OutOfMemoryError e) {
            return httpUploadListener != null ? httpUploadListener
                    .onUploadFail(UploadErrorCode.UPLOAD_ERROR_FILE_TOO_LARGE) : "";
        } finally {
            closeSafely(fis);
            closeSafely(baos);
        }
    }

    /**
     * Upload core
     *
     * @param httpUploadListener   上传回调
     * @param httpUrl              上传url
     * @param bytes                上传字节数组
     * @param httpProgressListener 上传进度监听
     */
    private static String upload(HttpUploadListener httpUploadListener, String httpUrl,
                                 byte[] bytes, HttpProgressListener httpProgressListener) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setConnectParams(conn, bytes);
        return doRequest(httpUploadListener, conn, bytes, httpProgressListener);
    }

    /**
     * 执行Http Request
     *
     * @param httpUploadListener   上传监听
     * @param connection           HttpURLConnection
     * @param bytes                上传字节数组
     * @param httpProgressListener 上传进度监听
     */
    private static String doRequest(HttpUploadListener httpUploadListener,
                                    HttpURLConnection connection, byte[] bytes,
                                    final HttpProgressListener httpProgressListener) {
        InputStream is = null;
        BufferedReader rd = null;
        try {
            connection.connect();
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            // 写入数据源
            writeData(httpUploadListener, bytes, httpProgressListener, outputStream);
            outputStream.flush();
            closeSafely(outputStream);
            is = connection.getInputStream();
            // 获取response数据
            HttpResponse httpResponse = new HttpResponse(httpUploadListener, is).invoke();
            StringBuffer response = httpResponse.getResponse();
            rd = httpResponse.getRd();
            return response.toString();
        } catch (Exception e) {
            return httpUploadListener != null ? httpUploadListener.onUploadFail() : "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            closeSafely(is);
            closeSafely(rd);
        }
    }

    /**
     * 设置HttpURLConnection参数
     *
     * @param conn  HttpURLConnection
     * @param bytes 字节数组
     */
    private static void setConnectParams(HttpURLConnection conn, byte[] bytes)
            throws ProtocolException {
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(50 * 1000);
        conn.setReadTimeout(50 * 1000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Connection", "close");
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        System.setProperty("http.keepAlive", "false");
        // 禁用缓存
        conn.setFixedLengthStreamingMode(bytes.length);
    }

    /**
     * 写入数据
     *
     * @param httpUploadListener   上传回调
     * @param bytes                写入的字节数据源
     * @param httpProgressListener 上传进度
     * @param outputStream         写入的outStream
     */
    private static void writeData(HttpUploadListener httpUploadListener, byte[] bytes,
                                  HttpProgressListener httpProgressListener, DataOutputStream outputStream) throws IOException {
        try {
            long len = bytes.length;
            long index = 0;
            int percentInterval;
            if (len >= 0 && len <= 10 * 1024 * 1024) {
                percentInterval = 20;
            } else if (len > 10 * 1024 * 1024 && len <= 40 * 1024 * 1024) {
                percentInterval = 10;
            } else if (len > 40 * 1024 * 1024 && len <= 100 * 1024 * 1024) {
                percentInterval = 5;
            } else {
                percentInterval = 1;
            }
            for (byte data : bytes) {
                outputStream.write(data);
                index++;
                int percent = (int) (index * 100 / len);
                if (httpProgressListener != null && percent >= percentInterval && (percent % percentInterval == 0)) {
                    httpProgressListener.transferredPercent(percent);
                }
            }
        } catch (OutOfMemoryError e) {
            if (httpUploadListener != null) {
                httpUploadListener.onUploadFail();
            }
        }
    }

    /**
     * 访问http请求
     *
     * @param httpUrl  访问的url
     * @param listener http回调
     */
    public static void doHttpRequest(String httpUrl, final HttpListener listener) {
        InputStream iStream = null;
        BufferedReader br = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();
            iStream = doResponse(listener, iStream, urlConnection);
        } catch (UnknownHostException e) {
            listener.onFail();
        } catch (SocketTimeoutException e) {
            listener.onTimeOut();
        } catch (ConnectException e) {
            listener.onTimeOut();
        } catch (Exception e) {
            LogUtils.error(TAG, e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            closeSafely(br);
            closeSafely(iStream);
        }
    }

    /**
     * 处理http响应
     *
     * @param listener      Http回调
     * @param iStream       返回的响应流
     * @param urlConnection HttpURLConnection
     */
    private static InputStream doResponse(HttpListener listener, InputStream iStream,
                                          HttpURLConnection urlConnection) throws IOException {
        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            iStream = urlConnection.getInputStream();
            String data = IOUtils.getStringFromInputStream(iStream);
            if (TextUtils.isEmpty(data)) {
                listener.onFail();
            } else {
                listener.onSuccess(data);
            }
        } else {
            listener.onFail();
        }
        return iStream;
    }

    /**
     * 同步http返回(response)
     */
    private static class HttpResponse {

        private HttpUploadListener httpUploadListener;
        private InputStream is;
        private BufferedReader rd;
        private StringBuffer response;

        HttpResponse(HttpUploadListener httpUploadListener, InputStream is) {
            this.httpUploadListener = httpUploadListener;
            this.is = is;
        }

        BufferedReader getRd() {
            return rd;
        }

        public StringBuffer getResponse() {
            return response;
        }

        public HttpResponse invoke() throws IOException {
            rd = new BufferedReader(new InputStreamReader(is));
            String line;
            response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            if (httpUploadListener != null) {
                httpUploadListener.onUploadSuccess();
            }

            return this;
        }
    }
}
