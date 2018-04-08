package com.artamonov.popularmovies;


import java.util.List;

public class PopularMovies {

    List<String> result;
    private String title;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String posterPath;

    public PopularMovies(List<String> result) {
        this.result = result;
    }

    public PopularMovies() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public void setBaseUrl(String baseUrl) {
        this.posterPath = baseUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {

        return posterPath;
    }
}
