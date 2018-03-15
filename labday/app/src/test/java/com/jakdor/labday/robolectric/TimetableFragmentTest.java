package com.jakdor.labday.robolectric;

import android.arch.lifecycle.MutableLiveData;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.Path;
import com.jakdor.labday.common.model.Timetable;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.view.ui.TimetableFragment;
import com.jakdor.labday.viewmodel.TimetableViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class TimetableFragmentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String appDataJsonPath = "app/src/test/assets/api/app_data.json";

    private TimetableFragment timetableFragment;

    private TimetableViewModel viewModel;
    private MutableLiveData<RxResponse<AppData>> appData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>();
    private AppData data;

    private void initMocks() throws Exception{
        Gson gson = new Gson();
        data = gson.fromJson(TestUtils.readFile(appDataJsonPath), AppData.class);
        appData.setValue(RxResponse.success(data));
        loadingStatus.setValue(false);
    }

    @Before
    public void setUp() throws Exception{
        initMocks();
        timetableFragment = TimetableFragment.newInstance(2);
        timetableFragment.setTestMode();

        viewModel = Mockito.mock(TimetableViewModel.class);
        Mockito.when(viewModel.getResponse()).thenReturn(appData);
        Mockito.when(viewModel.getLoadingStatus()).thenReturn(loadingStatus);

        timetableFragment.setViewModel(viewModel);
    }

    /**
     * Layout inflation test
     */
    @Test
    public void viewTest() throws Exception{
        startFragment(timetableFragment);
        View view = timetableFragment.getView();
        Assert.assertNotNull(view);

        AppBarLayout appBarLayout = view.findViewById(R.id.appbar_container);
        Toolbar toolbar = view.findViewById(R.id.timetable_title_bar);
        NestedScrollView nestedScrollView = view.findViewById(R.id.timetable_nested_scroll_view);
        RecyclerView recyclerView = view.findViewById(R.id.timetable_recycler_view);

        Assert.assertNotNull(appBarLayout);
        Assert.assertNotNull(toolbar);
        Assert.assertNotNull(nestedScrollView);
        Assert.assertNotNull(recyclerView);
    }

    /**
     * Test if correct title bar set
     */
    @Test
    public void titleBarTest() throws Exception{
        String expectedTitle = TestUtils.randomString();
        Path path = data.getPaths().get(1);
        path.setInfo(expectedTitle);
        data.getPaths().add(1, path);
        appData.setValue(RxResponse.success(data));
        startFragment(timetableFragment);
        timetableFragment.getBinding().executePendingBindings();

        View view = timetableFragment.getView();
        Assert.assertNotNull(view);

        Toolbar toolbar = view.findViewById(R.id.timetable_title_bar);
        Assert.assertEquals(expectedTitle, toolbar.getTitle().toString());
    }

    /**
     * Argument set in newInstance() method test
     */
    @Test
    public void argumentsTest() throws Exception{
        int args = new Random().nextInt();
        timetableFragment = TimetableFragment.newInstance(args);
        timetableFragment.setViewModel(viewModel);
        timetableFragment.setTestMode();
        startFragment(timetableFragment);

        Assert.assertNotNull(timetableFragment.getArguments());
        Assert.assertEquals(args, timetableFragment.getArguments().getInt("path"));
    }

    /**
     * Test if items in recycler view are displayed in correct order
     */
    @Test
    public void recyclerViewCorrectOrderTest() throws Exception{
        startFragment(timetableFragment);
        View view = timetableFragment.getView();
        Assert.assertNotNull(view);
        RecyclerView recyclerView = view.findViewById(R.id.timetable_recycler_view);

        recyclerView.measure(0,0);
        recyclerView.layout(0,0,100,1000);
        ArrayList<Timetable> expectedTimetableList = new ArrayList<>();
        for(Timetable timetable : data.getTimetables()){
            if(timetable.getPathId() == 2){
                expectedTimetableList.add(timetable);
            }
        }
        Collections.sort(expectedTimetableList, (t1, t2) -> t1.getTimeStart() - t2.getTimeStart());

        Assert.assertEquals(expectedTimetableList.size(), recyclerView.getAdapter().getItemCount());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        for(int i = 0; i < expectedTimetableList.size(); ++i){
            Date start = new Date((long)expectedTimetableList.get(i).getTimeStart()*1000);
            Date end = new Date((long)expectedTimetableList.get(i).getTimeEnd()*1000);

            View item = recyclerView.findViewHolderForAdapterPosition(i).itemView;
            TextView startView = item.findViewById(R.id.timetable_item_time_start);
            TextView endView = item.findViewById(R.id.timetable_item_time_end);

            Assert.assertEquals(simpleDateFormat.format(start), startView.getText().toString());
            Assert.assertEquals(simpleDateFormat.format(end), endView.getText().toString());
        }
    }
}
