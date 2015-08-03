/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.engtoolsdev.Jose.funnyapp.backend;

import com.engtoolsdev.Jose.funnyapp.backend.models.Joke;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.javajoker.Joker;

import java.util.ArrayList;
import java.util.Random;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "funnyApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.funnyapp.Jose.engtoolsdev.com",
                ownerName = "backend.funnyapp.Jose.engtoolsdev.com",
                packagePath = ""
        )
)
public class FunnyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "tellMeAJoke")
    public Joke getJoke() {

        ArrayList<String> jokeList = Joker.getInstance().getJokes();
        String joke;
        if(jokeList.size() != 0) {
            int index = new Random().nextInt(jokeList.size());
            joke = jokeList.get(index);
        }else{
            joke = "Sorry! I'll shut myself down!";
        }

        Joke response = new Joke();
        response.setJoke(joke);

        return response;
    }

    @ApiMethod(name = "knockknock", httpMethod = ApiMethod.HttpMethod.GET)
    public Joke ping(){
        Joke response = new Joke();
        response.setJoke("Who's there?");

        return response;
    }

}
