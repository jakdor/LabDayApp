package com.jakdor.labday.common.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.common.model.Path;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

/**
 * DAO for Path model
 */
public abstract class PathDao {

    public static final String TABLE = "path";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String INFO = "info";
    public static final String ACTIVE = "active";

    public static final String CREATE_PATH = ""
            + "CREATE TABLE " + TABLE + "("
            + ID + " INTEGER NOT NULL,"
            + NAME + " TEXT,"
            + INFO + " TEXT,"
            + ACTIVE + " INTEGER NOT NULL"
            + ")";

    public static long insertPathList(BriteDatabase db, List<Path> paths){
        long pos = -1;

        for(Path path : paths) {
            ContentValues values = new Builder()
                    .id(path.getId())
                    .name(path.getName())
                    .info(path.getInfo())
                    .active(path.getActive())
                    .build();

            pos = db.insert(TABLE, CONFLICT_FAIL, values);
        }

        return pos;
    }

    public static Observable<List<Path>> getAllPaths(BriteDatabase db){
        Observable<SqlBrite.Query> dbQuery = db.createQuery(TABLE, "SELECT * FROM " + TABLE);
        return dbQuery.map(query -> {
            ArrayList<Path> paths = new ArrayList<>();

            Cursor cursor = query.run();
            if (cursor == null){
                return paths;
            }

            while(cursor.moveToNext()){
                paths.add(new Path(cursor.getInt(cursor.getColumnIndex(ID)),
                                cursor.getString(cursor.getColumnIndex(NAME)),
                                cursor.getString(cursor.getColumnIndex(INFO)),
                                cursor.getInt(cursor.getColumnIndex(ACTIVE)) == 1));
            }

            cursor.close();
            return paths;
        });
    }

    public static void deleteAll(BriteDatabase db){
        db.delete(TABLE, null);
    }

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(Integer id){
            values.put(ID, id);
            return this;
        }

        public Builder name(String name){
            values.put(NAME, name);
            return this;
        }

        public Builder info(String info){
            values.put(INFO, info);
            return this;
        }

        public Builder active(Boolean active){
            values.put(ACTIVE, active);
            return this;
        }

        public ContentValues build(){
            return values;
        }
    }
}
