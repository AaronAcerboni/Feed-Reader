package com.halfmelt.feedreader;

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
    @SuppressWarnings("unused")
	private void buildContentView() {
    	
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
    			touch_addFeed();
    			return true;
    		case R.id.menuItemRemove:
    			touch_removeFeed();
    			return true;
    		case R.id.menuItemSettings:
    			touch_settings();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    private void touch_settings() {
    	
	}

	private void touch_removeFeed() {
		
	}

	private void touch_addFeed() {
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