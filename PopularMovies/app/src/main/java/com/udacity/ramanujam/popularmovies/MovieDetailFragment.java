package com.udacity.ramanujam.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance() {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        MovieItem movieItemParcel = null;
        if(intent != null) {
            movieItemParcel = (MovieItem) intent.getParcelableExtra("movie");
            ImageView movieImage = (ImageView)rootView.findViewById(R.id.movieImage);
            ((TextView)rootView.findViewById(R.id.movieTitle)).setText(movieItemParcel.getTitle());
            ((TextView)rootView.findViewById(R.id.movieRating)).setText(new Double(movieItemParcel.getUserRating()).toString() + "/10");
            ((TextView)rootView.findViewById(R.id.movieReleaseDate)).setText(movieItemParcel.getReleaseDate());
            ((TextView)rootView.findViewById(R.id.movieSynopsis)).setText(movieItemParcel.getSynopsis());
            Picasso.with(getContext()).load(movieItemParcel.getImageUrl()).into(movieImage);
        }

        return rootView;
    }
}
