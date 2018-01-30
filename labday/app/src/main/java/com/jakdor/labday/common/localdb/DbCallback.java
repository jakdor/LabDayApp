package com.jakdor.labday.common.localdb;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.util.Log;

public class DbCallback extends SupportSQLiteOpenHelper.Callback {

    private static final String CLASS_TAG = "DbCallback";

    private static final int VERSION = 1;

    private static final String CREATE_PATH = ""
            + "CREATE TABLE " + "path" + "("
            + "_id" + " INTEGER NOT NULL PRIMARY KEY,"
            + "name" + " TEXT,"
            + "info" + " TEXT,"
            + "active" + " INTEGER NOT NULL"
            + ")";

    /**
     * Creates a new Callback to get database lifecycle events.
     */
    public DbCallback() {
        super(VERSION);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        Log.i(CLASS_TAG, "local db init");
        db.execSQL(CREATE_PATH);
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

