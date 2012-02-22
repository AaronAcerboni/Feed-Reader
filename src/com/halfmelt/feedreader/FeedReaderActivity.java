package com.halfmelt.feedreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FeedReaderActivity extends Activity {
	
	public static DatabaseHelper databaseHelper;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this, getAppName());
        setContentView(R.layout.latest);
    }

    public void onPause(Bundle savedInstanceState) {
    	super.onPause();
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
    		case R.id.menuItemAdd:
    			
    			return true;
    		case R.id.menuItemRemove:
    			
    			return true;
    			
    		case R.id.menuItemSettings:
    			
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    // Build the view based on feed items
    private void buildContentView() {
    	
    }
    
    // Utility method for retrieving the app_name string
    private String getAppName() {
    	return getResources().getString(R.string.app_name);
    }
    
}