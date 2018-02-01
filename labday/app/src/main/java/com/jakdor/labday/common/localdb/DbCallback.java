package com.jakdor.labday.common.localdb;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.util.Log;

import com.jakdor.labday.common.dao.PathDao;

public class DbCallback extends SupportSQLiteOpenHelper.Callback {

    private static final String CLASS_TAG = "DbCallback";

    private static final int VERSION = 1;

    /**
     * Creates a new Callback to get database lifecycle events.
     */
    public DbCallback() {
        super(VERSION);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        Log.i(CLASS_TAG, "local db init");
        db.execSQL(PathDao.CREATE_PATH);
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

