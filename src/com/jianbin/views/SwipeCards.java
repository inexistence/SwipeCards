package com.jianbin.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.FrameLayout;

@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
public class SwipeCards extends FrameLayout {

	private Adapter mAdapter;

	private int mScreenWidth;
	private int mHeight;
	// 第一张卡片的大小
	private int mCardHeight;
	private int mCardWidth;

	// 逐层卡片间距
	private int mThin = 7;
	// 逐层递减的卡片倍率
	private float scaleSize = 0.09f;

	private boolean isAnim = false;

	private OnLeftSwipeListener mOnLeftSwipeListener;
	private OnRightSwipeListener mOnRightSwipeListener;

	public SwipeCards(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// 获取屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;

		// 把dp转化成px
		mThin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mThin, context.getResources().getDisplayMetrics());
	}

	public SwipeCards(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeCards(Context context) {
		this(context, null);
	}

	public void setOnLeftSwipeListener(OnLeftSwipeListener listener) {
		mOnLeftSwipeListener = listener;
	}

	public void setOnRightSwipeListener(OnRightSwipeListener listener) {
		mOnRightSwipeListener = listener;
	}

	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			mAdapter.getView(i, null, this);
		}
	}

	private boolean first = true;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childCount = getChildCount();

		if (first) {
			if (childCount > 0) {
				mHeight = 0;
				View child = getChildAt(childCount - 1);
				mCardHeight = child.getLayoutParams().height;
				mCardWidth = child.getLayoutParams().width;
				// = mScreenWidth;
				mHeight = mCardHeight + (childCount - 1) * mThin;
				getLayoutParams().height = mHeight;

				for (int i = 0; i < childCount; i++) {
					float scale = (1.0f + scaleSize) - scaleSize
							* (childCount - i);
					View child1 = getChildAt(i);
					child1.setPivotX(mCardWidth / 2);
					child1.setPivotY(0);
					child1.setScaleX(scale);
					child1.setScaleY(scale);
				}
			}
			first = false;
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				float marginTop = (mHeight - mCardHeight)
						- (childCount - i + 0.5f) * mThin;
				child.setTranslationY(marginTop);
			}
		}
	}

	public View getTopView(float x, float y) {
		int childCount = getChildCount();
		if (childCount == 0)
			return null;
		View child = getChildAt(childCount - 1);
		float curX = child.getX();
		float curY = child.getY();
		if (x >= curX && x <= curX + mCardWidth && y >= curY
				&& y <= curY + mCardHeight) {
			return child;
		}
		return null;

	}

	private boolean isMoving = false;
	private boolean rotateEnd = false;

	private float mScaleX;

	private float mInitX;
	private float mDistX;

	private float mInitY;
	private float mDistY;

	private int MIN_TRANS_X = 15;
	private int MIN_ROTATE_Y = 50;

	private View mTopView;

	public boolean translateable(float distX) {
		return !(distX <= MIN_TRANS_X && distX >= -MIN_TRANS_X);
	}

	public boolean rotateable(float distY) {
		return !(distY <= MIN_ROTATE_Y && distY >= -MIN_ROTATE_Y)
				&& rotateEnd == false;
	}

	private boolean mOnLeftSwipe = false;
	private boolean mOnRightSwipe = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mTopView = getTopView(event.getX(), event.getY());
			mInitX = event.getX();
			mInitY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			isMoving = true;
			if (!isAnim && null != mTopView) {
				mDistY = event.getY() - mInitY;
				mDistX = event.getX() - mInitX;
				mScaleX = Math.abs(mDistX / mCardWidth);

				if (translateable(mDistX)) {
					mTopView.setTranslationX(mDistX);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (!isAnim && isMoving && null != mTopView) {
				if (rotateable(mDistY)) {// 翻转
					mTopView.setPivotY(mCardHeight / 2);
					if ((mDistY >= mCardHeight / 3) && !translateable(mDistX)) {
						swapFrontBack();
					}
				}
				if (translateable(mDistX) && mScaleX > 0.15f && mDistX > 0) {// 向右滑动
					mOnLeftSwipe = false;
					mOnRightSwipe = true;
					mTopView.animate().translationX(mCardWidth + mScreenWidth)
							.setListener(mRemoveAnimListener);

				} else if (translateable(mDistX) && mScaleX > 0.15f
						&& mDistX < 0) {// 向左滑动
					mOnLeftSwipe = true;
					mOnRightSwipe = false;
					mTopView.animate().translationX(-mCardWidth - mScreenWidth)
							.setListener(mRemoveAnimListener);
				} else {// 向中间回
					mTopView.animate().translationX(0)
							.setListener(mAnimListener);
				}
				mTopView = null;
				isMoving = false;
			}
			return true;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void swapFrontBack() {
		mTopView.setPivotX(mCardWidth / 2);
		mTopView.setPivotY(mCardHeight / 2);
		mTopView.animate().translationX(0).alpha(1.0f).rotationX(180f)
				.setListener(mAnimListener);
		if (mTopView instanceof CardView) {
			((CardView) mTopView).getBackView().animate().alpha(1.0f);
			((CardView) mTopView).getFrontView().animate().alpha(0.0f);
		}
		rotateEnd = true;
	}

	private void reLayout() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			float marginTop = (mHeight - mCardHeight) - (childCount - i + 0.5f)
					* mThin;
			child.animate().translationY(marginTop).setDuration(500)
					.setListener(mAnimListener);
		}
	}

	private void reMeasure() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			float scale = (1.0f + scaleSize) - scaleSize * (childCount - i);
			View child1 = getChildAt(i);
			child1.setPivotX(mCardWidth / 2);
			child1.setPivotY(0);
			child1.animate().scaleX(scale).setDuration(500)
					.setListener(mAnimListener);
			child1.animate().scaleY(scale).setDuration(500)
					.setListener(mAnimListener);
		}
	}

	private SlidingAnimatorListenerAdapter mRemoveAnimListener = new SlidingAnimatorListenerAdapter() {
		@Override
		public void onAnimationEnd(Animator animation) {
			super.onAnimationEnd(animation);
			mTopView = null;
			removeViewAt(getChildCount() - 1);
			//设置滑动删除结束监听
			if (mOnLeftSwipe) {
				if (null != mOnLeftSwipeListener)
					mOnLeftSwipeListener.onLeftSwipe(SwipeCards.this);
			} else if (mOnRightSwipe) {
				if (null != mOnRightSwipeListener)
					mOnRightSwipeListener.onRightSwipe(SwipeCards.this);
			}
			rotateEnd = false;
			reLayout();
			reMeasure();
		}
	};
	private SlidingAnimatorListenerAdapter mAnimListener = new SlidingAnimatorListenerAdapter();

	private class SlidingAnimatorListenerAdapter extends
			AnimatorListenerAdapter {
		@Override
		public void onAnimationStart(Animator animation) {
			isAnim = true;
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			isAnim = false;
		}
	}

	/**
	 * 
	 * 向左滑动删除视图结束时监听
	 */
	public interface OnLeftSwipeListener {
		/**
		 * 向左滑动删除视图结束监听
		 * 
		 * @param v
		 *            该卡片视图
		 */
		public void onLeftSwipe(View v);
	}

	/**
	 * 
	 * 向右滑动删除视图结束时监听
	 */
	public interface OnRightSwipeListener {
		/**
		 * 向右滑动删除视图结束时监听
		 * 
		 * @param v
		 *            该卡片视图
		 */
		public void onRightSwipe(View v);
	}
}
