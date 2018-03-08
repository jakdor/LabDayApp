package com.jakdor.labday.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import com.jakdor.labday.common.model.AppData
import com.jakdor.labday.common.model.Event
import com.jakdor.labday.common.model.Speaker
import com.jakdor.labday.common.network.LabService

import com.jakdor.labday.common.repository.ProjectRepository
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxSchedulersFacade
import com.jakdor.labday.view.ui.EventFragment

import javax.inject.Inject

/**
 * ViewModel for [com.jakdor.labday.view.ui.EventFragment]
 */
class EventViewModel
@Inject
constructor(projectRepository: ProjectRepository,
            application: Application,
            rxSchedulersFacade: RxSchedulersFacade)
    : BaseViewModel(projectRepository, application, rxSchedulersFacade){

    internal val eventSpeakerPair = MutableLiveData< RxResponse<Pair<Event?, Speaker?>>>()

    /**
     * loadAppData from [BaseViewModel] with additional call to getEventAndSpeaker()
     */
    fun loadAppData(context: Context?, eventId: Int) {
        if (projectRepository.repositoryState == ProjectRepository.repositoryStates.READY) {
            appData.value = projectRepository.data as RxResponse<AppData>?
            getEventAndSpeaker(projectRepository.data.data as AppData, eventId)
        } else {
            disposable.add(projectRepository.getUpdate(LabService.API_URL, context)
                    .subscribeOn(rxSchedulersFacade.io())
                    .observeOn(rxSchedulersFacade.ui())
                    .doOnSubscribe { _ -> loadingStatus.value = true }
                    .doAfterTerminate { loadingStatus.value = false }
                    .subscribe({ t -> appData.value = t
                        getEventAndSpeaker(t.data, eventId)
                    }))
        }
    }

    /**
     * Get Event/Speaker by provided eventId
     */
    fun getEventAndSpeaker(data: AppData?, eventId: Int) {
        if(data == null){
            Log.wtf(EventFragment.CLASS_TAG, "AppData returned null")
            eventSpeakerPair.value = RxResponse.error(Throwable("AppData returned null"))
            return
        }
        if(data.events == null){
            Log.wtf(EventFragment.CLASS_TAG, "Events in AppData are null")
            eventSpeakerPair.value = RxResponse.error(Throwable("Events in AppData are null"))
            return
        }
        if(data.speakers == null){
            Log.wtf(EventFragment.CLASS_TAG, "Speakers in AppData are null")
            eventSpeakerPair.value = RxResponse.error(Throwable("Speakers in AppData are null"))
            return
        }

        val eventFound: Event? = data.events.firstOrNull { it.id == eventId }
        val speakerFound: Speaker? = data.speakers.firstOrNull { it.id == eventFound?.speakerId}

        eventSpeakerPair.value = RxResponse.success(Pair(eventFound, speakerFound))
    }
}
