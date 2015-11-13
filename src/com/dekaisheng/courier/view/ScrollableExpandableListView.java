package com.dekaisheng.courier.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 自定义ExpangableListView，当ExpangableListView嵌套在
 * ScrollView时，使用该自定义的ExpangableListView，否则高度
 * 无法设置
 * @author Dorian
 *
 */
public class ScrollableExpandableListView extends ExpandableListView{
	
	public ScrollableExpandableListView(Context context) {
		super(context);
	}
	
	public ScrollableExpandableListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ScrollableExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
