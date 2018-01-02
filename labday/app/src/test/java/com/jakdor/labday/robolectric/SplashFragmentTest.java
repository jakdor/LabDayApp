package com.jakdor.labday.robolectric;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.ImageView;

import com.jakdor.labday.R;
import com.jakdor.labday.di.AppComponent;
import com.jakdor.labday.di.ViewModelSubComponent;
import com.jakdor.labday.view.ui.SplashFragment;
import com.jakdor.labday.viewmodel.LoginViewModel;
import com.jakdor.labday.viewmodel.MainViewModel;
import com.jakdor.labday.viewmodel.SplashViewModel;
import com.jakdor.labday.viewmodel.ViewModelFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class SplashFragmentTest {

    @Rule public final DaggerMockRule<AppComponent> rule = new RobolectricMockTestRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    ViewModelProvider.Factory viewModelFactory = new ViewModelFactory(new ViewModelSubComponent() {
        @Override
        public MainViewModel mainViewModel() {
            return null;
        }

        @Override
        public SplashViewModel splashViewModel() {
            return Mockito.mock(SplashViewModel.class);
        }

        @Override
        public LoginViewModel loginViewModel() {
            return null;
        }
    });

    private SplashFragment splashFragment;

    @Before
    public void setUp(){
        splashFragment = new SplashFragment();

        Mockito.when(ViewModelProviders.of(splashFragment, viewModelFactory)
                .get(SplashViewModel.class)).thenReturn(Mockito.mock(SplashViewModel.class));

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
