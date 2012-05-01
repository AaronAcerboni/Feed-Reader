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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LatestMenuActivity extends Activity implements OnClickListener{
	
	private DatabaseHelper persistance;
	private ArrayList<Feed> feedsCollection;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));
        if(persistance.firstRun){
        	// Display welcome or introductory message
        }
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        setContentView(R.layout.latest);
        buildContentView();
    	super.onStart();
    }

    public void onResume() {
        setContentView(R.layout.latest);
        buildContentView();
        super.onResume();
    }
    
    // Build the view based on feed items
    private void buildContentView() {
    	feedsCollection = new ArrayList<Feed>();
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
        	ll.addView(heading);
        	
        	// Draw publisher feeds
        	
        	HashMap<String, Object> map = (HashMap<String, Object>) persistance.getFeeds(publishers.get(i), "date", 2);
        	Cursor feeds = (Cursor) map.get("cursor");
        	feeds.moveToFirst();
        	
        	int n = 0;
        	int unreadAmt = 0;
        	while(n < feeds.getCount()){
        		// Create Feed object with a correlating index to the view item.
        		// This means when the onClickListener is handled the view item id will match the 
        		// appropriate feed object.
        		Feed feed = new Feed();
        		feed.title = feeds.getString(feeds.getColumnIndex("title"));
        		feed.date = feeds.getString(feeds.getColumnIndex("date"));
        		feed.url = feeds.getString(feeds.getColumnIndex("url"));
        		feed.content = feeds.getString(feeds.getColumnIndex("content"));
        		feed.hasRead = feeds.getInt(feeds.getColumnIndex("hasRead"));
        		
        		feedsCollection.add(feed);
        		
        		// Display view item
        		View feedHeading = inflater.inflate(R.layout.feeditem, null);
        		((TextView)feedHeading.findViewById(R.id.title)).setText(feed.title);
        		((TextView)feedHeading.findViewById(R.id.date)).setText(feed.date);
        		
        		if(feed.hasRead == 0){
            		((TextView)feedHeading.findViewById(R.id.title)).setTypeface(null, Typeface.BOLD);
            		unreadAmt++;
        		}
        		
        		ll.addView(feedHeading);
        		ll.setId(n);
        		
        		// Assign view item listener
        		 ll.setOnClickListener(this);
        		
        		// Iteration logic
        		feeds.moveToNext();
        		n++;
        	} // end feed item loop
        	

        	
        	// Set how many are unread
        	if(unreadAmt > 0){
        		unread.setText("(" + unreadAmt + ")");
        	}
        	
        	// If no feeds fetched then display lack of feeds
        	if(feeds.getCount() == 0){
        		View noFeed = inflater.inflate(R.layout.nofeeditem, null);
        		ll.addView(noFeed);
        	}
        	
        	persistance.close((SQLiteDatabase) map.get("connection"));
    	} // end publisher loop

    }
    
    
    // Click for a feed item
    public void onClick(View v) {
		Feed feed = (Feed) feedsCollection.get(v.getId());
		Intent intent = new Intent(this, com.halfmelt.feedreader.ArticleActivity.class);
		intent.putExtra("title", feed.title);
		intent.putExtra("date", feed.date);
		intent.putExtra("url", feed.url);
		intent.putExtra("content", feed.content);
		startActivity(intent);
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
		// Runs gathering of feeds asynchronously
    	
    	final Handler mHandler = new Handler(){
    	    public void handleMessage(Message msg){   
    	        buildContentView();
    	    }
    	};
    	 
    	new Thread(new Runnable(){
    		public void run(){
    			// Refresh database with possibly new feeds
    			new Reader(persistance);
    			mHandler.sendEmptyMessage(0);
    	    }
    	}).start();
    }
    
}