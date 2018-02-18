package com.jakdor.labday.robolectric;

import android.arch.lifecycle.MutableLiveData;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.model.AppData;
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

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class TimetableFragmentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String appDataJsonPath = "app/src/test/assets/api/app_data.json";

    private TimetableFragment timetableFragment;

    private TimetableViewModel viewModel;
    private MutableLiveData<RxResponse<AppData>> appData;

    private void initMocks() throws Exception{
        appData = new MutableLiveData<>();
        Gson gson = new Gson();
        AppData data = gson.fromJson(TestUtils.readFile(appDataJsonPath), AppData.class);
        appData.setValue(RxResponse.success(data));
    }

    @Before
    public void setUp() throws Exception{
        initMocks();
        timetableFragment = TimetableFragment.newInstance(2);

        viewModel = Mockito.mock(TimetableViewModel.class);
        Mockito.when(viewModel.getResponse()).thenReturn(appData);

        timetableFragment.setViewModel(viewModel);
    }

    @Test
    public void titleBarTest() throws Exception{
        startFragment(timetableFragment);
        timetableFragment.getBinding().executePendingBindings();

        View view = timetableFragment.getView();
        Assert.assertNotNull(view);

        Toolbar toolbar = view.findViewById(R.id.timetable_title_bar);
        Assert.assertEquals("path info", toolbar.getTitle().toString());
    }

}
