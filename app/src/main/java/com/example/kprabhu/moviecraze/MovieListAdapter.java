package com.example.kprabhu.moviecraze;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kprabhu on 2/6/2017.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieGridViewHolder> {
    private final ArrayList<MovieInfo> mMovieList;
    Context mContext;
    final private MovieListItemClickListener mOnClickListener;

    public MovieListAdapter(Context context, ArrayList<MovieInfo> movieList, MovieListItemClickListener listener) {
        mContext = context;
        mMovieList = movieList;
        mOnClickListener = listener;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_image,parent,false);
        return new MovieGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieGridViewHolder holder, int position) {
        if (holder != null && holder instanceof MovieGridViewHolder) {

            Picasso.with(mContext)
                .load(mMovieList.get(position).getMoviePosterURL())
                .placeholder(R.drawable.placeholder_poster)
                .error(R.mipmap.ic_launcher)
                .fit()
                .into(holder.imageView, new Callback() {

                    @Override
                    public void onSuccess() {
                        holder.imageView.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.imageView.setVisibility(View.INVISIBLE);
                    }
                });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    class MovieGridViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        ProgressBar progressBar;

        public MovieGridViewHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.gridImage);
            progressBar= (ProgressBar) itemView.findViewById(R.id.gridImageProgress);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int clickedPosition = getLayoutPosition();
            mOnClickListener.onMovieListItemClick(clickedPosition);
        }
    }

    public interface  MovieListItemClickListener{
        void onMovieListItemClick(int clickedItemPosition);
    }


}
