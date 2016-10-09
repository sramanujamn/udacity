package com.udacity.ramanujam.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.ramanujam.popularmovies.databinding.FragmentMovieDetailBinding;

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
            FragmentMovieDetailBinding binding = DataBindingUtil.bind(rootView);
            binding.movieTitle.setText(movieItemParcel.getTitle());
            binding.movieRating.setText(new Double(movieItemParcel.getUserRating()).toString() + "/10");
            binding.movieReleaseDate.setText(movieItemParcel.getReleaseDate());
            binding.movieSynopsis.setText(movieItemParcel.getSynopsis());
            ImageView movieImage = binding.movieImage;
            Picasso.with(getContext()).load(movieItemParcel.getImageUrl()).into(movieImage);
        }

        return rootView;
    }
}
