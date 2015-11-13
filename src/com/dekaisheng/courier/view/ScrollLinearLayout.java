package com.dekaisheng.courier.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollLinearLayout extends LinearLayout {

	public ScrollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(context);
	}

	private Scroller mScroller;

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), 0);
			postInvalidate();
		}
	}


	public void snapToScreen(int whichScreen) {
		int curscrollerx = getScrollX();
		mScroller.startScroll(curscrollerx, 0, whichScreen - curscrollerx, 0, 500);
		invalidate();

	}

}
