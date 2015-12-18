package com.app.movie.cinephilia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by GAURAV on 12-12-2015.
 */
public class GridViewAdapter extends ArrayAdapter<MovieModel> {
    private static final String TAG=GridViewAdapter.class.getSimpleName();
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<MovieModel> mGridData = new ArrayList<>();

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<MovieModel> mGridData){
        super(mContext, layoutResourceId, mGridData);
        this.mContext = mContext;
        this.layoutResourceId = layoutResourceId;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<MovieModel> mGridData){
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View counterView, ViewGroup parent){
        View row = counterView;
        ViewHolder holder;
        Log.v(TAG,"GridViewAdapter");
        if(row==null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else
            holder = (ViewHolder)row.getTag();

        MovieModel item = mGridData.get(pos);
        Log.v(TAG,"get view: "+item.getTitle());
        //holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

        Picasso.with(mContext).load(item.getPosterUrl()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}