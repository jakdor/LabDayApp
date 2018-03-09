package com.jakdor.labday.view.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.jakdor.labday.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private Handler backHandler = new Handler();
    private boolean doubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        //setup ActionBar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            SplashFragment splashFragment = new SplashFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.fragmentLayout, splashFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backHandler.removeCallbacksAndMessages(null); //remove all callbacks
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    /**
     * toolbar navigation same as back navigation
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * discard back press if MainFragment/LoginFragment/SplashFragment loaded, double tap to app exit
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if(fragment instanceof MainFragment
                || fragment instanceof LoginFragment
                || fragment instanceof SplashFragment){

            if(doubleBackToExit) {
                finishAndRemoveTask();
            }

            doubleBackToExit = true;
            Toast.makeText(this, getString(R.string.double_back_info), Toast.LENGTH_SHORT).show();

            backHandler.postDelayed(() -> doubleBackToExit = false, 2000);

            return;
        }

        super.onBackPressed();
    }
}
