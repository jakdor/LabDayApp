package com.jakdor.labday.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.jakdor.labday.common.model.maps.MapPath
import com.jakdor.labday.common.repository.ProjectRepository
import com.jakdor.labday.rx.RxResponse
import com.jakdor.labday.rx.RxSchedulersFacade
import io.reactivex.disposables.CompositeDisposable

import javax.inject.Inject

/**
 * ViewModel for [com.jakdor.labday.view.ui.MapFragment]
 * Doesn't extends [BaseViewModel] because it makes calls to different api
 */
class MapViewModel : AndroidViewModel {

    private var projectRepository: ProjectRepository
    private var rxSchedulersFacade: RxSchedulersFacade

    private var disposable = CompositeDisposable()

    val loadingStatus = MutableLiveData<Boolean>()
    val mapPath = MutableLiveData<RxResponse<MapPath>>()

    @Inject
    constructor(projectRepository: ProjectRepository,
                      application: Application,
                      rxSchedulersFacade: RxSchedulersFacade) : super(application) {
        this.projectRepository = projectRepository
        this.rxSchedulersFacade = rxSchedulersFacade
    }

    /**
     * Handle received google API response
     */
    fun makePathRequest(
            startLat: String, startLong: String, endLat: String, endLong: String, apiKey: String){

        val origin = startLat + "," + startLong
        val dest = endLat + "," + endLong

        disposable.add(projectRepository.mapPathRequest(origin, dest, apiKey)
                .subscribeOn(rxSchedulersFacade.io())
                .observeOn(rxSchedulersFacade.ui())
                .doOnSubscribe({ _ -> loadingStatus.value = true })
                .doAfterTerminate { loadingStatus.value = false }
                .subscribe({ t -> mapPath.value = t }))
    }
}