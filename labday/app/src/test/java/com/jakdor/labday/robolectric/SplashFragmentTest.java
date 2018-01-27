package com.jakdor.labday.robolectric;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.view.ui.MainFragment;
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

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class SplashFragmentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SplashFragment splashFragment;

    private SplashViewModel splashViewModel;
    private MutableLiveData<RxResponse<AppData>> appData;

    private void initMockData(){
        appData = new MutableLiveData<>();
        appData.setValue(RxResponse.success(new AppData()));
    }

    @Before
    public void setUp(){
        initMockData();
        splashFragment = new SplashFragment();

        splashViewModel = Mockito.mock(SplashViewModel.class);
        Mockito.when(splashViewModel.getResponse()).thenReturn(appData);

        splashFragment.setViewModel(splashViewModel);
    }

    @Test
    public void viewTest() throws Exception {
        startFragment(splashFragment);

        View view = splashFragment.getView();
        Assert.assertNotNull(view);

        ImageView splashImage = view.findViewById(R.id.splash_logo);
        Assert.assertNotNull(splashImage);
    }

    /**
     * Integration test - check if fragment gets replaced with {@link MainFragment}
     * after lastUpdate Success response
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    public void isLoggedInTrueResponseSuccessIntegrationTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No view found for id 0x7f070036 (com.jakdor.labday:id/fragmentLayout) for fragment MainFragment");

        Mockito.when(splashViewModel.isLoggedIn(Mockito.any(Context.class))).thenReturn(true);
        startFragment(splashFragment);
    }

    /**
     * Integration test - check if fragment gets replaced with {@link com.jakdor.labday.view.ui.LoginFragment}
     * after lastUpdate Error response
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    public void isLoggedInTrueResponseErrorIntegrationTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No view found for id 0x7f070036 (com.jakdor.labday:id/fragmentLayout) for fragment LoginFragment");

        MutableLiveData<RxResponse<AppData>> appData = new MutableLiveData<>();
        appData.setValue(RxResponse.error(new Throwable("dummy error")));
        Mockito.when(splashViewModel.getResponse()).thenReturn(appData);
        Mockito.when(splashViewModel.isLoggedIn(Mockito.any(Context.class))).thenReturn(true);
        startFragment(splashFragment);
    }

    /**
     * Integration test - check if Handler has scheduled task to lunch
     * {@link com.jakdor.labday.view.ui.LoginFragment} after isLoggedIn() returns false.
     */
    @Test
    public void isLoggedInFalseIntegrationTest() throws Exception {
        Mockito.when(splashViewModel.isLoggedIn(Mockito.any(Context.class))).thenReturn(false);
        startFragment(splashFragment);

        Assert.assertTrue(splashFragment.getDelayedTransactionHandler().hasMessages(0));
    }

    /**
     * unit test switchToMainFragment()
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    public void switchToMainFragmentTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No view found for id 0x7f070036 (com.jakdor.labday:id/fragmentLayout) for fragment MainFragment");

        startFragment(splashFragment);
        splashFragment.switchToMainFragment(appData.getValue());
    }

    /**
     * unit test switchToLoginFragment()
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    public void switchToLoginFragmentTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No view found for id 0x7f070036 (com.jakdor.labday:id/fragmentLayout) for fragment LoginFragment");

        startFragment(splashFragment);
        splashFragment.switchToLoginFragment();
    }
}
