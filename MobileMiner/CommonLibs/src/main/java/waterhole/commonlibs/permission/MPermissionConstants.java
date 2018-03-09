package waterhole.commonlibs.permission;

import android.Manifest;

import waterhole.commonlibs.utils.APIUtils;

/**
 * 权限相关常量
 *
 * @author kzw on on 2017/07/05.
 */
public final class MPermissionConstants {

    static {
        if (APIUtils.hasJellyBean()) {
            READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
        } else {
            READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
        }
    }

    public MPermissionConstants() {
        throw new RuntimeException("MPermissionConstants stub!");
    }

    // 精确定位权限
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    // 粗略定位权限
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    // 读取电话状态权限
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    // 录音权限
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    // 写外部存储权限
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    // 读外部存储权限
    public static final String READ_EXTERNAL_STORAGE;
    // 拍照权限
    public static final String CAMERA = Manifest.permission.CAMERA;
    // 读取手机联系人权限
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;

    // 是否请求过某个权限，本地进行存储
    public static final String SP_IS_FIRST_REQUEST_LOCATION_PERMISSION = "is_first_request_location_permission";
    public static final String SP_IS_FIRST_REQUEST_READ_PHONE_STATE_PERMISSION = "is_first_request_read_phone_state_permission";
    public static final String SP_IS_FIRST_REQUEST_CAMERA_PERMISSION = "is_first_request_camera_permission";
    public static final String SP_IS_FIRST_REQUEST_RECORD_AUDIO_PERMISSION = "is_first_request_record_audio_permission";
    public static final String SP_IS_FIRST_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = "is_first_request_write_external_storage_permission";
    public static final String SP_IS_FIRST_REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = "is_first_request_read_external_storage";
    public static final String SP_IS_FIRST_REQUEST_READ_CONTACTS_PERMISSION = "is_first_request_read_contacts_permission";

    /**
     * Android 6.0以下动态检测权限，这些API不是让第三方app使用的，而是供系统应用调用的，只能通过反射来调用。
     *
     * @author kzw 2017/07/08.
     * @see android.app.AppOpsManager
     */
    @Deprecated
    public static final class AppOpsConstant {

        public AppOpsConstant() {
            throw new RuntimeException("AppOpsConstant stub!");
        }

        // 定位权限
        public static final int OP_FINE_LOCATION = 1;
        // 通讯录权限
        public static final int OP_READ_CONTACTS = 4;
        // 电话权限
        public static final int OP_CALL_PHONE = 13;
        // 悬浮窗权限
        public static final int OP_SYSTEM_ALERT_WINDOW = 24;
        // 拍照权限
        public static final int OP_CAMERA = 26;
        // 录音权限
        public static final int OP_RECORD_AUDIO = 27;
    }
}
