package com.bentonq.newsexpress;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class SideScrollListView extends ListView {
	
	private static final String TAG = "SideScrollListView";

	private int mTouchSlop;
	private boolean mIsScrolling;

	public SideScrollListView(Context context) {
		super(context);
		init();
	}

	public SideScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SideScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		Log.d(TAG, "onInterceptTouchEvent " + event.getActionMasked());
		final int action = event.getActionMasked();

		// Always handle the case of the touch gesture being complete.
		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			// Release the scroll.
			mIsScrolling = false;
			return false; // Do not intercept touch event, let the child handle
							// it
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (mIsScrolling) {
				// We're currently scrolling, so yes, intercept the
				// touch event!
				return true;
			}

			// If the user has dragged her finger horizontally more than
			// the touch slop, start the scroll

			// left as an exercise for the reader
			final int xDiff = calculateDistanceX(event);
			Log.d(TAG, "xDiff = " + xDiff);

			// Touch slop should be calculated using ViewConfiguration
			// constants.
			if (xDiff > mTouchSlop) {
				// Start scrolling!
				mIsScrolling = true;
				return true;
			}
			break;
		}

		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getActionMasked();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void init() {
		ViewConfiguration vc = ViewConfiguration.get(getContext());
		mTouchSlop = vc.getScaledTouchSlop();
		mIsScrolling = false;
	}

	private int calculateDistanceX(MotionEvent event) {
		int historyCount = event.getHistorySize();
		if (historyCount > 2) {
			return (int) (event.getHistoricalX(historyCount - 1) - event
					.getHistoricalX(historyCount - 2));
		}
		return 0;
	}
}
