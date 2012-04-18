package com.halfmelt.feedreader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	
	public void close(SQLiteDatabase db) {
		db.close();
	}
	
	// Database getters
	
	public ArrayList<String> getPublishers() {
		ArrayList<String> publishers = new ArrayList<String>();
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT pubName From publishers", null);
		
		int i = 0;
		c.moveToFirst();
		while(i < c.getCount()){
			publishers.add(c.getString(0));
			c.moveToNext();
			i++;
		}
		db.close();
		return publishers;
	}
	
	public String getPublisherUrl(String pubName) {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT url From publishers WHERE pubName=" + escape(pubName), null);
		c.moveToFirst();
		db.close();
		return c.getString(0);
	}

	public HashMap<String, Object> getFeeds(String pubName) {
		return getFeeds(pubName, "date");
	}
	
	public HashMap<String, Object> getFeeds(String pubName, String by) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM feeds WHERE pubName = " + escape(pubName) +
					 "ORDER BY date";
		
		Cursor c = db.rawQuery(sql, null);
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("connection", db);
		map.put("cursor", c);
		
		return map;
	}
	
	public HashMap getFeeds(String pubName, String by, int upto) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM feeds WHERE pubName = " + escape(pubName) + " ORDER BY " +
					 escape(by) + " LIMIT 0," + upto;

		Cursor c = db.rawQuery(sql, null);
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("connection", db);
		map.put("cursor", c);
		
		return map;
	}
	
	public Cursor getFeed(String pubName, String date, String title) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM feeds WHERE pubName = " + escape(pubName) +
										      "AND date = " + escape(date) +
										      "AND title = " + escape(title);
		db.close();		
		return db.rawQuery(sql, null);
	}
	
	public Boolean feedExists(String pubName, String date, String title){
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT 1 FROM feeds WHERE pubName=" + escape(pubName) +
										" AND date=" + escape(date) +
										" AND title=" + escape(title);
		Cursor c = db.rawQuery(sql, null);
		
		return (c.getCount() > 0);
	}
	
	// Database setters

	
	public boolean addPublisher(String pubName, String pubUrl) {
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL("INSERT INTO publishers VALUES (" + escape(pubName) + ", " + escape(pubUrl) + ")");
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}
	
	public boolean removePublisher(String pubName) {
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL("DELETE FROM publishers WHERE pubName=" + escape(pubName));
			db.execSQL("DELETE FROM feeds WHERE pubName=" + escape(pubName));
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}
	
	public boolean addFeed(String pubName, String title, String date, String url, String content, int hasRead) {
		SQLiteDatabase db = getWritableDatabase();
		try{
			String sql = "INSERT INTO feeds VALUES(" +
						 escape(pubName) + ", " + 
						 escape(title) + ", " + 
						 escape(date) + ", " + 
						 escape(url) + ", " + 
						 escape(content) + ", " + 
						 hasRead + ")";

			db.execSQL(sql);
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}

	public boolean removeAll() {
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL("DELETE FROM publishers");
			db.execSQL("DELETE FROM feeds");
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}

	public boolean removeAllFeeds() {
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL("DELETE FROM feeds");
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}
	
	public boolean resetAppData() {
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL("DELETE FROM publishers");
			db.execSQL("DELETE FROM feeds");
			db.execSQL("DELETE FROM settings");
			db.close();
			return true;
		} catch (SQLiteException e){
			db.close();
			return false;
		}
	}
	

	public boolean populateDummyData()  {
		String dummySql1 = "INSERT INTO publishers VALUES ('planet.js', 'http://planetjs.tumblr.com')";
		String dummySql2 = "INSERT INTO feeds VALUES ('planet.js', 'alpha', 'Sun Feb 26 2012 19:25:10', " +
						   "'http://planetjs.tumblr.com/post/16356254895', '<h1>plswerk</h1><b>;-;</b>', 0)";
		String dummySql3 = "INSERT INTO feeds VALUES ('planet.js', 'beta', 'Sat Feb 25 2012 15:25:10', " +
						   "'http://planetjs.tumblr.com/post/16356254895', '<h1>plswerk</h1><b>;-;</b>', 0)";
		String dummySql4 = "INSERT INTO feeds VALUES ('planet.js', 'zeta', 'Fri Feb 24 2012 11:25:10', " +
						   "'http://planetjs.tumblr.com/post/16356254895', '<h1>plswerk</h1><b>;-;</b>', 0)";
		SQLiteDatabase db = getWritableDatabase();
		try{
			db.execSQL(dummySql1);
			db.execSQL(dummySql2);
			db.execSQL(dummySql3);
			db.execSQL(dummySql4);
			db.close();
		} catch (SQLiteException e){
			db.close();	
			return false;
		}
		return true;
	}
	
	// RFC822 compliant date
	
	private long stringDateToInt(String strdate) {
	   SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	   Date date = null;
	   try {
		   date = formatter.parse(strdate);
	   } catch (ParseException e) {
		   e.printStackTrace();
	   }
	   return date.getTime();
	}
	
	private String longDateToString(long milli) {
		Date date = new Date(milli);
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		return formatter.format(date);
	}
	
	// Utility
	
	private String escape(String escapee){
		return DatabaseUtils.sqlEscapeString(escapee);
	}
}