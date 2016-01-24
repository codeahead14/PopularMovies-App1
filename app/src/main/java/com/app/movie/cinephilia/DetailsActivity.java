package com.app.movie.cinephilia;

/**
 * Created by GAURAV on 13-12-2015.
 */

import android.os.Bundle;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.app.movie.cinephilia.DetailsFragment.UpdateToolBarWidget;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity
                    implements DetailsFragment.UpdateToolBarWidget{

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        Toolbar toolbar = (Toolbar)findViewById(R.id.detailstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("Details Activity");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment())
                    .commit();
        }
    }

    @Override
    public void setTitleandBackDrop(String title, String backDrop){
        collapsingToolbar.setTitle(title);
        imageView = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(this).load(backDrop).into(imageView);
    }
}
