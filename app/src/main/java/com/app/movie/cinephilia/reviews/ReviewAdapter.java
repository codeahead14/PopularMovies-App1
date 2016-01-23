package com.app.movie.cinephilia.reviews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.movie.cinephilia.R;

import java.util.ArrayList;

/**
 * Created by GAURAV on 22-01-2016.
 */
public class ReviewAdapter extends ArrayAdapter<MovieReviewModel> {
    private ArrayList<MovieReviewModel> mReviewData = new ArrayList<>();

    private static class ViewHolder{
        TextView author;
        TextView content;
    }

    public ReviewAdapter(Context context, int layoutResourceId, ArrayList<MovieReviewModel> reviews){
        super(context, layoutResourceId, reviews);
        this.mReviewData = reviews;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_review, parent, false);
            viewHolder.author = (TextView) convertView.findViewById(R.id.text_view_author);
            viewHolder.content = (TextView) convertView.findViewById(R.id.text_view_content);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        MovieReviewModel movieReviewModel = mReviewData.get(pos);
        Log.v("ReviewAdapter",movieReviewModel.mAuthor);
        viewHolder.author.setText(movieReviewModel.mAuthor);
        viewHolder.content.setText(movieReviewModel.mContent);

        return convertView;
    }
}
