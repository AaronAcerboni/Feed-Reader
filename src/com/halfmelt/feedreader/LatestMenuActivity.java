package com.halfmelt.feedreader;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LatestMenuActivity extends Activity implements OnClickListener, OnTouchListener {
	
	private DatabaseHelper persistance;
	private ArrayList pubCollection;
	
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
    	pubCollection = new ArrayList();
    	
    	LinearLayout ll = (LinearLayout) findViewById(R.id.main);
    	
    	// Remove any previous drawn views
    	ll.removeAllViews();
    	
    	// Draw views
    	
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	ArrayList<String> publishers = persistance.getPublishers();
 
    	for(int i = 0; i < publishers.size(); i++){
    		
        	ArrayList feedCollection = new ArrayList();
    		
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
        		
        		feedCollection.add(feed);
        		
        		// Draw view item
        		View feedHeading = inflater.inflate(R.layout.feeditem, null);
        		((TextView)feedHeading.findViewById(R.id.title)).setText(feed.title);
        		((TextView)feedHeading.findViewById(R.id.date)).setText(feed.date);
        		
        		if(feed.hasRead == 0){
            		((TextView)feedHeading.findViewById(R.id.title)).setTypeface(null, Typeface.BOLD);
            		unreadAmt++;
        		}
        		
        		ll.addView(feedHeading);
        		
        		// Mapping to the associative publisher in pubCollection
        		feedHeading.setId(i);
        		// Mapping to the associative feed feedCollection within pubCollection
        		feedHeading.setTag(n);
        		
        		// Assign view item listeners
        		feedHeading.setOnClickListener(this);
        		feedHeading.setOnTouchListener(this);
        		
        		// Iteration logic
        		feeds.moveToNext();
        		n++;
        	} // end feed item loop
        	
        	
        	// Set how many are unread
        	if(unreadAmt > 0){
        		unread.setText(unreadAmt + " unread");
        	}
        	
        	// If no feeds fetched then display lack of feeds
        	if(feeds.getCount() == 0){
        		View noFeed = inflater.inflate(R.layout.nofeeditem, null);
        		ll.addView(noFeed);
        	} else {
            	// Add a "see all" view item
            	View seeAll = inflater.inflate(R.layout.seeall, null);
            	((TextView) seeAll.findViewById(R.id.title)).setText("See all items");
            	seeAll.setOnClickListener(this);
            	seeAll.setOnTouchListener(this);
            	seeAll.setTag(publishers.get(i));
            	ll.addView(seeAll);
        	}

        	pubCollection.add(feedCollection);
        	
        	try {
            	persistance.close((SQLiteDatabase) map.get("connection"));
			} catch (Exception e) {
				// TODO: handle exception
			}
    	} // end publisher loop
    	
    	// If no publishers then display add publisher message
    	if(publishers.size() == 0){
        	View noneAdded = inflater.inflate(R.layout.nopublishers, null);
        	ll.addView(noneAdded);
    	}
    	
    }
    
    
    // Click for a feed item
    public void onClick(View v) {    	
    	if((String) ((TextView) v.findViewById(R.id.title)).getText() == "See all items"){
    		// See more from publisher view
    		
    		Intent intent = new Intent(this, com.halfmelt.feedreader.PublisherActivity.class);
    		intent.putExtra("publisher", (String) v.getTag());
    		startActivity(intent);
    	} else {
    		// Specific feed item view
    		
        	ArrayList feeds = (ArrayList) pubCollection.get(v.getId());
    		Feed feed = (Feed) feeds.get((Integer) v.getTag());

    		Intent intent = new Intent(this, com.halfmelt.feedreader.ArticleActivity.class);
    		intent.putExtra("title", feed.title);
    		intent.putExtra("date", feed.date);
    		intent.putExtra("url", feed.url);
    		intent.putExtra("content", feed.content);
    		startActivity(intent);
    	}
	}
    
    // Touch for a feed item
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			v.setBackgroundColor(0xFFFF6A00);
			((TextView) v.findViewById(R.id.title)).setTextColor(Color.WHITE);
			((TextView) v.findViewById(R.id.date)).setTextColor(Color.WHITE);
		}
		if(event.getAction() == MotionEvent.ACTION_CANCEL){
			v.setBackgroundColor(0xFFFFFFFF);
			((TextView) v.findViewById(R.id.title)).setTextColor(Color.LTGRAY);
			((TextView) v.findViewById(R.id.date)).setTextColor(Color.LTGRAY);
		}
		return false;
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
    			mHandler.sendEmptyMessage(0);
    			// Refresh database with possibly new feeds
    			// Horribly blocking !
    			new Reader(persistance);
    	    }
    	}).start();
    }
    
}