package com.artamonov.popularmovies;


import android.graphics.Bitmap;

class PopularMovies {

    private Integer id;
    private String title;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String posterPath;

    private String trailerKey;
    private String trailerName;
    private String trailerQuality;

    private String reviewContent;
    private String reviewAuthor;
    private Bitmap poster;

    public byte[] getPosterByte() {
        return posterByte;
    }

    public void setPosterByte(byte[] posterByte) {
        this.posterByte = posterByte;
    }

    private byte[] posterByte;

    public PopularMovies() {

    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getTrailerQuality() {
        return trailerQuality;
    }

    public void setTrailerQuality(String trailerQuality) {
        this.trailerQuality = trailerQuality;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

    public Integer getId() {
        return id;

    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {

        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
