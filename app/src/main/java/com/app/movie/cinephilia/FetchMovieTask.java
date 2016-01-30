package com.app.movie.cinephilia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by GAURAV on 20-12-2015.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieModel>> {
    private final String LOG_TAG2 = FetchMovieTask.class.getSimpleName();
    String inp_url, JSONResponse;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    private String sort_by = null;
    private ProgressDialog progress;
    private Activity mActivity;
    private GridViewAdapter mGridAdapter;
    private OnMovieDataFetchFinished fetchFinishedCallback;

    private static final int PAGE_LIMIT = 2;
    public static final String TOTAL_PAGES_KEY = "total_pages";
    public static final String TOTAL_RESULTS_KEY = "total_results";
    public static final String RESULTS_KEY = "results";
    public static final String ORIGINAL_TITLE_KEY = "original_title";
    public static final String VOTE_AVERAGE_KEY = "vote_average";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String OVERVIEW_KEY = "overview";
    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String VOTE_COUNT = "vote_count";
    public static final String BACKDROP_PATH_KEY = "backdrop_path";
    public static final String PAGE_NUMBER_KEY = "page";
    public static final String RESULTS_kEY = "results";
    public static final String ID = "id";

    public FetchMovieTask(Activity activity, OnMovieDataFetchFinished callback, GridViewAdapter mGridAdapter){
        this.mActivity = activity;
        this.fetchFinishedCallback = callback;
        this.mGridAdapter = mGridAdapter;
    }



    public ArrayList<MovieModel> getMovies(String sort_by) throws IOException{
        String responseJSONStr;
        ArrayList<MovieModel> movies = new ArrayList<>();

        for(int i=1; i<=PAGE_LIMIT; i++) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", sort_by)
                    .appendQueryParameter("page", String.valueOf(i))
                    .appendQueryParameter("api_key", mActivity.getString(R.string.api_key));
            inp_url = builder.build().toString();

            URL url = new URL(inp_url);
            Log.v(LOG_TAG2,"url: "+url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode;
            try {
                responseCode = urlConnection.getResponseCode();
            }catch (IOException e){
                responseCode = urlConnection.getResponseCode();
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                responseJSONStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                responseJSONStr = null;
            }
            responseJSONStr = buffer.toString();
            try {
                movies.addAll(parseResult(responseJSONStr));
            }catch (JSONException e) {
                Log.e(LOG_TAG2, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return movies;
    }

    @Override
    protected void onPreExecute(){
        progress = new ProgressDialog(mActivity);
        progress.setMessage("Loading Data");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    protected ArrayList<MovieModel> doInBackground(String... params) {
        Log.v(LOG_TAG2,"in background");
        try {
            if(params[0].equals("Most Popular"))
                sort_by = "popularity.desc";
            else if(params[0].equals("Highest Rated"))
                sort_by = "vote_average.asc";
            return getMovies(sort_by);
        } catch (IOException e) {
            JSONResponse = null;
            Log.d(LOG_TAG2, e.getLocalizedMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG2, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieModel> result){
        if(result != null){
            mGridAdapter.clear();
            fetchFinishedCallback.MovieDataFetchFinished(result);
            /*for(MovieModel elem: result) {
                mGridAdapter.add(elem);
            }*/
        }
        progress.dismiss();
    }

    private ArrayList<MovieModel> parseResult(String result) throws JSONException {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("results");
            ArrayList<MovieModel> items = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                MovieModel movie = new MovieModel(post.getString(ORIGINAL_TITLE_KEY),
                        post.getDouble(VOTE_AVERAGE_KEY),
                        post.getString(RELEASE_DATE_KEY),
                        post.getString(OVERVIEW_KEY),
                        post.getString(VOTE_COUNT),
                        post.getString(BACKDROP_PATH_KEY),
                        post.getInt(ID),
                        post.getString(POSTER_PATH_KEY));
                items.add(movie);
            }
            return items;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
