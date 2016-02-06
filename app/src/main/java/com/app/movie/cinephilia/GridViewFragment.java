package com.app.movie.cinephilia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.Toast;

import com.app.movie.cinephilia.MovieDBAPIs.MovieContract;
import com.facebook.stetho.Stetho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class GridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                        OnMovieDataFetchFinished {

    private static final String TAG = GridViewFragment.class.getSimpleName();
    private GridViewAdapter mGridAdapter;
    private ArrayList<MovieModel> mGridData;
    private static final int LOADER_FAVOURITE_MOVIES_ID = 1001;
    private static final String MOVIES_TAG = "MovieModel";
    private GridView mGridView;
    private Bundle bundle;

    public static final String BUNDLE_TAG = "MoviesList";

    public interface Callback {
        public void onItemSelected(MovieModel item);
        public void defaultItemSelected(MovieModel item);
    }

    public GridViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridData = null;
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, new ArrayList<MovieModel>());
        if (savedInstanceState != null) {
            mGridData = savedInstanceState.getParcelableArrayList(MOVIES_TAG);
        }
        if (mGridData == null) {
            updateGrid();
        } else {
            mGridAdapter.updateValues(mGridData);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gridviewfragment, menu);

        // Adding Debugging using Stetho
        Stetho.initialize(
                Stetho.newInitializerBuilder(getActivity())
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(getActivity()))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(getActivity()))
                        .build());
    }

    public void updateGrid(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = sharedPreferences.getString("Sort",getString(R.string.pref_sort_order));

        if(order.equals("Show Favorites")){
            Log.v(TAG,"order: "+order);
            getLoaderManager().restartLoader(LOADER_FAVOURITE_MOVIES_ID, null, this);
        }else {
            Log.v(TAG, "fetch order: " + order);
            FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(),this);
            fetchMovieTask.execute(order);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList(MOVIES_TAG, mGridAdapter.getMovies());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateGrid();
    }

    Parcelable state;
    @Override
    public void onPause(){
        state = mGridView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void MovieDataFetchFinished(ArrayList<MovieModel> movies){
        mGridAdapter.clear();
        mGridAdapter.updateValues(movies);
        ((Callback)getActivity()).defaultItemSelected(movies.get(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.v(TAG,"On Createview");
        View rootView = (View) inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setEmptyView(rootView.findViewById(R.id.emptyView));

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridView.setAdapter(mGridAdapter);
        Log.v(TAG, "view count: " + mGridAdapter.getCount());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieModel item = mGridAdapter.getItem(position);
                //bundle.putParcelable(BUNDLE_TAG,item);
                ((Callback) getActivity()).onItemSelected(item);
                //Intent intent = new Intent(getActivity(), DetailsActivity.class).putExtra(Intent.EXTRA_TEXT, item);
                //startActivity(intent);
            }
        });

        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            mGridView.onRestoreInstanceState(state);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mGridAdapter.clear();
        return new CursorLoader(getActivity(), MovieContract.FavoriteMoviesEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ArrayList<MovieModel> movies = new ArrayList<>();
        mGridAdapter.clear();
        Log.v(TAG,"fragment count: "+Integer.toString(cursor.getCount()));
        while (cursor.moveToNext()) {
            Log.v(TAG,"title: "+cursor.getString(
                    cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE)));
            MovieModel movie = new MovieModel(cursor.getString(
                            cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE)),
                    cursor.getDouble(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_VOTE_AVG)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_OVERVIEW)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_VOTE_COUNT)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_BACKDROP_URL)),
                    cursor.getInt(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_POSTER_URL)));
            movies.add(movie);
        }
        cursor.close();
        mGridAdapter.updateValues(movies);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
