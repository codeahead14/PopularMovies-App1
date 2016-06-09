package com.app.movie.cinephilia;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.design.widget.FloatingActionButton;
import com.app.movie.cinephilia.CastandCrew.CreditsAdapter;
import com.app.movie.cinephilia.CastandCrew.FetchCreditsTask;
import com.app.movie.cinephilia.CastandCrew.MovieCreditsModel;
import com.github.clans.fab.FloatingActionButton;
//import com.github.fab.sample.R;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.ShareActionProvider;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
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
import com.app.movie.cinephilia.trailers.FetchTrailerTask;
import com.app.movie.cinephilia.trailers.MovieTrailerModel;
import com.app.movie.cinephilia.trailers.TrailerAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by GAURAV on 19-12-2015.
 */
public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private boolean mHasData, isExpanded = false, mHasTrailers=false, mHasCredits = false;
    private String trailerUrl = "no URLs at the moment!";
    private Intent intent;
    private MovieModel movie;
    private ArrayList<MovieReviewModel> mReviewData;
    private ArrayList<MovieCreditsModel> mCreditsData;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private CreditsAdapter mCreditsAdapter;
    private LinearLayout mLinearLayoutReview, mLinearLayoutTrailer;
    private static Toolbar toolbar;
    private static CollapsingToolbarLayout collapsingToolbar;
    private ImageView imageView;
    private ContentResolver resolver;
    private String youtubeId, shareYoutube;
    private static Button mButton_Credits;
    private AVLoadingIndicatorDialog dialog;

    /* Floating Action Buttons - Using Clans Library */
    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    // Public members
    public static final String ARG_MOVIE = "movieFragment";
    public static int mMovieId;

    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.v(TAG, "On create");

        intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mHasData = true;
            movie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
            mMovieId = movie.getId();
            FetchMovieElements(mMovieId);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mHasData = true;
                Log.v(TAG, "Arguments !null");
                movie = arguments.getParcelable(Intent.EXTRA_TEXT);
                mMovieId = movie.getId();
                FetchMovieElements(mMovieId);
            }
        }
    }

    @Override
    public void onResume() {
        Log.v(TAG, "On Resume");
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        Log.v(TAG, "On Pause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_layout, container, false);
        setupWidgets(rootView);
        dialog = new AVLoadingIndicatorDialog(getActivity(), rootView);
        dialog.setMessage("Fetching Details.. Just for You!!");
        dialog.show();

        menuRed = (FloatingActionMenu) rootView.findViewById(R.id.menu_red);
        fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) rootView.findViewById(R.id.fab3);

        mButton_Credits = (Button) rootView.findViewById(R.id.credits_button);

        if (mHasData) {
            mLinearLayoutReview = (LinearLayout) rootView.findViewById(R.id.review_list);
            mLinearLayoutTrailer = (LinearLayout) rootView.findViewById(R.id.trailer_list);

            if(!Utility.hasConnection(getContext())){
                Toast.makeText(getActivity(),"Unable to Fetch Trailers and Reviews!! No Connectivity!!",
                                                Toast.LENGTH_LONG).show();
            }

            mButton_Credits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHasCredits){
                        Toast.makeText(getActivity(), "Credits Fetch Completed", Toast.LENGTH_SHORT).show();
                        mCreditsData = mCreditsAdapter.getCreditsData();
                        Intent intent = new Intent(getActivity(), CreditsActivity.class);
                        intent.putExtra("CreditsData", mCreditsData);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
                    }
                }
            });

            TextView titleTextView = (TextView) rootView.findViewById(R.id.text_view_title);
            titleTextView.setText(movie.getTitle());

            // Set poster
            ImageView imageViewPoster = (ImageView) rootView.findViewById(R.id.image_view_poster);
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
            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.text_view_release_date);
            releaseDateTextView.setText(getString(R.string.releaseDate) + ":" + movie.getReleaseDate());

            // Set user rating
            TextView userRatingTextView = (TextView) rootView.findViewById(R.id.text_view_user_rating);
            userRatingTextView.setText(movie.getUserRating() + "/10");

            // Set synopsis
            TextView viewSynopsisTextView = (TextView) rootView.findViewById(R.id.text_view_synopsis);
            viewSynopsisTextView.setText(movie.getSynopsis());

            // Set Vote Count
            TextView voteCountTextView = (TextView) rootView.findViewById(R.id.text_view_vote_count);
            voteCountTextView.setText(getString(R.string.voteCount) + ": " + movie.getVoteCount());

            final TextView trailerTitle = (TextView) rootView.findViewById(R.id.trailer_head);
            trailerTitle.setText("TRAILERS");

            final TextView reviewTitle = (TextView) rootView.findViewById(R.id.review_head);
            reviewTitle.setText("REVIEWS");

            //collapsingToolbar.setTitle(movie.getTitle());
            imageView = (ImageView) rootView.findViewById(R.id.backdrop);
            Picasso.with(getActivity())
                    .load(movie.getBackdropUrl())
                    .fit().centerCrop()
                    .transform(PaletteTransformation.instance())
                    .into(imageView, new PaletteTransformation.PaletteCallback(imageView) {
                        @Override
                        protected void onSuccess(Palette palette) {
                            Palette.Swatch vibrant = palette.getVibrantSwatch();
                            if (vibrant != null){
                                int srcColor = vibrant.getRgb();
                                collapsingToolbar.setContentScrimColor(srcColor);
                                trailerTitle.setTextColor(srcColor);
                                reviewTitle.setTextColor(srcColor);
                                mButton_Credits.setTextColor(srcColor);

                                int darkVibrant = palette.getDarkVibrantColor(vibrant.getRgb());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Window window = getActivity().getWindow();
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(darkVibrant);
                                }
                                /*float lightFactor = 1.5f;
                                int lightVibrant = Color.argb(
                                        Color.alpha(darkVibrant),
                                        Color.red((int)(Color.red(darkVibrant)*lightFactor)),
                                        Color.green((int)(Color.green(darkVibrant)*lightFactor)),
                                        Color.blue((int)(Color.blue(darkVibrant)*lightFactor))
                                );

                                Log.v(TAG,"lightvibrant: "+lightVibrant);
                                collapsingToolbar.setContentScrimColor(lightVibrant);
                                trailerTitle.setTextColor(lightVibrant);
                                reviewTitle.setTextColor(lightVibrant);
                                mButton_Credits.setTextColor(lightVibrant);*/

                                //int darkVibrant = palette.getDarkVibrantColor(vibrant.getRgb());
                                /*float factor = 1.4f;
                                int darkVibrant = Color.argb(
                                        Color.alpha(srcColor),
                                        Color.red((int)(Color.red(srcColor)*factor)),
                                        Color.green((int)(Color.green(srcColor)*factor)),
                                        Color.blue((int)(Color.blue(srcColor)*factor))
                                );
                                Log.v(TAG,"darkvibrant: "+darkVibrant);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Window window = getActivity().getWindow();
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(darkVibrant);
                                }*/
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });


            //menuRed.hideMenuButton(false);
            menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuRed.isOpened()) {
                        //Toast.makeText(getActivity(), menuRed.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                    }
                    menuRed.toggle(true);
                }
            });

            if (isFavourtie(movie.getId()))
                fab1.setImageResource(R.drawable.ic_favorite_white_24dp);
            else
                fab1.setImageResource(R.drawable.ic_favorite_border_white_24dp);

            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFavourtie(movie.getId());
                    updateDatabase(isFavourtie(movie.getId()), movie.getId(), view);
                }
            });

            fab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("DetailFragment","inside Fab2 clicklistener");
                    createShareUrlIntent(trailerUrl);
                }
            });
        }
        return rootView;
    }

    public void FetchMovieElements(int movieId) {
        mReviewData = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, mReviewData);
        mTrailerAdapter = new TrailerAdapter(getActivity(), R.layout.list_item_trailer, new ArrayList<MovieTrailerModel>());
        mCreditsAdapter = new CreditsAdapter(getActivity(), R.layout.list_item_credits, new ArrayList<MovieCreditsModel>());
        if(Utility.hasConnection(getActivity())) {
            // Fetch Review data
            FetchReviewTask reviewTask = new FetchReviewTask(getActivity(), mReviewAdapter);
            reviewTask.execute(Integer.toString(movieId));

            // Fetch Trailer data
            FetchTrailerTask trailerTask = new FetchTrailerTask(getActivity(), mTrailerAdapter);
            trailerTask.execute(Integer.toString(movieId));

            // Fetch Credits data
            FetchCreditsTask creditsTask = new FetchCreditsTask(getActivity(), mCreditsAdapter);
            creditsTask.execute(Integer.toString(movieId));
        }
    }

    public boolean isFavourtie(int movieId) {
        resolver = getContext().getContentResolver();
        Cursor movieCursor = resolver.query(MovieContract.FavoriteMoviesEntry.
                buildFavouriteMoviesUriWithMovieId(movieId), null, null, null, null);
        if (movieCursor.getCount() == 0)
            return false;
        else
            return true;
    }

    public void updateDatabase(boolean isFavourite, int movieId, View view) {
        if (isFavourite) {
            /* Movie already in Favourites - Delete the movie from DB*/
            resolver.delete(MovieContract.FavoriteMoviesEntry.
                    buildFavouriteMoviesUriWithMovieId(movieId), null, null);
            Snackbar.make(view, "REMOVED FROM FAVOURITES!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            fab1.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        } else {
            /*Add values into DB*/
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE, movie.getTitle());
            contentValues.put(FavoriteMoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(FavoriteMoviesEntry.COLUMN_OVERVIEW, movie.getSynopsis());
            contentValues.put(FavoriteMoviesEntry.COLUMN_VOTE_AVG, movie.getUserRating());
            contentValues.put(FavoriteMoviesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            contentValues.put(FavoriteMoviesEntry.COLUMN_POSTER_URL, movie.getPosterUrl());
            contentValues.put(FavoriteMoviesEntry.COLUMN_BACKDROP_URL, movie.getBackdropUrl());

            resolver.insert(FavoriteMoviesEntry.CONTENT_URI, contentValues);

            //Toast.makeText(getContext(), "ADDED TO FAVOURITES!", Toast.LENGTH_SHORT).show();

            Snackbar.make(view, "ADDED TO FAVOURITES!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            fab1.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
    }


    private void createShareUrlIntent(String videoLink) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.setType("text/plain");
        if(mHasTrailers){
            shareYoutube = "https://www.youtube.com/watch?v="+youtubeId;
        }else
            shareYoutube = "No Trailers Available Right Now";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareYoutube);
        startActivity(Intent.createChooser(shareIntent, "Share link using"));
        //return shareIntent;
    }

    private void setupWidgets(View rootView) {
        if (!MainActivity.mTwoPane) {
            toolbar = (Toolbar) rootView.findViewById(R.id.detailstoolbar);
            toolbar.setVisibility(View.VISIBLE);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(movie.getTitle());
        }
    }

    @Subscribe
    public void onMovieLoaded(AsyncTaskResultEvent event) {
        if (event.getResult()) {
            if (event.getName().equals("FetchReviewTask")) {
                for (int iter = 0; iter < mReviewAdapter.getCount(); iter++) {
                    View item = mReviewAdapter.getView(iter, null, null);
                    mLinearLayoutReview.addView(item);
                }
            } else if (event.getName().equals("FetchTrailerTask")) {
                if(mTrailerAdapter.getCount()>0) {
                    mHasTrailers=true;
                    youtubeId = mTrailerAdapter.getItem(0).mKey;
                    //if (mShareActionProvider != null)
                        //mShareActionProvider.setShareIntent(createShareUrlIntent(youtubeId));
                    //trailerUrl = youtubeId;
                    for (int iter = 0; iter < mTrailerAdapter.getCount(); iter++) {
                        View item = mTrailerAdapter.getView(iter, null, null);
                        mLinearLayoutTrailer.addView(item);
                    }
                }
            } else if (event.getName().equals("FetchCreditsTask")){
                for (int iter=0; iter < mCreditsAdapter.getCount(); iter++){
                    mHasCredits = true;
                }

            }
            dialog.dismiss();
        }
    }
}

