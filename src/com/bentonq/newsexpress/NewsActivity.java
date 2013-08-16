package com.bentonq.newsexpress;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class NewsActivity extends Activity {

	private static final String TAG = "NewsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		String url = getIntent().getData().toString();
		WebView webView = (WebView) findViewById(R.id.web_view);
		webView.loadUrl(url);
	}
}
