package com.udacity.ramanujam.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by padmavidhya on 9/19/2016.
 */
public class PopularMoviesGridViewAdapter extends ArrayAdapter<MovieItem> {

    private Context context;
    private int resourceLayoutId;
    private ArrayList<MovieItem> popularMoviesGrid = new ArrayList<MovieItem>();
    private static final String LOG_TAG = PopularMoviesGridViewAdapter.class.getSimpleName();

    public PopularMoviesGridViewAdapter(Context context, int resource, ArrayList<MovieItem> popularMoviesGrid) {
        super(context, resource, popularMoviesGrid);
        this.context = context;
        this.resourceLayoutId = resource;
        this.popularMoviesGrid = popularMoviesGrid;
    }

    public void setGridData(ArrayList<MovieItem> gridData) {
        this.popularMoviesGrid = gridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MoviesViewHolder holder;

        if(view == null) {
            //LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
            //row = layoutInflater.inflate(resourceLayoutId, parent, false);
            view = LayoutInflater.from(getContext()).inflate(resourceLayoutId, parent, false);
            holder = new MoviesViewHolder();
            holder.movieImage = (ImageView)view.findViewById(R.id.movieGridItemImage);
            holder.movieTitle = (TextView)view.findViewById(R.id.movieGridItemTitle);
            view.setTag(holder);
        } else {
            holder = (MoviesViewHolder)view.getTag();
        }


        MovieItem movieItem = popularMoviesGrid.get(position);
        holder.movieTitle.setText(movieItem.getTitle());
        Picasso.with(context).load(movieItem.getImageUrl()).into(holder.movieImage);
        return view;
    }

    class MoviesViewHolder {
        ImageView movieImage;
        TextView movieTitle;

    }
}
