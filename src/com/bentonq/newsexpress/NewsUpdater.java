package com.bentonq.newsexpress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Xml;

public class NewsUpdater {

	// private static final String TAG = null;
	private static final String NAME_SPACE = null;

	private UpdateListener mListener;
	private UpdateTask mUpdateTask;

	public interface UpdateListener {
		public void onNewsUpdated(List<News> news);
	}

	private class UpdateTask extends AsyncTask<String, Void, List<News>> {

		@Override
		protected List<News> doInBackground(String... urls) {
			List<News> result = new ArrayList<News>();
			try {
				InputStream stream = downloadFromNetwork(urls[0]);
				result = parseXml(stream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<News> result) {
			// for (News news : result) {
			// Log.d(TAG, news.title);
			// }
			if (mListener != null) {
				mListener.onNewsUpdated(result);
			}
		}

		private InputStream downloadFromNetwork(String urlString)
				throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* ms */);
			conn.setConnectTimeout(15000 /* ms */);
			conn.setRequestMethod("GET");
			conn.connect();
			return conn.getInputStream();
		}

		private List<News> parseXml(InputStream xml)
				throws XmlPullParserException, IOException {
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
						false);
				parser.setInput(xml, null);
				parser.nextTag();
				return readRss(parser);
			} finally {
				xml.close();
			}
		}

		private List<News> readRss(XmlPullParser parser)
				throws XmlPullParserException, IOException {
			List<News> newsList = new ArrayList<News>();

			parser.require(XmlPullParser.START_TAG, NAME_SPACE, "rss");
			parser.nextTag();
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				if (name.equals("item")) {
					newsList.add(readItem(parser));
				} else {
					skip(parser);
				}
			}
			return newsList;
		}

		private News readItem(XmlPullParser parser)
				throws XmlPullParserException, IOException {
			parser.require(XmlPullParser.START_TAG, NAME_SPACE, "item");
			String title = null;
			String summary = null;
			String link = null;
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				if (name.equals("title")) {
					title = readTitle(parser);
				} else if (name.equals("description")) {
					summary = readSummary(parser);
				} else if (name.equals("link")) {
					link = readLink(parser);
				} else {
					skip(parser);
				}
			}

			Bitmap bitmap = parseImage(summary);
			return new News(title, summary, bitmap, link);
		}

		// Processes title tags in the feed.
		private String readTitle(XmlPullParser parser) throws IOException,
				XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, NAME_SPACE, "title");
			String title = readText(parser);
			parser.require(XmlPullParser.END_TAG, NAME_SPACE, "title");
			return title;
		}

		// Processes link tags in the feed.
		private String readLink(XmlPullParser parser) throws IOException,
				XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, NAME_SPACE, "link");
			String link = readText(parser);
			parser.require(XmlPullParser.END_TAG, NAME_SPACE, "link");
			return link;
		}

		// Processes summary tags in the feed.
		private String readSummary(XmlPullParser parser) throws IOException,
				XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, NAME_SPACE, "description");
			String summary = readText(parser);
			parser.require(XmlPullParser.END_TAG, NAME_SPACE, "description");
			return summary;
		}

		// For the tags title and summary, extracts their text values.
		private String readText(XmlPullParser parser) throws IOException,
				XmlPullParserException {
			String result = "";
			if (parser.next() == XmlPullParser.TEXT) {
				result = parser.getText();
				parser.nextTag();
			}
			return result;
		}

		private void skip(XmlPullParser parser) throws XmlPullParserException,
				IOException {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				throw new IllegalStateException();
			}
			int depth = 1;
			while (depth != 0) {
				switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
				}
			}
		}

		private Bitmap parseImage(String summary) throws IOException {
			String imgUrl = "";
			XmlPullParser parser = Xml.newPullParser();
			try {
				parser.setInput(new StringBufferInputStream(summary), null);
				while (parser.nextTag() == XmlPullParser.START_TAG) {
					String name = parser.getName();
					if (name.equals("img")) {
						imgUrl = parser.getAttributeValue(0);
						break;
					}
				}
			} catch (XmlPullParserException e) {
			}

			Bitmap bitmap = null;
			if (imgUrl != "") {
				InputStream inputStream = downloadFromNetwork(imgUrl.trim());

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte data[] = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				bitmap = BitmapFactory.decodeByteArray(
						outputStream.toByteArray(), 0, outputStream.size());
			}

			return bitmap;
		}
	}

	public NewsUpdater() {
		mUpdateTask = new UpdateTask();
	}

	public void update(String url, UpdateListener listener) {
		mListener = listener;
		if (mUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
			// TODO Cancel task
		}

		mUpdateTask.execute(url);
	}

}