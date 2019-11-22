package com.jakdor.labday.UITests

import androidx.lifecycle.MutableLiveData
import androidx.cardview.widget.CardView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.view.ui.LoginFragment
import com.jakdor.labday.view.ui.TestActivity
import com.jakdor.labday.viewmodel.LoginViewModel

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class LoginFragmentTest : UITestBase() {

    private lateinit var scenario: LoginFragment

    private var loginViewModel: LoginViewModel? = null
    private var appData: MutableLiveData<RxResponse<AppData>>? = null
    private var loadingStatus: MutableLiveData<Boolean>? = null

    private val DUMMY_LOGIN = "dummyLogin123"
    private val DUMMY_PASSWORD = "password123"

    @Before
    @Throws(Exception::class)
    fun setUp() {
        scenario = LoginFragment()

        appData = MutableLiveData()
        loadingStatus = MutableLiveData()

        uiThreadTestRule.runOnUiThread {
            appData!!.value = RxResponse.error(Throwable("dummy throwable")) //block login
            loadingStatus!!.value = false //no loading animation
        }

        loginViewModel = Mockito.mock(LoginViewModel::class.java)
        Mockito.`when`(loginViewModel!!.response).thenReturn(appData)
        Mockito.`when`(loginViewModel!!.loadingStatus).thenReturn(loadingStatus)

        scenario.setViewModel(loginViewModel!!)
        uiThreadTestRule.runOnUiThread {
            scenario.observeLogin()
            scenario.observeLoadingStatus()
        }

        testActivity = getCurrentActivity() as TestActivity
    }

    /**
     * Check layout inflation
     */
    @Test
    @Throws(Exception::class)
    fun viewTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val logo = view!!.findViewById<ImageView>(R.id.login_logo)
        val logoText = view.findViewById<TextView>(R.id.logo_text)
        val loginCard = view.findViewById<CardView>(R.id.login_card)
        val loadingAnim = view.findViewById<ImageView>(R.id.login_loading_anim)

        //card
        val loginText = view.findViewById<TextView>(R.id.textView)
        val loginLabel = view.findViewById<TextView>(R.id.textView2)
        val passwordLabel = view.findViewById<TextView>(R.id.textView3)
        val loginField = view.findViewById<EditText>(R.id.login_text_field)
        val passwordField = view.findViewById<EditText>(R.id.password_text_field)
        val loginButton = view.findViewById<Button>(R.id.login_button)
        val loginStatus = view.findViewById<TextView>(R.id.login_status_info)

        Assert.assertNotNull(logo)
        Assert.assertNotNull(logoText)
        Assert.assertNotNull(loginCard)
        Assert.assertNotNull(loadingAnim)
        Assert.assertNotNull(loginText)
        Assert.assertNotNull(loginLabel)
        Assert.assertNotNull(passwordLabel)
        Assert.assertNotNull(loginField)
        Assert.assertNotNull(passwordField)
        Assert.assertNotNull(loginButton)
        Assert.assertNotNull(loginStatus)

        Assert.assertEquals(View.GONE.toLong(), loadingAnim.visibility.toLong())
    }

    /**
     * Integration test - empty fields, login button click
     */
    @Test
    @Throws(Exception::class)
    fun emptyFieldsIntegrationTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginButton = view!!.findViewById<Button>(R.id.login_button)
        val loginStatus = view.findViewById<TextView>(R.id.login_status_info)

        uiThreadTestRule.runOnUiThread {
            loginButton.performClick()
        }

        Assert.assertEquals(View.VISIBLE.toLong(), loginStatus.visibility.toLong())
        Assert.assertEquals("Uzupełnij wszystkie pola", loginStatus.text.toString())
    }

    /**
     * Integration test - empty password field, login button click
     */
    @Test
    @Throws(Exception::class)
    fun loginFieldOnlyIntegrationTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginButton = view!!.findViewById<Button>(R.id.login_button)
        val loginStatus = view.findViewById<TextView>(R.id.login_status_info)
        val loginField = view.findViewById<EditText>(R.id.login_text_field)

        uiThreadTestRule.runOnUiThread {
            loginField.setText(DUMMY_LOGIN)
            loginButton.performClick()
        }

        Assert.assertEquals(View.VISIBLE.toLong(), loginStatus.visibility.toLong())
        Assert.assertEquals("Uzupełnij wszystkie pola", loginStatus.text.toString())
    }

    /**
     * Integration test - empty login field, login button click
     */
    @Test
    @Throws(Exception::class)
    fun passwordFieldOnlyIntegrationTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginButton = view!!.findViewById<Button>(R.id.login_button)
        val loginStatus = view.findViewById<TextView>(R.id.login_status_info)
        val passwordField = view.findViewById<EditText>(R.id.password_text_field)

        passwordField.setText(DUMMY_PASSWORD)

        uiThreadTestRule.runOnUiThread {
            loginButton.performClick()
        }

        Assert.assertEquals(View.VISIBLE.toLong(), loginStatus.visibility.toLong())
        Assert.assertEquals("Uzupełnij wszystkie pola", loginStatus.text.toString())
    }

    /**
     * Integration test - valid inputs, login button click
     * @throws Exception expected view not found (Parent Activity view with fragmentLayout not loaded)
     */
    @Test
    @Throws(Exception::class)
    fun successfulValidationIntegrationTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginButton = view!!.findViewById<Button>(R.id.login_button)
        val loginField = view.findViewById<EditText>(R.id.login_text_field)
        val passwordField = view.findViewById<EditText>(R.id.password_text_field)
        val loadingAnim = view.findViewById<ImageView>(R.id.login_loading_anim)

        uiThreadTestRule.runOnUiThread {
            loginField.setText(DUMMY_LOGIN)
            passwordField.setText(DUMMY_PASSWORD)
        }

        Assert.assertTrue(scenario.validateInputs())

        loginButton.setOnClickListener {

            scenario.onLoginButtonClick()

            loadingStatus!!.value = true
            Assert.assertEquals(View.VISIBLE.toLong(), loadingAnim.visibility.toLong())
        }

        uiThreadTestRule.runOnUiThread {
            loginButton.performClick()
        }
    }

    /**
     * Check if correct indicator status displayed after failed login from getResponse()
     */
    @Test
    @Throws(Exception::class)
    fun loginErrorStatusTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginStatus = view!!.findViewById<TextView>(R.id.login_status_info)

        Assert.assertEquals(View.VISIBLE.toLong(), loginStatus.visibility.toLong())
        Assert.assertEquals("Nie można zalogować", loginStatus.text.toString())
    }

    /**
     * Unit test behaviour after Error login RXStatus
     */
    @Test
    @Throws(Exception::class)
    fun SwitchToMainFragmentErrorResponseTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginStatus = view!!.findViewById<TextView>(R.id.login_status_info)
        val loadingAnim = view.findViewById<ImageView>(R.id.login_loading_anim)

        loginStatus.visibility = View.GONE
        loadingAnim.visibility = View.VISIBLE

        uiThreadTestRule.runOnUiThread {
            scenario.switchToMainFragment(RxResponse.error(Throwable("dummy error")))
        }

        Assert.assertEquals(View.VISIBLE.toLong(), loginStatus.visibility.toLong())
        Assert.assertEquals("Nie można zalogować", loginStatus.text.toString())
        Assert.assertEquals(View.GONE.toLong(), loadingAnim.visibility.toLong())
    }

    /**
     * Unit test behaviour after NoInternet login RXStatus
     */
    @Test
    @Throws(Exception::class)
    fun SwitchToMainFragmentNoInternetResponseTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginStatus = view!!.findViewById<TextView>(R.id.login_status_info)
        val loadingAnim = view.findViewById<ImageView>(R.id.login_loading_anim)

        loginStatus.visibility = View.GONE
        loadingAnim.visibility = View.VISIBLE

        uiThreadTestRule.runOnUiThread {
            scenario.switchToMainFragment(RxResponse.noInternetNoDb(Throwable("dummy error")))
        }

        Assert.assertEquals(View.VISIBLE.toLong(), loginStatus.visibility.toLong())
        Assert.assertEquals("Brak połączenia z internetem", loginStatus.text.toString())
        Assert.assertEquals(View.GONE.toLong(), loadingAnim.visibility.toLong())
    }

    /**
     * Unit test input validation method - empty fields
     */
    @Test
    @Throws(Exception::class)
    fun inputValidationEmptyTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }
        Assert.assertFalse(scenario.validateInputs())
    }

    /**
     * Unit test input validation method - Login only
     */
    @Test
    @Throws(Exception::class)
    fun inputValidationLoginOnlyTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginField = view!!.findViewById<EditText>(R.id.login_text_field)
        loginField.setText(DUMMY_LOGIN)

        Assert.assertFalse(scenario.validateInputs())
    }

    /**
     * Unit test input validation method - password only
     */
    @Test
    @Throws(Exception::class)
    fun inputValidationPasswordOnlyTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val passwordField = view!!.findViewById<EditText>(R.id.password_text_field)
        passwordField.setText(DUMMY_PASSWORD)

        Assert.assertFalse(scenario.validateInputs())
    }

    /**
     * Unit test input validation method - valid inputs
     */
    @Test
    @Throws(Exception::class)
    fun inputValidationValidInputsTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)

        val loginField = view!!.findViewById<EditText>(R.id.login_text_field)
        val passwordField = view.findViewById<EditText>(R.id.password_text_field)
        loginField.setText(DUMMY_LOGIN)
        passwordField.setText(DUMMY_PASSWORD)

        Assert.assertTrue(scenario.validateInputs())
    }

}
