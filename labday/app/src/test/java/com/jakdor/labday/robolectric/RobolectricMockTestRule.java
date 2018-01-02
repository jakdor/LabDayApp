package com.jakdor.labday.robolectric;

import com.jakdor.labday.App;
import com.jakdor.labday.di.AppComponent;
import com.jakdor.labday.di.AppModule;

import org.robolectric.RuntimeEnvironment;

import it.cosenonjaviste.daggermock.DaggerMockRule;


class RobolectricMockTestRule extends DaggerMockRule<AppComponent> {

    public RobolectricMockTestRule() {
        super(AppComponent.class, new AppModule());

        customizeBuilder((BuilderCustomizer<AppComponent.Builder>)
                builder -> builder.application(getApplication()));

        set(component -> component.inject(getApplication()));
    }

    private static App getApplication() {
        return ((App) RuntimeEnvironment.application);
    }
}