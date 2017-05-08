package com.example.kprabhu.moviecraze.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.kprabhu.moviecraze.android";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class  MovieEntry implements BaseColumns{

        public static final int PROJECTION_MOVIE_ID = 1;
        public static final int PROJECTION_MOVIE_BACKDROP_URI = 2;
        public static final int PROJECTION_MOVIE_TITLE = 3;
        public static final int PROJECTION_MOVIE_POSTER = 4;
        public static final int PROJECTION_MOVIE_SYNOPSIS = 5;
        public static final int PROJECTION_MOVIE_VOTE_AVERAGE = 6;
        public static final int PROJECTION_MOVIE_RELEASE_DATE = 7;
        public static final int PROJECTION_MOVIE_FAVORITE = 8;
        public static final int PROJECTION_MOVIE_REVIEWS = 9;
        public static final int PROJECTION_MOVIE_TRAILERS = 10;

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_BACKDROP_URI = "movie_backdrop_path";
        public static final String MOVIE_TITLE = "movie_original_title";
        public static final String MOVIE_POSTER = "movie_poster_path";
        public static final String MOVIE_SYNOPSIS = "movie_synopsis";
        public static final String MOVIE_VOTE_AVERAGE = "movie_vote_average";
        public static final String MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String MOVIE_FAVORITE = "movie_favorite";
        public static final String MOVIE_REVIEWS = "movie_reviews";
        public static final String MOVIE_TRAILERS = "movie_trailers";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
               ContentResolver.CURSOR_DIR_BASE_TYPE+ CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE  + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
