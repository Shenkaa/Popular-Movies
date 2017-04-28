package eu.ramich.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private int ID;
    private String title;
    private String originalTitle;
    private String posterPath;
    private String backdropPath;
    private String overview;
    private double voteAverage;
    private String releaseDate;


    public Movie(int id) {

    }

    public Movie(int id, String title, String original_title, String poster_path,
                 String backdrop_path, String overview, double vote_average,
                 String release_date) {
        this.ID = id;
        this.title = title;
        this.originalTitle = original_title;
        this.posterPath = poster_path;
        this.backdropPath = backdrop_path;
        this.overview = overview;
        this.voteAverage = vote_average;
        this.releaseDate = release_date;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
    }

    private Movie(Parcel in) {
        this.ID = in.readInt();
        this.title = in.readString();
        this.originalTitle = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR
            = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String original_title) {
        this.originalTitle = original_title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String poster_path) {
        this.posterPath = poster_path;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdrop_path) {
        this.backdropPath = backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double vote_average) {
        this.voteAverage = vote_average;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String release_date) {
        this.releaseDate = release_date;
    }
}
