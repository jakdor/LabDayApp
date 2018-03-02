package com.jakdor.labday.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.jakdor.labday.common.repository.ProjectRepository
import com.jakdor.labday.rx.RxSchedulersFacade

import javax.inject.Inject

/**
 * ViewModel for [com.jakdor.labday.view.ui.MapFragment]
 */
class MapViewModel : AndroidViewModel {

    private var projectRepository: ProjectRepository
    private var rxSchedulersFacade: RxSchedulersFacade

    @Inject
    constructor(projectRepository: ProjectRepository,
                      application: Application,
                      rxSchedulersFacade: RxSchedulersFacade) : super(application) {
        this.projectRepository = projectRepository
        this.rxSchedulersFacade = rxSchedulersFacade
    }

    fun plotPathRequest(startLat: Double, startLong: Double, endLat: Double, endLong: Double){

    }
}