package com.udacity.gradle.funnydroids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.engtoolsdev.funnyactivity.FunnyActivity;
import com.engtoolsdev.jose.funnyapp.backend.funnyApi.FunnyApi;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    private FunnyApi funnyEndpoint;
    private CompositeSubscription mSubscriptions;

    private View progressLayout;
    private View fabInstructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0f);

        fabInstructionsTextView = findViewById(R.id.fab_instructions);
        progressLayout = findViewById(R.id.progress_layout);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        requestNewInterstitial();

        setUpEndpoint();

    }

    /**
     * Instantiates {@link FunnyApi} object
     */
    private void setUpEndpoint() {
        if(funnyEndpoint == null) {  // Only do this once
            FunnyApi.Builder builder = new FunnyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setApplicationName(getResources().getString(R.string.app_name))
                            // options for running against local devappserver
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            // - turn off compression when running against local devappserver
                    .setRootUrl("https://nanodegree-funny-endpoint-1024.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            funnyEndpoint = builder.build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.knock:
                knock();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSubscriptions.clear();
    }

    /**
     * Retrieves and tells a joke.
     * @param view View tapped
     */
    public void tellJoke(View view){

        showProgress(true);

        Subscription subscription = AppObservable.bindActivity(this, retrieveJoke())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, e.getMessage());
                        Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }

                    @Override
                    public void onNext(final String joke) {

                        showProgress(false);

                        //If Interstitial Ad is loaded, show it and then start FunnyActivity. If not loaded, start FunnyActivity
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    requestNewInterstitial();
                                    Intent intent = new Intent(MainActivity.this, FunnyActivity.class);
                                    intent.putExtra(FunnyActivity.EXTRA_JOKE, joke);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Intent intent = new Intent(MainActivity.this, FunnyActivity.class);
                            intent.putExtra(FunnyActivity.EXTRA_JOKE, joke);
                            startActivity(intent);
                        }


                    }
                });

        mSubscriptions.add(subscription);
    }

    /**
     * Pings backend
     */
    public void knock(){

        Subscription subscription = AppObservable.bindActivity(this, knockBackend())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, e.getMessage());
                        Toast.makeText(MainActivity.this, R.string.error_message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(final String joke) {
                        Toast.makeText(MainActivity.this, joke, Toast.LENGTH_LONG).show();
                    }
                });

        mSubscriptions.add(subscription);
    }

    /**
     * Retrieves a {@link String} joke from backend.
     * @return {@link Observable} emitting String retrieved from the backend.
     */
    public Observable<String> retrieveJoke(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                String joke = null;
                try {
                    joke = funnyEndpoint.tellMeAJoke().execute().getJoke();

                } catch (IOException e) {
                    subscriber.onError(e);
                }

                subscriber.onNext(joke);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io());
    }

    /**
     * Retrieves {@link String} ping response from backend.
     * @return {@link Observable} emitting String retrieved from the backend.
     */
    public Observable<String> knockBackend(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                String response = null;
                try {
                    response = funnyEndpoint.knockknock().execute().getJoke();

                } catch (IOException e) {
                    subscriber.onError(e);
                }

                subscriber.onNext(response);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /**
     * Requests a new Interstitial Ad from AdMob
     */
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Hides or Show progress bar and Instructions
     * @param show boolean, true = show, false = hide
     */
    public void showProgress(boolean show){
        if(show){
            progressLayout.setVisibility(View.VISIBLE);
            fabInstructionsTextView.setVisibility(View.GONE);
        }else{
            progressLayout.setVisibility(View.GONE);
            fabInstructionsTextView.setVisibility(View.VISIBLE);
        }
    }


}
