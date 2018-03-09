package waterhole.commonlibs.net.apache;

/**
 * When you download, use this interface for callback
 *
 * @author kzw on 2017/07/12.
 */
public interface DownloadListener {

    /**
     * When the file starts downloading, call back this method
     */
    void startDownload();

    /**
     * When the file is in, the callback method returns the download schedule
     *
     * @param progress download schedule
     */
    void downloading(int progress);

    /**
     * This method is called when the download is complete
     */
    void downloadSuccess();

    /**
     * This method is called when the download fails
     */
    void downloadFail();
}
