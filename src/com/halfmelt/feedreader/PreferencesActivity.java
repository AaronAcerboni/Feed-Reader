package com.halfmelt.feedreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity{
	
	private DatabaseHelper persistance;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        persistance = new DatabaseHelper(this, getResources().getString(R.string.app_name));
        addPreferencesFromResource(R.xml.preferences);
        setClicklisteners();
	}
	
	private boolean setClicklisteners() {
        findPreference("clearCache").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
            	removeCacheDialog();
                return true;
            }
        });
        findPreference("about").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
            	showAboutDialog();
                return true;
            }
        });
		return true;
	}
	
	private void removeCacheDialog() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(R.string.remove_cache_dialog);
		alertbox.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				persistance.removeAllFeeds();
				Toast.makeText(getBaseContext(), R.string.item_clear_toast, Toast.LENGTH_SHORT).show();
			}
		});
		alertbox.setNegativeButton(R.string.deny, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {}});
		alertbox.show();
	}
	
	private void showAboutDialog() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setMessage(R.string.item_about_content);
		alertbox.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		alertbox.show();
	}

}
