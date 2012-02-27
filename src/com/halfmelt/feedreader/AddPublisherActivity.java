package com.halfmelt.feedreader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddPublisherActivity extends Activity{
	
	private DatabaseHelper persistance;
	
	public void onCreate(Bundle savedInstanceState) {
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));
		super.onCreate(savedInstanceState);
	}

    public void onStart() {
    	setContentView(R.layout.addpublisher);
		assignListeners();
    	super.onStart();
    }
    
    private boolean addPublisher() {
    	EditText url = (EditText) findViewById(R.id.pubUrl);
    	EditText name = (EditText) findViewById(R.id.pubName);
    	return persistance.addPublisher(name.getText().toString(), url.getText().toString());
    }
    
    private boolean assignListeners() {	
    	Button addPub = (Button) findViewById(R.id.addPubButton);
    	addPub.setOnClickListener(new OnClickListener() {
    	    public void onClick(View v) {
            	if(fieldsValid()){
            		if(addPublisher()){
            			finish();
            		} else {
            			// TODO Dialog about duplicate primary key
            			Log.d("gui says","DUPLICATE KEY!!");
            		}
            	} else {
            		// TODO Add warning message about invalid fields
            	}
    	    }
    	  });
    	return true;
    }
    
    private boolean fieldsValid() {
    	return true;
    }
}
