package com.udacity.gradle.funnydroids;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Created by Jose on 8/2/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mainActivity = getActivity();
    }

    public void testPreconditions() {
        assertNotNull("mainActivity is null", mainActivity);
    }

    @MediumTest
    public void testJokeRetriever(){
        String joke = mainActivity.retrieveJoke().toBlocking().first();

        assertNotNull("joke is null", joke);
    }

    @MediumTest
    public void testKnock(){
        String response = mainActivity.knockBackend().toBlocking().first();

        assertNotNull("no one is home", response);
    }
}

