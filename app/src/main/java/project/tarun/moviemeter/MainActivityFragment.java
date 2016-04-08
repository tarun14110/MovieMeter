package project.tarun.moviemeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by tarun on 5/4/16.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public GridView gridview;
    private MovieDetails[] movieList;          //contains all movie trailer.
    private String Preference;
    private View rootView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String LOG_TAG = FetchPopularMovieList.class.getSimpleName();
        PreferenceManager.setDefaultValues(getActivity().getApplication(), R.xml.pref_general, true);
        FetchPopularMovieList fetchMovieData;

        rootView=inflater.inflate(R.layout.fragment_main, container, false);

        //check whether Movies key is present in sharedpref.
        if (savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
            updateMovie(getActivity(),rootView);
            gridview = (GridView) rootView.findViewById(R.id.gridView);
        }

        //if sharedpref already contain a key Movies
        else {
            movieList = (MovieDetails[]) savedInstanceState.getParcelableArray("Movies");
            String[] movie_list = new String[movieList.length];     //Image path of movie poster.
            for (int i = 0; i < movieList.length; i++) {
                movie_list[i] = movieList[i].getPoster_path();
            }

            gridview = (GridView) rootView.findViewById(R.id.gridView);
            ImageAdapter adapter = new ImageAdapter(getActivity(), movie_list);
            gridview.setAdapter(adapter);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieDetails movie = movieList[position];
                Intent intent = new Intent(getActivity().getApplication(), MovieDetailActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("MOVIE", movie);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie(getActivity(), rootView);    //whenever activity start it will update the content.
    }

    @Override
    public void onSaveInstanceState(Bundle saving_State) {
        saving_State.putParcelableArray("Movies", movieList);
        saving_State.putString("Preference", Preference);
        super.onSaveInstanceState(saving_State);
    }

    public boolean Is_Online(){
        ConnectivityManager connectivity = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    private void updateMovie(Context context, View rootView){
        //If internet is available
        if(Is_Online()==true) {
            FetchPopularMovieList updateMovies = new FetchPopularMovieList(context,rootView);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Preference = sharedPref.getString(getString(R.string.pref_order), getString(R.string.pref_popularity));
            updateMovies.execute(Preference);
        }
        else
        {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.offline_message),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 10);
            toast.show();
        }
    }





    public class FetchPopularMovieList extends AsyncTask<String,Void,MovieDetails[]> {

        private Context mContext;
        private View rootView;

        private final String LOG_TAG = FetchPopularMovieList.class.getSimpleName();
        public  FetchPopularMovieList(Context context, View rootView){
            this.mContext=context;
            this.rootView=rootView;
        }


        protected MovieDetails[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;




            try {
                Uri buildUri = Uri.parse(getString(R.string.movie_url)).buildUpon()
                        .appendQueryParameter(getString(R.string.sort_by), params[0])
                        .appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value))
                        .build();
                Log.e(LOG_TAG, buildUri.toString());
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(getString(R.string.request_method));
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null)
                    movieJsonStr= null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0)
                    movieJsonStr = null;

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("ImageAdaptor", "Error ", e);
                // If the code didn't successfully get the popular movies list, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ImageAdaptor", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }

    @Override
    protected void onPostExecute(MovieDetails[] result) {
        String[] image_url = new String[result.length];
        movieList=result;
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                image_url[i] = result[i].getPoster_path();
                Log.v(LOG_TAG, "Image Url: " + image_url[i]);
            }
            GridView  gridview = (GridView) rootView.findViewById(R.id.gridView);
            ImageAdapter adapter = new ImageAdapter(mContext,  image_url);
            gridview.setAdapter(adapter);
        }
    }


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private MovieDetails[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            Log.v(LOG_TAG, "starting getMovieData : " + movieJsonStr);

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS= "results";

            final String POSTER_PATH = "poster_path";
            final String ADULT = "adult";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String MOVIE_ID ="id";
            final String ORIGINAL_TITLE = "original_title";
            final String ORIGINAL_LANGUAGE = "original_language";
            final String TITLE = "title";
            final String BACKDROP_PATH = "backdrop_path";
            final String POPULARITY = "popularity";
            final String VOTE_COUNT = "vote_count";
            final String VIDEO = "video";
            final String VOTE_AVERAGE ="vote_average";



            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(RESULTS);



            MovieDetails[] movieResultStr = new MovieDetails[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {


                movieResultStr[i] = new MovieDetails();



                // Get the JSON object of movies individually
                JSONObject movieDetailsObj = movieArray.getJSONObject(i);

                movieResultStr[i].setPoster_path(getString(R.string.poster_path)+movieDetailsObj.getString(POSTER_PATH));
                movieResultStr[i].setAdult(movieDetailsObj.getString(ADULT));
                movieResultStr[i].setOverview(movieDetailsObj.getString(OVERVIEW));
                movieResultStr[i].setRelease_date(movieDetailsObj.getString(RELEASE_DATE));
                movieResultStr[i].setMovie_id(movieDetailsObj.getString(MOVIE_ID));
                movieResultStr[i].setOriginal_title(movieDetailsObj.getString(ORIGINAL_TITLE));
                movieResultStr[i].setTitle(movieDetailsObj.getString(TITLE));
                movieResultStr[i].setOriginal_language(movieDetailsObj.getString(ORIGINAL_LANGUAGE));
                movieResultStr[i].setVote_count(movieDetailsObj.getString(VOTE_COUNT));
                movieResultStr[i].setVideo(movieDetailsObj.getString(VIDEO));
                movieResultStr[i].setVote_average(movieDetailsObj.getString(VOTE_AVERAGE));
                movieResultStr[i].setBackdrop_path(getString(R.string.backdrop_path) + movieDetailsObj.getString(BACKDROP_PATH));

            }

            for (MovieDetails s : movieResultStr) {
                Log.v(LOG_TAG, "Movie Title: " + s.getTitle());
                Log.v(LOG_TAG, "Movie Poster Path: " + s.getTitle());
            }
            return movieResultStr;

        }


    }




}




