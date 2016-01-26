package com.app.movie.cinephilia.FavouriteMovies;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by GAURAV on 26-01-2016.
 */
public class FavouritesAdapter extends CursorAdapter {

    public FavouritesAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return null;
    }

}
