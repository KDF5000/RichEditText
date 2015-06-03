package com.richtext.example;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLinearLayout extends LinearLayout {
	private OnResizeListener mListener;

	public interface OnResizeListener {
		void OnResize(int w, int h, int oldw, int oldh);
	}

	public void setOnResizeListener(OnResizeListener l) {
		mListener = l;
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mListener != null) {
			mListener.OnResize(w, h, oldw, oldh);
		}
	}
}