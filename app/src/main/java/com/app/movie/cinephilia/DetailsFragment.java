package com.app.movie.cinephilia;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.movie.cinephilia.DataBus.AsyncTaskResultEvent;
import com.app.movie.cinephilia.DataBus.BusProvider;
import com.app.movie.cinephilia.MovieDBAPIs.MovieContract;
import com.app.movie.cinephilia.MovieDBAPIs.MovieContract.FavoriteMoviesEntry;
import com.app.movie.cinephilia.reviews.FetchReviewTask;
import com.app.movie.cinephilia.reviews.MovieReviewModel;
import com.app.movie.cinephilia.reviews.ReviewAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by GAURAV on 19-12-2015.
 */
public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private boolean mHasData, isExpanded=false;
    private Intent intent;
    private MovieModel movie;
    private ArrayList<MovieReviewModel> mReviewData;
    private ReviewAdapter mReviewAdapter;
    private LinearLayout mLinearLayout;
    private UpdateToolBarWidget mDetailsActivityCallback;
    private static Toolbar toolbar;
    private static CollapsingToolbarLayout collapsingToolbar;
    private static FloatingActionButton fab;
    private static ImageButton mButton;
    private ImageView imageView;

    private ContentResolver resolver;

    private static final String ARG_MOVIE = "movieFragment";

    public interface UpdateToolBarWidget{
        public void setTitleandBackDrop(String title, String backDropPath);
    }

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(MovieModel movie) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDetailsActivityCallback = (UpdateToolBarWidget) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        intent = getActivity().getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mHasData = true;
            // Using Parcelable to get parcel object
            movie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
            // Initializing the ReviewAdapter over listview
            mReviewData = new ArrayList<>();
            mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, mReviewData);
            FetchReviewTask reviewTask = new FetchReviewTask(getActivity(), mReviewAdapter);
            reviewTask.execute(Integer.toString(movie.getId()));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    public boolean isFavourtie(int movieId){
        resolver = getContext().getContentResolver();
        Cursor movieCursor = resolver.query(MovieContract.FavoriteMoviesEntry.
                buildFavouriteMoviesUriWithMovieId(movieId),null,null,null,null);
        if(movieCursor.getCount()==0)
            return false;
        else
            return true;
    }

    public void updateDatabase(boolean isFavourite, int movieId, View view){
        if(isFavourite){
            /* Movie already in Favourites - Delete the movie from DB*/
            resolver.delete(MovieContract.FavoriteMoviesEntry.
                    buildFavouriteMoviesUriWithMovieId(movieId), null, null);
            //Toast.makeText(getContext(), "REMOVED FROM FAVOURITES!", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "REMOVED FROM FAVOURITES!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }else{
            /*Add values into DB*/
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteMoviesEntry.COLUMN_MOVIE_ID,movie.getId());
            contentValues.put(FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE,movie.getTitle());
            contentValues.put(FavoriteMoviesEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate());
            contentValues.put(FavoriteMoviesEntry.COLUMN_OVERVIEW,movie.getSynopsis());
            contentValues.put(FavoriteMoviesEntry.COLUMN_VOTE_AVG,movie.getUserRating());
            contentValues.put(FavoriteMoviesEntry.COLUMN_VOTE_COUNT,movie.getVoteCount());
            contentValues.put(FavoriteMoviesEntry.COLUMN_POSTER_URL,movie.getPosterUrl());
            contentValues.put(FavoriteMoviesEntry.COLUMN_BACKDROP_URL, movie.getBackdropUrl());

            resolver.insert(FavoriteMoviesEntry.CONTENT_URI, contentValues);

            //Toast.makeText(getContext(), "ADDED TO FAVOURITES!", Toast.LENGTH_SHORT).show();

            Snackbar.make(view, "ADDED TO FAVOURITES!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_layout, container, false);
        setupWidgets(rootView);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mButton = (ImageButton)rootView.findViewById(R.id.show_review_button);
        mButton.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);

        if(mHasData){
            mDetailsActivityCallback.setTitleandBackDrop(movie.getTitle(), movie.getBackdropUrl());
            mLinearLayout = (LinearLayout)rootView.findViewById(R.id.review_list);

            if(isFavourtie(movie.getId()))
                fab.setImageResource(R.drawable.ic_favorite_white_24dp);
            else
                fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFavourtie(movie.getId());
                    updateDatabase(isFavourtie(movie.getId()), movie.getId(), view);
                }
            });

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded) {
                        mButton.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                        mLinearLayout.setVisibility(View.GONE);
                        isExpanded = false;
                    }else {
                        isExpanded = true;
                        mButton.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                        mLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            TextView titleTextView = (TextView)rootView.findViewById(R.id.text_view_title);
            titleTextView.setText(movie.getTitle());

            // Set poster
            ImageView imageViewPoster = (ImageView)rootView.findViewById(R.id.image_view_poster);
            Picasso.with(getActivity()).load(movie.getPosterUrl()).into(imageViewPoster);
            imageViewPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), showImageActivity.class);
                    intent.putExtra("image", movie.getPosterUrl());
                    startActivity(intent);
                }
            });

            // Set release date
            TextView releaseDateTextView = (TextView)rootView.findViewById(R.id.text_view_release_date);
            releaseDateTextView.setText(getString(R.string.releaseDate) + ":" + movie.getReleaseDate());

            // Set user rating
            TextView userRatingTextView = (TextView)rootView.findViewById(R.id.text_view_user_rating);
            userRatingTextView.setText(movie.getUserRating() + "/10");

            // Set synopsis
            TextView viewSynopsisTextView = (TextView)rootView.findViewById(R.id.text_view_synopsis);
            viewSynopsisTextView.setText(movie.getSynopsis());

            // Set Vote Count
            TextView voteCountTextView = (TextView)rootView.findViewById(R.id.text_view_vote_count);
            voteCountTextView.setText(getString(R.string.voteCount) + ": " + movie.getVoteCount());

            collapsingToolbar.setTitle(movie.getTitle());
            imageView = (ImageView) rootView.findViewById(R.id.backdrop);
            Picasso.with(getActivity()).load(movie.getBackdropUrl()).into(imageView);

        }
        return rootView;
    }

    private void setupWidgets(View rootView) {
        toolbar = (Toolbar)rootView.findViewById(R.id.detailstoolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
    }

    @Subscribe
    public void onMovieLoaded(AsyncTaskResultEvent event){
        if(event.getResult()){
            for(int iter=0; iter<mReviewAdapter.getCount();iter++){
                View item = mReviewAdapter.getView(iter,null,null);
                //mRelativeLayout.addView(item);
                mLinearLayout.addView(item);
            }
        }
    }
}

