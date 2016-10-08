package com.udacity.ramanujam.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PopularMoviesFragment extends Fragment {
    private static final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private PopularMoviesGridViewAdapter popularMoviesGridViewAdapter;
    private ArrayList<MovieItem> popularMoviesDataGrid;
    private GridView popularMoviesGridView;

    public PopularMoviesFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PopularMoviesFragment newInstance() {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies_main, container, false);

        popularMoviesGridView = (GridView)rootView.findViewById(R.id.moviesGridView);
        popularMoviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie = (MovieItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), com.udacity.ramanujam.popularmovies.MovieDetailActivity.class)
                        .putExtra("movie", (Parcelable)movie);
                startActivity(intent);
            }
        });
        popularMoviesDataGrid = new ArrayList<MovieItem>();
        popularMoviesGridViewAdapter = new PopularMoviesGridViewAdapter(getActivity(), R.layout.fragment_popular_movies, popularMoviesDataGrid);
        popularMoviesGridView.setAdapter(popularMoviesGridViewAdapter);

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_popular_movies_main, container, false);
        updateMovies();
        return rootView;
    }

    public class FetchPopularMovies extends AsyncTask<String, Void, Integer> {

        final String LOG_TAG = FetchPopularMovies.class.getSimpleName();
        StringBuilder stringBuilder;

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = 0;
            if(strings.length < 1) {
                return null;
            }

            final String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
            final String BASE_URL_TOP_RATING = "http://api.themoviedb.org/3/movie/top_rated";
            final String API_KEY_PARAM = "api_key";
            final String API_KEY = "";
            HttpURLConnection httpURLConnection = null;

            BufferedReader reader = null;

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String preferredUrl = sharedPreferences.getString(getString(R.string.pref_sortby_key),
                    getString(R.string.pref_sortby_value));


            try {
                Uri uri = Uri.parse((preferredUrl.equals(getString(R.string.pref_sortby_value)) ? BASE_URL_POPULAR : BASE_URL_TOP_RATING))
                        .buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();
                URL url = new URL(uri.toString());
                Log.i(LOG_TAG, "URL formed: " + uri.toString());
                Log.i(LOG_TAG, "PrefURL" + preferredUrl +", pref_sortby_value" + R.string.pref_sortby_value);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                stringBuilder = new StringBuilder();

                if(inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append('\n');
                }

                if(stringBuilder.length() == 0) {
                    return null;
                }
                populateMovieDataFromJson(stringBuilder.toString());

                reader.close();

                popularMoviesDataGrid = populateMovieDataFromJson(stringBuilder.toString());
                result = 1;
                //return stringBuilder.toString();
            } catch(Exception e) {
                Log.e(LOG_TAG, "Error: ", e);
            } finally {
                if(httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch(Exception e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }

                }
            }

            return result;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Integer result) {
            //if(result == 1) {
                //popularMoviesGridViewAdapter.clear();
                //for(MovieItem movieItem : moviesGrid) {
                //    popularMoviesGridViewAdapter.add(movieItem);
                //}
                Log.i(LOG_TAG, "Reached onPostExecute()" + popularMoviesDataGrid.size());
                popularMoviesGridViewAdapter = new PopularMoviesGridViewAdapter(getActivity(), R.layout.fragment_popular_movies, popularMoviesDataGrid);

                popularMoviesGridView.setAdapter(popularMoviesGridViewAdapter);
                //popularMoviesGridViewAdapter.setGridData(popularMoviesDataGrid);
                //popularMoviesGridView.setAdapter(popularMoviesGridViewAdapter);
                Log.i(LOG_TAG, "Testing: " + popularMoviesDataGrid.get(1).getImageUrl());
            //}
        }
    }

    private ArrayList<MovieItem> populateMovieDataFromJson(String movieJsonString) throws JSONException {
        final String MOVIE_LIST = "results";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_USER_RATING = "vote_average";
        final String MOVIE_POSTER_SIZE = "w185";

        JSONObject movieJSONObject = new JSONObject(movieJsonString);
        JSONArray moviesArray = movieJSONObject.getJSONArray(MOVIE_LIST);

        ArrayList<MovieItem> moviesDataGrid = new ArrayList<MovieItem>();

        String[] resultsStr = new String[moviesArray.length()];

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            MovieItem movieItem = new MovieItem(
                    new StringBuilder().append(MOVIE_POSTER_BASE_URL).append(MOVIE_POSTER_SIZE).append(movie.getString(MOVIE_POSTER_PATH)).toString(),
                    movie.getString(MOVIE_TITLE)
            );
            movieItem.setReleaseDate(movie.getString(MOVIE_RELEASE_DATE));
            movieItem.setSynopsis(movie.getString(MOVIE_SYNOPSIS));
            movieItem.setUserRating(movie.getDouble(MOVIE_USER_RATING));
            moviesDataGrid.add(movieItem);

        }

        return moviesDataGrid;
    }

    private void updateMovies() {
        new FetchPopularMovies().execute("test");
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateMovies();
    }

}
