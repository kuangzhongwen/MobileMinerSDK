package waterhole.commonlibs;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import waterhole.commonlibs.okhttp.OkHttpUtils;
import waterhole.commonlibs.okhttp.callback.BitmapCallback;
import waterhole.commonlibs.okhttp.callback.Callback;
import waterhole.commonlibs.okhttp.callback.FileCallBack;
import waterhole.commonlibs.okhttp.callback.GenericsCallback;
import waterhole.commonlibs.okhttp.callback.IGenericsSerializator;
import waterhole.commonlibs.okhttp.callback.StringCallback;
import waterhole.commonlibs.okhttp.cookie.CookieJarImpl;
import waterhole.commonlibs.utils.LogUtils;

/**
 * OkHttp的单元测试统一写到此处。
 *
 * @author kzw on 2017/06/26.
 */
@RunWith(AndroidJUnit4.class)
public final class OkHttpTest extends BaseTest {

    private static final String TAG = "OkHttpTest";

    private String mBaseUrl = "http://192.168.31.242:8888/okHttpServer/";

    public OkHttpTest() {
        // 需要加上默认构造器，否则会报Test running failed: No test results
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onBefore(Request request, int id) {
            LogUtils.info(TAG, "onBefore");
        }

        @Override
        public void onAfter(int id) {
            LogUtils.info(TAG, "onAfter");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            LogUtils.error(TAG, "onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.info(TAG, "onResponse：complete");
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            LogUtils.info(TAG, "inProgress:" + progress);
        }
    }

    public class JsonGenericsSerializator implements IGenericsSerializator {

        Gson mGson = new Gson();

        @Override
        public <T> T transform(String response, Class<T> classOfT) {
            return mGson.fromJson(response, classOfT);
        }
    }

    public abstract class ListUserCallback extends Callback<List<User>> {

        @Override
        public List<User> parseNetworkResponse(Response response, int id) throws IOException {
            String string = response.body().string();
            List<User> user = new Gson().fromJson(string, List.class);
            return user;
        }
    }

    public class User implements Serializable {

        public String userName;
        public String password;

        public User(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }

    @Test
    public void testGetHtml() {
        String url = "http://www.391k.com/api/xapi.ashx/info.json?key=bd_hyrzjjfb4modhj&size=10&page=1";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .useCache(true)
                .cacheTimeOut(15 * 60 * 1000)
                .useRetry(true)
                .maxRetryCounts(2)
                .execute(new MyStringCallback());
    }

    @Test
    public void testPostString() {
        String url = mBaseUrl + "user!postString";
        OkHttpUtils
                .postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(new User("kzw", "123")))
                .build()
                .useCache(true)
                .cacheTimeOut(15 * 60 * 1000)
                .useRetry(true)
                .maxRetryCounts(3)
                .execute(new MyStringCallback());
    }

    @Test
    public void testPostFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists()) {
            return;
        }
        String url = mBaseUrl + "user!postFile";
        OkHttpUtils
                .postFile()
                .url(url)
                .file(file)
                .build()
                .execute(new MyStringCallback());
    }

    @Test
    public void testGetUser() {
        String url = mBaseUrl + "user!getUser";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username", "kzw")
                .addParams("password", "123")
                .build()//
                .execute(new GenericsCallback<User>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(User response, int id) {

                    }
                });
    }

    @Test
    public void testGetUsers() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "kzw");
        String url = mBaseUrl + "user!getUsers";
        OkHttpUtils
                .post()
                .url(url)
                .params(params)
                .build()//
                .execute(new ListUserCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(List<User> response, int id) {

                    }
                });
    }

    @Test
    public void testGetHttpsHtml() {
        String url = "https://kyfw.12306.cn/otn/";
        OkHttpUtils
                .get()
                .url(url)
                .id(101)
                .build()
                .execute(new MyStringCallback());
    }

    @Test
    public void testGetImage() {
        String url = "http://images.csdn.net/20150817/1.jpg";
        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {

                    }
                });
    }

    @Test
    public void testUploadFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "kzw");
        params.put("password", "123");

        Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = mBaseUrl + "user!uploadFile";

        OkHttpUtils.post()
                .addFile("mFile", "messenger_01.png", file)
                .url(url)
                .params(params)
                .headers(headers)
                .build()
                .execute(new MyStringCallback());
    }

    @Test
    public void testMultiFileUpload() {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1#.txt");
        if (!file.exists()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "kzw");
        params.put("password", "123");

        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)
                .addFile("mFile", "test1.txt", file2)
                .url(url)
                .params(params)
                .build()//
                .execute(new MyStringCallback());
    }

    @Test
    public void testDownloadFile() {
        String url = "https://github.com/kzwAndroid/okhttp-utils/blob/master/okhttputils-2_4_1.jar?raw=true";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "gson-2.2.1.jar") {

                    @Override
                    public void onBefore(Request request, int id) {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        LogUtils.info(TAG, "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.error(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        LogUtils.info(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


    @Test
    public void testOtherRequest() {
        //also can use delete ,head , patch
        OkHttpUtils.put()
                .url("http://11111.com")
                .requestBody("may be something")
                .build()//
                .execute(new MyStringCallback());
        try {
            OkHttpUtils.head()
                    .url("http://22222.com")
                    .addParams("name", "kzw")
                    .build()
                    .execute();
        } catch (IOException e) {
            LogUtils.printStackTrace(e);
        }
    }

    @Test
    public void testClearSession() {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl) {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }
}