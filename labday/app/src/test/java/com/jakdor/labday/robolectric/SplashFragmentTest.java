package com.jakdor.labday.robolectric;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;
import android.widget.ImageView;

import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;
import com.jakdor.labday.view.ui.SplashFragment;
import com.jakdor.labday.viewmodel.SplashViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.jakdor.labday.rx.RxResponse.success;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class SplashFragmentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SplashFragment splashFragment;

    private MutableLiveData<RxResponse<AppData>> appData;

    private void initMockData(){
        appData = new MutableLiveData<>();
        appData.setValue(RxResponse.success(new AppData()));
    }

    @Before
    public void setUp(){
        initMockData();
        splashFragment = new SplashFragment();

        SplashViewModel splashViewModel = Mockito.mock(SplashViewModel.class);
        Mockito.when(splashViewModel.getResponse()).thenReturn(appData);

        splashFragment.setViewModel(splashViewModel);

        startFragment(splashFragment);
        Assert.assertNotNull(splashFragment);
    }

    @Test
    public void viewTest() throws Exception {
        View view = splashFragment.getView();
        Assert.assertNotNull(view);

        ImageView splashImage = view.findViewById(R.id.splash_logo);
        Assert.assertNotNull(splashImage);
    }

}
