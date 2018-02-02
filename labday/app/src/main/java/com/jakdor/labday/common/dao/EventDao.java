package com.jakdor.labday.common.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.common.model.Event;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

/**
 * DAO for Event model
 */
public abstract class EventDao {

    public static final String TABLE = "event";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String IMG = "img";
    public static final String ADDRESS = "address";
    public static final String ROOM = "room";
    public static final String INFO = "info";
    public static final String TOPIC = "topic";
    public static final String SPEAKER_ID = "speaker_id";
    public static final String DOR1_IMG = "dor1_img";
    public static final String DOR2_IMG = "dor2_img";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String CRATE_EVENT = ""
            + "CREATE TABLE " + TABLE + " ("
            + ID + " INTEGER NOT NULL,"
            + NAME + " TEXT,"
            + IMG + " TEXT,"
            + ADDRESS + " TEXT,"
            + ROOM + " TEXT,"
            + INFO + " TEXT,"
            + TOPIC + " TEXT,"
            + SPEAKER_ID + " INTEGER NOT NULL,"
            + DOR1_IMG + " TEXT,"
            + DOR2_IMG + " TEXT,"
            + LATITUDE + " TEXT,"
            + LONGITUDE + " TEXT"
            + ")";

    public static long insertEventList(BriteDatabase db, List<Event> events){
        long pos = -1;

        for(Event event : events) {
            ContentValues values = new EventDao.Builder()
                    .id(event.getId())
                    .name(event.getName())
                    .img(event.getImg())
                    .address(event.getAddress())
                    .room(event.getRoom())
                    .info(event.getInfo())
                    .topic(event.getTopic())
                    .speakerId(event.getSpeakerId())
                    .dor1Img(event.getDor1Img())
                    .dor2Img(event.getDor2Img())
                    .latitude(event.getLatitude())
                    .longitude(event.getLongitude())
                    .build();

            pos = db.insert(TABLE, CONFLICT_FAIL, values);
        }

        return pos;
    }

    public static Observable<List<Event>> getAllEvents(BriteDatabase db){
        Observable<SqlBrite.Query> dbQuery = db.createQuery(TABLE, "SELECT * FROM " + TABLE);
        return dbQuery.map(query -> {
            ArrayList<Event> events = new ArrayList<>();

            Cursor cursor = query.run();
            if (cursor == null){
                return events;
            }

            while(cursor.moveToNext()){
                events.add(new Event(cursor.getInt(cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(IMG)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(ROOM)),
                        cursor.getString(cursor.getColumnIndex(INFO)),
                        cursor.getString(cursor.getColumnIndex(TOPIC)),
                        cursor.getInt(cursor.getColumnIndex(SPEAKER_ID)),
                        cursor.getString(cursor.getColumnIndex(DOR1_IMG)),
                        cursor.getString(cursor.getColumnIndex(DOR2_IMG)),
                        cursor.getString(cursor.getColumnIndex(LATITUDE)),
                        cursor.getString(cursor.getColumnIndex(LONGITUDE))));
            }

            cursor.close();
            return events;
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

        public Builder img(String img){
            values.put(IMG, img);
            return this;
        }

        public Builder address(String address){
            values.put(ADDRESS, address);
            return this;
        }

        public Builder room(String room){
            values.put(ROOM, room);
            return this;
        }

        public Builder info(String info){
            values.put(INFO, info);
            return this;
        }

        public Builder topic(String topic){
            values.put(TOPIC, topic);
            return this;
        }

        public Builder speakerId(Integer speakerID){
            values.put(SPEAKER_ID, speakerID);
            return this;
        }

        public Builder dor1Img(String dor1Img){
            values.put(DOR1_IMG, dor1Img);
            return this;
        }

        public Builder dor2Img(String dor2Img){
            values.put(DOR2_IMG, dor2Img);
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
