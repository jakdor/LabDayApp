package com.jakdor.labday.common.localdb;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

public class LocalDbHandler {

    private static final String CLASS_TAG = "LocalDbHandler";

    private BriteDatabase db;

    public LocalDbHandler(Application application){
        SqlBrite sqlBrite = new SqlBrite.Builder()
                .logger(message -> Log.d("LocalDB", message))
                .build();

        SupportSQLiteOpenHelper.Configuration configuration
                = SupportSQLiteOpenHelper.Configuration.builder(application)
                .name("localDb.db")
                .callback(new DbCallback())
                .build();

        SupportSQLiteOpenHelper.Factory factory = new FrameworkSQLiteOpenHelperFactory();
        SupportSQLiteOpenHelper helper = factory.create(configuration);
        db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());

        //insert test
        ContentValues values = new ContentValues();
        values.put("name", "dummyName");
        values.put("info", "dummy info");
        values.put("active", 2);

        db.insert("path", CONFLICT_FAIL, values);

        //select test
        Observable<SqlBrite.Query> users = db.createQuery("path", "SELECT * FROM path");
        users.subscribe(query -> {
            Cursor cursor = query.run();

            cursor.moveToFirst();
            Log.d(CLASS_TAG, "Test local DB:");
            Log.d(CLASS_TAG, cursor.getString(cursor.getColumnIndex("name")));
        });
    }

}
