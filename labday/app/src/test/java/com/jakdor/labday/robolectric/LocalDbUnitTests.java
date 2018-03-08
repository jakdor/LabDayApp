package com.jakdor.labday.robolectric;

import android.content.ContentValues;
import android.database.Cursor;

import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.dao.EventDao;
import com.jakdor.labday.common.dao.MapOtherDao;
import com.jakdor.labday.common.dao.PathDao;
import com.jakdor.labday.common.dao.SpeakerDao;
import com.jakdor.labday.common.dao.TimetableDao;
import com.jakdor.labday.common.localdb.LocalDbHandler;
import com.jakdor.labday.common.model.Event;
import com.jakdor.labday.common.model.MapOther;
import com.jakdor.labday.common.model.Path;
import com.jakdor.labday.common.model.Speaker;
import com.jakdor.labday.common.model.Timetable;

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
import java.util.Random;

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
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            mapOthers.add(new MapOther(random.nextInt(100), random.nextInt(),
                    TestUtils.randomString(), TestUtils.randomString(), TestUtils.randomString(),
                    TestUtils.randomString(), TestUtils.randomString()));
        }

        MapOtherDao.insertMapOtherList(localDbHandler.getDb(), mapOthers);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + MapOtherDao.TABLE);

        Assert.assertEquals(mapOthers.size(), cursor.getCount());

        for(MapOther mapOther : mapOthers){
            Assert.assertTrue(cursor.moveToNext());
            Assert.assertEquals(mapOther.getId().intValue(), cursor.getInt(cursor.getColumnIndex(MapOtherDao.ID)));
            Assert.assertEquals(mapOther.getType().intValue(), cursor.getInt(cursor.getColumnIndex(MapOtherDao.TYPE)));
            Assert.assertEquals(mapOther.getName(), cursor.getString(cursor.getColumnIndex(MapOtherDao.NAME)));
            Assert.assertEquals(mapOther.getInfo(), cursor.getString(cursor.getColumnIndex(MapOtherDao.INFO)));
            Assert.assertEquals(mapOther.getImg(), cursor.getString(cursor.getColumnIndex(MapOtherDao.IMG)));
            Assert.assertEquals(mapOther.getLatitude(), cursor.getString(cursor.getColumnIndex(MapOtherDao.LATITUDE)));
            Assert.assertEquals(mapOther.getLongitude(), cursor.getString(cursor.getColumnIndex(MapOtherDao.LONGITUDE)));
        }

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllMapOthersTest() throws Exception{
        ArrayList<MapOther> mapOthers = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            mapOthers.add(new MapOther(random.nextInt(100), random.nextInt(),
                    TestUtils.randomString(), TestUtils.randomString(), TestUtils.randomString(),
                    TestUtils.randomString(), TestUtils.randomString()));
        }

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
            for(int i = 0; i < mapOthers.size(); ++i){
                Assert.assertEquals(mapOthers.get(i), dbMapOthers.get(i));
            }
            return true;
        });

        testObserver.onComplete();
    }

    @Test
    public void insertPathListTest() throws Exception{
        ArrayList<Path> paths = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            paths.add(new Path(random.nextInt(100), TestUtils.randomString(),
                    TestUtils.randomString(), random.nextBoolean()));
        }

        PathDao.insertPathList(localDbHandler.getDb(), paths);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + PathDao.TABLE);

        Assert.assertEquals(paths.size(), cursor.getCount());

        for(Path path : paths){
            Assert.assertTrue(cursor.moveToNext());
            Assert.assertEquals(path.getId().intValue(), cursor.getInt(cursor.getColumnIndex(PathDao.ID)));
            Assert.assertEquals(path.getName(), cursor.getString(cursor.getColumnIndex(PathDao.NAME)));
            Assert.assertEquals(path.getInfo(), cursor.getString(cursor.getColumnIndex(PathDao.INFO)));
            Assert.assertEquals(path.getActive(), cursor.getInt(cursor.getColumnIndex(PathDao.ACTIVE)) == 1);
        }

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllPathsTest() throws Exception{
        ArrayList<Path> paths = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            paths.add(new Path(random.nextInt(100), TestUtils.randomString(),
                    TestUtils.randomString(), random.nextBoolean()));
        }

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
            for(int i = 0; i < paths.size(); ++i){
                Assert.assertEquals(paths.get(i), dbPaths.get(i));
            }
            return true;
        });

        testObserver.onComplete();
    }

    @Test
    public void insertSpeakersListTest() throws Exception{
        ArrayList<Speaker> speakers = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            speakers.add(new Speaker(random.nextInt(100),
                    TestUtils.randomString(), TestUtils.randomString(), TestUtils.randomString()));
        }

        SpeakerDao.insertSpeakerList(localDbHandler.getDb(), speakers);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + SpeakerDao.TABLE);

        Assert.assertEquals(speakers.size(), cursor.getCount());

        for(Speaker speaker : speakers){
            Assert.assertTrue(cursor.moveToNext());
            Assert.assertEquals(speaker.getId().intValue(), cursor.getInt(cursor.getColumnIndex(SpeakerDao.ID)));
            Assert.assertEquals(speaker.getSpeakerName(), cursor.getString(cursor.getColumnIndex(SpeakerDao.NAME)));
            Assert.assertEquals(speaker.getSpeakerInfo(), cursor.getString(cursor.getColumnIndex(SpeakerDao.INFO)));
            Assert.assertEquals(speaker.getSpeakerImg(), cursor.getString(cursor.getColumnIndex(SpeakerDao.IMG)));
        }

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllSpeakers() throws Exception{
        ArrayList<Speaker> speakers = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            speakers.add(new Speaker(random.nextInt(100),
                    TestUtils.randomString(), TestUtils.randomString(), TestUtils.randomString()));
        }

        for(Speaker speaker : speakers) {
            ContentValues values = new SpeakerDao.Builder()
                    .id(speaker.getId())
                    .name(speaker.getSpeakerName())
                    .info(speaker.getSpeakerInfo())
                    .img(speaker.getSpeakerImg())
                    .build();

           localDbHandler.getDb().insert(SpeakerDao.TABLE, CONFLICT_FAIL, values);
        }

        TestObserver<List<Speaker>> testObserver
                = SpeakerDao.getAllSpeakers(localDbHandler.getDb()).test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();
        testObserver.assertValue(dbSpeakers -> {
            Assert.assertEquals(speakers.size(), dbSpeakers.size());
            for(int i = 0; i < speakers.size(); ++i){
                Assert.assertEquals(speakers.get(i), dbSpeakers.get(i));
            }
            return true;
        });

        testObserver.onComplete();
    }

    @Test
    public void insertTimetableList() throws Exception{
        ArrayList<Timetable> timetables = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            timetables.add(new Timetable(random.nextInt(100), random.nextInt(100),
                    random.nextInt(100), random.nextInt(), random.nextInt()));
        }

        TimetableDao.insertTimetableList(localDbHandler.getDb(), timetables);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + TimetableDao.TABLE);

        Assert.assertEquals(timetables.size(), cursor.getCount());

        for(Timetable timetable : timetables){
            Assert.assertTrue(cursor.moveToNext());
            Assert.assertEquals(timetable.getId().intValue(), cursor.getInt(cursor.getColumnIndex(TimetableDao.ID)));
            Assert.assertEquals(timetable.getPathId().intValue(), cursor.getInt(cursor.getColumnIndex(TimetableDao.PATH_ID)));
            Assert.assertEquals(timetable.getEventId().intValue(), cursor.getInt(cursor.getColumnIndex(TimetableDao.EVENT_ID)));
            Assert.assertEquals(timetable.getTimeStart().intValue(), cursor.getInt(cursor.getColumnIndex(TimetableDao.TIME_START)));
            Assert.assertEquals(timetable.getTimeEnd().intValue(), cursor.getInt(cursor.getColumnIndex(TimetableDao.TIME_END)));
        }

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllTimetablesTest() throws Exception{
        ArrayList<Timetable> timetables = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            timetables.add(new Timetable(random.nextInt(100), random.nextInt(100),
                    random.nextInt(100), random.nextInt(), random.nextInt()));
        }

        for(Timetable timetable : timetables) {
            ContentValues values = new TimetableDao.Builder()
                    .id(timetable.getId())
                    .pathId(timetable.getPathId())
                    .eventId(timetable.getEventId())
                    .timeStart(timetable.getTimeStart())
                    .timeEnd(timetable.getTimeEnd())
                    .build();

            localDbHandler.getDb().insert(TimetableDao.TABLE, CONFLICT_FAIL, values);
        }

        TestObserver<List<Timetable>> testObserver =
                TimetableDao.getAllTimetables(localDbHandler.getDb()).test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();
        testObserver.assertValue(dbTimetables -> {
            Assert.assertEquals(timetables.size(), dbTimetables.size());
            for(int i = 0; i < timetables.size(); ++i){
                Assert.assertEquals(timetables.get(i), dbTimetables.get(i));
            }
            return true;
        });

        testObserver.onComplete();
    }

    @Test
    public void insertEventList() throws Exception{
        ArrayList<Event> events = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            events.add(new Event(random.nextInt(100), TestUtils.randomString(),
                    TestUtils.randomString(),TestUtils.randomString(), TestUtils.randomString(),
                    TestUtils.randomString(), TestUtils.randomString(), random.nextInt(100),
                    TestUtils.randomString(), TestUtils.randomString(), TestUtils.randomString(),
                    TestUtils.randomString()));
        }

        EventDao.insertEventList(localDbHandler.getDb(), events);

        Cursor cursor = localDbHandler.getDb().query("SELECT * FROM " + EventDao.TABLE);

        Assert.assertEquals(events.size(), cursor.getCount());

        for(Event event : events){
            Assert.assertTrue(cursor.moveToNext());
            Assert.assertEquals(event.getId().intValue(), cursor.getInt(cursor.getColumnIndex(EventDao.ID)));
            Assert.assertEquals(event.getName(), cursor.getString(cursor.getColumnIndex(EventDao.NAME)));
            Assert.assertEquals(event.getImg(), cursor.getString(cursor.getColumnIndex(EventDao.IMG)));
            Assert.assertEquals(event.getAddress(), cursor.getString(cursor.getColumnIndex(EventDao.ADDRESS)));
            Assert.assertEquals(event.getRoom(), cursor.getString(cursor.getColumnIndex(EventDao.ROOM)));
            Assert.assertEquals(event.getInfo(), cursor.getString(cursor.getColumnIndex(EventDao.INFO)));
            Assert.assertEquals(event.getTopic(), cursor.getString(cursor.getColumnIndex(EventDao.TOPIC)));
            Assert.assertEquals(event.getSpeakerId().intValue(), cursor.getInt(cursor.getColumnIndex(EventDao.SPEAKER_ID)));
            Assert.assertEquals(event.getDor1Img(), cursor.getString(cursor.getColumnIndex(EventDao.DOR1_IMG)));
            Assert.assertEquals(event.getDor2Img(), cursor.getString(cursor.getColumnIndex(EventDao.DOR2_IMG)));
            Assert.assertEquals(event.getLatitude(), cursor.getString(cursor.getColumnIndex(EventDao.LATITUDE)));
            Assert.assertEquals(event.getLongitude(), cursor.getString(cursor.getColumnIndex(EventDao.LONGITUDE)));
        }

        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void getAllEvents() throws Exception{
        ArrayList<Event> events = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < random.nextInt(10) + 1; ++i){
            events.add(new Event(random.nextInt(100), TestUtils.randomString(),
                    TestUtils.randomString(),TestUtils.randomString(), TestUtils.randomString(),
                    TestUtils.randomString(), TestUtils.randomString(), random.nextInt(100),
                    TestUtils.randomString(), TestUtils.randomString(), TestUtils.randomString(),
                    TestUtils.randomString()));
        }

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

            localDbHandler.getDb().insert(EventDao.TABLE, CONFLICT_FAIL, values);
        }

        TestObserver<List<Event>> testObserver =
                EventDao.getAllEvents(localDbHandler.getDb()).test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();
        testObserver.assertValue(dbEvents -> {
            Assert.assertEquals(events.size(), dbEvents.size());
            for(int i = 0; i <  events.size(); ++i){
                Assert.assertEquals(events.get(i), dbEvents.get(i));
            }
            return true;
        });

        testObserver.onComplete();
    }

}
