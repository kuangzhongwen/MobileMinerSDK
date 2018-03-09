package waterhole.commonlibs.db;

import android.database.sqlite.SQLiteDatabase;

import waterhole.commonlibs.utils.LogUtils;

/**
 * Abstract helper base class for SQLite write transactions.
 *
 * @author kzw on 2017/03/17
 */
public abstract class SQLiteTransaction {

    private static final String TAG = "SQLiteTransaction";

    private TransactionListener transactionListener;

    /**
     * Default constructor.
     */
    protected SQLiteTransaction() {
    }

    /**
     * Constructor that accepts a {@link TransactionListener} to track execution
     * of the transaction.
     *
     * @param listener {@link TransactionListener}
     */
    public SQLiteTransaction(final TransactionListener listener) {
        transactionListener = listener;
    }

    /**
     * Executes the statements that form the transaction.
     *
     * @param db A writable database.
     * @return {@code true} if the transaction should be committed.
     */
    protected abstract boolean performTransaction(SQLiteDatabase db);

    /**
     * Runs the transaction against the database. The results are committed if
     * {@link #performTransaction(SQLiteDatabase)} completes normally and returns {@code true}.
     *
     * @param db DataBase
     */
    public void run(final SQLiteDatabase db) {
        if (db == null) return;
        if (transactionListener != null) {
            transactionListener.onTransactionBegin();
        }
        boolean success = false;
        db.beginTransaction();
        try {
            success = performTransaction(db);
            if (success) {
                db.setTransactionSuccessful();
            }
        } catch (Throwable t) {
            LogUtils.error(TAG, "SQLiteTransaction.run()", t);
        } finally {
            db.endTransaction();
            //此操作有可能引发与查询的冲突，因为getWritableDatabase和getReadableDataBase是一个实例。
            //在程序退出的时候执行关闭即可。
            //db.close();
        }
        if (transactionListener != null) {
            transactionListener.onTransactionEnd(success);
        }
    }
}
