package com.halfmelt.feedreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArticleActivity extends Activity implements OnClickListener{
	
	DatabaseHelper persistance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));  
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onStart() {
        Bundle info = getIntent().getExtras();
        setContentView(R.layout.article);
        // Mark view as read
        persistance.feedAsRead(info.getString("title"), info.getString("date"), info.getString("url"));
        buildContentView(info);      
    	super.onStart();
    }
    
    private void buildContentView(Bundle extras){
    	String title = extras.getString("title");
    	String url = extras.getString("url");
    	String date = extras.getString("date");
    	String content = extras.getString("content");
    	
    	// Concatinate full html string from feed object property
    	
    	content = "<h1>" + title + "</h1>" + "<p>" + date + "</p><p><a href='" + url + "'>" + url + "</a></p>" + content;
    	
    	// Inject it into web view
    	
    	LinearLayout ll = (LinearLayout) findViewById(R.id.article);
    	((WebView) ll.findViewById(R.id.webview)).loadDataWithBaseURL("", content, "text/html", Encoding.UTF_8.toString(), "");
    }

    public void onClick(View v){
    	String uri = (String) ((TextView) v).getText();
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(uri)); 
    	startActivity(i);
    }
    
}