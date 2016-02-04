package com.app.movie.cinephilia;

/**
 * Created by GAURAV on 13-12-2015.
 */

import android.os.Bundle;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
        if (savedInstanceState == null) {
            //Bundle arguments = getIntent().getExtras();
            //arguments.putParcelable(DetailsFragment.ARG_MOVIE, arguments.getBundle(GridViewFragment.BUNDLE_TAG));

            //DetailsFragment fragment = new DetailsFragment();
            //fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailsFragment())
                    .commit();
        }
    }

    @Override
    public void setTitleandBackDrop(String title, String backDrop){
        Log.v("Hi","There");
    }
}
