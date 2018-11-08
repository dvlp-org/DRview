package news.dvlp.dcarview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class MyCardList extends RelativeLayout {

	/**
	 * 可设置：卡列表的高度（单位dp），设置-1为全屏
	 */
	private int cardlist_height = -1;

	/**
	 * 可设置：卡列表的上边距（单位dp）
	 */
	private int cardlist_top = 0;

	/**
	 * 可设置：卡片列表中每张卡片的间距--选中某张卡片状态（单位dp）
	 */
	private int open_spacing = 10;

	/**
	 * 可设置：选中的图片的上边距（单位dp）
	 */
	private int topMargin = 10;

	/**
	 * 可设置：卡片的宽度（单位dp）
	 */
	private int card_width = 300;

	/**
	 * 可设置：打开或折叠卡片列表时，动画执行的速度（单位ms）
	 */
	private int openOrCloseDuration = 400;

	/**
	 * 可设置：拖动卡片抬起时，卡片回归原位置执行动画的速度（单位ms）
	 */
	private int touchDuration = 260;

	/**
	 * 上下文
	 */
	private Context context;

	/**
	 * 滚动视图（解决动画执行过程中图片显示不全问题）
	 */
	private ScrollView sv_cardlist;

	/**
	 * 滚动视图的内部布局
	 */
	private RelativeLayout rl_inner;

	/**
	 * 装有卡列表的视图
	 */
	private RelativeLayout rl_cardlist;

	/**
	 * 图片信息集合
	 */
	List<CardInfoBean> cbs;

	/**
	 * imageview对象集合
	 */
	List<ImageView> ivs;

	/**
	 * 屏幕宽
	 */
	private int screen_width;

	/**
	 * 屏幕高（去除状态栏）
	 */
	private int screen_height;

	/**
	 * 状态栏高
	 */
	private int statusbar_height;

	/**
	 * 是否正在执行动画，0表示没有执行动画
	 */
	private int isAnim = 0;

	/**
	 * 是否正在拖动某张银行卡
	 */
	private boolean isMove = false;

	/**
	 * 卡片列表中每张卡片的间距--折叠状态
	 */
	private int show_spacing = -1;

	/**
	 * 卡片的高度
	 */
	private int card_height = -1;

	/**
	 * 当前打开的卡片索引（折叠状态为-1）
	 */
	private int openIndex = -1;

	/**
	 * 触摸当前打开的卡片时，手指与卡片上边直接的垂直距离
	 */
	private int openTouchY;

	public MyCardList(Context context, List<CardInfoBean> cbs) {
		super(context);
		this.context = context;
		this.cbs = cbs;

		// 将dp单位的各种变量转换成px单位
		open_spacing = ScreenUtil.dip2px(context, open_spacing);
		topMargin = ScreenUtil.dip2px(context, topMargin);
		card_width = ScreenUtil.dip2px(context, card_width);

		// 获取屏幕尺寸信息
		screen_width = ScreenUtil.getScreenWidth(context);
		statusbar_height = ScreenUtil.getStatusBarHeight(context);
		screen_height = ScreenUtil.getScreenHeight(context);

		// 将最外层滚动视图高度和上边距的单位转换成px
		cardlist_top = ScreenUtil.dip2px(context, cardlist_top);
		cardlist_height = cardlist_height == -1 ? screen_height - cardlist_top
				: ScreenUtil.dip2px(context, cardlist_height);

		// 创建卡列表
		createImageView();
	}

	/**
	 * 初始化卡列表中所有卡片
	 */
	private void createImageView() {
		// 设置背景色
		this.setBackgroundColor(Color.WHITE);

		// 初始化滚动视图
		sv_cardlist = new MyScrollView(context);
		// 隐藏滚动条
		sv_cardlist.setVerticalScrollBarEnabled(false);
		sv_cardlist.setHorizontalScrollBarEnabled(false);
		LayoutParams sv_Params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.addView(sv_cardlist, sv_Params);

		// 初始化滚动视图的内部布局
		rl_inner = new RelativeLayout(context);
		LayoutParams inner_params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		sv_cardlist.addView(rl_inner, inner_params);

		// 初始化装有卡列表的视图
		rl_cardlist = new RelativeLayout(context);

		// 初始化卡列表
		ivs = new ArrayList<ImageView>();
		// 遍历卡信息，根据卡信息创建imageview对象
		for (int i = 0; i < cbs.size(); i++) {
			CardInfoBean cardBean = cbs.get(i);
			String url = cardBean.getUrl();
			ImageView iv = new ImageView(context);
			int imageId = context.getResources().getIdentifier(url, "drawable",
					context.getPackageName());
			Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), imageId);
			// 获取图片宽高
			int height = bitmap.getHeight();
			int width = bitmap.getWidth();
			if (card_height == -1) {
				// 根据图片宽高比和规定的卡片的宽，计算出卡片的高
				// 为了美观，此高度只计算一次，后续卡片复用，建议使用尺寸相同的图片
				card_height = (int) (card_width * (height * 1.0 / width));
			}

			// 将图片缩放到和卡片相同大小，避免适配问题
			bitmap = chengeBitmap(bitmap, card_width, card_height);
			// 从左上角开始构图
			iv.setScaleType(ImageView.ScaleType.MATRIX);
			iv.setImageBitmap(bitmap);
			// 根据最外层滚动视图的大小，计算出折叠时每张卡片的间距
			if (show_spacing == -1) {
				if (card_height * cbs.size() > cardlist_height) {
					// 如果卡片总高度大于滚动视图的高度，则层叠展示
					show_spacing = (int) (1.0f * cardlist_height / cbs.size());
				} else {
					// 如果卡片总高度小于滚动视图的高度，则间隔展示
					show_spacing = card_height + 30;
				}

				// 计算出滚动视图内部布局的高度
				int rl_height = cardlist_height + card_height - show_spacing;
				LayoutParams rl_params = new LayoutParams(
						LayoutParams.MATCH_PARENT, rl_height);
				rl_inner.addView(rl_cardlist, rl_params);
			}

			// 初始化卡片的样式
			LayoutParams params = new LayoutParams(card_width, card_height);
			// 层叠展示时，下面的卡片遮盖上面的卡片
			params.topMargin = i * show_spacing;
			// 居中展示
			params.leftMargin = (int) (1.0f * (screen_width - card_width) / 2);
			iv.setLayoutParams(params);

			// 将卡片折叠状态的信息存入卡片的tag中
			iv.setTag(new CardImageBean(i, card_width, card_height,
					params.topMargin));
			// 设置点击事件
			iv.setOnClickListener(onclick);
			// 设置触摸事件
			iv.setOnTouchListener(ontouch);
			ivs.add(iv);
			rl_cardlist.addView(iv);
		}
	}

	/**
	 * 缩放bitmap
	 * 
	 * @param bitmap
	 * @param newWidth
	 *            缩放后bitmap的宽
	 * @param newHeight
	 *            缩放后bitmap的高
	 * @return
	 */
	private Bitmap chengeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	/**
	 * 点击事件
	 */
	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isAnim != 0) {
				// 正在执行动画，不可点击
				return;
			}

			// 获得当前点击卡片的信息
			Object tag = v.getTag();
			if (tag == null || !(tag instanceof CardImageBean)) {
				return;
			}
			CardImageBean imageBean = (CardImageBean) tag;

			// 获得当前点击卡片的索引
			int index = imageBean.getIndex();
			if (openIndex == -1) {
				// openIndex等于-1，说明当前是层叠状态，展开当前卡片

				// 设置当前展开的卡片索引
				openIndex = index;

				// 遍历卡片集合，所有的卡片执行相应动画
				for (int i = 0; i < ivs.size(); i++) {
					if (ivs.get(i).getTag() != null) {
						// 获得卡片信息
						CardImageBean imageI = (CardImageBean) ivs.get(i)
								.getTag();

						// 上下位移的距离
						int toY = 0;
						if (imageI.getIndex() == index) {
							// 如果是当前点击的图片，位移到滚动视图顶部
							toY = topMargin - imageI.getTopMargin();
						} else if (imageI.getIndex() > index) {
							// 如果是当前卡片之后的卡片，按顺序位移到滚动视图底部
							int bottomMargin = (ivs.size() - imageI.getIndex())
									* open_spacing;
							toY = cardlist_height - bottomMargin
									- imageI.getTopMargin();
						} else {
							// 如果是当前卡片之前的卡片，去除当前卡片上移的影响，按顺序位移到滚动视图底部
							int bottomMargin = (ivs.size() - imageI.getIndex() - 1)
									* open_spacing;
							toY = cardlist_height - bottomMargin
									- imageI.getTopMargin();
						}
						// 开始执行位移动画
						startTranslate(ivs.get(i), 0, 0, 0, toY,
								openOrCloseDuration);
					}
				}
			} else if (index == openIndex) {
				// openIndex不等于-1，说明当前是层叠状态，点击打开的卡片

				// 回归初始状态
				closeCardList();
			}

		}
	};

	/**
	 * 触摸事件
	 */
	private OnTouchListener ontouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// 准入条件：1.没有执行动画，2.处于打开状态
			if (isAnim == 0 && openIndex != -1 && v instanceof ImageView
					&& v.getTag() != null) {
				// 活动触摸卡片的信息
				CardImageBean imageBean = (CardImageBean) v.getTag();
				int index = imageBean.getIndex();
				// 准入条件：触摸卡片为当前打开的卡片
				if (index == openIndex) {
					// 获取当前触摸卡片的样式
					LayoutParams params = (LayoutParams) v.getLayoutParams();
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// 记录手指与卡片上边直接的垂直距离
						openTouchY = (int) (event.getRawY() - params.topMargin - cardlist_top);

						// 正在滑动
						isMove = true;
						break;
					case MotionEvent.ACTION_MOVE:
						// 准入条件：正在滑动
						if (!isMove) {
							break;
						}

						// 设置卡片随手指移动
						int rawY = (int) event.getRawY();
						params.topMargin = rawY - openTouchY - cardlist_top;
						v.setLayoutParams(params);
						// 准入添加，手指滑动到滚动视图以上的位置
						if (rawY < statusbar_height + cardlist_top) {
							// 滑动取消
							isMove = false;
							openTouchY = -1;
							// 使图片回到滑动前的位置
							int toY = topMargin - params.topMargin;
							startTranslate(v, 0, 0, 0, toY, touchDuration);
						}
						break;
					case MotionEvent.ACTION_UP:
						// 准入条件：正在滑动
						if (!isMove) {
							break;
						}
						// 滑动取消
						isMove = false;
						openTouchY = -1;

						// 计算滑动的位置
						int toY = topMargin - params.topMargin;
						if (toY != 0) {
							// 底部最上面卡片的上边距
							LayoutParams firstParams = null;
							for (int i = 0; i < ivs.size(); i++) {
								Object tag = ivs.get(i).getTag();
								int first = index == 0 ? 1 : 0;
								if (tag != null
										&& tag instanceof CardImageBean
										&& ((CardImageBean) tag).getIndex() == first) {
									firstParams = (LayoutParams) ivs.get(i)
											.getLayoutParams();
									break;
								}
							}
							if (firstParams != null
									&& (card_height + params.topMargin) > firstParams.topMargin) {
								// 如果当前滑动的卡片底边滑动到底部卡片区域，则折叠卡列表
								closeCardList();
							} else {
								// 如果当前滑动的卡片底边没有滑动到底部卡片区域，则使图片回到滑动前的位置
								startTranslate(v, 0, 0, 0, toY, touchDuration);
							}
						}
						break;
					}
				}
			}
			return false;
		}
	};

	/**
	 * 使卡片列表回到初始状态
	 */
	private void closeCardList() {
		for (int i = 0; i < ivs.size(); i++) {
			if (ivs.get(i).getTag() != null) {
				CardImageBean imageI = (CardImageBean) ivs.get(i).getTag();
				int toY = 0;
				LayoutParams params = (LayoutParams) ivs.get(i)
						.getLayoutParams();
				toY = imageI.getTopMargin() - params.topMargin;
				startTranslate(ivs.get(i), 0, 0, 0, toY, openOrCloseDuration);
			}
		}
		openIndex = -1;
	}

	/**
	 * 执行位移动画
	 * 
	 * @param beginX
	 *            起始坐标X（传0,不支持横向滑动）
	 * @param beginY
	 *            起始坐标Y（传0,仅支持从当前位置开始滑动）
	 * @param endX
	 *            终点坐标X（传0,不支持横向滑动）
	 * @param endY
	 *            终点坐标Y（值为相对自身左上角位置的坐标）
	 * @param duration
	 *            动画持续的时长（单位毫秒）
	 */
	public void startTranslate(final View view, float beginX, float beginY,
			float endX, final float endY, int duration) {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, beginX, Animation.ABSOLUTE, endX,
				Animation.RELATIVE_TO_SELF, beginY, Animation.ABSOLUTE, endY);
		translateAnimation.setDuration(duration);
		// 动画执行到终点位置停止，不回到初始位置
		translateAnimation.setFillAfter(true);
		// 动画监听
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画执行完毕，更新卡片样式
				LayoutParams layoutParams = (LayoutParams) view
						.getLayoutParams();
				layoutParams.topMargin = (int) (layoutParams.topMargin + endY);
				view.clearAnimation();
				view.setLayoutParams(layoutParams);
				// 记录当前动画执行完毕
				isAnim--;
			}
		});
		// 开始动画
		view.startAnimation(translateAnimation);
		// 记录当前动画正在执行
		isAnim++;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 设置卡列表的高度和上边距
		ViewGroup.LayoutParams sv_params = this.getLayoutParams();
		sv_params.height = cardlist_height;
		if (sv_params instanceof AbsoluteLayout.LayoutParams) {
			AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) this
					.getLayoutParams();
			params.y = cardlist_top;
		} else if (sv_params instanceof MarginLayoutParams) {
			MarginLayoutParams params = (MarginLayoutParams) this
					.getLayoutParams();
			params.topMargin = cardlist_top;
		}
		this.setLayoutParams(sv_params);
		super.onSizeChanged(w, h, oldw, oldh);
	}
}
