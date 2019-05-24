package com.nanodegree.android.stevenson.popularmovies.data.network.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nanodegree.android.stevenson.popularmovies.models.Movie;

import java.lang.reflect.Type;
import java.util.List;

public class MoviesDeserializer implements JsonDeserializer<List<Movie>> {

    private static final String RESULTS_KEY = "results";

    @Override
    public List<Movie> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonElement results = json.getAsJsonObject().get(RESULTS_KEY);
        Type listType = new TypeToken<List<Movie>>() {}.getType();

        return new Gson().fromJson(results, listType);
    }
}
