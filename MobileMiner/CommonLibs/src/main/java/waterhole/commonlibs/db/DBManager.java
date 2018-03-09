package waterhole.commonlibs.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import java.io.Closeable;

import waterhole.commonlibs.utils.LogUtils;

/**
 * 数据库管理，db包属于data下的一种数据存储方式，可以供缓存实现用
 *
 * @author kzw on 2017/03/17
 */
public abstract class DBManager implements Closeable {

    protected static final String TAG = "DBManager";

    protected final DBOpenHelper mDBOpenHelper;

    protected DBManager(Context context, String dbName, int dbVersion) {
        mDBOpenHelper = DBOpenHelper.getInstance(context, dbName, dbVersion);
    }

    @Override
    public void close() {
        mDBOpenHelper.close();
    }

    /**
     * 执行数据库操作。
     *
     * @param transaction Refer to {@link SQLiteTransaction}
     */
    protected final void runTransaction(final SQLiteTransaction transaction) {
        if (transaction == null) {
            return;
        }
        try {
            transaction.run(getWritableDatabase());
        } catch (SQLiteException e) {
            // 有些机器会报 android.database.sqlite.SQLiteConnection.nativeExecute
            LogUtils.error(TAG, e.getMessage());
        }
    }

    /**
     * 增加非null value
     *
     * @param contentValue ContentValues
     * @param key          Key
     * @param value        value
     * @return ContentValues ContentValues
     */
    protected static ContentValues putNotNullStrValue(final ContentValues contentValue,
                                                      String key, String value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return contentValue;
        }
        contentValue.put(key, value);
        return contentValue;
    }

    private SQLiteDatabase getWritableDatabase() {
        return mDBOpenHelper.getWritableDatabase();
    }

    protected final SQLiteDatabase getReadableDatabase() {
        return mDBOpenHelper.getReadableDatabase();
    }

    /**
     * 用于跨library的表的创建与升级，如果在core包中的表不用重写以下两方法，
     * 直接在{@link DBOpenHelper} 中创建与升级即可
     */
    protected void onCreate(SQLiteDatabase db) {
    }

    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
