package com.example.kprabhu.moviecraze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kprabhu on 2/6/2017.
 */

public class MovieListAdapter extends BaseAdapter {
    private final ArrayList<MovieInfo> mMovieList;
    private LayoutInflater inflater;
    Context mContext;

    public MovieListAdapter(Context context, ArrayList<MovieInfo> movieList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mMovieList = movieList;
    }

    @Override
    public int getCount() {
        return mMovieList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovieList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_grid_image, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.gridImage);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.gridImageProgress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(mContext)
                .load(mMovieList.get(position).getMovieImageURL())
//                    .placeholder(R.drawable.ic_stub)
//                    .error(R.drawable.ic_launcher)
                .fit()
                .into(viewHolder.imageView, new Callback() {

                    @Override
                    public void onSuccess() {
                        viewHolder.imageView.setVisibility(View.VISIBLE);
                        viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                        viewHolder.imageView.setVisibility(View.INVISIBLE);
                    }
                });
        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}
