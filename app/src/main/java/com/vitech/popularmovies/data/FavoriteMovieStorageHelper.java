package com.vitech.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vitech.popularmovies.data.FavoriteMoviesContract;


public class FavoriteMovieStorageHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favorite_movies.db";
    static final int DATABASE_VERSION = 1;

    public FavoriteMovieStorageHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE  = "CREATE TABLE "+ FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME+" ("+
                FavoriteMoviesContract.FavoriteMoviesEntry._ID+" INTEGER PRIMARY KEY,"+
                FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATA+" TEXT UNIQUE NOT NULL)";
                db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
