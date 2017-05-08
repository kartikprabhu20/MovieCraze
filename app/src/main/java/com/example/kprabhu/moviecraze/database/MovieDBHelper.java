package com.example.kprabhu.moviecraze.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kprabhu.moviecraze.database.MoviesContract.MovieEntry;

public class MovieDBHelper extends SQLiteOpenHelper {
     public static final String DATABASE_NAME = "movieCraze.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_MOVIECRAZE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.MOVIE_ID + " TEXT UNIQUE NOT NULL," +
                MovieEntry.MOVIE_BACKDROP_URI + " TEXT NOT NULL," +
                MovieEntry.MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_POSTER + " TEXT NOT NULL," +
                MovieEntry.MOVIE_SYNOPSIS + " TEXT NOT NULL," +
                MovieEntry.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.MOVIE_FAVORITE + " INTEGER DEFAULT 0," +
                MovieEntry.MOVIE_REVIEWS + " TEXT NOT NULL," +
                MovieEntry.MOVIE_TRAILERS + " TEXT NOT NULL," +
                "UNIQUE (" + MovieEntry.MOVIE_ID + ") ON CONFLICT IGNORE" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIECRAZE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
