package com.bentonq.newsexpress;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SlidingFrameLayout extends FrameLayout {

	private static final String TAG = "SlidingFrameLayout";
	
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mTouchSlop;
	private int mMinimumVelocity;
	private int mMaximumVelocity;

	private boolean mIsBeingDragged = false;
	private int mLastMotionX;
	private int mLastMotionY;

	public SlidingFrameLayout(Context context) {
		super(context);
		init();
	}

	public SlidingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			scrollTo(x, y);
			invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		Log.v(TAG, "onInterceptTouchEvent: " + event.getAction() + ", ("
				+ event.getX() + ", " + event.getY() + ")");

		boolean intercepted = false;
		return intercepted;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(TAG, "onTouchEvent: " + event.getAction() + ", (" + event.getX()
				+ ", " + event.getY() + ")");

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getActionMasked();
		final int motionX = (int) event.getX();
		final int motionY = (int) event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mIsBeingDragged = false;
			mLastMotionX = (int) event.getX();
			mLastMotionY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = mLastMotionX - motionX;
			int dy = mLastMotionY - motionY;
			if (mIsBeingDragged) {
				mLastMotionX = motionX;
				mLastMotionY = motionY;
			} else {
				int absDx = Math.abs(dx);
				int absDy = Math.abs(dy);
				if (absDx > mTouchSlop && absDx > absDy) {
					if (dx > 0) {
						dx -= mTouchSlop;
					} else {
						dx += mTouchSlop;
					}
					mIsBeingDragged = true;
					mLastMotionX = motionX;
					mLastMotionY = motionY;
				}
			}
			scrollBy(dx, 0);
			break;
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();
			int scrollY = getScrollY();
			int width = -getWidth();
			int endX = 0;
			if (mIsBeingDragged) {
				mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocity = (int) mVelocityTracker.getXVelocity();
				if (velocity > mMinimumVelocity) {
					endX = width;
				}
			} else if (scrollX < width / 2) {
				endX = width;
			}
			mScroller.startScroll(scrollX, scrollY, endX - scrollX, 0);
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return true;
	}

	@Override
	public boolean shouldDelayChildPressedState() {
		return true;
	}

	private void initOrResetVelocityTracker() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
}
