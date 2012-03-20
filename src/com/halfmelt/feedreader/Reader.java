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
import java.util.List;

public class Reader{
	
	public Reader() {
		try {
			SyndFeed feed = read("http://planetjs.tumblr.com/rss");
			Log.d("SIZE OF FEED", feed.getFeedType());
			List e = feed.getEntries();
			SyndEntry item = (SyndEntry) e.get(0);
			String title = item.getTitle();
			Log.d("First title is!!", title);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FetcherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public SyndFeed read(String url) throws IOException, FeedException, FetcherException {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
		return feedFetcher.retrieveFeed(new URL(url));
	}
	
}
