package com.app.movie.cinephilia;

import android.media.Image;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by GAURAV on 19-12-2015.
 */
public class DetailsActivityFragment extends Fragment {
    private static final String TAG = DetailsActivityFragment.class.getSimpleName();
    private ImageView imageView;

    public DetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)){
            // Using Parcelable to get parcel object
            final MovieModel movie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);

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

