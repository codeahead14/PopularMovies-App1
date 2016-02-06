package com.app.movie.cinephilia.trailers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.movie.cinephilia.DetailsFragment;
import com.app.movie.cinephilia.MovieModel;
import com.app.movie.cinephilia.R;
import com.app.movie.cinephilia.Utility;
import com.app.movie.cinephilia.reviews.MovieReviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by GAURAV on 22-01-2016.
 */
public class TrailerAdapter extends ArrayAdapter<MovieTrailerModel> {
    private ArrayList<MovieTrailerModel> mTrailerData = new ArrayList<>();
    private Context mContext;
    private ViewHolder viewHolder;
    MovieTrailerModel movieTrailerModel;

    public TrailerAdapter(Context context, int layoutResourceId, ArrayList<MovieTrailerModel> trailers){
        super(context, layoutResourceId, trailers);
        this.mContext = context;
        this.mTrailerData = trailers;
    }

    public static class ViewHolder{
        public TextView name;
        public ImageView trailerImg;
    }

    @Override
    public int getCount(){
        return mTrailerData.size();
    }

    @Override
    public MovieTrailerModel getItem(int position) {
        return mTrailerData.get(position);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        String movieId = Integer.toString(DetailsFragment.mMovieId);

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_trailer, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.trailerName);
            viewHolder.trailerImg = (ImageView)convertView.findViewById(R.id.trailerImg);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        movieTrailerModel = mTrailerData.get(pos);
        Log.v("ReviewAdapter", movieTrailerModel.mName);
        viewHolder.name.setText(movieTrailerModel.mName);
        final String BASE_URL = "http://img.youtube.com/vi/";
        final String url = BASE_URL + movieTrailerModel.mKey + "/0.jpg";
        Picasso
                .with(mContext)
                .load(url)
                .resize(300,200)
                .centerCrop()
                .placeholder(R.drawable.imagenotfound)
                .into(viewHolder.trailerImg);

        final String trailerUrl="https://www.youtube.com/watch?v="+movieTrailerModel.mKey;
        viewHolder.trailerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
