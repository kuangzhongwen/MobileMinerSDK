package waterhole.commonlibs.preferences;

/**
 * 处理读写锁里的事务
 *
 * @author kzw on 2017/03/19.
 */
interface RWLockTransactionListener {

    /**
     * 用事务对SharedPreferences进行写入操作
     */
    void onWriteTransaction();

    /**
     * 用事务对SharedPreferences进行读取操作
     */
    String onReadTransaction();
}
