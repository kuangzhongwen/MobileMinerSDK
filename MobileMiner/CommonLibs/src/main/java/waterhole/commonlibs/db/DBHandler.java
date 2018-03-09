package waterhole.commonlibs.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * DB handler
 *
 * @param <Bean> db bean
 * @author kzw on 2017/03/22
 */
public interface DBHandler<Bean> {

    void insert(Bean bean);

    void update(Bean bean);

    void remove(String where);

    Cursor query(String where);

    Cursor queryLimit(String where, String limit);

    ContentValues getContentValues(Bean bean);

    boolean has(String where);
}
