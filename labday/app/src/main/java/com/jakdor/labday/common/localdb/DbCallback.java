package com.jakdor.labday.common.localdb;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.jakdor.labday.common.dao.EventDao;
import com.jakdor.labday.common.dao.MapOtherDao;
import com.jakdor.labday.common.dao.PathDao;
import com.jakdor.labday.common.dao.SpeakerDao;
import com.jakdor.labday.common.dao.TimetableDao;

import timber.log.Timber;

public class DbCallback extends SupportSQLiteOpenHelper.Callback {

    private static final int VERSION = 1;

    /**
     * Creates a new Callback to get database lifecycle events.
     */
    public DbCallback() {
        super(VERSION);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        Timber.i("local db init");
        db.execSQL(MapOtherDao.CREATE_MAP_OTHER);
        db.execSQL(PathDao.CREATE_PATH);
        db.execSQL(SpeakerDao.CREATE_SPEAKER);
        db.execSQL(TimetableDao.CREATE_TIMETABLE);
        db.execSQL(EventDao.CRATE_EVENT);
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

