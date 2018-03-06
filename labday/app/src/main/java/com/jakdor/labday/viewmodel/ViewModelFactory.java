package com.jakdor.labday.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.jakdor.labday.di.ViewModelSubComponent;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory for ViewModel instances
 */
@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final ArrayMap<Class, Callable<? extends ViewModel>> creators;

    /**
     * ViewModels injected into creators ArrayMap
     * @param viewModelSubComponent Dagger SubComponent ViewModel interface
     */
    @Inject
    public ViewModelFactory(ViewModelSubComponent viewModelSubComponent){
        creators = new ArrayMap<>();

        creators.put(MainViewModel.class, viewModelSubComponent::mainViewModel);
        creators.put(SplashViewModel.class, viewModelSubComponent::splashViewModel);
        creators.put(LoginViewModel.class, viewModelSubComponent::loginViewModel);
        creators.put(TimetableViewModel.class, viewModelSubComponent::timetableViewModel);
        creators.put(EventViewModel.class, viewModelSubComponent::eventViewModel);
        creators.put(MapViewModel.class, viewModelSubComponent::mapViewModel);
        creators.put(PlacesViewModel.class, viewModelSubComponent::placesViewModel);
    }

    /**
     * creates ViewModels
     * @param modelClass (viewModelChildName).class
     * @param <T> class
     * @return ViewModel custom instance
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Callable<? extends ViewModel> creator = creators.get(modelClass);
        if(creator == null){
            for(Map.Entry<Class, Callable<? extends ViewModel>> entry : creators.entrySet()){
                if(modelClass.isAssignableFrom(entry.getKey())){
                    creator = entry.getValue();
                    break;
                }
            }
        }

        if(creator == null){
            throw new IllegalArgumentException("Model class not found" + modelClass);
        }

        try {
            return (T) creator.call();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
