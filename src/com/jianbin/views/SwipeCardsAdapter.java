package com.jianbin.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianbin.swipecards.R;

public class SwipeCardsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	private List<SwipeData> mData;

	public class SwipeData {
		public int frontRes;
		public String backString;

		public SwipeData(int f, String b) {
			frontRes = f;
			backString = b;
		}
	}

	public List<SwipeData> getData() {
		if (mData != null)
			return mData;
		mData = new ArrayList<SwipeData>();
		mData.add(new SwipeData(R.drawable.card_front4, "Œ“∫‹√¶"));
		mData.add(new SwipeData(R.drawable.card_front3, "Slow"));
		mData.add(new SwipeData(R.drawable.card_front2, "Blonde On Blonde"));
		mData.add(new SwipeData(R.drawable.card_front1, "Like I Can"));
		return mData;
	}

	public SwipeCardsAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return getData().size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView tvFront;
		TextView tvBack;

		convertView = mInflater.inflate(R.layout.view_card, parent);
		CardView child = (CardView) parent
				.getChildAt(parent.getChildCount() - 1);

		tvBack = (TextView) child.getChildAt(0);
		tvFront = (ImageView) child.getChildAt(1);

		tvBack.setText(mData.get(position).backString);
		tvFront.setBackgroundResource(mData.get(position).frontRes);

		return convertView;
	}
}
