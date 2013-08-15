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

	private int mSlidingPadding;

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
		initSlidingFrameLayout();
	}

	public SlidingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSlidingFrameLayout();
	}

	public SlidingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSlidingFrameLayout();
	}

	private void initSlidingFrameLayout() {
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mSlidingPadding = 96; // TODO add set/get
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
			final int motionX = (int) event.getX();
			final int motionY = (int) event.getY();
			int dx = 0;
			if (mIsBeingDragged) {
				dx = mLastMotionX - motionX;
				mLastMotionX = motionX;
			} else {
				int absDx = Math.abs(mLastMotionX - motionX);
				int absDy = Math.abs(mLastMotionY - motionY);
				if (absDx > mTouchSlop && absDx > absDy) {
					dx = mLastMotionX - motionX;
					if (dx > 0) {
						dx -= mTouchSlop;
					} else {
						dx += mTouchSlop;
					}
					mIsBeingDragged = true;
					mLastMotionX = motionX;
				}
			}
			slideBy(dx);
			break;
		case MotionEvent.ACTION_UP:
			final int scrollX = getScrollX();
			final int rightClamp = mSlidingPadding - getWidth();
			int endX = 0;
			if (mIsBeingDragged) {
				mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				final int velocity = (int) mVelocityTracker.getXVelocity();
				Log.v(TAG, "Speed " + velocity + "," + mMinimumVelocity);
				if (velocity > mMinimumVelocity
						|| (Math.abs(velocity) < mMinimumVelocity && scrollX < rightClamp / 2)) {
					endX = rightClamp;
				}
			} else if (scrollX < rightClamp / 2) {
				endX = rightClamp;
			}
			flingBy(endX - scrollX);
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

	private void slideBy(int dx) {
		final int rightClamp = mSlidingPadding - getWidth();
		final int newScrollX = getScrollX() + dx;
		if (newScrollX < 0 && newScrollX > rightClamp) {
			scrollBy(dx, 0);
		}
	}

	private void flingBy(int dx) {
		final int scrollX = getScrollX();
		final int scrollY = getScrollY();
		mScroller.startScroll(scrollX, scrollY, dx, 0);
		invalidate();
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
