package com.jakdor.labday.viewmodel

import android.app.Application

import com.jakdor.labday.common.repository.ProjectRepository
import com.jakdor.labday.rx.RxSchedulersFacade

import javax.inject.Inject

/**
 * ViewModel for [com.jakdor.labday.view.ui.EventFragment]
 */
class EventViewModel
@Inject
constructor(projectRepository: ProjectRepository,
            application: Application,
            rxSchedulersFacade: RxSchedulersFacade)
    : BaseViewModel(projectRepository, application, rxSchedulersFacade)
