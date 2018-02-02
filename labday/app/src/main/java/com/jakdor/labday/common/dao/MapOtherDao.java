package com.jakdor.labday.common.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.common.model.MapOther;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

/**
 * DAO for MapOther model
 */
public abstract class MapOtherDao {

    public static final String TABLE = "mapother";

    public static final String ID = "_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String INFO = "info";
    public static final String IMG = "img";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String CREATE_MAP_OTHER = ""
            + "CREATE TABLE " + TABLE + "("
            + ID + " INTEGER NOT NULL,"
            + TYPE + " INTEGER NOT NULL,"
            + NAME + " TEXT,"
            + INFO + " TEXT,"
            + IMG + " TEXT,"
            + LATITUDE + " TEXT,"
            + LONGITUDE + " TEXT"
            + ")";

    public static long insertMapOtherList(BriteDatabase db, List<MapOther> mapOthers){
        long pos = -1;

        for(MapOther mapOther : mapOthers) {
            ContentValues values = new MapOtherDao.Builder()
                    .id(mapOther.getId())
                    .type(mapOther.getType())
                    .name(mapOther.getName())
                    .info(mapOther.getInfo())
                    .img(mapOther.getImg())
                    .latitude(mapOther.getLatitude())
                    .longitude(mapOther.getLongitude())
                    .build();

            pos = db.insert(TABLE, CONFLICT_FAIL, values);
        }

        return pos;
    }

    public static Observable<List<MapOther>> getMapOthers(BriteDatabase db){
        Observable<SqlBrite.Query> dbQuery = db.createQuery(TABLE, "SELECT * FROM " + TABLE);
        return dbQuery.map(query -> {
            ArrayList<MapOther> mapOthers = new ArrayList<>();

            Cursor cursor = query.run();
            if (cursor == null){
                return mapOthers;
            }

            while(cursor.moveToNext()){
                mapOthers.add(new MapOther(cursor.getInt(cursor.getColumnIndex(ID)),
                        cursor.getInt(cursor.getColumnIndex(TYPE)),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(INFO)),
                        cursor.getString(cursor.getColumnIndex(IMG)),
                        cursor.getString(cursor.getColumnIndex(LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(LONGITUDE))));
            }

            cursor.close();
            return mapOthers;
        });
    }

    public static void deleteAll(BriteDatabase db){
        db.delete(TABLE, null);
    }

    public static final class Builder{
        private final ContentValues values = new ContentValues();

        public Builder id(Integer id){
            values.put(ID, id);
            return this;
        }

        public Builder type(Integer type){
            values.put(TYPE, type);
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

        public Builder img(String img){
            values.put(IMG, img);
            return this;
        }

        public Builder latitude(String latitude){
            values.put(LATITUDE, latitude);
            return this;
        }

        public Builder longitude(String longitude){
            values.put(LONGITUDE, longitude);
            return this;
        }

        public ContentValues build(){
            return values;
        }
    }
}
