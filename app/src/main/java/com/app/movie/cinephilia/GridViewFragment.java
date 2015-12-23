package com.app.movie.cinephilia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
    private GridViewAdapter mGridAdapter;
    private ArrayList<MovieModel> mGridData;
    public GridViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gridviewfragment, menu);
    }

    public void updateGrid(){
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), mGridAdapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = sharedPreferences.getString("Sort",getString(R.string.pref_sort_order));
        fetchMovieTask.execute(order, Integer.toString(5));
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
                Intent intent = new Intent(getActivity(), DetailsActivity.class).putExtra(Intent.EXTRA_TEXT, item);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
