package com.jakdor.labday.di;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.jakdor.labday.App;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Dagger setup class / automated fragments injection if they implement {@link InjectableFragment}
 */
public class AppInjector {

    private AppInjector(){}

    public static void init(App app){
        DaggerAppComponent
                .builder()
                .application(app)
                .build()
                .inject(app);

        app.registerActivityLifecycleCallbacks(new App.ActivityLifecycleCallbacks(){
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * Automated fragment injector
     * @param activity provided by ActivityLifecycleCallbacks()
     */
    private static void handleActivity(Activity activity){
        if(activity instanceof HasSupportFragmentInjector){
            AndroidInjection.inject(activity);
        }

        if(activity instanceof FragmentActivity){
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(
                            new FragmentManager.FragmentLifecycleCallbacks() {

                        @Override
                        public void onFragmentCreated(
                                FragmentManager fm, Fragment f, Bundle savedInstanceState) {

                            if(f instanceof InjectableFragment){
                                AndroidSupportInjection.inject(f);
                            }

                        }

                    }, true);
        }
    }
}
