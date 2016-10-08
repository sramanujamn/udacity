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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {-@link PopularMoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PopularMoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularMoviesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private PopularMoviesGridViewAdapter popularMoviesGridViewAdapter;
    private ArrayList<MovieItem> popularMoviesDataGrid;
    private GridView popularMoviesGridView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public PopularMoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularMoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularMoviesFragment newInstance(String param1, String param2) {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies_main, container, false);

        popularMoviesGridView = (GridView)rootView.findViewById(R.id.moviesGridView);
        popularMoviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String movie = parent.getItemAtPosition(position).toString();
                MovieItem movie = (MovieItem) parent.getItemAtPosition(position);
                //Toast.makeText(getActivity(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
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




    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
