package com.jakdor.labday.common.localdb;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.util.Log;

import com.jakdor.labday.common.dao.MapOtherDao;
import com.jakdor.labday.common.dao.PathDao;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import io.reactivex.schedulers.Schedulers;

/**
 * Handles all communication between local db and project repository
 */
public class LocalDbHandler {

    private static final String CLASS_TAG = "LocalDbHandler";

    private static final String DB_NAME = "localDb.db";

    private Application app;
    private BriteDatabase db;

    /**
     * Init local db connection/create
     * @param application required by SupportSQLiteOpenHelper
     */
    public LocalDbHandler(Application application){
        this.app = application;

        SqlBrite sqlBrite = new SqlBrite.Builder()
                .logger(message -> Log.d("LocalDB", message))
                .build();

        SupportSQLiteOpenHelper.Configuration configuration
                = SupportSQLiteOpenHelper.Configuration.builder(application)
                .name(DB_NAME)
                .callback(new DbCallback())
                .build();

        SupportSQLiteOpenHelper.Factory factory = new FrameworkSQLiteOpenHelperFactory();
        SupportSQLiteOpenHelper helper = factory.create(configuration);
        db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
    }

    public BriteDatabase getDb() {
        return db;
    }

    /**
     * Drops local database
     */
    public void dropDb(){
        closeDb();
        app.getApplicationContext().deleteDatabase(DB_NAME);
        Log.i(CLASS_TAG, "local db - dropped");
    }

    /**
     * Delete all entries from all tables
     */
    public void purgeDb(){
        MapOtherDao.deleteAll(db);
        PathDao.deleteAll(db);
        Log.i(CLASS_TAG, "local db - cleared all entries");
    }

    public void closeDb(){
        db.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeDb();
    }
}
