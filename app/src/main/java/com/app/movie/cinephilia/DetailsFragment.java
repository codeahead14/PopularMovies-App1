package com.app.movie.cinephilia;

import android.database.DataSetObserver;
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
import android.widget.TextView;

import com.app.movie.cinephilia.reviews.FetchReviewTask;
import com.app.movie.cinephilia.reviews.MovieReviewModel;
import com.app.movie.cinephilia.reviews.ReviewAdapter;
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

    /** ListView for Reviews**/
    ListView reviewList;

    public DetailsFragment() {
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
            FetchReviewTask reviewTask = new FetchReviewTask(getActivity(),mReviewAdapter);
            reviewTask.execute(Integer.toString(movie.getId()));
            // Initializing the TrailerAdapter
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(mHasData){
            /*reviewList = (ListView)rootView.findViewById(R.id.review_list);
            reviewList.setAdapter(mReviewAdapter);
            mReviewAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    Utility.setListViewHeightBasedOnChildren(reviewList);
                }
            });*/

            LinearLayout linearlayout = (LinearLayout)rootView.findViewById(R.id.review_list);
            ListAdapter adapter = mReviewAdapter;
            for(int iter=0; iter<adapter.getCount();iter++){
                linearlayout.addView(adapter.getView(iter, null, null));
            }

            TextView titleTextView = (TextView)rootView.findViewById(R.id.text_view_title);
            titleTextView.setText(movie.getTitle());

            // Set poster
            ImageView imageViewPoster = (ImageView)rootView.findViewById(R.id.image_view_poster);
            Picasso.with(getActivity()).load(movie.getPosterUrl()).into(imageViewPoster);
            imageViewPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), showImageActivity.class);
                    intent.putExtra("image",movie.getPosterUrl());
                    startActivity(intent);
                }
            });

            // Set release date
            TextView releaseDateTextView = (TextView)rootView.findViewById(R.id.text_view_release_date);
            releaseDateTextView.setText(getString(R.string.releaseDate) + ":" + movie.getReleaseDate());

            // Set user rating
            TextView userRatingTextView = (TextView)rootView.findViewById(R.id.text_view_user_rating);
            userRatingTextView.setText(getString(R.string.userRatings)+": "+movie.getUserRating() + "/10");

            // Set synopsis
            TextView viewSynopsisTextView = (TextView)rootView.findViewById(R.id.text_view_synopsis);
            viewSynopsisTextView.setText(movie.getSynopsis());

            // Set Vote Count
            TextView voteCountTextView = (TextView)rootView.findViewById(R.id.text_view_vote_count);
            voteCountTextView.setText(getString(R.string.voteCount)+": "+ movie.getVoteCount());

        }
        return rootView;
    }
}

