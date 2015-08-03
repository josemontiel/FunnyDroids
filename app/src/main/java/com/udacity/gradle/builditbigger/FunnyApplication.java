package com.udacity.gradle.builditbigger;

import android.support.multidex.MultiDexApplication;

import timber.log.Timber;

/**
 * Created by Jose on 8/1/15.
 */
public class FunnyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }
}
