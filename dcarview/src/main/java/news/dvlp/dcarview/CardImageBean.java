package news.dvlp.dcarview;

public class CardImageBean {

	/**
	 * 折叠状态的上边距
	 */
	private int topMargin;

	/**
	 * 图片的宽
	 */
	private int width;

	/**
	 * 图片的高
	 */
	private int height;

	/**
	 * 图片的索引
	 */
	private int index;

	public CardImageBean() {
	}

	public CardImageBean(int index, int width, int height, int topMargin) {
		this.topMargin = topMargin;
		this.width = width;
		this.height = height;
		this.index = index;
	}

	public int getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
