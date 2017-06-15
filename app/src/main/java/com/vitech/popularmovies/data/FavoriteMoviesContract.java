package com.vitech.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class FavoriteMoviesContract {

    public static final String AUTHORITY = "com.vitech.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static class FavoriteMoviesEntry implements BaseColumns{
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_DATA = "movie_data";
        public static final Uri CONTENT_URI  = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


    }

}
