package waterhole.commonlibs.net.apache;

/**
 * 上传时的错误码
 *
 * @author SwainLi on 2016/06/27
 */
public final class UploadErrorCode {

    private UploadErrorCode() {
    }

    public static final int UPLOAD_ERROR_UNKNOWN = 0;
    public static final int UPLOAD_ERROR_FILE_TOO_LARGE = 1;
}
