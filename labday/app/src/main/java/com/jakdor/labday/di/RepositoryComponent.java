package com.jakdor.labday.di;

import com.jakdor.labday.common.repository.NetworkManager;
import com.jakdor.labday.common.repository.ProjectRepository;

import dagger.Component;

/**
 * Dagger Repository component setup interface
 */
@Component(dependencies = NetworkManager.class)
public interface RepositoryComponent {
    ProjectRepository getProjectRepository();
}
