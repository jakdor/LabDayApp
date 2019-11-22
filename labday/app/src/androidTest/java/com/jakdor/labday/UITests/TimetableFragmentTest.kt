package com.jakdor.labday.UITests

import androidx.lifecycle.MutableLiveData
import com.google.android.material.appbar.AppBarLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import com.google.gson.Gson
import com.jakdor.labday.R
import com.jakdor.labday.TestUtils
import com.jakdor.labday.view.ui.TestActivity
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.common.model.Timetable
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.TestUtils.readAssetFile
import com.jakdor.labday.view.ui.TimetableFragment
import com.jakdor.labday.viewmodel.TimetableViewModel

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RunWith(AndroidJUnit4::class)
class TimetableFragmentTest : UITestBase() {

    private lateinit var scenario: TimetableFragment

    private var viewModel: TimetableViewModel? = null
    private val appData = MutableLiveData<RxResponse<AppData>>()
    private val loadingStatus = MutableLiveData<Boolean>()
    private var data: AppData? = null

    @Throws(Exception::class)
    private fun initMocks() {
        val gson = Gson()
        data = gson.fromJson<AppData>(readAssetFile(InstrumentationRegistry.getInstrumentation().context,
                "api/app_data.json"), AppData::class.java)

        uiThreadTestRule.runOnUiThread {
            appData.value = RxResponse.success(data)
            loadingStatus.value = false
        }
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initMocks()

        scenario = TimetableFragment.newInstance(2)
        scenario.setTestMode()

        viewModel = Mockito.mock(TimetableViewModel::class.java)
        Mockito.`when`(viewModel!!.response).thenReturn(appData)
        Mockito.`when`(viewModel!!.loadingStatus).thenReturn(loadingStatus)

        scenario.setViewModel(viewModel)
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
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view
        Assert.assertNotNull(view)

        val appBarLayout = view!!.findViewById<AppBarLayout>(R.id.appbar_container)
        val toolbar = view.findViewById<Toolbar>(R.id.timetable_title_bar)
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.timetable_nested_scroll_view)
        val recyclerView = view.findViewById<RecyclerView>(R.id.timetable_recycler_view)

        Assert.assertNotNull(appBarLayout)
        Assert.assertNotNull(toolbar)
        Assert.assertNotNull(nestedScrollView)
        Assert.assertNotNull(recyclerView)
    }

    /**
     * Test if correct title bar set
     */
    @Test
    @Throws(Exception::class)
    fun titleBarTest() {
        val expectedTitle = TestUtils.randomString()
        val path = data!!.paths[1]
        path.info = expectedTitle
        data!!.paths.add(1, path)

        uiThreadTestRule.runOnUiThread {
            appData.value = RxResponse.success(data)
            testActivity.setFragment(scenario)
            scenario.binding.executePendingBindings()
        }

        val view = scenario.view
        Assert.assertNotNull(view)

        val toolbar = view!!.findViewById<Toolbar>(R.id.timetable_title_bar)
        Assert.assertEquals(expectedTitle, toolbar.title.toString())
    }

    /**
     * Test if items in recycler view are displayed in correct order
     */
    @Test
    @Throws(Exception::class)
    fun recyclerViewCorrectOrderTest() {
        uiThreadTestRule.runOnUiThread { testActivity.setFragment(scenario) }

        val view = scenario.view

        Assert.assertNotNull(view)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.timetable_recycler_view)

        uiThreadTestRule.runOnUiThread {
            recyclerView.measure(0, 0)
            recyclerView.layout(0, 0, 100, 1000)
            val expectedTimetableList = ArrayList<Timetable>()
            for (timetable in data!!.timetables) {
                if (timetable.pathId == 2) {
                    expectedTimetableList.add(timetable)
                }
            }

            expectedTimetableList.sortWith(Comparator { t1, t2 -> t1.timeStart!! - t2.timeStart!! })

            Assert.assertEquals(expectedTimetableList.size.toLong(), recyclerView.adapter!!.itemCount.toLong())

            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.GERMAN)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+1")
            for (i in expectedTimetableList.indices) {
                val start = Date(expectedTimetableList[i].timeStart.toLong() * 1000)
                val end = Date(expectedTimetableList[i].timeEnd.toLong() * 1000)

                val item = recyclerView.findViewHolderForAdapterPosition(i)!!.itemView
                val startView = item.findViewById<TextView>(R.id.timetable_item_time_start)
                val endView = item.findViewById<TextView>(R.id.timetable_item_time_end)

                Assert.assertEquals(simpleDateFormat.format(start), startView.text.toString())
                Assert.assertEquals(simpleDateFormat.format(end), endView.text.toString())
            }
        }
    }
}
