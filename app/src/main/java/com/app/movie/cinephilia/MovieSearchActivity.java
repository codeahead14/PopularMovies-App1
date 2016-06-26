package com.app.movie.cinephilia;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by GAURAV on 26-06-2016.
 */
public class MovieSearchActivity extends AppCompatActivity implements OnMovieDataFetchFinished{
    private static final String TAG = MovieSearchActivity.class.getName();
    private FetchMovieTask fetchMovieTask;
    private SearchListAdapter mSearchAdapter;
    private ArrayList<MovieModel> mSearchList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout activity1 = (LinearLayout) findViewById(R.id.container);
        activity1.setVisibility(View.GONE);
        RelativeLayout activity2 = (RelativeLayout) findViewById(R.id.searchContainer);
        activity2.setVisibility(View.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final EditText editText = (EditText) findViewById(R.id.search_edit);
        editText.setImeActionLabel("Done",KeyEvent.KEYCODE_ENTER);
        mSearchList = null;
        mSearchAdapter = new SearchListAdapter(this,R.layout.list_item_search,new ArrayList<MovieModel>());

        /*Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(TAG,query);
            Toast.makeText(MovieSearchActivity.this, query, Toast.LENGTH_SHORT).show();
            String[] args = {query};
            fetchMovieTask = new FetchMovieTask(this,this,"searchQuery");
            fetchMovieTask.execute(args);
        }*/

        /*TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] args = {s.toString()};
                //fetchMovieTask = new FetchMovieTask(getParent(),MovieSearchActivity.this,"searchQuery");
                //fetchMovieTask.execute(args);
            }
        };*/

        TextView.OnEditorActionListener editorActionListener =  new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //if (!event.isShiftPressed()) {
                        // the user is done typing.
                        String[] args = {editText.getText().toString()};
                    Log.v(TAG,args[0]);
                        Log.v(TAG,args[0]);
                        fetchMovieTask = new FetchMovieTask(MovieSearchActivity.this,MovieSearchActivity.this,"searchQuery");
                        fetchMovieTask.execute(args);
                        return true; // consume.
                    //}
                }
                return false;
            }
        };

        editText.setOnEditorActionListener(editorActionListener);

        //editText.addTextChangedListener(textWatcher);

        ListView listView = (ListView) findViewById(R.id.searchList);
        listView.setAdapter(mSearchAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(TAG,"new intent"+query);
            Toast.makeText(MovieSearchActivity.this, query, Toast.LENGTH_SHORT).show();
            //fetchMovieTask = new FetchMovieTask(this,this,"searchQuery");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*getMenuInflater().inflate(R.menu.menu_details, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search_activity);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.onActionViewExpanded();*/
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/
        return true;
    }

    @Override
    public void MovieDataFetchFinished(ArrayList<MovieModel> movies) {
        Log.v(TAG,"result returned"+movies.size());
        //mSearchAdapter.clear();
        mSearchAdapter.updateList(movies);
    }
}
