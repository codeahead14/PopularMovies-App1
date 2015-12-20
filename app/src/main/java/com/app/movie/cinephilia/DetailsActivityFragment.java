package com.app.movie.cinephilia;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by GAURAV on 19-12-2015.
 */
public class DetailsActivityFragment extends Fragment {
    private static final String TAG = DetailsActivityFragment.class.getSimpleName();
    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)){
            // Using Parcelable to get parcel object
            MovieModel movie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
            Log.v(TAG, "title: "+movie.getPosterUrl());

            TextView titleTextView = (TextView)rootView.findViewById(R.id.text_view_title);
            titleTextView.setText(movie.getTitle());

            // Set poster
            ImageView imageViewPoster = (ImageView)rootView.findViewById(R.id.image_view_poster);
            Picasso.with(getActivity()).load(movie.getPosterUrl()).into(imageViewPoster);

            // Set release date
            TextView releaseDateTextView = (TextView)rootView.findViewById(R.id.text_view_release_date);
            releaseDateTextView.setText(releaseDateTextView.getText().toString()+"-"+movie.getReleaseDate());

            // Set user rating
            TextView userRatingTextView = (TextView)rootView.findViewById(R.id.text_view_user_rating);
            userRatingTextView.setText(userRatingTextView.getText().toString()+": "+movie.userRating + "/10");

            // Set synopsis
            TextView viewSynopsisTextView = (TextView)rootView.findViewById(R.id.text_view_synopsis);
            viewSynopsisTextView.setText(movie.getSynopsis());

        }
        return rootView;
    }
}

