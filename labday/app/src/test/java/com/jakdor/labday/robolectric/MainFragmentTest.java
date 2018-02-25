package com.jakdor.labday.robolectric;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.Path;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.view.ui.MainFragment;
import com.jakdor.labday.viewmodel.MainViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class MainFragmentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MainFragment mainFragment;

    private MainViewModel mainViewModel;
    private MutableLiveData<RxResponse<AppData>> appData;

    private void initMockData(){
        appData = new MutableLiveData<>();
        AppData data = new AppData();
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(new Path(1,"dummyNameWrong", "PathInfoWrong", false));
        paths.add(new Path(2,"dummyName", "PathInfo", true));
        data.setPaths(paths);
        appData.setValue(RxResponse.success(data));
    }

    @Before
    public void setUp() throws Exception{
        initMockData();
        mainFragment = new MainFragment();

        mainViewModel = Mockito.mock(MainViewModel.class);
        Mockito.when(mainViewModel.getResponse()).thenReturn(appData);

        mainFragment.setViewModel(mainViewModel);
    }

    /**
     * Layout inflation test
     */
    @Test
    public void viewTest() throws Exception{
        startFragment(mainFragment);
        mainFragment.getBinding().executePendingBindings();

        View view = mainFragment.getView();
        Assert.assertNotNull(view);

        ImageView logo = view.findViewById(R.id.menu_logo);
        Assert.assertNotNull(logo);

        View[] cards = {
                view.findViewById(R.id.menu_timetable),
                view.findViewById(R.id.menu_map),
                view.findViewById(R.id.menu_media),
                view.findViewById(R.id.menu_info)
        };

        for(View card : cards){
            Assert.assertNotNull(card);

            ImageView icon = card.findViewById(R.id.menu_card_icon);
            ImageView image = card.findViewById(R.id.menu_card_image);
            TextView title = card.findViewById(R.id.menu_card_title);
            TextView path = card.findViewById(R.id.menu_card_path_chip);

            Assert.assertNotNull(icon);
            Assert.assertNotNull(image);
            Assert.assertNotNull(title);
            Assert.assertNotNull(path);
        }
    }

    /**
     * Test path chip visibility parameter and correct path info
     */
    @Test
    public void pathChipTest() throws Exception{
        startFragment(mainFragment);
        mainFragment.getBinding().executePendingBindings();

        View view = mainFragment.getView();
        Assert.assertNotNull(view);

        View[] cards = {
                view.findViewById(R.id.menu_timetable),
                view.findViewById(R.id.menu_map),
                view.findViewById(R.id.menu_media),
                view.findViewById(R.id.menu_info)
        };

        for(int i = 0; i < cards.length; ++i){
            if(i == 0){
                TextView pathChip = cards[i].findViewById(R.id.menu_card_path_chip);
                Assert.assertNotNull(pathChip);
                Assert.assertEquals(View.VISIBLE, pathChip.getVisibility());
                Assert.assertEquals("dummyName", pathChip.getText().toString());
            }
            else {
                TextView pathChip = cards[i].findViewById(R.id.menu_card_path_chip);
                Assert.assertNotNull(pathChip);
                Assert.assertEquals(View.GONE, pathChip.getVisibility());
            }
        }
    }
}
