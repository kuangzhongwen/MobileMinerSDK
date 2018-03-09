/*
 * Copyright (c) 2015. Zlianjie Inc. All rights reserved.
 */

package waterhole.commonlibs.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import waterhole.commonlibs.utils.IOUtils;
import waterhole.commonlibs.utils.LogUtils;

/**
 * 数据库工具类
 *
 * @author kzw on 2017/03/17
 */
public final class DBUtils {

    private static final String TAG = "DBUtils";

    private DBUtils() {
    }

    public static Cursor querySafely(final SQLiteDatabase db, String table, final String[] columns,
                                     String selection) {
        return querySafely(db, table, columns, selection, null, null, null, null, null);
    }

    public static Cursor querySafely(final SQLiteDatabase db, String table, final String[] columns,
                                     String selection, String orderBy) {
        return querySafely(db, table, columns, selection, null, null, null, orderBy, null);
    }

    public static Cursor querySafely(final SQLiteDatabase db, String table, final String[] columns,
                                     String selection, String orderBy, String limit) {
        Cursor c = null;
        if (db != null) {
            try {
                c = db.query(table, columns, selection, null, null, null, orderBy, limit);
            } catch (Throwable t) {
                logError(TAG, "Error during query!", t);
            }
        }
        return c;
    }

    public static Cursor querySafely(final SQLiteDatabase db, String table, final String[] columns,
                                     String selection, String[] selectionArgs, String groupBy,
                                     String having, String orderBy) {
        return querySafely(db, table, columns, selection, selectionArgs, groupBy, having, orderBy,
                null);
    }

    public static Cursor querySafely(final SQLiteDatabase db, String table, final String[] columns,
                                     String selection, String[] selectionArgs, String groupBy, String having,
                                     String orderBy, String limit) {
        Cursor c = null;
        if (db != null) {
            try {
                c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy,
                        limit);
            } catch (Throwable t) {
                logError(TAG, "Error during query!", t);
            }
        }
        return c;
    }

    public static long insertOrThrowSafely(final SQLiteDatabase db, String table,
                                           String nullColumnHack,
                                           final ContentValues values) {
        try {
            return db.insertOrThrow(table, nullColumnHack, values);
        } catch (Throwable t) {
            logError(TAG, "Error during insert!", t);
            return 0;
        }
    }

    static void logError(String tag, String msg, final Throwable t) {
        Log.e(tag, msg, t);
    }

    public static String getAddColumnSql(String table, String column, String type,
                                         String restraint, String defaultValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(table).append(" ADD ").append(column).append(" ")
                .append(type).append(" DEFAULT ").append("'").append(defaultValue).append("'");
        if (restraint != null) {
            sb.append(" ").append(restraint);
        }
        return sb.toString();
    }

    private static boolean assertCursorAndColumn(final Cursor cursor, String column) {
        return cursor != null && !TextUtils.isEmpty(column);
    }

    private static int getColumnIndexSafely(final Cursor cursor, String column) {
        try {
            return cursor.getColumnIndexOrThrow(column);
        } catch (Throwable t) {
            LogUtils.error(TAG, t.getMessage());
            return 0;
        }
    }

    public static String getStringSafely(final Cursor cursor, String column) {
        try {
            return assertCursorAndColumn(cursor, column) ? cursor.getString(getColumnIndexSafely(
                    cursor, column)) : "";
        } catch (Throwable t) {
            LogUtils.error(TAG, t.getMessage());
            return "";
        }
    }

    public static int getIntSafely(final Cursor cursor, String column) {
        try {
            return assertCursorAndColumn(cursor, column) ? cursor.getInt(getColumnIndexSafely(
                    cursor, column)) : 0;
        } catch (Throwable t) {
            LogUtils.error(TAG, t.getMessage());
            return 0;
        }
    }

    public static long getLongSafely(final Cursor cursor, String column) {
        try {
            return assertCursorAndColumn(cursor, column) ? cursor.getLong(getColumnIndexSafely(
                    cursor, column)) : 0L;
        } catch (Throwable t) {
            LogUtils.error(TAG, t.getMessage());
            return 0L;
        }
    }

    public static short getShortSafely(final Cursor cursor, String column) {
        try {
            return assertCursorAndColumn(cursor, column) ? cursor.getShort(getColumnIndexSafely(
                    cursor, column)) : 0;
        } catch (Throwable t) {
            LogUtils.error(TAG, t.getMessage());
            return 0;
        }
    }

    public static double getDoubleSafely(final Cursor cursor, String column) {
        try {
            return assertCursorAndColumn(cursor, column) ? cursor.getDouble(getColumnIndexSafely(
                    cursor, column)) : 0.0d;
        } catch (Throwable t) {
            LogUtils.error(TAG, t.getMessage());
            return 0.0d;
        }
    }

    public static int getCountSafely(final SQLiteDatabase db, String table, String where,
                                     final String[] colums) {
        Cursor cursor = querySafely(db, table, colums, where);
        try {
            if (cursor != null) {
                return cursor.getCount();
            }
        } finally {
            IOUtils.closeSafely(cursor);
        }
        return 0;
    }
}
