package com.jakdor.labday.automatedUITests

import androidx.test.ext.junit.runners.AndroidJUnit4
import android.view.View
import android.view.ViewGroup

import com.jakdor.labday.R
import com.jakdor.labday.view.ui.SplashActivity

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.allOf

@RunWith(AndroidJUnit4::class)
class LoginEspressoTest {

    @Rule @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun loginEspressoTest() {

        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val imageView = onView(
                allOf(withId(R.id.login_logo), withContentDescription("logo"),
                        childAtPosition(
                                allOf(withId(R.id.login_layout),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
                                                0)),
                                0),
                        isDisplayed()))
        imageView.check(matches(isDisplayed()))

        val editText = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2),
                        isDisplayed()))
        editText.check(matches(withText("")))

        val editText2 = onView(
                allOf(withId(R.id.password_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                4),
                        isDisplayed()))
        editText2.check(matches(withText("")))

        val button = onView(
                allOf(withId(R.id.login_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5),
                        isDisplayed()))
        button.check(matches(isDisplayed()))

        val button2 = onView(
                allOf(withId(R.id.login_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5),
                        isDisplayed()))
        button2.check(matches(isDisplayed()))

        val appCompatButton = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton.perform(scrollTo(), click())

        pressBack()

        val textView5 = onView(
                allOf(withId(R.id.login_status_info), withText("Uzupełnij wszystkie pola"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                6),
                        isDisplayed()))
        textView5.check(matches(withText("Uzupełnij wszystkie pola")))

        val appCompatEditText = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText.perform(scrollTo(), click())

        val appCompatEditText2 = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText2.perform(scrollTo(), replaceText("any"), closeSoftKeyboard())

        val appCompatButton2 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton2.perform(scrollTo(), click())

        val textView6 = onView(
                allOf(withId(R.id.login_status_info), withText("Uzupełnij wszystkie pola"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                6),
                        isDisplayed()))
        textView6.check(matches(withText("Uzupełnij wszystkie pola")))

        val appCompatEditText3 = onView(
                allOf(withId(R.id.login_text_field), withText("any"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText3.perform(scrollTo(), click())

        val appCompatEditText4 = onView(
                allOf(withId(R.id.login_text_field), withText("any"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText4.perform(scrollTo(), replaceText(""))

        val appCompatEditText5 = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2),
                        isDisplayed()))
        appCompatEditText5.perform(closeSoftKeyboard())

        val appCompatEditText6 = onView(
                allOf(withId(R.id.password_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                4)))
        appCompatEditText6.perform(scrollTo(), replaceText("any"), closeSoftKeyboard())

        val appCompatButton3 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton3.perform(scrollTo(), click())

        val textView7 = onView(
                allOf(withId(R.id.login_status_info), withText("Uzupełnij wszystkie pola"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                6),
                        isDisplayed()))
        textView7.check(matches(withText("Uzupełnij wszystkie pola")))

        val appCompatEditText7 = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText7.perform(scrollTo(), replaceText("dupa"), closeSoftKeyboard())

        val appCompatButton4 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton4.perform(scrollTo(), click())

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val textView8 = onView(
                allOf(withId(R.id.login_status_info), withText("Nie można zalogować"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                6),
                        isDisplayed()))
        textView8.check(matches(withText("Nie można zalogować")))

        val appCompatEditText8 = onView(
                allOf(withId(R.id.login_text_field), withText("dupa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText8.perform(scrollTo(), click())

        val appCompatEditText9 = onView(
                allOf(withId(R.id.login_text_field), withText("dupa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText9.perform(scrollTo(), replaceText("test"))

        val appCompatEditText10 = onView(
                allOf(withId(R.id.login_text_field), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2),
                        isDisplayed()))
        appCompatEditText10.perform(closeSoftKeyboard())

        val appCompatButton5 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton5.perform(scrollTo(), click())

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val textView9 = onView(
                allOf(withId(R.id.login_status_info), withText("Nie można zalogować"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                6),
                        isDisplayed()))
        textView9.check(matches(withText("Nie można zalogować")))

        val appCompatEditText11 = onView(
                allOf(withId(R.id.login_text_field), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText11.perform(scrollTo(), click())

        val appCompatEditText12 = onView(
                allOf(withId(R.id.login_text_field), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText12.perform(scrollTo(), replaceText(""))

        val appCompatEditText13 = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2),
                        isDisplayed()))
        appCompatEditText13.perform(closeSoftKeyboard())

        val appCompatEditText14 = onView(
                allOf(withId(R.id.password_text_field), withText("any"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                4)))
        appCompatEditText14.perform(scrollTo(), replaceText("1234asd"))

        val appCompatEditText15 = onView(
                allOf(withId(R.id.password_text_field), withText("1234asd"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                4),
                        isDisplayed()))
        appCompatEditText15.perform(closeSoftKeyboard())

        val appCompatButton6 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton6.perform(scrollTo(), click())

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val textView10 = onView(
                allOf(withId(R.id.login_status_info), withText("Uzupełnij wszystkie pola"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                6),
                        isDisplayed()))
        textView10.check(matches(withText("Uzupełnij wszystkie pola")))

        val appCompatEditText16 = onView(
                allOf(withId(R.id.login_text_field),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                2)))
        appCompatEditText16.perform(scrollTo(), replaceText("test"), closeSoftKeyboard())

        val appCompatButton7 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton7.perform(scrollTo(), click())

        val appCompatEditText17 = onView(
                allOf(withId(R.id.password_text_field), withText("1234asd"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                4)))
        appCompatEditText17.perform(scrollTo(), replaceText("1234asdf"))

        val appCompatEditText18 = onView(
                allOf(withId(R.id.password_text_field), withText("1234asdf"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                4),
                        isDisplayed()))
        appCompatEditText18.perform(closeSoftKeyboard())

        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val appCompatButton8 = onView(
                allOf(withId(R.id.login_button), withText("rozpocznij"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_card),
                                        0),
                                5)))
        appCompatButton8.perform(scrollTo(), click())

        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val viewGroup3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menu_timetable),
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                        1)),
                        0),
                        isDisplayed()))
        viewGroup3.check(matches(isDisplayed()))

        val viewGroup4 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menu_map),
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                        2)),
                        0),
                        isDisplayed()))
        viewGroup4.check(matches(isDisplayed()))

        val viewGroup5 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menu_media),
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                        3)),
                        0),
                        isDisplayed()))
        viewGroup5.check(matches(isDisplayed()))

        val viewGroup6 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.menu_info),
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                                        4)),
                        0),
                        isDisplayed()))
        viewGroup6.check(matches(isDisplayed()))

    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return (parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position))
            }
        }
    }
}
