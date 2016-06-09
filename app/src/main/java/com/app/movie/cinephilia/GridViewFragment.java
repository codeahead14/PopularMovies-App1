package com.app.movie.cinephilia;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.movie.cinephilia.MovieDBAPIs.MovieContract;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

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
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private View rootView;
    private AVLoadingIndicatorDialog dialog;

    // Page Number for Endless Scrolling
    private String pageNumber = "1";
    private static int pgIntNum = 1;

    public static final String BUNDLE_TAG = "MoviesList";

    public interface Callback {
        void onItemSelected(MovieModel item);
        void defaultItemSelected(MovieModel item);
    }

    public GridViewFragment() {
    }

    public static GridViewFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GridViewFragment fragment = new GridViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
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

        setHasOptionsMenu(false);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.appbar_menu, menu);

        // Adding Debugging using Stetho
        Stetho.initialize(
                Stetho.newInitializerBuilder(getActivity())
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(getActivity()))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(getActivity()))
                        .build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(R.layout.fragment_main, container, false);
        dialog = new AVLoadingIndicatorDialog(getActivity(), rootView);
        dialog.setMessage("Fetching Awesomeness");

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        //mGridView.setEmptyView(rootView.findViewById(R.id.emptyView));

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final int width = mGridView.getWidth();
                        int numCol = (int) Math.round((double)width/(double)getResources()
                                                            .getDimensionPixelSize(R.dimen.poster_width));
                        mGridView.setNumColumns(numCol);
                    }
                }
        );

        if(Utility.hasConnection(getActivity())){
            dialog.show();
            //startAnim();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieModel item = mGridAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(item);
            }
        });
        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            Log.d(TAG, "trying to restore gridview state..");
            mGridView.onRestoreInstanceState(state);
        }
    }

    public void updateGrid(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = sharedPreferences.getString("Sort",getString(R.string.pref_sort_order));

        if(mPage == 3){
            Log.v(TAG,"Page: "+Integer.toString(mPage));
            getLoaderManager().restartLoader(LOADER_FAVOURITE_MOVIES_ID, null, this);
        }else{
            if(mPage == 1)
                order = "Most Popular";
            else if(mPage == 2)
                order = "Highest Rated";

            pageNumber = Integer.toString(pgIntNum);
            String[] args = {order, pageNumber};
            FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(),this);
            fetchMovieTask.execute(args);
        }

        /*if(order.equals("Show Favorites")){
            Log.v(TAG,"order: "+order);
            getLoaderManager().restartLoader(LOADER_FAVOURITE_MOVIES_ID, null, this);
        }else {
            Log.v(TAG, "fetch order: " + order);
            FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(),this);
            fetchMovieTask.execute(order);
        }*/
    }

    @Override
    public void MovieDataFetchFinished(ArrayList<MovieModel> movies){
        //mGridAdapter.clear();
        mGridAdapter.updateValues(movies);
        ((Callback)getActivity()).defaultItemSelected(movies.get(0));
        //rootView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                pgIntNum++;
                updateGrid();
                return true;
            }
        });
        dialog.cancel();
        //stopAnim();
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
        //rootView.findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
        dialog.cancel();
        //stopAnim();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
