
package waterhole.commonlibs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络工具
 *
 * @author kzw on 2016/07/22.
 */
public final class NetworkUtils {

    // 网络不可用
    private static final int NONETWORK = 0;
    // 是wifi连接
    private static final int WIFI = 1;
    // 不是wifi连接
    private static final int NOWIFI = 2;

    private NetworkUtils() {
    }

    private static int getNetWorkType(Context context) {
        if (!isNetWorkAvalible(context)) {
            return NetworkUtils.NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
            return NetworkUtils.WIFI;
        } else {
            return NetworkUtils.NOWIFI;
        }
    }

    public static boolean isNetWorkAvalible(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return !(ni == null || !ni.isAvailable());
    }

    public static boolean isWifi(Context context) {
        return context != null && getNetWorkType(context) == WIFI;
    }

    public static boolean is4G(Context context) {
        String strNetworkType = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    public static boolean vpnConnected(Context context) {
        if (!isNetWorkAvalible(context)) {
            return false;
        }
        try {
            for (Enumeration<NetworkInterface> en =
                 NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("tun0") || intf.getName().contains("ppp0")) {
                    return true;
                }
            }
        } catch (SocketException e) {
            return false;
        }
        return false;
    }

    /**
     * 当连接wifi时networkInterface中的name字段是wlanx(x是0,1...)
     * 当使用移动网络时 待查
     */
    public static String getIPAddress() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() &&
                            InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LogUtils.printStackTrace(e);
        }
        return ipaddress;
    }
}
