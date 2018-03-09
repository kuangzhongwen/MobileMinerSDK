package waterhole.commonlibs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ObjectStreamException;

import waterhole.commonlibs.observer.Observer;
import waterhole.commonlibs.utils.LogUtils;

/**
 * 数据库创建/升级辅助类
 *
 * @author kzw on 2017/03/17
 */
public final class DBOpenHelper extends SQLiteOpenHelper {

    /**
     * log tag
     */
    private static final String TAG = "DBOpenHelper";

    /**
     * 创建db的action，配合{@link Observer} 使用，当数据库创建时会发送此通知
     */
    public static final String DB_CREATE_OBSERVER_ACTION = "action.db_create_observer";
    public static final String DB_UPGRADE_OBSERVER_ACTION = "action.db_upgrade_observer";

    private Context mContext;
    private String mDBName;

    private static DBOpenHelper sInstance = null;

    private DBOpenHelper(final Context context, String name,
                         final SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mDBName = name;
    }

    public static synchronized DBOpenHelper getInstance(Context context, String name, int version) {
        if (context == null) {
            throw new RuntimeException("Please invoke MtbGlobalAdConfig.initMtbAd() first!");
        }
        if (sInstance == null) {
            LogUtils.info(TAG, "getInstance DBOpenHelper  version : " + version);
            sInstance = new DBOpenHelper(context, name, null, version);
        }
        return sInstance;
    }

    /**
     * forbid reflect create new instance
     */
    private Object readResolve() throws ObjectStreamException {
        return sInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(final SQLiteDatabase db) {
        //XXX 务必注意新增的表同样需要在onUpgrade中处理
        Observer.getInstance().fireUpdate(DB_CREATE_OBSERVER_ACTION, db);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.error(TAG, "DB new version= " + newVersion + "DB old version=" + oldVersion);
        Observer.getInstance().fireUpdate(DB_UPGRADE_OBSERVER_ACTION, db);
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = null;
        try {
            db = super.getReadableDatabase();
        } catch (Exception e) {
            LogUtils.error(TAG, e.getMessage());
            // getReadableDatabase()出现异常，则删掉数据库文件，然后再创建一遍
            try {
                if (mContext.deleteDatabase(mDBName)) {
                    db = super.getReadableDatabase();
                } else {
                    LogUtils.error(TAG, "getReadableDatabase() throw Exception, but failed to delete it.");

                }
            } catch (Exception e1) {
                LogUtils.error(TAG, e1.getMessage());
            }
        }
        return db;
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = null;
        try {
            db = super.getWritableDatabase();
        } catch (Exception e) {
            LogUtils.error(TAG, e.getMessage());
            // getWritableDatabase()出现异常，则删掉数据库文件，然后再创建一遍
            try {
                if (mContext.deleteDatabase(mDBName)) {
                    db = super.getWritableDatabase();
                } else {
                    LogUtils.error(TAG, "getWritableDatabase() throw Exception, but failed to delete it.");

                }
            } catch (Exception e1) {
                LogUtils.error(TAG, e1.getMessage());
            }
        }
        return db;
    }
}
