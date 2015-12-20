package com.app.movie.cinephilia;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by GAURAV on 13-12-2015.
 */
public class MovieModel implements Parcelable{
    /*private String image;
    private String title;
    private double userRating;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;
    private int id;*/

    String image;
    String title;
    double userRating;
    String releaseDate;
    String synopsis;
    String posterUrl;
    int id;

    public MovieModel(String originalTitle, double userRating, String releaseDate, String plotSynopsis, String posterPath, int id) {
        this.title = originalTitle;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.synopsis = plotSynopsis;
        this.id = id;
        this.posterUrl = "http://image.tmdb.org/t/p/w185" + posterPath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        Log.v("MoveModel","Title: "+title);
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getUserRating(){
        return userRating;
    }

    public String getReleaseDate(){
        String[] formatDate = releaseDate.split("-");
        return formatDate[2]+"-"+formatDate[1]+"-"+formatDate[0];  //System.out.format("%s-%s-%s",formatDate[0],formatDate[1],formatDate[2]).toString();
    }

    public String getSynopsis(){
        return synopsis;
    }

    public String getPosterUrl(){
        return posterUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(synopsis);
        dest.writeString(posterUrl);
        dest.writeString(title);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {

        @Override
        public MovieModel createFromParcel(Parcel parcel) {
            return new MovieModel(parcel);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public MovieModel(Parcel parcel){
        this.id = parcel.readInt();
        this.synopsis = parcel.readString();
        this.posterUrl = parcel.readString();
        this.title = parcel.readString();
        this.userRating = parcel.readDouble();
        this.releaseDate = parcel.readString();
    }
}
