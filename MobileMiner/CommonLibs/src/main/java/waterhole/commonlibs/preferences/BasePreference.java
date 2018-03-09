package waterhole.commonlibs.preferences;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import waterhole.commonlibs.utils.LogUtils;

/**
 * 抽象Preference，提供最基本的SharedPreference的增删改查
 *
 * @author kzw on 2017/03/20.
 */
public abstract class BasePreference implements IPreference<String, String> {

    protected static final String TAG = "BasePreference";

    // Read-write lock，重入锁
    private final ReadWriteLock mRWLock = new ReentrantReadWriteLock();

    protected abstract String getTableName();

    @Override
    public void save(final Context context, final PreferenceValues values) {
        if (values != null) {
            lockWriteInterruptibly(new RWLockTransactionListener() {
                @Override
                public void onWriteTransaction() {
                    Set<String> keys = values.keySet();
                    for (String key : keys) {
                        String value = values.getAsString(key);
                        if (TextUtils.isEmpty(value)) {
                            continue;
                        }
                        LogUtils.info(TAG, "save key=" + key);
                        saveValue(context, key, value);
                    }
                }

                @Override
                public String onReadTransaction() {
                    return null;
                }
            });
        }
    }

    @Override
    public String get(final Context context, final String key) {
        LogUtils.info(TAG, "get key=" + key);
        return lockReadInterruptibly(new RWLockTransactionListener() {
            @Override
            public void onWriteTransaction() {
            }

            @Override
            public String onReadTransaction() {
                return getValue(context, key);
            }
        });
    }

    @Override
    public void remove(final Context context, final String key) {
        if (!TextUtils.isEmpty(key)) {
            LogUtils.debug(TAG, "remove key=" + key);
            lockWriteInterruptibly(new RWLockTransactionListener() {
                @Override
                public void onWriteTransaction() {
                    removeValue(context, key);
                }

                @Override
                public String onReadTransaction() {
                    return null;
                }
            });
        }
    }

    @Override
    public void clear(final Context context) {
        LogUtils.debug(TAG, "clear");
        lockWriteInterruptibly(new RWLockTransactionListener() {
            @Override
            public void onWriteTransaction() {
                clearValues(context);
            }

            @Override
            public String onReadTransaction() {
                return null;
            }
        });
    }

    protected void saveValue(Context context, String key, String value) {
        SharedPreferencesUtils.setString(context, getTableName(), key, value);
    }

    protected String getValue(Context context, String key) {
        return SharedPreferencesUtils.getString(context, getTableName(), key, "");
    }

    protected void removeValue(Context context, String key) {
        SharedPreferencesUtils.removeValue(context, getTableName(), key);
    }

    private void clearValues(Context context) {
        SharedPreferencesUtils.clear(context, getTableName());
    }

    private void lockWriteInterruptibly(@NonNull final RWLockTransactionListener listener) {
        try {
            mRWLock.writeLock().lockInterruptibly();
            listener.onWriteTransaction();
        } catch (InterruptedException e) {
            LogUtils.printStackTrace(e);
        } finally {
            mRWLock.writeLock().unlock();
        }
    }

    private String lockReadInterruptibly(@NonNull final RWLockTransactionListener listener) {
        try {
            mRWLock.readLock().lockInterruptibly();
            return listener.onReadTransaction();
        } catch (InterruptedException e) {
            LogUtils.printStackTrace(e);
            return null;
        } finally {
            mRWLock.readLock().unlock();
        }
    }
}
