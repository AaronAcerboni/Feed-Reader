package com.halfmelt.feedreader;

import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContentImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Reader{
	
	DatabaseHelper persistance;
	
	public Reader(DatabaseHelper persistance) {
        this.persistance = persistance;
		try {
			// Gathers all the feeds for each publisher and set them 
			// in the database.
			
			gatherPublisherFeeds();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FeedException e) {
			e.printStackTrace();
		} catch (FetcherException e) {
			e.printStackTrace();
		}
		
	}
	
	private void gatherPublisherFeeds() throws IOException, FeedException, FetcherException {
		ArrayList<String> publishers = persistance.getPublishers();
		for(int i = 0; i < publishers.size(); i++){
			readFeed(publishers.get(i));
		}
	}
	
	private void readFeed(String publisher) throws IOException, FeedException, FetcherException{
		String url = persistance.getPublisherUrl(publisher);
		Log.d("Attempting: ", url);
		SyndFeed feed = read(url);
		Log.d("Encountered feed of type: ", feed.getFeedType());
		List entries = feed.getEntries();
		for(int i = 0; i < entries.size(); i++){
			SyndEntry item = (SyndEntry) entries.get(i);	
			storeFeed(
					publisher,
					item.getTitle(),
					item.getPublishedDate().toString(),
					item.getUri(),
					item.getDescription().getValue()
			);
		}
	}
	
	private void storeFeed(String pubName, String title, String date, String url, String content){
		// Store only if not present
		Boolean exists = persistance.feedExists(pubName, title, date);
		if(!exists){
			int hasRead = 0;
			persistance.addFeed(pubName, title, date, url, content, hasRead);
		}
	}
	
	private SyndFeed read(String url) throws IOException, FeedException, FetcherException {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
		return feedFetcher.retrieveFeed(new URL(url));
	}
	
}
