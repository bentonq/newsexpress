package com.bentonq.newsexpress;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListActivity extends Activity {

	private static final String TAG = "NewsListActivity";

	private NewsUpdater mNewsUpdater;
	private ListView mNewsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNewsListView = (ListView) findViewById(R.id.news_list);
		mNewsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String link = (String) parent
								.getItemAtPosition(position);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(link));
						startActivity(intent);
					}
				});
		mNewsListView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int historySize = event.getHistorySize();
				final int pointerCount = event.getPointerCount();
				for (int h = 0; h < historySize; h++) {
					Log.d(TAG, "At time " + event.getHistoricalEventTime(h));
					for (int p = 0; p < pointerCount; p++) {
						Log.d(TAG,
								"  pointer " + event.getPointerId(p) + " ("
										+ event.getHistoricalX(p, h) + ", "
										+ event.getHistoricalY(p, h) + ")");
					}
				}
				Log.d(TAG, "At time " + event.getEventTime());
				for (int p = 0; p < pointerCount; p++) {
					Log.d(TAG, "  pointer " + event.getPointerId(p) + " ("
							+ event.getX(p) + ", " + event.getY(p) + ")");
				}
				return false;
			}
		});

		String url = new String(
				"http://news.163.com/special/00011K6L/rss_newstop.xml");
		mNewsUpdater = new NewsUpdater();
		mNewsUpdater.update(url, new NewsUpdater.UpdateListener() {

			@Override
			public void onNewsUpdated(List<News> news) {
				final List<News> fnews = news;

				mNewsListView.setAdapter(new BaseAdapter() {

					@Override
					public int getCount() {
						return fnews.size();
					}

					@Override
					public Object getItem(int position) {
						return fnews.get(position).link;
					}

					@Override
					public long getItemId(int position) {
						return position;
					}

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						LayoutInflater inflater = LayoutInflater
								.from(NewsListActivity.this);
						View view = inflater.inflate(R.layout.view_summary,
								parent, false);

						TextView textView = (TextView) view
								.findViewById(R.id.title);
						textView.setText(fnews.get(position).title);

						TextView summaryView = (TextView) view
								.findViewById(R.id.summary);
						summaryView.setText(fnews.get(position).summary);

						return view;
					}
				});
			}
		});

	}
}
