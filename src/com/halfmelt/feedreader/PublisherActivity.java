package com.halfmelt.feedreader;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PublisherActivity extends Activity implements OnClickListener, OnTouchListener {
	
	private DatabaseHelper persistance;
	private ArrayList<Feed> feedsCollection;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));
        
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        setContentView(R.layout.feeds);
        buildContentView(getIntent().getExtras().getString("publisher"));
    	super.onStart();
    }
    
	private void buildContentView(String publisher) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	LinearLayout ll = (LinearLayout) findViewById(R.id.main);
		
		feedsCollection = new ArrayList<Feed>();
		
		HashMap<String, Object> map = (HashMap<String, Object>) persistance.getFeeds(publisher, "date");
    	Cursor feeds = (Cursor) map.get("cursor");
    	
    	feeds.moveToFirst();    	
    	int i = 0;
    	while(i < feeds.getCount()){
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
    		// Draw view item
    		View feedItem = inflater.inflate(R.layout.feeditem, null);
    		((TextView)feedItem.findViewById(R.id.title)).setText(feed.title);
    		((TextView)feedItem.findViewById(R.id.date)).setText(feed.date);
    		
    		feedItem.setId(i);
    		feedItem.setOnClickListener(this);
    		feedItem.setOnTouchListener(this);
    		
    		ll.addView(feedItem);
    		
    		// Iteration logic
    		feeds.moveToNext();
    		i++;
    	}
    	
    	// Set publisher title
    	((TextView) findViewById(R.id.publisher_title)).setText(publisher);
    	
    	// Set go up button to return to latest activity
    	findViewById(R.id.latest).setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				finish();
			}
		});
    	
    	try {
        	feeds.close();
		} catch (Exception e) {
			// TODO: handle exception
		}		
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
    
    // Touch for a feed item
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			v.setBackgroundColor(0xFFFF6A00);
			((TextView) v.findViewById(R.id.title)).setTextColor(Color.WHITE);
			try{
				((TextView) v.findViewById(R.id.date)).setTextColor(Color.WHITE);
			} catch (Exception e){
				
			}
		}
		if(event.getAction() == MotionEvent.ACTION_CANCEL){
			v.setBackgroundColor(0xFFFFFFFF);
			((TextView) v.findViewById(R.id.title)).setTextColor(Color.LTGRAY);
			try{
				((TextView) v.findViewById(R.id.date)).setTextColor(Color.LTGRAY);
			} catch (Exception e){
				
			}
		}
		return false;
	}
    
}