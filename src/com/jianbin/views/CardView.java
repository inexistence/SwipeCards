package com.jianbin.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

public class CardView extends FrameLayout {

	private View mFrontView;
	private View mBackView;

	public CardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CardView(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		initView();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		ViewHelper.setRotationX(mBackView, 180.0f);
	}

	public void initView() {
		if (getChildCount() >= 2) {
			mFrontView = getChildAt(1);
			mBackView = getChildAt(0);
		}
	}

	public View getFrontView() {
		return mFrontView;
	}

	public View getBackView() {
		return mBackView;
	}

}
