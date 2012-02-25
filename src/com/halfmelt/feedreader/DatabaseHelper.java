package com.halfmelt.feedreader;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final int    DATABASE_VERSION = 2;

	private static final String MAKE_APP_SETTINGS_TABLE = "CREATE TABLE settings " +
														  "(firstRun integer)";
	
	private static final String MAKE_PUBLISHER_TABLE    = "CREATE TABLE publishers " +
													      "(pubName text PRIMARY KEY, url text)";
	
	private static final String MAKE_FEEDS_TABLE        = "CREATE TABLE feeds (" +
													      "FOREIGN KEY(pubName) REFERENCES publishers(pubName), " +
														  "title text, " +
													      "date text, " +
													      "url text, " +
														  "content text, " +
														  "hasRead integer)";
	
	public final boolean firstRun = true;

	public DatabaseHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
		// TODO: Set firstRun based on firstRun in database
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MAKE_APP_SETTINGS_TABLE);
		db.execSQL(MAKE_PUBLISHER_TABLE);
		db.execSQL(MAKE_FEEDS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public ArrayList<String> getPublishers(){
		return new ArrayList<String>();
	}
	
	public DatabaseHelper getFeeds(String pubName){
		return getFeeds(pubName, "date", 3);
	}
	
	public DatabaseHelper getFeeds(String pubName, String by, int upto) {
		return this;
	}
	
	private int stringDateToInt(String date){
		return 0;
	}
}