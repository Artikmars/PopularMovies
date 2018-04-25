package com.artamonov.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import static com.artamonov.popularmovies.DBContract.DBEntry.COLUMN_ID;
import static com.artamonov.popularmovies.DBContract.DBEntry.COLUMN_MOVIE_ID;
import static com.artamonov.popularmovies.DBContract.DBEntry.COLUMN_NAME;
import static com.artamonov.popularmovies.DBContract.DBEntry.TABLE_NAME;

public class FavoritesProvider extends ContentProvider {

    private static final String AUTHORITY = "com.artamonov.popularmovies";
    private static final String BASE_PATH = "favorites";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final int FAVORITES = 1;
    private static final int FAVORITES_ID = 2;

    private static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/favorites."
            + AUTHORITY + "." + BASE_PATH;
    private static final String CONTACT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/favorites."
            + AUTHORITY + "." + BASE_PATH;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, FAVORITES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", FAVORITES_ID);
    }

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.i(MainActivity.TAG, "query: " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                Log.i(MainActivity.TAG, "FAVORITES");
                if (TextUtils.isEmpty(sortOrder)) {
                    Log.i(MainActivity.TAG, "sortOrder is Empty");
                    sortOrder = COLUMN_NAME + " ASC";
                } else
                    break;

            case FAVORITES_ID:
                String id = uri.getLastPathSegment();
                Log.d(MainActivity.TAG, "FAVORITES_ID: " + id);
                if (TextUtils.isEmpty(selection)) {
                    Log.d(MainActivity.TAG, "selection is empty");
                    selection = FAVORITES_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FAVORITES_ID + " = " + id;
                    Log.d(MainActivity.TAG, "selection is not empty " + selection);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                CONTENT_URI);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.i(MainActivity.TAG, "Insert: " + uri.toString());
        if (uriMatcher.match(uri) != FAVORITES) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        } else {
            database = dbHelper.getWritableDatabase();
            long rowID = database.insert(TABLE_NAME, null, contentValues);
            Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            Log.i(MainActivity.TAG, "resultUri: " + resultUri.toString());
            getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.i(MainActivity.TAG, "Delete: " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                Log.i(MainActivity.TAG, "FAVORITES");
                break;
            case FAVORITES_ID:
                String id = uri.getLastPathSegment();
                Log.i(MainActivity.TAG, "URI_FAVORITES_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COLUMN_MOVIE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_MOVIE_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = dbHelper.getWritableDatabase();
        int cnt = database.delete(TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.i(MainActivity.TAG, "Update: " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                Log.d(MainActivity.TAG, "FAVORITES");
                break;
            case FAVORITES_ID:
                String id = uri.getLastPathSegment();
                Log.d(MainActivity.TAG, "FAVORITES_ID: " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = DBContract.DBEntry.COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = dbHelper.getWritableDatabase();
        int cnt = database.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        Log.d(MainActivity.TAG, "getType: " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                return CONTACT_CONTENT_TYPE;
            case FAVORITES_ID:
                return CONTACT_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


    }
}
