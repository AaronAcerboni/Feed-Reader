package com.halfmelt.feedreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 2;

	private static final String MAKE_PUBLISHER_TABLE = "CREATE TABLE Publishers " +
													   "(pubName TEXT, url TEXT)";
	private static final String MAKE_FEEDS_TABLE = "CREATE TABLE Publishers (" +
												   "pubName TEXT, " +
												   "title TEXT, " +
												   "date Date, " +
												   "url TEXT, " +
												   "content TEXT, " +
												   "hasRead BOOLEAN)";

	public DatabaseHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MAKE_PUBLISHER_TABLE);
		db.execSQL(MAKE_FEEDS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}