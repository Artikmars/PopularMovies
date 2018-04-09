package com.artamonov.popularmovies;


public class PopularMovies {

    private String title;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String posterPath;

    public PopularMovies() {

    }

    public void setBaseUrl(String baseUrl) {
        this.posterPath = baseUrl;
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
}
