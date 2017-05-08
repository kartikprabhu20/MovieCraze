package com.example.kprabhu.moviecraze.database;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.kprabhu.moviecraze.MovieCrazeApplication;
import com.example.kprabhu.moviecraze.MovieInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieUtils {
    private static final String TAG = "MovieUtils";

    public static String getApiKey() {
        try {
            Context appContext = MovieCrazeApplication.getAppContext();
            ApplicationInfo ai = appContext.getPackageManager().getApplicationInfo(appContext.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString("api_key");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }

    public static String buildUrl(String sortOrder) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sortOrder)
                .appendQueryParameter("api_key", getApiKey());
        return builder.build().toString();
    }

    private static String buildUrlWithMovieID(int movieId,String type) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Integer.toString(movieId))
                .appendPath(type)
                .appendQueryParameter("api_key", getApiKey()).appendQueryParameter("language", "en-US");
        return builder.build().toString();
    }

    public static ArrayList<MovieInfo> getMovieDataFromJson(String movieCollection, String param) {
        ArrayList<MovieInfo> movieList = new ArrayList<>();
        try {
            String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
            JSONObject jsonObject = new JSONObject(movieCollection);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if(jsonArray!=null){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    int movieId = object.getInt("id");
                    String moviePosterImgUrl = object.getString("poster_path");
                    String movieBackdropImgUr = object.getString("backdrop_path");
                    String movieTitle = object.getString("original_title");
                    String movieSynopsis = object.getString("overview");
                    String movieReleaseDate = object.getString("release_date");
                    double movieVoteAvg = object.getDouble("vote_average");
                    double moviePopularity = object.getDouble("popularity");
                    int movieVoteCount = object.getInt("vote_count");
                    String movieTrailers = getMovieTrailerOrReview(movieId, "videos");
                    String movieReviews = getMovieTrailerOrReview(movieId, "reviews");

                    moviePosterImgUrl = IMAGE_BASE_URL + "w342" + moviePosterImgUrl;

                    if (!movieBackdropImgUr.equals("null")) {
                        movieBackdropImgUr = IMAGE_BASE_URL + "w500" + movieBackdropImgUr;
                    }

                    MovieInfo info = new MovieInfo(movieId, movieTitle, moviePosterImgUrl, movieBackdropImgUr,
                            movieSynopsis, movieReleaseDate, movieVoteAvg, moviePopularity,
                            movieVoteCount,movieTrailers,movieReviews);

                    movieList.add(info);
                }}
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieList;
    }


    private static String getMovieTrailerOrReview(int movieId, String type) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String movieJson = null;
        try {
            URL url = new URL(buildUrlWithMovieID(movieId,type));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                movieJson = null;
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    movieJson = null;
                }
                movieJson = buffer.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            movieJson = null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return movieJson;
    }

    public static JSONArray getTrailers(MovieInfo movieInfo){
        JSONObject jsonObject;
        JSONArray trailerArray = new JSONArray();
        try {
            jsonObject = new JSONObject(movieInfo.getMovieTrailers());
            trailerArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            Log.e("MovieUtils","JSONException");
        }

        return trailerArray;
    }
}
