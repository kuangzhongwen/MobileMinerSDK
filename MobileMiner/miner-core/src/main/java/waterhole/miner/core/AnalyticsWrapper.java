package waterhole.miner.core;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;

/**
 * 统计包装类，内部使用友盟统计
 * <a>http://dev.umeng.com/analytics/android-doc</a>
 *
 * @author kzw on 2017/07/10.
 */
public final class AnalyticsWrapper {

    public AnalyticsWrapper() {
        throw new RuntimeException("AnalyticsWrapper stub!");
    }

    public static void init(Context context, String appkey) {
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, appkey);
    }

    /**
     * 注册 Activity: 在每个Activity的onResume方法中调用 MobclickAgent.onResume(Context),
     * 传入的参数为当前context的引用，这个方法将会自动地从AndroidManifest.xml文件里读取Appkey。
     * 这里请不要将全局的application context传入
     *
     * @param context 当前Activity对象
     */
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /**
     * {@link #onResume(Context)}
     * <p>
     * <br>确保在所有的activity中都调用 MobclickAgent.onResume() 和MobclickAgent.onPause()方法，
     * 这两个调用将不会阻塞应用程序的主线程，也不会影响应用程序的性能。注意如果您的Activity之间有继承
     * 或者控制关系请不要同时在父和子Activity中重复添加onPause和onResume方法，否则会造成重复统计
     * (eg.使用TabHost、TabActivity、ActivityGroup时)。一个应用程序在多个activity之间连续切换时，
     * 将会被视为同一个session(启动)。
     * <p>
     * 当用户两次使用之间间隔超过30秒时，将被认为是两个的独立的session(启动)，例如用户回到home，
     * 或进入其他程序，经过一段时间后再返回之前的应用。 在V3.1.1.1以上版本中我们提供了新的接口来自定义这个时间间隔，
     * 您只要调用：MobclickAgent.setSessionContinueMillis(long)传入适当的参数，就可以控制session重新启动
     * 时间，注意参数是以毫秒为单位的。 例如，如果您认为在60秒之内返回应用可视为同一次启动，超过60秒返回当前应用
     * 可视为一次新的启动，那么请写成：
     * MobclickAgent.setSessionContinueMillis(60000)
     * </br>
     *
     * @param context 当前Activity对象
     */
    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    /**
     * 统计发生次数
     * <example>
     * 统计微博应用中"转发"事件发生的次数，那么在转发的函数里调用：
     * MobclickAgent.onEvent(mContext,"Forward");
     * </example>
     *
     * @param context 上下文对象
     * @param eventId 为当前统计的事件ID
     */
    public static void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 统计点击行为各属性被触发的次数
     * <example>
     * 统计电商应用中“购买”事件发生的次数，以及购买的商品类型及数量，那么在购买的函数里调用：
     * HashMap<String,String> map = new HashMap<String,String>();
     * map.put("type","book");
     * map.put("quantity","3");
     * MobclickAgent.onEvent(mContext, "purchase", map);
     * </example>
     *
     * @param context 上下文对象
     * @param eventId 为当前统计的事件ID
     * @param map     为当前事件的属性和取值（Key-Value键值对）
     */
    public static void onEvent(Context context, String eventId, HashMap<String, String> map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

    /**
     * 统计数值型变量的值的分布
     * <a>统计一个数值类型的连续变量（该变量必须为整数），用户每次触发的数值的分布情况，
     * 如事件持续时间、每次付款金额等，可以调用如下方法：
     * MobclickAgent.onEventValue(Context context, String id, Map<String,String> m, int du)
     * </a>
     * <br>
     * example:统计一次音乐播放，包括音乐类型，作者和播放时长，可以在音乐播放结束后这么调用：
     * //开发者需要自己计算音乐播放时长
     * int duration = 12000;
     * 　　Map<String, String> map_value = new HashMap<String, String>();
     * 　　map_value.put("type" , "popular" );
     * 　　map_value.put("artist" , "JJLin" );
     * MobclickAgent.onEventValue(this, "music" , map_value, duration);
     * </br>
     *
     * @param context
     * @param id
     * @param map
     * @param value
     */
    public static void onEvent(Context context, String id, HashMap<String, String> map, long value) {
        // 上面方法在Android统计分析V5.2.2之后(>=)才提供，之前的版本可以通过下面的方式封装计算事件：
        map.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, map);
    }

    /**
     * 账号的统计
     * <a>可以在登陆的地方调用这个方法来统计手机号码登陆和第三方登陆的用户信息</a>
     *
     * @param id 用户账号id，长度小于64字节
     */
    public static void onProfileSignIn(String id) {
        MobclickAgent.onProfileSignIn(id);
    }

    /**
     * {@link #onProfileSignIn(String)}
     * <example>
     * 当用户使用第三方账号（如新浪微博）登录时，可以这样统计：
     * MobclickAgent.onProfileSignIn("WB"，"userID");
     * </example>
     *
     * @param Provider 账号来源。如果用户通过第三方账号登陆，可以调用此接口进行统计。
     *                 支持自定义，不能以下划线”_”开头，使用大写字母和数字标识，长度小于32 字节;
     *                 如果是上市公司，建议使用股票代码
     * @param id       用户账号id，长度小于64字节
     */
    public static void onProfileSignIn(String Provider, String id) {
        MobclickAgent.onProfileSignIn(id);
    }

    /**
     * 账号登出时需调用此接口，调用之后不再发送账号相关内容
     */
    public static void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    /**
     * 错误统计
     * <a>Android统计SDK从V4.6版本开始内建错误统计，不需要开发者再手动集成</a>
     * <br>SDK通过Thread.UncaughtExceptionHandler捕获程序崩溃日志，并在程序下次启动时发送到服务器。
     * 如不需要错误统计功能，可通过此方法关闭：
     * MobclickAgent.setCatchUncaughtExceptions(false); </br>
     * <p>
     * 如果开发者自己捕获了错误，则调用此方法
     *
     * @param context 上下文对象
     * @param error   错误信息
     */
    public static void reportError(Context context, String error) {
        MobclickAgent.reportError(context, error);
    }

    /**
     * {@link #reportError(Context, String)}
     *
     * @param context 上下文对象
     * @param e       异常对象
     */
    public static void reportError(Context context, Throwable e) {
        MobclickAgent.reportError(context, e);
    }
}
