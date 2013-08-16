package com.bentonq.newsexpress;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Scroller;

// TODO Adjust animation
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
	private int mOriginMotionX;

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

		mSlidingPadding = 0;
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
		boolean intercepted = false;

		final int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// Reset velocity tracker
			initOrResetVelocityTracker();
			mVelocityTracker.addMovement(event);

			// Remember touch location
			mLastMotionX = (int) event.getX();
			mLastMotionY = (int) event.getY();
			mOriginMotionX = mLastMotionX;

			// If being scrolling and user touches, start dragging right now.
			// The user is being blocked to touching the child view at this
			// time.
			// But when the scroll animation is almost finish, the user can
			// touches.
			mIsBeingDragged = (!mScroller.isFinished() && Math.abs(mScroller
					.getCurrX() - mScroller.getFinalX()) > 5);
			break;
		case MotionEvent.ACTION_MOVE:
			final int motionX = (int) event.getX();
			final int motionY = (int) event.getY();
			if (mIsBeingDragged || isStartDragging(motionX, motionY)) {
				intercepted = true;
				initVelocityTrackerIfNotExists();
				mVelocityTracker.addMovement(event);
				disallowParentInterceptTouchEvent();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// Reset the drag
			mIsBeingDragged = false;
			recycleVelocityTracker();
			break;
		}

		Log.v(TAG, "onInterceptTouchEvent: " + event.getAction() + ", ("
				+ event.getX() + ", " + event.getY() + ")");

		return intercepted;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(event);

		final int action = event.getActionMasked();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// If is scrolling, stop it
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE: {
			// Follow finger touches
			final int motionX = (int) event.getX();
			final int motionY = (int) event.getY();
			int dx = 0;
			if (mIsBeingDragged) {
				dx = mLastMotionX - motionX;
				mLastMotionX = motionX;
			} else if (isStartDragging(motionX, motionY)) {
				dx = mLastMotionX - motionX;
				// Avoid glitter
				if (dx > 0) {
					dx -= mTouchSlop;
				} else {
					dx += mTouchSlop;
				}
				mLastMotionX = motionX;
			}
			slideBy(dx);
			break;
		}
		case MotionEvent.ACTION_UP: {
			final int scrollX = getScrollX();
			final int rightClamp = mSlidingPadding - getWidth();
			final boolean isScrollXCrossMiddleLine = (scrollX < rightClamp / 2) ? true
					: false;
			int endX = 0;
			if (mIsBeingDragged) {
				// Check finger movement speed
				mVelocityTracker.computeCurrentVelocity(1000 /* MS */,
						mMaximumVelocity);
				final int velocity = (int) mVelocityTracker.getXVelocity();
				if (velocity > mMinimumVelocity
						|| (Math.abs(velocity) < mMinimumVelocity && isScrollXCrossMiddleLine)) {
					endX = rightClamp;
				}
			} else if (isScrollXCrossMiddleLine) {
				endX = rightClamp;
			}
			flingBy(endX - scrollX);
			recycleVelocityTracker();
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			// Reset to origin position
			final int scrollX = getScrollX();
			final int rightClamp = mSlidingPadding - getWidth();
			final boolean isOriginXCrossMiddleLine = (mOriginMotionX < rightClamp / 2) ? true
					: false;
			int endX = 0;
			if (isOriginXCrossMiddleLine) {
				endX = rightClamp;
			}
			flingBy(endX - scrollX);
			recycleVelocityTracker();
			break;
		}
		}

		Log.v(TAG, "onTouchEvent: " + event.getAction() + ", (" + event.getX()
				+ ", " + event.getY() + ")");

		return true;
	}

	@Override
	public boolean shouldDelayChildPressedState() {
		return true;
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		if (disallowIntercept) {
			recycleVelocityTracker();
		}
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	public int getSlidingPadding() {
		return mSlidingPadding;
	}

	public void setSlidingPadding(int slidingPadding) {
		mSlidingPadding = slidingPadding;
	}

	public void slideBy(int x) {
		final int rightClamp = mSlidingPadding - getWidth();

		int newScrollX = 0;
		if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
			// Use newest position
			newScrollX = mScroller.getCurrX() + x;
			mScroller.abortAnimation();
		} else {
			newScrollX = getScrollX() + x;
		}

		// Never scroll outside the bound
		if (newScrollX < 0 && newScrollX > rightClamp) {
			scrollBy(x, 0);
		}
	}

	public void flingBy(int x) {
		final int rightClamp = mSlidingPadding - getWidth();

		int startX = 0;
		int startY = 0;
		int dx = 0;
		if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
			// Use newest position
			startX = mScroller.getCurrX();
			startY = mScroller.getCurrY();
			dx = x + getScrollX() - startX;
			mScroller.abortAnimation();
		} else {
			startX = getScrollX();
			startY = getScrollY();
			dx = x;
		}

		// Never scroll outside the bound
		int endX = startX + dx;
		if (endX < rightClamp) {
			dx = rightClamp - startX;
		} else if (endX > 0) {
			dx = -startX;
		}

		if (dx != 0) {
			mScroller.startScroll(startX, startY, dx, 0, 500 /* MS */);
			invalidate();
		}
	}

	private boolean isStartDragging(int motionX, int motionY) {
		int absDx = Math.abs(mLastMotionX - motionX);
		int absDy = Math.abs(mLastMotionY - motionY);
		if (absDx > mTouchSlop && absDx > absDy) {
			return mIsBeingDragged = true;
		}
		return false;
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

	private void disallowParentInterceptTouchEvent() {
		final ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
	}
}
