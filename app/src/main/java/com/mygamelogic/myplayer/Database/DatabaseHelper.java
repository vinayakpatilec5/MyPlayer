package com.mygamelogic.myplayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mygamelogic.myplayer.Response.MusicResponseRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 13/07/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "mypayer_db";

    private static final String TABLE_NAME = "favouriteTable";
    private static final String ARTIST_NAME = "artistName";
    private static final String IMAGE_URL = "artworkUrl100";
    private static final String DESC = "longDescription";
    private static final String PREW_URL = "previewUrl";
    private static final String TRACK_NAME = "trackName";
    private static final String GENERE_NAME = "primaryGenreName";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        String query="CREATE TABLE"+ TABLE_NAME+"(id  INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ARTIST_NAME+" TEXT, "+
                IMAGE_URL+ " TEXT, " +
                DESC+ " TEXT, " +
                PREW_URL+" TEXT, "+
                TRACK_NAME+" TEXT, "+
                GENERE_NAME+" TEXT)";
        db.execSQL(query);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
 //--------------------------------------
 //insert new row in database as preview url is uniqu using it identify row
 public long insertNewData(MusicResponseRow musicResponseRow) {
     // get writable database as we want to write data
     SQLiteDatabase db = this.getWritableDatabase();

     ContentValues values = new ContentValues();
     values.put(ARTIST_NAME, musicResponseRow.getArtistName());
     values.put(IMAGE_URL, musicResponseRow.getArtworkUrl100());
     values.put(DESC, musicResponseRow.getLongDescription());
     values.put(PREW_URL, musicResponseRow.getPreviewUrl());
     values.put(TRACK_NAME, musicResponseRow.getTrackName());
     values.put(GENERE_NAME, musicResponseRow.getPrimaryGenreName());

     // insert row
     long id = db.insert(TABLE_NAME, null, values);

     // close db connection
     db.close();
     // return newly inserted row id
     return id;
 }

 //check whether row is exist or not
    public Boolean checkForDataExist(MusicResponseRow musicResponseRow) {
        try {
            // get writable database as we want to write data
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_NAME,
                    new String[]{"id", ARTIST_NAME, IMAGE_URL, DESC, PREW_URL, TRACK_NAME, GENERE_NAME},
                    PREW_URL + "=?",
                    new String[]{musicResponseRow.getPreviewUrl()}, null, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            if (count > 0) {
                return true;
            }
        }catch (Exception e){}
        return false;
    }
 //delete row on swipe on favourite screen
    public void deleteRow(MusicResponseRow musicResponseRow) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, PREW_URL + " = ?",
                new String[]{musicResponseRow.getPreviewUrl()});
        db.close();
    }

 //get all favourite list on favourite page
    public List<MusicResponseRow> getAllFavouriteList() {
        // get writable database as we want to write data
        List<MusicResponseRow> musicResponseRows=new ArrayList<>();
        String countQuery = "SELECT * FROM " +TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        if (cursor.moveToFirst()) {
            do {
                MusicResponseRow musicResponseRow = new MusicResponseRow();
                musicResponseRow.setArtistName(cursor.getString(cursor.getColumnIndex(ARTIST_NAME)));
                musicResponseRow.setArtworkUrl100(cursor.getString(cursor.getColumnIndex(IMAGE_URL)));
                musicResponseRow.setLongDescription(cursor.getString(cursor.getColumnIndex(DESC)));
                musicResponseRow.setPreviewUrl(cursor.getString(cursor.getColumnIndex(PREW_URL)));
                musicResponseRow.setTrackName(cursor.getString(cursor.getColumnIndex(TRACK_NAME)));
                musicResponseRow.setPrimaryGenreName(cursor.getString(cursor.getColumnIndex(GENERE_NAME)));
                musicResponseRows.add(musicResponseRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return musicResponseRows;
    }
}
