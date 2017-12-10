package com.jakdor.labday.rx;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Facade for RxJava Schedulers for mocking in tests
 */
public class RxSchedulersFacade {
    public Scheduler io() {
        return Schedulers.io();
    }

    public Scheduler computation() {
        return Schedulers.computation();
    }

    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
