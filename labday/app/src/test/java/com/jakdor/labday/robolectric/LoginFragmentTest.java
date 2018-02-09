package com.jakdor.labday.robolectric;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.view.ui.LoginFragment;
import com.jakdor.labday.viewmodel.LoginViewModel;

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
public class LoginFragmentTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private LoginFragment loginFragment;

    private LoginViewModel loginViewModel;
    private MutableLiveData<RxResponse<AppData>> appData;
    private MutableLiveData<Boolean> loadingStatus;

    private final String DUMMY_LOGIN = "dummyLogin123";
    private final String DUMMY_PASSWORD = "password123";

    @Before
    public void setUp() throws Exception {
        loginFragment = new LoginFragment();

        appData = new MutableLiveData<>();
        appData.setValue(RxResponse.error(new Throwable("dummy throwable"))); //block login

        loadingStatus = new MutableLiveData<>();
        loadingStatus.setValue(false); //no loading animation

        loginViewModel = Mockito.mock(LoginViewModel.class);
        Mockito.when(loginViewModel.getResponse()).thenReturn(appData);
        Mockito.when(loginViewModel.getLoadingStatus()).thenReturn(loadingStatus);

        loginFragment.setViewModel(loginViewModel);
    }

    /**
     * Check layout inflation
     */
    @Test
    public void viewTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        ImageView logo = view.findViewById(R.id.login_logo);
        TextView logoText = view.findViewById(R.id.logo_text);
        CardView loginCard = view.findViewById(R.id.login_card);
        ImageView loadingAnim = view.findViewById(R.id.login_loading_anim);

        //card
        TextView loginText = view.findViewById(R.id.textView);
        TextView loginLabel = view.findViewById(R.id.textView2);
        TextView passwordLabel = view.findViewById(R.id.textView3);
        EditText loginField = view.findViewById(R.id.login_text_field);
        EditText passwordField = view.findViewById(R.id.password_text_field);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView loginStatus = view.findViewById(R.id.login_status_info);

        Assert.assertNotNull(logo);
        Assert.assertNotNull(logoText);
        Assert.assertNotNull(loginCard);
        Assert.assertNotNull(loadingAnim);
        Assert.assertNotNull(loginText);
        Assert.assertNotNull(loginLabel);
        Assert.assertNotNull(passwordLabel);
        Assert.assertNotNull(loginField);
        Assert.assertNotNull(passwordField);
        Assert.assertNotNull(loginButton);
        Assert.assertNotNull(loginStatus);

        Assert.assertEquals(View.GONE, loadingAnim.getVisibility());
    }

    /**
     * Integration test - empty fields, login button click
     */
    @Test
    public void emptyFieldsIntegrationTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        Button loginButton = view.findViewById(R.id.login_button);
        TextView loginStatus = view.findViewById(R.id.login_status_info);

        loginButton.performClick();

        Assert.assertEquals(View.VISIBLE, loginStatus.getVisibility());
        Assert.assertEquals("Uzupełnij wszystkie pola", loginStatus.getText().toString());
    }

    /**
     * Integration test - empty password field, login button click
     */
    @Test
    public void loginFieldOnlyIntegrationTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        Button loginButton = view.findViewById(R.id.login_button);
        TextView loginStatus = view.findViewById(R.id.login_status_info);
        EditText loginField = view.findViewById(R.id.login_text_field);

        loginField.setText(DUMMY_LOGIN);

        loginButton.performClick();

        Assert.assertEquals(View.VISIBLE, loginStatus.getVisibility());
        Assert.assertEquals("Uzupełnij wszystkie pola", loginStatus.getText().toString());
    }

    /**
     * Integration test - empty login field, login button click
     */
    @Test
    public void passwordFieldOnlyIntegrationTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        Button loginButton = view.findViewById(R.id.login_button);
        TextView loginStatus = view.findViewById(R.id.login_status_info);
        EditText passwordField = view.findViewById(R.id.password_text_field);

        passwordField.setText(DUMMY_PASSWORD);

        loginButton.performClick();

        Assert.assertEquals(View.VISIBLE, loginStatus.getVisibility());
        Assert.assertEquals("Uzupełnij wszystkie pola", loginStatus.getText().toString());
    }

    /**
     * Integration test - valid inputs, login button click
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    public void successfulValidationIntegrationTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No view found for id 0x7f070036" +
                " (com.jakdor.labday:id/fragmentLayout) for fragment MainFragment");

        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        Button loginButton = view.findViewById(R.id.login_button);
        EditText loginField = view.findViewById(R.id.login_text_field);
        EditText passwordField = view.findViewById(R.id.password_text_field);
        ImageView loadingAnim = view.findViewById(R.id.login_loading_anim);

        loginField.setText(DUMMY_LOGIN);
        passwordField.setText(DUMMY_PASSWORD);

        loginButton.setOnClickListener(view1 -> {
            loginFragment.onLoginButtonClick();
            loadingStatus.setValue(true);
            Assert.assertEquals(View.VISIBLE, loadingAnim.getVisibility());
        });

        loginButton.performClick();

        appData.setValue(RxResponse.success(new AppData()));
    }

    /**
     * Check if correct indicator status displayed after failed login from getResponse()
     */
    @Test
    public void loginErrorStatusTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        TextView loginStatus = view.findViewById(R.id.login_status_info);

        Assert.assertEquals(View.VISIBLE, loginStatus.getVisibility());
        Assert.assertEquals("Nie można zalogować", loginStatus.getText().toString());
    }

    /**
     * Unit test switching to Main Fragment after Success login RXStatus
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    public void switchToMainFragmentSuccessResponseTest() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No view found for id 0x7f070036" +
                " (com.jakdor.labday:id/fragmentLayout) for fragment MainFragment");

        startFragment(loginFragment);
        loginFragment.switchToMainFragment(RxResponse.success(new AppData()));
    }

    /**
     * Unit test behaviour after Error login RXStatus
     */
    @Test
    public void SwitchToMainFragmentErrorResponseTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        TextView loginStatus = view.findViewById(R.id.login_status_info);
        ImageView loadingAnim = view.findViewById(R.id.login_loading_anim);

        loginStatus.setVisibility(View.GONE);
        loadingAnim.setVisibility(View.VISIBLE);

        loginFragment.switchToMainFragment(RxResponse.error(new Throwable("dummy error")));

        Assert.assertEquals(View.VISIBLE, loginStatus.getVisibility());
        Assert.assertEquals("Nie można zalogować", loginStatus.getText().toString());
        Assert.assertEquals(View.GONE, loadingAnim.getVisibility());
    }

    /**
     * Unit test behaviour after NoInternet login RXStatus
     */
    @Test
    public void SwitchToMainFragmentNoInternetResponseTest() throws  Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        TextView loginStatus = view.findViewById(R.id.login_status_info);
        ImageView loadingAnim = view.findViewById(R.id.login_loading_anim);

        loginStatus.setVisibility(View.GONE);
        loadingAnim.setVisibility(View.VISIBLE);

        loginFragment.switchToMainFragment(RxResponse.noInternetNoDb(new Throwable("dummy error")));

        Assert.assertEquals(View.VISIBLE, loginStatus.getVisibility());
        Assert.assertEquals("Brak połączenia z internetem", loginStatus.getText().toString());
        Assert.assertEquals(View.GONE, loadingAnim.getVisibility());
    }

    /**
     * Unit test input validation method - empty fields
     */
    @Test
    public void inputValidationEmptyTest() throws Exception {
        startFragment(loginFragment);
        Assert.assertFalse(loginFragment.validateInputs());
    }

    /**
     * Unit test input validation method - Login only
     */
    @Test
    public void inputValidationLoginOnlyTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        EditText loginField = view.findViewById(R.id.login_text_field);
        loginField.setText(DUMMY_LOGIN);

        Assert.assertFalse(loginFragment.validateInputs());
    }

    /**
     * Unit test input validation method - password only
     */
    @Test
    public void inputValidationPasswordOnlyTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        EditText passwordField = view.findViewById(R.id.password_text_field);
        passwordField.setText(DUMMY_PASSWORD);

        Assert.assertFalse(loginFragment.validateInputs());
    }

    /**
     * Unit test input validation method - valid inputs
     */
    @Test
    public void inputValidationValidInputsTest() throws Exception {
        startFragment(loginFragment);

        View view = loginFragment.getView();
        Assert.assertNotNull(view);

        EditText loginField = view.findViewById(R.id.login_text_field);
        EditText passwordField = view.findViewById(R.id.password_text_field);
        loginField.setText(DUMMY_LOGIN);
        passwordField.setText(DUMMY_PASSWORD);

        Assert.assertTrue(loginFragment.validateInputs());
    }

}
