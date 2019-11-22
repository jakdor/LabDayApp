package com.jakdor.labday.UITests

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.common.model.Path
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.view.ui.MainFragment
import com.jakdor.labday.view.ui.TestActivity
import com.jakdor.labday.viewmodel.MainViewModel

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import java.util.ArrayList

class MainFragmentTest : UITestBase() {

    private lateinit var scenario: MainFragment

    private var mainViewModel: MainViewModel? = null

    private val appData = MutableLiveData<RxResponse<AppData>>()
    private val loadingStatus = MutableLiveData<Boolean>()

    private var hasLooper = false

    private fun initMockData() {
        val data = AppData()
        val paths = ArrayList<Path>()
        paths.add(Path(1, "dummyNameWrong", "PathInfoWrong", false))
        paths.add(Path(2, "dummyName", "PathInfo", true))
        data.paths = paths

        uiThreadTestRule.runOnUiThread {
            appData.value = RxResponse.success(data)
            loadingStatus.value = false
        }
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initMockData()

        uiThreadTestRule.runOnUiThread {
            scenario = MainFragment()
            scenario.setTestMode()
        }

        mainViewModel = Mockito.mock(MainViewModel::class.java)
        Mockito.`when`(mainViewModel!!.response).thenReturn(appData)
        Mockito.`when`(mainViewModel!!.loadingStatus).thenReturn(loadingStatus)

        scenario.setViewModel(mainViewModel)
        uiThreadTestRule.runOnUiThread {
            scenario.observeAppData()
            scenario.observeLoadingStatus()
        }

        testActivity = getCurrentActivity() as TestActivity
    }

    /**
     * Layout inflation test
     */
    @Test
    @Throws(Exception::class)
    fun viewTest() {
        uiThreadTestRule.runOnUiThread {
            testActivity.setFragment(scenario)
            scenario.binding.executePendingBindings()
        }

        val view = scenario.view

        Assert.assertNotNull(view)

        val logo = view!!.findViewById<ImageView>(R.id.menu_logo)
        Assert.assertNotNull(logo)

        val cards = arrayOf(view.findViewById(R.id.menu_timetable),
                view.findViewById(R.id.menu_map),
                view.findViewById(R.id.menu_media),
                view.findViewById<View>(R.id.menu_info))

        for (card in cards) {
            Assert.assertNotNull(card)

            val icon = card.findViewById<ImageView>(R.id.menu_card_icon)
            val image = card.findViewById<ImageView>(R.id.menu_card_image)
            val title = card.findViewById<TextView>(R.id.menu_card_title)
            val path = card.findViewById<TextView>(R.id.menu_card_path_chip)

            Assert.assertNotNull(icon)
            Assert.assertNotNull(image)
            Assert.assertNotNull(title)
            Assert.assertNotNull(path)
        }
    }

    /**
     * Test path chip visibility parameter and correct path info
     */
    @Test
    @Throws(Exception::class)
    fun pathChipTest() {
        uiThreadTestRule.runOnUiThread {
            testActivity.setFragment(scenario)
            scenario.binding.executePendingBindings()
        }

        val view = scenario.view

        Assert.assertNotNull(view)

        val cards = arrayOf(view!!.findViewById(R.id.menu_timetable),
                view.findViewById(R.id.menu_map),
                view.findViewById(R.id.menu_media),
                view.findViewById<View>(R.id.menu_info))

        for (i in cards.indices) {
            if (i == 0) {
                val pathChip = cards[i].findViewById<TextView>(R.id.menu_card_path_chip)
                Assert.assertNotNull(pathChip)
                Assert.assertEquals(View.VISIBLE.toLong(), pathChip.visibility.toLong())
                Assert.assertEquals("dummyName", pathChip.text.toString())
            } else {
                val pathChip = cards[i].findViewById<TextView>(R.id.menu_card_path_chip)
                Assert.assertNotNull(pathChip)
                Assert.assertEquals(View.GONE.toLong(), pathChip.visibility.toLong())
            }
        }
    }
}
