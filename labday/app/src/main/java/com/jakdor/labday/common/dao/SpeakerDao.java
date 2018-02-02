package com.jakdor.labday.common.dao;

import android.content.ContentValues;

import com.squareup.sqlbrite3.BriteDatabase;

/**
 * DAO for Speaker model
 */
public class SpeakerDao {

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
