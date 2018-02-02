package com.jakdor.labday.common.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.common.model.Timetable;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

/**
 * DAO for Timetable model
 */
public abstract class TimetableDao {

    public static final String TABLE = "timetable";

    public static final String ID = "_id";
    public static final String PATH_ID = "path_id";
    public static final String EVENT_ID = "event_id";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";

    public static final String CREATE_TIMETABLE = ""
            + "CREATE TABLE " + TABLE + "("
            + ID + " INTEGER NOT NULL,"
            + PATH_ID + " INTEGER NOT NULL,"
            + EVENT_ID + " INTEGER NOT NULL,"
            + TIME_START + " INTEGER,"
            + TIME_END + " INTEGER"
            + ")";

    public static long insertTimetableList(BriteDatabase db, List<Timetable> timetables){
        long pos = -1;

        for(Timetable timetable : timetables) {
            ContentValues values = new TimetableDao.Builder()
                    .id(timetable.getId())
                    .pathId(timetable.getPathId())
                    .eventId(timetable.getEventId())
                    .timeStart(timetable.getTimeStart())
                    .timeEnd(timetable.getTimeEnd())
                    .build();

            pos = db.insert(TABLE, CONFLICT_FAIL, values);
        }

        return pos;
    }

    public static Observable<List<Timetable>> getAllTimetables(BriteDatabase db){
        Observable<SqlBrite.Query> dbQuery = db.createQuery(TABLE, "SELECT * FROM " + TABLE);
        return dbQuery.map(query -> {
            ArrayList<Timetable> timetables = new ArrayList<>();

            Cursor cursor = query.run();
            if (cursor == null){
                return timetables;
            }

            while(cursor.moveToNext()){
                timetables.add(new Timetable(cursor.getInt(cursor.getColumnIndex(ID)),
                        cursor.getInt(cursor.getColumnIndex(PATH_ID)),
                        cursor.getInt(cursor.getColumnIndex(EVENT_ID)),
                        cursor.getInt(cursor.getColumnIndex(TIME_START)),
                        cursor.getInt(cursor.getColumnIndex(TIME_END))));
            }

            cursor.close();
            return timetables;
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

        public Builder pathId(Integer pathId){
            values.put(PATH_ID, pathId);
            return this;
        }

        public Builder eventId(Integer eventId){
            values.put(EVENT_ID, eventId);
            return this;
        }

        public Builder timeStart(Integer timeStart){
            values.put(TIME_START, timeStart);
            return this;
        }

        public Builder timeEnd(Integer timeEnd){
            values.put(TIME_END, timeEnd);
            return this;
        }

        public ContentValues build(){
            return values;
        }
    }

}
