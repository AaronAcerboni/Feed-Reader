package com.halfmelt.feedreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class FeedGetter {
	
	private static HttpClient client = new DefaultHttpClient();

	/* w3mentor.com/learn/java/android-development/
	 * android-http-services/example-of-http-get-request-using-httpclient-in-android/
	 */
	
	public static String get(String unknownUrl) {
		String url = unknownUrl;
		if(url.indexOf("http://") == -1){
			url = "http://" + url;
		}
		HttpGet get = new HttpGet(url);
		String feed = "Feed not set";
		try {
			HttpResponse response = client.execute(get);
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            feed = sb.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return feed;
	}

}
