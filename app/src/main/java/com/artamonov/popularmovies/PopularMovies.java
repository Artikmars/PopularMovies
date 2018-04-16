package com.artamonov.popularmovies;


public class PopularMovies {

    private Integer id;
    private String title;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String posterPath;

    private String trailerKey;
    private String trailerName;
    private String trailerQuality;



    public PopularMovies() {

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
