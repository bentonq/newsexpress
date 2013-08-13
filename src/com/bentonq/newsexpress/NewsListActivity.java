package com.bentonq.newsexpress;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListActivity extends Activity {

	private NewsUpdater mNewsUpdater;
	private ListView mNewsListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNewsListView = (ListView) findViewById(R.id.news_list);

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
					public View getView(int position, View convertView,
							ViewGroup parent) {
						LayoutInflater inflater = LayoutInflater
								.from(NewsListActivity.this);
						View view = inflater.inflate(R.layout.view_summary,
								parent, false);
						
						TextView textView = (TextView) view.findViewById(R.id.title);
						textView.setText(fnews.get(position).title);
						
						TextView summaryView = (TextView) view.findViewById(R.id.summary);
						summaryView.setText(fnews.get(position).summary);
						
						return view;
					}
				});
			}
		});

	}
}
