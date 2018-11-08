package news.dvlp.dcarview;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	public MyScrollView(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 向下传递触摸事件
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 不允许滚动
		return true;
	}

}
