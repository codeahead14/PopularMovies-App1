package com.app.movie.cinephilia;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class GridViewFragment extends Fragment {

    private static final String TAG = GridViewFragment.class.getSimpleName();
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<MovieModel> mGridData;
    public static final String TOTAL_PAGES_KEY = "total_pages";
    public static final String TOTAL_RESULTS_KEY = "total_results";
    public static final String RESULTS_KEY = "results";
    public static final String ORIGINAL_TITLE_KEY = "original_title";
    public static final String VOTE_AVERAGE_KEY = "vote_average";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String OVERVIEW_KEY = "overview";
    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String PAGE_NUMBER_KEY = "page";
    public static final String RESULTS_kEY = "results";
    public static final String ID = "id";

    public GridViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gridviewfragment, menu);
    }

    public void updateGrid(){
        new FetchMovieTask().execute("45");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("MovieModel", mGridData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateGrid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_main, container, false);
        GridView mGridView = (GridView) rootView.findViewById(R.id.gridview);

        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieModel item = mGridAdapter.getItem(position);
                Log.v(TAG,"movie at "+position+" is "+item.title);
                Intent intent = new Intent(getActivity(), DetailsActivity.class).putExtra(Intent.EXTRA_TEXT, item);
                startActivity(intent);
            }
        });
        //new FetchMovieTask().execute(FEED_URL);
        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieModel>> {
        private final String LOG_TAG2 = FetchMovieTask.class.getSimpleName();
        String inp_url;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String responseJSONStr = null;

        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by","popularity.desc")
                        .appendQueryParameter("api_key","f7097986478ba5bd9b98f5167305fcbd");
                inp_url = builder.build().toString();
                //Log.v(LOG_TAG2, "uri "+inp_url);

                URL url = new URL(inp_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    responseJSONStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    responseJSONStr = null;
                }
                responseJSONStr = buffer.toString();
            } catch (IOException e) {
                responseJSONStr = null;
                Log.d(TAG, e.getLocalizedMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        // Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return parseResult(responseJSONStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> result){
            if(result != null){
                mGridAdapter.clear();
                //mForecastAdapter.addAll(strings);
                for(MovieModel elem: result) {
                    mGridAdapter.add(elem);
                    Log.v(LOG_TAG2, "String element: "+elem);
                }
            }
            //Log.v(LOG_TAG2, "data: "+mGridData.get(1).getTitle());
        }
    }

    private ArrayList<MovieModel> parseResult(String result) throws JSONException {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.getJSONArray("results");
            ArrayList<MovieModel> items = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                //Log.v(TAG,"jsonobject:"+post.getString(POSTER_PATH_KEY));
                MovieModel movie = new MovieModel(post.getString(ORIGINAL_TITLE_KEY),
                        post.getDouble(VOTE_AVERAGE_KEY),
                        post.getString(RELEASE_DATE_KEY),
                        post.getString(OVERVIEW_KEY),
                        post.getString(POSTER_PATH_KEY),
                        post.getInt(ID));
                items.add(movie);
            }
            return items;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
