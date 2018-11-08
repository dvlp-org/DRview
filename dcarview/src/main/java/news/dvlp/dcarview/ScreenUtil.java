package news.dvlp.dcarview;

import android.content.Context;
import android.view.WindowManager;

public class ScreenUtil {

	/**
	 * 根据手机的分辨率从dp的单位转成为px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从px(像素)的单位转成为dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	/**
	 * 获取屏幕高度（不含状态栏）
	 * 
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight() - getStatusBarHeight(context);
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}