package com.example.kprabhu.moviecraze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.kprabhu.moviecraze.database.MovieUtils;
import com.example.kprabhu.moviecraze.database.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kprabhu on 2/5/2017.
 */
public class MainFragment extends Fragment {

    private RecyclerView gridView;
    private MovieListAdapter mMovieListAdapter;
    private MovieListAdapter.MovieListItemClickListener clickListener = null;
    private ArrayList<MovieInfo> mMovieList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        gridView = (RecyclerView) rootView.findViewById(R.id.movieListRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        gridView.setLayoutManager(gridLayoutManager);

        clickListener = new MovieListAdapter.MovieListItemClickListener() {

            @Override
            public void onMovieListItemClick(int clickedItemPosition) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("movie_info", mMovieList.get(clickedItemPosition));
                startActivity(intent);
            }
        };

        mMovieListAdapter = new MovieListAdapter(getContext(),mMovieList,clickListener);
        gridView.setAdapter(mMovieListAdapter);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateList();
    }

    private void updateList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_default));

        if(sortOrder.equalsIgnoreCase(getString(R.string.pref_order_label_favorites))){
            FetchFavMoviesTask fetchFavMoviesTask = new FetchFavMoviesTask();
            fetchFavMoviesTask.execute();
        }else {
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(sortOrder);
        }
    }

    private class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<MovieInfo>> {
        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {
            Boolean connectivityIssue = false;
            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieCollectionJson = null;

            try {
                URL url = new URL(MovieUtils.buildUrl(params[0]));
                Log.v("ForecastFragment","Built URI:"+ MovieUtils.buildUrl(params[0]));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieCollectionJson = buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(getClass().getName(),"MalformedURLException while fetching json");
                connectivityIssue = true;
            } catch (ProtocolException e) {
                Log.e(getClass().getName(),"ProtocolException while fetching json");
                connectivityIssue = true;
            } catch (IOException e) {
                Log.e(getClass().getName(),"IOException while fetching json");
                connectivityIssue = true;
                movieCollectionJson = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return MovieUtils.getMovieDataFromJson(movieCollectionJson,params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> movieInfos) {
            mMovieListAdapter = new MovieListAdapter(getContext(), movieInfos,clickListener);
            gridView.setAdapter(mMovieListAdapter);
            mMovieListAdapter.notifyDataSetChanged();
            mMovieList=movieInfos;
        }


    }


    private class FetchFavMoviesTask extends AsyncTask<String,Void,ArrayList<MovieInfo>> {

        @Override
        protected ArrayList<MovieInfo> doInBackground(String... strings) {
            mMovieList.clear();
            Cursor movieCursor = MovieCrazeApplication.getAppContext().getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
            if (movieCursor != null && movieCursor.moveToFirst()) {
                do {
                    int movieId = movieCursor.getInt(MoviesContract.MovieEntry.PROJECTION_MOVIE_ID);
                    String moviePosterImgUrl = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_POSTER);
                    String movieBackdropImgUr = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_BACKDROP_URI);
                    String movieTitle = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_TITLE);
                    String movieSynopsis = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_SYNOPSIS);
                    String movieReleaseDate = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_RELEASE_DATE);
                    double movieVoteAvg = movieCursor.getDouble(MoviesContract.MovieEntry.PROJECTION_MOVIE_VOTE_AVERAGE);
                    String movieTrailers = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_TRAILERS);
                    String movieReviews = movieCursor.getString(MoviesContract.MovieEntry.PROJECTION_MOVIE_REVIEWS);

                    MovieInfo info = new MovieInfo(movieId, movieTitle, moviePosterImgUrl, movieBackdropImgUr,
                            movieSynopsis, movieReleaseDate, movieVoteAvg, 0,
                            0, movieTrailers, movieReviews);

                    mMovieList.add(info);
                } while (movieCursor.moveToNext());

            }
            if (movieCursor != null) {
                movieCursor.close();
            }
            return mMovieList;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> movieInfos) {
            mMovieListAdapter = new MovieListAdapter(getActivity(), movieInfos,clickListener);
            gridView.setAdapter(mMovieListAdapter);
            mMovieListAdapter.notifyDataSetChanged();
        }
    }
}
