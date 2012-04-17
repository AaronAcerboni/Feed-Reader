package com.halfmelt.feedreader;

import android.util.Log;

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
			ArrayList<String> extractedContent = readFeed(publishers.get(i));
			String pubName = extractedContent.get(0);
			String title   = extractedContent.get(1);
			String date    = extractedContent.get(2);
			String url     = extractedContent.get(3);
			String content = extractedContent.get(4);
			int hasRead    = 0;
			persistance.addFeed(pubName, title, date, url, content, hasRead);
		}
	}
	
	private ArrayList readFeed(String pubName) throws IOException, FeedException, FetcherException{
		ArrayList extractedContent = new ArrayList();
		SyndFeed feed = read("http://planetjs.tumblr.com/rss");
		Log.d("SIZE OF FEED", feed.getFeedType());
		List e = feed.getEntries();
		SyndEntry item = (SyndEntry) e.get(0);
		String title = item.getTitle();
		Log.d("First title is!!", title);
		return extractedContent;
	}
	
	private SyndFeed read(String url) throws IOException, FeedException, FetcherException {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
		return feedFetcher.retrieveFeed(new URL(url));
	}
	
}
