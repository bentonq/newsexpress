package com.bentonq.newsexpress;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prototype);

		ViewServer.get(this).addWindow(this);
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
