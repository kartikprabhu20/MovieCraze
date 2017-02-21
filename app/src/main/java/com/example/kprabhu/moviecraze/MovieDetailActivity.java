package com.example.kprabhu.moviecraze;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by kprabhu on 2/19/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private Bundle bundle;
    private MovieInfo movie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_activity);

        bundle = getIntent().getExtras();
        movie =  bundle.getParcelable("movie_info");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(movie.getMovieTitle());
        }

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_activity,detailFragment)
                    .commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);

    }

    public static class DetailFragment extends Fragment {

        private ImageView mPosterImg;
        private TextView mSynopsisDes;
        private RatingBar mMovieRatingBar;
        private TextView mMovieReleaseDate;
        private TextView mRatingTextView;


        private MovieInfo mMovieData = new MovieInfo(0/* id */, "title", "url", "url",
                "synopsis", "date", 5/* Avg vote */, 1.0 /* popularity */);

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            initView(rootView);

            Bundle bundle = getArguments();
            if(bundle !=null){
                setData(bundle);
            }

            return rootView;
        }

        private void setData(Bundle bundle) {
            mMovieData = bundle.getParcelable("movie_info");

            mSynopsisDes.setText(mMovieData.getMovieSynopsis());
            mMovieReleaseDate.setText(mMovieData.getMovieReleaseDate());
            mRatingTextView.setText(Double.toString(mMovieData.getUserRatings())+"/10");

            Picasso.with(getContext())
                    .load(mMovieData.getMovieImageURL())
//                    .placeholder(R.drawable.ic_stub)
//                    .error(R.drawable.ic_launcher)
                    .fit()
                    .into(mPosterImg, new Callback() {

                        @Override
                        public void onSuccess() {
                            mPosterImg.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            mPosterImg.setVisibility(View.INVISIBLE);
                        }
                    });

        }

        private void initView(View rootView) {
            mPosterImg = (ImageView) rootView.findViewById(R.id.detail_image_view);
            mSynopsisDes = (TextView) rootView.findViewById(R.id.detail_synopsis);
//            mMovieRatingBar = (RatingBar) rootView.findViewById(R.id.movie_detail_rating_bar);
            mMovieReleaseDate = (TextView) rootView.findViewById(R.id.detail_releaseDate);
            mRatingTextView = (TextView) rootView.findViewById(R.id.detail_userRatings);

        }
    }
}
