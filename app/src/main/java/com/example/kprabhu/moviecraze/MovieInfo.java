package com.example.kprabhu.moviecraze;

import android.app.Service;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kprabhu on 2/15/2017.
 */
public class MovieInfo implements Parcelable {

    private int movieId;
    private String movieTitle;
    private String moviePosterImgUrl;
    private String movieBackdropImgUrl;
    private String movieSynopsis;
    private String movieReleaseDate;
    private double movieVoteAvg;
    private double moviePopularity;
    private int movieVoteCount;
    private String movieTrailers;
    private String movieReviews;


    public MovieInfo(int movieId, String movieTitle, String moviePosterImgUrl, String movieBackdropImgUrl, String movieSynopsis, String movieReleaseDate, double movieVoteAvg, double moviePopularity,
                     int movieVoteCount, String movieTrailers, String movieReviews) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterImgUrl = moviePosterImgUrl;
        this.movieBackdropImgUrl = movieBackdropImgUrl;
        this.movieSynopsis = movieSynopsis;
        this.movieReleaseDate = movieReleaseDate;
        this.movieVoteAvg = movieVoteAvg;
        this.moviePopularity = moviePopularity;
        this.movieVoteCount = movieVoteCount;
        this.movieTrailers = movieTrailers;
        this.movieReviews = movieReviews;

    }

    protected MovieInfo(Parcel in) {
        movieId = in.readInt();
        movieTitle = in.readString();
        moviePosterImgUrl = in.readString();
        movieBackdropImgUrl = in.readString();
        movieSynopsis = in.readString();
        movieReleaseDate = in.readString();
        movieVoteAvg = in.readDouble();
        moviePopularity = in.readDouble();
        movieVoteCount = in.readInt();
        movieTrailers = in.readString();
        movieReviews = in.readString();
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieId);
        parcel.writeString(movieTitle);
        parcel.writeString(moviePosterImgUrl);
        parcel.writeString(movieBackdropImgUrl);
        parcel.writeString(movieSynopsis);
        parcel.writeString(movieReleaseDate);
        parcel.writeDouble(movieVoteAvg);
        parcel.writeDouble(moviePopularity);
        parcel.writeInt(movieVoteCount);
        parcel.writeString(movieTrailers);
        parcel.writeString(movieReviews);
    }

    public String getMoviePosterURL() {
        return moviePosterImgUrl;
    }

    public String getMovieBackdropImgUrl(){return movieBackdropImgUrl;}

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public double getUserRatings() {
        return movieVoteAvg;
    }

    public int getMovieVoteCount(){ return movieVoteCount;}

    public String getMovieTrailers(){ return movieTrailers;}

    public String getMovieReviews(){return movieReviews;}

    public int getMovieID() {return movieId;}
}
