package com.app.movie.cinephilia;

import android.app.Activity;
import android.database.DataSetObserver;
import android.media.Image;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.movie.cinephilia.DataBus.AsyncTaskResultEvent;
import com.app.movie.cinephilia.DataBus.BusProvider;
import com.app.movie.cinephilia.reviews.FetchReviewTask;
import com.app.movie.cinephilia.reviews.MovieReviewModel;
import com.app.movie.cinephilia.reviews.ReviewAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by GAURAV on 19-12-2015.
 */
public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private boolean mHasData;
    private Intent intent;
    private MovieModel movie;
    private ArrayList<MovieReviewModel> mReviewData;
    private ReviewAdapter mReviewAdapter;
    private static boolean mReviewDataTaskOver;
    //private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearLayout;
    private UpdateToolBarWidget mDetailsActivityCallback;
    /** ListView for Reviews**/
    ListView reviewList;

    public interface UpdateToolBarWidget{
        public void setTitleandBackDrop(String title, String backDropPath);
    }


    public DetailsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDetailsActivityCallback = (UpdateToolBarWidget) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
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
            /*FetchReviewTask reviewTask = new FetchReviewTask(getActivity(), mReviewAdapter, new OnReviewDataFetchFinished() {
                @Override
                public void reviewDataFetchFinished(boolean taskFinished) {
                    mReviewDataTaskOver = taskFinished;
                }
            });*/
            reviewTask.execute(Integer.toString(movie.getId()));
            // Initializing the TrailerAdapter
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(mHasData){
            mDetailsActivityCallback.setTitleandBackDrop(movie.getTitle(),movie.getBackdropUrl());
            Log.v(TAG,"All true");
            /*reviewList = (ListView)rootView.findViewById(R.id.review_list);
            Utility.setListViewHeightBasedOnChildren(reviewList);
            //reviewList.addHeaderView(rootView,null,false);
            reviewList.setAdapter(mReviewAdapter);
            mReviewAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    //Utility.setListViewHeightBasedOnChildren(reviewList);
                }
            });*/

            //mRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.review_list);
            mLinearLayout = (LinearLayout)rootView.findViewById(R.id.review_list);
            //ListAdapter adapter = mReviewAdapter;
            /*for(int iter=0; iter<mReviewAdapter.getCount();iter++){
                View item = mReviewAdapter.getView(iter,null,null);
                linearlayout.addView(item);
            }*/

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
            voteCountTextView.setText(getString(R.string.voteCount)+": "+ movie.getVoteCount());

        }
        return rootView;
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

