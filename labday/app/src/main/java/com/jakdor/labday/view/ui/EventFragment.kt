package com.jakdor.labday.view.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakdor.labday.R
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.common.model.Event
import com.jakdor.labday.databinding.FragmentEventBinding

import com.jakdor.labday.di.InjectableFragment
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxStatus
import com.jakdor.labday.viewmodel.EventViewModel
import javax.inject.Inject

class EventFragment : Fragment(), InjectableFragment {

    var viewModel: EventViewModel? = null
    lateinit var binding: FragmentEventBinding

    private var eventId: Int = 0
    lateinit var event: Event

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            eventId = arguments!!.getInt("event")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_event, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(EventViewModel::class.java)
        }

        observeAppData()
        viewModel!!.loadAppData(context)
    }

    fun observeAppData() {
        viewModel!!.response.observe(this, Observer<RxResponse<AppData>> { response ->
            if (response != null) {
                this.processResponse(response)
            }
        })
    }

    /**
     * Set path name in timetable card
     */
    fun processResponse(response: RxResponse<AppData>) {
        if (response.status == RxStatus.SUCCESS || response.status == RxStatus.SUCCESS_DB) {

        } else {
            if (response.error != null) {
                Log.e(CLASS_TAG, response.error.toString())
            }
        }
    }

    companion object {
        const val CLASS_TAG: String = "EventFragment"

        fun newInstance(eventId: Int): EventFragment {
            val eventTimetable = EventFragment()

            val args = Bundle()
            args.putInt("event", eventId)
            eventTimetable.arguments = args

            return eventTimetable
        }
    }

}
