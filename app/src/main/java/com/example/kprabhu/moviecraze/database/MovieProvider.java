package com.example.kprabhu.moviecraze.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {
    private MovieDBHelper mMovieDBHelper;
    private static UriMatcher sUriMatcher = buildUriMatcher();


    private static final int MOVIES = 100;
    private static final int MOVIES_ID = 101;

    public MovieProvider() {
    }

    @Override
    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;    }


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIES_ID);

        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case MOVIES: cursor = getMovies(uri,projection,selection,selectionArgs,sortOrder);
                break;
            case MOVIES_ID: cursor = getMovieWithID(uri,projection,selection,selectionArgs,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri for query: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getMovieWithID(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor movieCursor;
        SQLiteDatabase database = mMovieDBHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MoviesContract.MovieEntry.TABLE_NAME);
        builder.appendWhere(MoviesContract.MovieEntry.MOVIE_ID + "=" + uri.getLastPathSegment());
        movieCursor = builder.query(database,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return movieCursor;
    }

    private Cursor getMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor movieCursor;
        SQLiteDatabase database = mMovieDBHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MoviesContract.MovieEntry.TABLE_NAME);
        movieCursor = builder.query(database,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return movieCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues){
    final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri returnUri;
    switch (match) {
        case MOVIES: {
            long id = db.insertOrThrow(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
            if (id > 0)
                returnUri = MoviesContract.MovieEntry.buildMovieUri(id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
            break;
        }
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
        db.close();
    return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
    final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsDeleted = 0;
        if(selection==null) selection="1";// This makes delete all rows return the number of rows deleted

    switch (match) {
        case MOVIES:
            rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsDeleted!= 0) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME,contentValues,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
