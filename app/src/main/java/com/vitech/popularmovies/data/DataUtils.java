package com.vitech.popularmovies.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.vitech.popularmovies.R;
import com.vitech.popularmovies.data.FavoriteMoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

import static com.vitech.popularmovies.MovieDetails.ARG_MOVIE_ID;
import static com.vitech.popularmovies.MovieDetails.KEY_ERROR;
import static com.vitech.popularmovies.MovieDetails.KEY_REVIEWS;
import static com.vitech.popularmovies.MovieDetails.KEY_TRAILERS;
import static com.vitech.popularmovies.MoviesDashboard.QUERY_ENDPOINT;
public class DataUtils {
     public   static String getFromWeb(Context context,Bundle args){
        String API_KEY = context.getResources().getString(R.string.api_key);
        String response = "";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(context.getResources().getString(args.getInt(QUERY_ENDPOINT), API_KEY)).openConnection();
            connection.setConnectTimeout(30 * 1000);
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream responseStream = connection.getInputStream();
            Scanner scanner = new Scanner(responseStream);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                response = new JSONObject(scanner.next()).getJSONArray("results").toString();
            }
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof UnknownHostException) {
                response = context.getResources().getString(R.string.no_internet);
            } else if (e instanceof SocketException) {
                response = context.getResources().getString(R.string.connection_error);
            } else if (e instanceof SocketTimeoutException) {
                response = context.getResources().getString(R.string.connection_timed_out);
            } else {
                response = context.getResources().getString(R.string.unknown_error);
            }
        }
        return response;

        }

   public static String getDataFromLocalContentProvider(Context context){
        Cursor cursor = context.getContentResolver().query(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,null,null,null,null);
        JSONArray array = new JSONArray();
        try {
            while (cursor.moveToNext()) {
                array.put(new JSONObject(cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATA))));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return array.toString();
    }

public static void markFavorite(Context context,JSONObject object) throws JSONException{
    ContentValues values = new ContentValues();
    values.put(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATA,object.toString());
    values.put(FavoriteMoviesContract.FavoriteMoviesEntry._ID,object.getInt("id"));
    context.getContentResolver().insert(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,values);
}
public static void removeFavorite(Context context,JSONObject object) throws JSONException{
    context.getContentResolver().delete(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI, FavoriteMoviesContract.FavoriteMoviesEntry._ID +" = "+object.getInt("id"),null);
}

public static HashMap<String,String> getTrailersAndReviewsFromWeb(Context context,Bundle args){
    HashMap<String,String> results = new HashMap<>();
    String API_KEY = context.getResources().getString(R.string.api_key);
    results.put(KEY_ERROR,"null");
    results.put(KEY_TRAILERS,"null");
    results.put(KEY_REVIEWS,"null");

    try {
        HttpURLConnection trailerConnection = (HttpURLConnection) new URL(context.getResources().getString(R.string.url_trailers,args.getInt(ARG_MOVIE_ID), API_KEY)).openConnection();

        trailerConnection.setConnectTimeout(30 * 1000);
        trailerConnection.setRequestMethod("GET");
        trailerConnection.connect();
        InputStream responseStream = trailerConnection.getInputStream();
        Scanner scanner = new Scanner(responseStream);
        scanner.useDelimiter("\\A");
        if (scanner.hasNext()) {
            String response = scanner.next();
            Log.d("response",response);
            results.put(KEY_TRAILERS,new JSONObject(response).getJSONArray("results").toString());
        }

        HttpURLConnection reviewConnection = (HttpURLConnection) new URL(context.getResources().getString(R.string.url_reviews,args.getInt(ARG_MOVIE_ID), API_KEY)).openConnection();
        reviewConnection.setConnectTimeout(30 * 1000);
        reviewConnection.setRequestMethod("GET");
        reviewConnection.connect();
        InputStream reviewStream = reviewConnection.getInputStream();
       Scanner scanner2 = new Scanner(reviewStream);
        scanner2.useDelimiter("\\A");
        if (scanner2.hasNext()) {
            String response = scanner2.next();

            results.put(KEY_REVIEWS,new JSONObject(response).getJSONArray("results").toString());
        }
    }catch (Exception e) {
        e.printStackTrace();
        if (e instanceof UnknownHostException) {
            results.put(KEY_ERROR,context.getResources().getString(R.string.no_internet));

        } else if (e instanceof SocketException) {
            results.put(KEY_ERROR, context.getResources().getString(R.string.connection_error));

        } else if (e instanceof SocketTimeoutException) {
            results.put(KEY_ERROR,context.getResources().getString(R.string.connection_timed_out));

        }
        else {
            results.put(KEY_ERROR,context.getResources().getString(R.string.unknown_error));
        }
    }
    return results;
}


}
