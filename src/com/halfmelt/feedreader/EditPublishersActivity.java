package com.halfmelt.feedreader;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EditPublishersActivity extends Activity{

	private DatabaseHelper persistance;
	private ArrayList<String> pubNames;
	private int pubAmt;
	
    public void onCreate(Bundle savedInstanceState) {
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));
        super.onCreate(savedInstanceState);
    }
    
    public void onStart() {
    	setContentView(buildContentView());
    	super.onStart();
    }
    
    private ScrollView buildContentView() {
    	pubNames = persistance.getPublishers();
    	pubAmt = pubNames.size();

    	// Draw all publisher checkboxes
    	
    	ScrollView sv = new ScrollView(this);
    	LinearLayout lin = new LinearLayout(this);
    	lin.setOrientation(LinearLayout.VERTICAL);
    	for (int i = 0; i < pubAmt; i++) {
        	CheckBox cb = new CheckBox(this);
        	cb.setId(i+1);
        	cb.setText(pubNames.get(i));
        	cb.setPadding(46, 4, 4, 4);
        	cb.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        	lin.addView(cb);
		}
    	// If none were drawn then display alternative text
    	if(pubAmt <= 0){
    		TextView tv = new TextView(this);
    		tv.setText(R.string.no_publishers_found_label);
    		lin.addView(tv);
    	}
    	sv.addView(lin);
    	return sv; 
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.edit_publishers_menu, menu);
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case R.id.menuItemCancel:
    			touch_cancel();
    			return true;
    		case R.id.menuItemRemove:
    			touch_removedChecked();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

    private boolean touch_cancel() {
    	finish();
    	return true;
    }
    
    private boolean touch_removedChecked() {
    	ArrayList<String> toRemove = new ArrayList<String>();
    	for(int i = 0; i < pubAmt; i++){
    		CheckBox cb = (CheckBox) findViewById(i+1);
    		if(cb.isChecked()){
    			toRemove.add((String) cb.getText());
    		}
    	}
    	if(toRemove.size() > 0){
    		confirmRemoval(toRemove);
    	} else {
    		Toast.makeText(this, R.string.no_checked_publishers_toast, Toast.LENGTH_LONG).show();
    	}
    	return true;
    }
    
    private boolean confirmRemoval(final ArrayList<String> toRemove) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(R.string.remove_publishers_dialog);
		alertbox.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				removeChecked(toRemove);
			}
		});
		alertbox.setNegativeButton(R.string.deny, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {}});
		alertbox.show();
    	return true;
    }
    
    private boolean removeChecked(ArrayList<String> toRemove) {
    	for(int i = 0; i < toRemove.size(); i++){
    		persistance.removePublisher(toRemove.get(i));
    	}
    	
    	// Redraw view
    	setContentView(buildContentView());
		Toast.makeText(getApplicationContext(),
				  R.string.removed_publishers_toast,
				  Toast.LENGTH_LONG)
		.show();					
		return true;
    }
	
}
