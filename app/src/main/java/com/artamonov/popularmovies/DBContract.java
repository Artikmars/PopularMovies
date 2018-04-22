package com.artamonov.popularmovies;

import android.provider.BaseColumns;

public class DBContract {

    private DBContract() {
    }

    public static abstract class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "poster";
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_MOVIE_ID + " INTEGER, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_RELEASE_DATE + " TEXT, " +
                        COLUMN_RATING + " TEXT, " +
                        COLUMN_OVERVIEW + " TEXT, " +
                        COLUMN_POSTER + " BLOB " +
                        ")";

    }
}
