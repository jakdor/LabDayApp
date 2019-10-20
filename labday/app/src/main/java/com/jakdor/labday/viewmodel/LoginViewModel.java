package com.jakdor.labday.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;

import javax.inject.Inject;

/**
 * ViewModel for {@link com.jakdor.labday.view.ui.LoginFragment}
 */
public class LoginViewModel extends BaseViewModel {
    @Inject
    public LoginViewModel(@NonNull ProjectRepository projectRepository,
                          @NonNull Application application,
                          @NonNull RxSchedulersFacade rxSchedulersFacade) {
        super(projectRepository, application, rxSchedulersFacade);
    }

    public void login(Context context, String login, String password){
        disposable.add(projectRepository.login(LabService.API_URL, context, login, password)
                .subscribeOn(rxSchedulersFacade.io())
                .observeOn(rxSchedulersFacade.ui())
                .doOnSubscribe(disposable1 -> loadingStatus.setValue(true))
                .doAfterTerminate(() -> loadingStatus.setValue(false))
                .doOnError(throwable -> appData.setValue(RxResponse.loginError(throwable)))
                .subscribe(appData::setValue)
        );
    }
}
