package com.app.movie.cinephilia;

/**
 * Created by GAURAV on 13-12-2015.
 */

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsActivityFragment())
                    .commit();
        }
    }
}
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/

        // Using Parcelable to get parcel object
       /* MovieModel movie = getIntent().getExtras().getParcelable("com.app.movie.cinephilia.MovieModel");

        TextView titleTextView = (TextView)findViewById(R.id.text_view_title);
        titleTextView.setText(movie.getTitle());

        // Set poster
        ImageView imageViewPoster = (ImageView)findViewById(R.id.image_view_poster);
        Picasso.with(this).load(movie.getPosterUrl()).into(imageViewPoster);

        // Set release date
        TextView releaseDateTextView = (TextView)findViewById(R.id.text_view_release_date);
        releaseDateTextView.setText(movie.getReleaseDate());

        // Set user rating
        TextView userRatingTextView = (TextView)findViewById(R.id.text_view_user_rating);
        userRatingTextView.setText(movie.getUserRating() + "/10");

        // Set synopsis
        TextView viewSynopsisTextView = (TextView)findViewById(R.id.text_view_synopsis);
        viewSynopsisTextView.setText(movie.getSynopsis());

    }
}*/