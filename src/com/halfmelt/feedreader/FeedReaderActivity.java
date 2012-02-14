package com.halfmelt.feedreader;

import android.app.Activity;
import android.os.Bundle;

public class FeedReaderActivity extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest);
    }
    
    private void buildContentView() {
    	
    }
}