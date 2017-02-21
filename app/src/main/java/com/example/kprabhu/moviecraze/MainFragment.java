package com.example.kprabhu.moviecraze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    GridView gridView;


    private MovieListAdapter mMovieListAdapter;
    private final ArrayList<MovieInfo> mMovieList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        gridView = (GridView) rootView.findViewById(R.id.main_grid);
        mMovieListAdapter = new MovieListAdapter(getContext(),mMovieList);
        gridView.setAdapter(mMovieListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("movie_info", (Parcelable) mMovieListAdapter.getItem(position));
                startActivity(intent);            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragmentmenu , menu);
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
        FetchMoviesTask weatherTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_default));
        weatherTask.execute(sortOrder);
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

            String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";
            final String APPID_VALUE = "get_api_key_from_www.themoviedb.org";

            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM,APPID_VALUE).build();
                URL url = new URL(builtUri.toString());
                Log.v("ForecastFragment","Built URI:"+ builtUri.toString());

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


            return getMovieDataFromJson(movieCollectionJson,params[0]);

            //return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> movieInfos) {
            mMovieListAdapter = new MovieListAdapter(getContext(), movieInfos);
            gridView.setAdapter(mMovieListAdapter);
        }

        private ArrayList<MovieInfo> getMovieDataFromJson(String movieCollection, String param) {
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

                    moviePosterImgUrl = IMAGE_BASE_URL + "w342" + moviePosterImgUrl;

                    if (!movieBackdropImgUr.equals("null")) {
                        movieBackdropImgUr = IMAGE_BASE_URL + "w500" + movieBackdropImgUr;
                    }

                    MovieInfo info = new MovieInfo(movieId, movieTitle, moviePosterImgUrl, movieBackdropImgUr,
                            movieSynopsis, movieReleaseDate, movieVoteAvg, moviePopularity);

                    movieList.add(info);
                }}
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movieList;
        }
    }
}
