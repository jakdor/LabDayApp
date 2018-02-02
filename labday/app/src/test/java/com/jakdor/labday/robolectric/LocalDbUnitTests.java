package com.jakdor.labday.robolectric;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.dao.MapOtherDao;
import com.jakdor.labday.common.dao.PathDao;
import com.jakdor.labday.common.localdb.LocalDbHandler;
import com.jakdor.labday.common.model.MapOther;
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

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

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
    public void insetMapOtherListTest() throws Exception{
        ArrayList<MapOther> mapOthers = new ArrayList<>();
        mapOthers.add(new MapOther(1, 1, "testName", "info",
                "imgUrl", "43.232", "23.124"));
        mapOthers.add(new MapOther(2, 2, "testName2", "info2",
                "imgUrl2", "433.232", "223.124"));

        MapOtherDao.insertMapOtherList(localDbHandler.getDb(), mapOthers);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + MapOtherDao.TABLE);
        cursor.moveToFirst();

        Assert.assertEquals(2, cursor.getCount());

        Assert.assertEquals(mapOthers.get(0).getId().intValue(), cursor.getInt(cursor.getColumnIndex(MapOtherDao.ID)));
        Assert.assertEquals(mapOthers.get(0).getType().intValue(), cursor.getInt(cursor.getColumnIndex(MapOtherDao.TYPE)));
        Assert.assertEquals(mapOthers.get(0).getName(), cursor.getString(cursor.getColumnIndex(MapOtherDao.NAME)));
        Assert.assertEquals(mapOthers.get(0).getInfo(), cursor.getString(cursor.getColumnIndex(MapOtherDao.INFO)));
        Assert.assertEquals(mapOthers.get(0).getImg(), cursor.getString(cursor.getColumnIndex(MapOtherDao.IMG)));
        Assert.assertEquals(mapOthers.get(0).getLatitude(), cursor.getString(cursor.getColumnIndex(MapOtherDao.LATITUDE)));
        Assert.assertEquals(mapOthers.get(0).getLongitude(), cursor.getString(cursor.getColumnIndex(MapOtherDao.LONGITUDE)));
        cursor.moveToNext();
        Assert.assertEquals(mapOthers.get(1).getId().intValue(), cursor.getInt(cursor.getColumnIndex(MapOtherDao.ID)));
        Assert.assertEquals(mapOthers.get(1).getType().intValue(), cursor.getInt(cursor.getColumnIndex(MapOtherDao.TYPE)));
        Assert.assertEquals(mapOthers.get(1).getName(), cursor.getString(cursor.getColumnIndex(MapOtherDao.NAME)));
        Assert.assertEquals(mapOthers.get(1).getInfo(), cursor.getString(cursor.getColumnIndex(MapOtherDao.INFO)));
        Assert.assertEquals(mapOthers.get(1).getImg(), cursor.getString(cursor.getColumnIndex(MapOtherDao.IMG)));
        Assert.assertEquals(mapOthers.get(1).getLatitude(), cursor.getString(cursor.getColumnIndex(MapOtherDao.LATITUDE)));
        Assert.assertEquals(mapOthers.get(1).getLongitude(), cursor.getString(cursor.getColumnIndex(MapOtherDao.LONGITUDE)));

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllMapOthersTest() throws Exception{
        ArrayList<MapOther> mapOthers = new ArrayList<>();
        mapOthers.add(new MapOther(1, 1, "testName", "info",
                "imgUrl", "43.232", "23.124"));
        mapOthers.add(new MapOther(2, 2, "testName2", "info2",
                "imgUrl2", "433.232", "223.124"));

        for(MapOther mapOther : mapOthers) {
            ContentValues values = new MapOtherDao.Builder()
                    .id(mapOther.getId())
                    .type(mapOther.getType())
                    .name(mapOther.getName())
                    .info(mapOther.getInfo())
                    .img(mapOther.getImg())
                    .latitude(mapOther.getLatitude())
                    .longitude(mapOther.getLongitude())
                    .build();

            localDbHandler.getDb().insert(MapOtherDao.TABLE, CONFLICT_FAIL, values);
        }


        TestObserver<List<MapOther>> testObserver = MapOtherDao.getMapOthers(localDbHandler.getDb()).test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(dbMapOthers -> {
            Assert.assertEquals(mapOthers.size(), dbMapOthers.size());
            Assert.assertEquals(mapOthers.get(0), dbMapOthers.get(0));
            Assert.assertEquals(mapOthers.get(1), dbMapOthers.get(1));
            return true;
        });

        testObserver.onComplete();
    }

    @Test
    public void insertPathListTest() throws Exception{
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(new Path(1, "testName", "info", 1));
        paths.add(new Path(2, "testName2", "info2", 0));

        PathDao.insertPathList(localDbHandler.getDb(), paths);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + PathDao.TABLE);
        cursor.moveToFirst();

        Assert.assertEquals(2, cursor.getCount());

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

        for(Path path : paths) {
            ContentValues values = new PathDao.Builder()
                    .id(path.getId())
                    .name(path.getName())
                    .info(path.getInfo())
                    .active(path.getActive())
                    .build();

            localDbHandler.getDb().insert(PathDao.TABLE, CONFLICT_FAIL, values);
        }

        TestObserver<List<Path>> testObserver = PathDao.getAllPaths(localDbHandler.getDb()).test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(dbPaths -> {
            Assert.assertEquals(paths.size(), dbPaths.size());
            Assert.assertEquals(paths.get(0), dbPaths.get(0));
            Assert.assertEquals(paths.get(1), dbPaths.get(1));
            return true;
        });

        testObserver.onComplete();
    }

}
