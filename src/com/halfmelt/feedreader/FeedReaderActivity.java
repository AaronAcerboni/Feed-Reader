package com.halfmelt.feedreader;

import android.app.Activity;
import android.os.Bundle;

public class FeedReaderActivity extends Activity {
    /** Called when the activity is first created. */
	
	public DatabaseHelper databaseHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest);
        databaseHelper = new DatabaseHelper(this, getAppName());
    }
    
    private String getAppName() {
    	return getResources().getString(R.string.app_name);
    }
    
    private void buildContentView() {
    	
    }
}