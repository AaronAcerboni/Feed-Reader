package com.halfmelt.feedreader;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final int    DATABASE_VERSION = 2;

	private static final String APP_SETTINGS_TABLE = "CREATE TABLE settings " +
														  "(firstRun integer)";
	
	private static final String PUBLISHER_TABLE    = "CREATE TABLE publishers " +
													      "(pubName text PRIMARY KEY, url text )";
	
	private static final String FEEDS_TABLE        = "CREATE TABLE feeds (" +
													    "pubName text, " + 
													    "title text, " +
												        "date text, " +
												        "url text, " +
												        "content text, " +
												        "hasRead integer, " +
												        "FOREIGN KEY(pubName) REFERENCES publishers(pubName) " +
													 ")";
	
	public final boolean firstRun = true;

	public DatabaseHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
		// TODO: Set firstRun based on firstRun in database
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(APP_SETTINGS_TABLE);
		db.execSQL(PUBLISHER_TABLE);
		db.execSQL(FEEDS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public boolean populateDummyData()  {
		String dummySql1 = "INSERT INTO publishers VALUES ('planet.js', 'http://planetjs.tumblr.com')";
		String dummySql2 = "INSERT INTO feeds VALUES ('planet.js', 'cool title', 'Sun Feb 26 2012 19:25:10', " +
						   "'http://planetjs.tumblr.com/post/16356254895', '<h1>plswerk</h1><b>;-;</b>', 0)";
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL(dummySql1);
			db.execSQL(dummySql2);
			db.close();
		} catch (SQLiteException e){
			db.close();
			return false;
		}
		return true;
	}
	
	public boolean addPublisher(String pubName, String pubUrl){
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL("INSERT INTO publishers VALUES ('" + pubName + "', '" + pubUrl + "')");
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}
	
	public ArrayList<String> getPublishers() {
		SQLiteDatabase db = getReadableDatabase();
		db.close();
		return new ArrayList<String>();
	}
	
	public DatabaseHelper getFeeds(String pubName) {
		return getFeeds(pubName, "date", 3);
	}
	
	public DatabaseHelper getFeeds(String pubName, String by, int upto) {
		SQLiteDatabase db = getReadableDatabase();
		db.close();
		return this;
	}
	
	@SuppressWarnings("unused")
	private int stringDateToInt(String date) {
		return 0;
	}
}