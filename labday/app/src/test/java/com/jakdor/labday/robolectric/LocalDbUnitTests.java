package com.jakdor.labday.robolectric;

import android.database.Cursor;

import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.dao.PathDao;
import com.jakdor.labday.common.localdb.LocalDbHandler;
import com.jakdor.labday.common.model.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Unit tests for local db
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class LocalDbUnitTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private LocalDbHandler localDbHandler;

    @Before
    public void setUp() throws Exception{
        localDbHandler = new LocalDbHandler(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() throws Exception{
        localDbHandler.dropDb();
    }

    @Test
    public void insertPathListTest() throws Exception{
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(new Path(1, "testName", "info", 1));
        paths.add(new Path(2, "testName2", "info2", 0));

        PathDao.insertPathList(localDbHandler.getDb(), paths);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + PathDao.TABLE);
        cursor.moveToFirst();

        Assert.assertEquals(paths.get(0).getId().intValue(), cursor.getInt(cursor.getColumnIndex(PathDao.ID)));
        Assert.assertEquals(paths.get(0).getName(), cursor.getString(cursor.getColumnIndex(PathDao.NAME)));
        Assert.assertEquals(paths.get(0).getInfo(), cursor.getString(cursor.getColumnIndex(PathDao.INFO)));
        Assert.assertEquals(paths.get(0).getActive().intValue(), cursor.getInt(cursor.getColumnIndex(PathDao.ACTIVE)));
        cursor.moveToNext();
        Assert.assertEquals(paths.get(1).getId().intValue(), cursor.getInt(cursor.getColumnIndex(PathDao.ID)));
        Assert.assertEquals(paths.get(1).getName(), cursor.getString(cursor.getColumnIndex(PathDao.NAME)));
        Assert.assertEquals(paths.get(1).getInfo(), cursor.getString(cursor.getColumnIndex(PathDao.INFO)));
        Assert.assertEquals(paths.get(1).getActive().intValue(), cursor.getInt(cursor.getColumnIndex(PathDao.ACTIVE)));

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllPathsTest() throws Exception{
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(new Path(1, "testName", "info", 1));
        paths.add(new Path(2, "testName2", "info2", 0));

        PathDao.insertPathList(localDbHandler.getDb(), paths);

        TestObserver<List<Path>> testObserver = PathDao.getAllPaths(localDbHandler.getDb()).test();

        testObserver.awaitCount(1);
        testObserver.assertNoErrors();
        testObserver.assertValue(dbPaths -> {
            Assert.assertEquals(paths.size(), dbPaths.size());
            Assert.assertEquals(paths.get(0), dbPaths.get(0));
            Assert.assertEquals(paths.get(1), dbPaths.get(1));
            return true;
        });
    }

}
