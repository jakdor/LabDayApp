package com.jakdor.labday.common.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.common.model.Speaker;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

/**
 * DAO for Speaker model
 */
public abstract class SpeakerDao {

    public static final String TABLE = "speaker";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String INFO = "info";
    public static final String IMG = "img";

    public static final String CREATE_SPEAKER = ""
            + "CREATE TABLE " + TABLE + "("
            + ID + " INTEGER NOT NULL,"
            + NAME + " TEXT,"
            + INFO + " TEXT,"
            + IMG + " TEXT"
            + ")";

    public static long insertSpeakerList(BriteDatabase db, List<Speaker> speakers){
        long pos = -1;

        for(Speaker speaker : speakers) {
            ContentValues values = new SpeakerDao.Builder()
                    .id(speaker.getId())
                    .name(speaker.getSpeakerName())
                    .info(speaker.getSpeakerInfo())
                    .img(speaker.getSpeakerImg())
                    .build();

            pos = db.insert(TABLE, CONFLICT_FAIL, values);
        }

        return pos;
    }

    public static Observable<List<Speaker>> getAllSpeakers(BriteDatabase db){
        Observable<SqlBrite.Query> dbQuery = db.createQuery(TABLE, "SELECT * FROM " + TABLE);
        return dbQuery.map(query -> {
            ArrayList<Speaker> speakers = new ArrayList<>();

            Cursor cursor = query.run();
            if (cursor == null){
                return speakers;
            }

            while(cursor.moveToNext()){
                speakers.add(new Speaker(cursor.getInt(cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(INFO)),
                        cursor.getString(cursor.getColumnIndex(IMG))));
            }

            cursor.close();
            return speakers;
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

        public ContentValues build(){
            return values;
        }
    }
}
