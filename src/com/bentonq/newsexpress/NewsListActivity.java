package com.bentonq.newsexpress;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bentonq.newsexpress.SlidingFrameLayout.OnSlidingListener;

public class NewsListActivity extends Activity {

	private static final String TAG = "NewsListActivity";

	private NewsUpdater mNewsUpdater;
	private ListView mNewsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		// Enable hierarchy view locally on real device
		ViewServer.get(this).addWindow(this);

		mNewsListView = (ListView) findViewById(R.id.newslist_body);
		mNewsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String link = (String) parent
								.getItemAtPosition(position);
						Intent intent = new Intent(NewsListActivity.this,
								NewsActivity.class);
						intent.setData(Uri.parse(link));
						startActivity(intent);
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
						View view = inflater.inflate(
								R.layout.listitem_newstitle, parent, false);

						TextView textView = (TextView) view
								.findViewById(R.id.newstitle_title);
						textView.setText(fnews.get(position).title);

						ImageView imageView = (ImageView) view
								.findViewById(R.id.newstitle_image);
						imageView.setImageBitmap(fnews.get(position).image);
						return view;
					}
				});
			}
		});

		final View slidingMenu = findViewById(R.id.sliding_menu);
		SlidingFrameLayout slidingRoot = (SlidingFrameLayout) findViewById(R.id.sliding_root);
		slidingRoot.setOnSlidingListener(new OnSlidingListener() {

			@Override
			public void onOpen() {
				slidingMenu.setVisibility(View.VISIBLE);
			}

			@Override
			public void onClose() {
				slidingMenu.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onTotalOpen() {
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
