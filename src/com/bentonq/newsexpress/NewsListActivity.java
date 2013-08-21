package com.bentonq.newsexpress;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListActivity extends Activity {

	private static final String TAG = "NewsListActivity";

	private NewsUpdater mNewsUpdater;
	private ListView mNewsListView;

	private static final int[] NEWSITEM_TYPE = {
			R.layout.listadapter_newsitem_normal,
			R.layout.listadapter_newsitem_shorttitle,
			R.layout.listadapter_newsitem_largeimage,
			R.layout.listadapter_newsitem_noimage };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newslist);

		// Enable hierarchy view locally on real device
		ViewServer.get(this).addWindow(this);

		mNewsListView = (ListView) findViewById(R.id.newslist_listview);
		mNewsListView.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 10;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = LayoutInflater
						.from(NewsListActivity.this);
				View view = inflater.inflate(NEWSITEM_TYPE[position % 4],
						parent, false);
				return view;
			}

		});

		String url = new String("http://www.36kr.com/feed");
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

						int type = position % 3;
						if (fnews.get(position).image == null) {
							type = 3; // no image
						}
						View view = inflater.inflate(NEWSITEM_TYPE[type],
								parent, false);

						TextView textView = (TextView) view
								.findViewById(R.id.newsitem_title);
						textView.setText(fnews.get(position).title);

						if (type != 2) { // large image
							TextView summaryText = (TextView) view
									.findViewById(R.id.newsitem_summary);
							summaryText.setText(fnews.get(position).summary);
						}

						if (type != 3) { // no image
							ImageView imageView = (ImageView) view
									.findViewById(R.id.newsitem_image);
							imageView.setImageBitmap(fnews.get(position).image);
						}
						return view;
					}
				});
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// For hierarchy view
		ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// For hierarchy view
		ViewServer.get(this).removeWindow(this);
	}
}
