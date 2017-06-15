package com.vitech.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class FavoriteMoviesProvider extends ContentProvider {
    FavoriteMovieStorageHelper storageHelper;
    private static final int END_POINT_MOVIES = 400;
    static final UriMatcher matcher = buildUriMatcher();
    static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_MOVIES,END_POINT_MOVIES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        storageHelper = new FavoriteMovieStorageHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = storageHelper.getWritableDatabase();
      Cursor returnUri;
        switch (matcher.match(uri)){
            case END_POINT_MOVIES:
                returnUri = database.query(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
                default:throw new UnsupportedOperationException("Unknown Operation : "+uri.toString());
        }


        return returnUri;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = storageHelper.getWritableDatabase();
        Uri returnUri;
        switch (matcher.match(uri)){
            case END_POINT_MOVIES:
                long id  = database.insertWithOnConflict(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);

                if(id>0) {
                    returnUri = ContentUris.withAppendedId(uri, id);

                }else {
                    throw new SQLiteException("Failed to insert into Database");
                }
                break;
            default:throw new UnsupportedOperationException("Unknown Operation : "+uri.toString());
        }

getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = storageHelper.getWritableDatabase();
        int deleted;
         switch (matcher.match(uri)){
            case END_POINT_MOVIES:
                deleted  = database.delete(FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:throw new UnsupportedOperationException("Unknown Operation : "+uri.toString());
        }

getContext().getContentResolver().notifyChange(uri,null);
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown Operation : "+uri.toString());
    }
}
