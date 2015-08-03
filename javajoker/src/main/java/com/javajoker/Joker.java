package com.javajoker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Joker {

    public static Joker jokerSingleton;
    private static List<String> jokeList = new ArrayList<>();

    public static Joker getInstance(){
        if(jokerSingleton == null){
            jokerSingleton = new Joker();

            try {
                retrieveList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jokerSingleton;
    }

    private static void retrieveList() throws IOException {
        InputStream inputStream = Joker.class.getResourceAsStream("/jokes.txt");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder json = new StringBuilder();

        String line;
        while( (line = bufferedReader.readLine()) != null ){
            json.append(line);
        }

        JSONObject jokesJson = new JSONObject(json.toString());
        try{
            JSONArray jokeArray = jokesJson.getJSONArray("jokes");
            for(int i = 0; i<jokeArray.length(); i++){
                String joke = jokeArray.getString(i);
                jokeList.add(joke);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getJokes(){
        return (ArrayList<String>) jokeList;
    }


}
