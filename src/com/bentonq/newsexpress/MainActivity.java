package com.bentonq.newsexpress;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final String TAG = "NE";

	private class FetchRssTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... urls) {
			int count = urls.length;
			long totalSize = 0;
			for (int i = 0; i < count; i++) {
				fetchRssSource(urls[i]);
			}
			return totalSize;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return 100;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView != null) {
					return convertView;
				}

				LayoutInflater inflater = LayoutInflater
						.from(MainActivity.this);
				View view = inflater.inflate(R.layout.view_summary, parent,
						false);
				return view;
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LayoutInflater inflater = LayoutInflater
						.from(MainActivity.this);
				ViewGroup rootView = (ViewGroup) view;
				View newsView = inflater.inflate(R.layout.view_news, rootView,
						false);
				rootView.removeAllViews();
				rootView.addView(newsView);
			}
		});

		URL url;
		try {
			url = new URL("http://cnbeta.feedsportal.com/c/34306/f/624776/index.rss");
			new FetchRssTask().execute(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void fetchRssSource(URL url) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			xpp.setInput(urlConnection.getInputStream(),
					urlConnection.getContentEncoding());

			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
					Log.d(TAG, "Start document");
				} else if (eventType == XmlPullParser.START_TAG) {
					Log.d(TAG, "Start tag " + xpp.getName());
				} else if (eventType == XmlPullParser.END_TAG) {
					Log.d(TAG, "End tag " + xpp.getName());
				} else if (eventType == XmlPullParser.TEXT) {
					Log.d(TAG, "Text " + xpp.getText());
				}
				eventType = xpp.next();
			}
			Log.d(TAG, "End document");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
