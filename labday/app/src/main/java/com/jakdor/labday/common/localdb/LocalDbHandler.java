package com.jakdor.labday.common.localdb;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.util.Log;

import com.jakdor.labday.common.dao.EventDao;
import com.jakdor.labday.common.dao.MapOtherDao;
import com.jakdor.labday.common.dao.PathDao;
import com.jakdor.labday.common.dao.SpeakerDao;
import com.jakdor.labday.common.dao.TimetableDao;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import java.sql.Time;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Handles all communication between local db and project repository
 *
 * - again a massive overkill just for sake of showing that I know how to write
 * SQL db and to use SQLBrite framework. Fastest approach would be to simply serialize and
 * cache AppData object.
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
     * Replaces any data in local db with given {@link AppData} object
     */
    public void pushAppDataToDb(AppData appData){
        purgeDb();
        EventDao.insertEventList(db, appData.getEvents());
        MapOtherDao.insertMapOtherList(db, appData.getMapOthers());
        PathDao.insertPathList(db, appData.getPaths());
        SpeakerDao.insertSpeakerList(db, appData.getSpeakers());
        TimetableDao.insertTimetableList(db, appData.getTimetables());
    }

    /**
     * Creates {@link RxResponse<AppData>} from local db zipped responses
     * @return RxResponse<AppData>
     */
    public Observable<RxResponse<AppData>> getAppDataFromDb(){
        return Observable.zip(EventDao.getAllEvents(db), MapOtherDao.getMapOthers(db),
                PathDao.getAllPaths(db), TimetableDao.getAllTimetables(db), SpeakerDao.getAllSpeakers(db),
                ((events, mapOthers, paths, timetables, speakers) -> {
                    AppData appData = new AppData(events, mapOthers, paths, timetables, speakers);
                    return RxResponse.successDb(appData);
                }))
                .onErrorReturn(RxResponse::error);
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
        EventDao.deleteAll(db);
        MapOtherDao.deleteAll(db);
        PathDao.deleteAll(db);
        SpeakerDao.deleteAll(db);
        TimetableDao.deleteAll(db);
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
