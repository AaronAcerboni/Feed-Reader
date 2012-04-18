package com.halfmelt.feedreader;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        buildContentView();
    	super.onStart();
    }

    public void onResume() {
        buildContentView();
        super.onResume();
    }
    
    // Build the view based on feed items
    private void buildContentView() {
    	LinearLayout ll = (LinearLayout) findViewById(R.id.main);
    	
    	// Remove any previous drawn views
    	ll.removeAllViews();
    	
    	// Draw views
    	
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	ArrayList<String> publishers = persistance.getPublishers();
 
    	for(int i = 0; i < publishers.size(); i++){
    		
    		// Draw publisher
    		
    		View heading = (LinearLayout) inflater.inflate(R.layout.subheading, null);
    		TextView title = (TextView) heading.findViewById(R.id.title);
    		TextView unread = (TextView) heading.findViewById(R.id.newAmount);

    		title.setText(publishers.get(i));
    		unread.setText("50");
        	ll.addView(heading);
        	
        	// Draw publisher feeds
        	
        	HashMap<String, Object> map = (HashMap<String, Object>) persistance.getFeeds(publishers.get(i), "date", 2);
        	Cursor feeds = (Cursor) map.get("cursor");
        	feeds.moveToFirst();
        	
        	int n = 0;
        	while(n < feeds.getCount()){
        		String feedTitle = feeds.getString(feeds.getColumnIndex("title"));
        		String feedDate = feeds.getString(feeds.getColumnIndex("date"));
        		int feedHasRead = feeds.getInt(feeds.getColumnIndex("hasRead"));
        		
        		View feedHeading = inflater.inflate(R.layout.feeditem, null);
        		((TextView)feedHeading.findViewById(R.id.title)).setText(feedTitle);
        		((TextView)feedHeading.findViewById(R.id.date)).setText(feedDate);
        		if(feedHasRead == 0){
            		((TextView)feedHeading.findViewById(R.id.title)).setTypeface(null, Typeface.BOLD);
        		}
        		Log.d("here", feedTitle);
        		ll.addView(feedHeading);
        		feeds.moveToNext();
        		n++;
        	}

        	persistance.close((SQLiteDatabase) map.get("connection"));
        	
    	} // end publisher loop
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
				new Reader(persistance);
    			//touch_refresh();
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
						buildContentView();
					}
				});
			}
		}).start(); 
    }
    
}