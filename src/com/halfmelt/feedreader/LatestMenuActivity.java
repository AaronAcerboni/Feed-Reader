package com.halfmelt.feedreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LatestMenuActivity extends Activity {
	
	private DatabaseHelper persistance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));
        if(persistance.firstRun){
        	
        } else {
        	
        }
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        setContentView(R.layout.latest);
    	super.onStart();
    }
    
    // Build the view based on feed items
    private void buildContentView() {
    	// for each publisher
    		// get top latest feeds up to 3
    			// add click listener
    			// add long press listener (view, mark as read, remove)
    			// make them bold if unread
    	ArrayList<String> publishers = persistance.getPublishers();
    	int pubSize = publishers.size();
    	
    	for(int i = 0; i < pubSize; i++){
    		ArrayList<String> feeds = persistance.getFeeds(publishers.get(i), "date", 3);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.lastest_menu, menu);
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case R.id.menuItemRefresh:
    			touch_refresh();
    			return true;
    		case R.id.menuItemAdd:
    			touch_addPublisher();
    			return true;
    		case R.id.menuItemEdit:
    			touch_editPublishers();
    			return true;
    		case R.id.menuItemSettings:
    			touch_settings();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    private void touch_settings() {
    	Intent i = new Intent(this, PreferencesActivity.class);
    	startActivity(i);
	}

	private void touch_editPublishers() {
    	Intent i = new Intent(this, EditPublishersActivity.class);
    	startActivity(i);
	}

	private void touch_addPublisher() {
    	Intent i = new Intent(this, AddPublisherActivity.class);
    	startActivity(i);
	}
	
    private void touch_refresh(){
		final Activity gui = this;
		new Thread(new Runnable() {
			public void run() {
				final String feed = FeedGetter.get("planetjs.tumblr.com/rss");
				Log.d("blah", feed);
				if(!persistance.populateDummyData()){
					Log.d("Warning!!!!!!", "That pubName was already set in the database!");
				}
				gui.runOnUiThread(new Runnable(){
					public void run(){
						// TODO something happens back on the GUI
					}
				});
			}
		}).start(); 
    }
    
}