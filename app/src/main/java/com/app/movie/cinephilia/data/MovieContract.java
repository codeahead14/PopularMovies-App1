package com.app.movie.cinephilia.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by GAURAV on 19-01-2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.cinephilia.app";
    // Build Base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // MovieEntry class declaring fields for table contents of Movie Database
    public static final class FavoriteMoviesEntry implements BaseColumns{
        public static final String TABLE_NAME = "favouritemovies";

    }
}
