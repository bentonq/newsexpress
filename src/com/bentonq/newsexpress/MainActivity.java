package com.bentonq.newsexpress;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prototype);

		ViewServer.get(this).addWindow(this);

		SlidingFrameLayout sfl = (SlidingFrameLayout) findViewById(R.id.sfl);
		sfl.setSlidingPadding(144);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
