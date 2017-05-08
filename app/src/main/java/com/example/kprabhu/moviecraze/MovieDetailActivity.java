package com.example.kprabhu.moviecraze;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.Toast;

import com.example.kprabhu.moviecraze.database.MovieUtils;
import com.example.kprabhu.moviecraze.database.MoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kprabhu on 2/19/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mPosterImg,mBackdropImg,mFavoriteImageView;
    private TextView mSynopsisDes,mMovieReleaseDate,mRatingTextView;
    private RatingBar mMovieRatingBar;

    private MovieInfo mMovieData = new MovieInfo(0/* id */, "title", "url", "url",
            "synopsis", "date", 5/* Avg vote */, 1.0 /* popularity */,0,"trailer","review");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mMovieData =  bundle.getParcelable("movie_info");
        initView();
        setData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);

    }

    private void initView() {
        setContentView(R.layout.detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details_activity);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mMovieData.getMovieTitle());

        mPosterImg = (ImageView) findViewById(R.id.detail_image_view);
        mSynopsisDes = (TextView) findViewById(R.id.detail_synopsis);
//      mMovieRatingBar = (RatingBar) findViewById(R.id.movie_detail_rating_bar);
        mMovieReleaseDate = (TextView) findViewById(R.id.detail_releaseDate);
        mRatingTextView = (TextView) findViewById(R.id.detail_userRatings);
        mBackdropImg = (ImageView) findViewById(R.id.backdrop);
        mFavoriteImageView = (ImageView) findViewById(R.id.fav_img);
    }

    private void setData() {
        mSynopsisDes.setText(mMovieData.getMovieSynopsis());
        mMovieReleaseDate.setText(mMovieData.getMovieReleaseDate());
        mRatingTextView.setText(Double.toString(mMovieData.getUserRatings()) + "/10");

        Picasso.with(getApplicationContext())
                .load(mMovieData.getMoviePosterURL())
                .placeholder(R.drawable.placeholder_poster)
                .error(R.mipmap.ic_launcher)
                .fit()
                .into(mPosterImg);

        if (!TextUtils.isEmpty(mMovieData.getMovieBackdropImgUrl()))
            Picasso.with(getApplicationContext())
                    .load(mMovieData.getMovieBackdropImgUrl())
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.mipmap.ic_launcher)
                    .fit()
                    .into(mBackdropImg);

        setFavorite();
        mFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavorite()) {
                    addToFavorites(mMovieData.getMovieID());
                } else {
                    removeFromFavorites(mMovieData.getMovieID());
                }
                setFavorite();
            }
        });

        setRelatedVideos(MovieUtils.getTrailers(mMovieData));
        setReviews();
    }


    private void setFavorite() {
        if (isFavorite()) {
            mFavoriteImageView.setImageResource(R.drawable.favorite_added);
        } else {
            mFavoriteImageView.setImageResource(R.drawable.favorite_removed);
        }
    }

    private void removeFromFavorites(int movieID) {
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = this.getContentResolver();
        resolver.delete(uri, MoviesContract.MovieEntry.MOVIE_ID + " = ? ",
                new String[]{movieID + ""});
    }

    private void addToFavorites(int movieID) {
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(MoviesContract.MovieEntry.MOVIE_ID, movieID);
        values.put(MoviesContract.MovieEntry.MOVIE_BACKDROP_URI, mMovieData.getMovieBackdropImgUrl());
        values.put(MoviesContract.MovieEntry.MOVIE_TITLE, mMovieData.getMovieTitle());
        values.put(MoviesContract.MovieEntry.MOVIE_POSTER, mMovieData.getMoviePosterURL());
        values.put(MoviesContract.MovieEntry.MOVIE_SYNOPSIS, mMovieData.getMovieSynopsis());
        values.put(MoviesContract.MovieEntry.MOVIE_VOTE_AVERAGE, mMovieData.getMovieVoteCount());
        values.put(MoviesContract.MovieEntry.MOVIE_RELEASE_DATE, mMovieData.getMovieReleaseDate());
        values.put(MoviesContract.MovieEntry.MOVIE_REVIEWS, mMovieData.getMovieReviews());
        values.put(MoviesContract.MovieEntry.MOVIE_TRAILERS, mMovieData.getMovieTrailers());
        resolver.insert(uri, values);
    }

    public boolean isFavorite() {
        Uri uri = MoviesContract.MovieEntry.buildMovieUri(mMovieData.getMovieID());
        ContentResolver cr =  this.getContentResolver();
        Cursor cursor = null;
        try{
            cursor = cr.query(uri,null,null,null,null);
            if(cursor!=null && cursor.moveToFirst())
                return true;
        }finally {
            if(cursor != null) cursor.close();
        }
        return false;
    }

    public void setRelatedVideos(JSONArray trailers) {
        if(trailers.length() > 0){
            LinearLayout trailerLinearLayout = (LinearLayout) this.findViewById(R.id.linearlayout_for_trailers);
            trailerLinearLayout.setVisibility(View.VISIBLE);
            trailerLinearLayout.addView(setHeaderTextView("Related Videos:"));

            for (int i=0; i< trailers.length(); ++i){
                try {
                    JSONObject trailer = trailers.getJSONObject(i);
                    final String trailerId = trailer.getString("key");

                    LinearLayout trailerItemLayout = new LinearLayout(this);
                    trailerItemLayout.setOrientation(LinearLayout.HORIZONTAL);
                    trailerItemLayout.addView(setImageView());
                    trailerItemLayout.addView(setNormalTextView(trailer.getString("name")));
                    trailerLinearLayout.addView(trailerItemLayout);

                    trailerItemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,  Uri.parse("vnd.youtube:" + trailerId));
                            intent.putExtra("VIDEO_ID", trailerId);

                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                Toast.makeText(getApplicationContext(),"Youtube app does not exist", Toast.LENGTH_SHORT).show();                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.e("setTrailers","JSONException");                }

            }
        }
    }


    private void setReviews() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(mMovieData.getMovieReviews());
            JSONArray reviews = jsonObject.getJSONArray("results");
            if (reviews.length() > 0) {
                LinearLayout reviewLinearLayout = (LinearLayout) this.findViewById(R.id.linearlayout_for_reviews);
                reviewLinearLayout.setVisibility(View.VISIBLE);
                reviewLinearLayout.addView(setHeaderTextView("Reviews:"));

                for (int i=0; i< reviews.length(); ++i) {
                        JSONObject review = reviews.getJSONObject(i);

                        LinearLayout reviewItemLayout = new LinearLayout(this);
                        reviewItemLayout.setOrientation(LinearLayout.VERTICAL);
                        TextView authorTextView = setNormalTextView(review.getString("author"));
                        authorTextView.setTypeface(null, Typeface.BOLD);
                        reviewItemLayout.addView(authorTextView);
                        reviewItemLayout.addView(setNormalTextView(review.getString("content")));
                        reviewLinearLayout.addView(reviewItemLayout);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TextView setHeaderTextView(String header){
        TextView textHeader = new TextView(this);
        textHeader.setText(header);
        textHeader.setTypeface(null, Typeface.BOLD_ITALIC);
        textHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        return textHeader;
    }

    public TextView setNormalTextView(String inputText){
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());;
        textView.setText(inputText);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        return textView;
    }

    public ImageView setImageView(){
         ImageView imageView= new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());;
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        return imageView;
    }
}
