package news.dvlp.dcarview;

public class CardInfoBean {

	/**
	 * 图片地址
	 */
	private String url;

	/**
	 * 图片点击回调
	 */
	private String callback;

	public CardInfoBean() {
	}

	public CardInfoBean(String url, String callback) {
		this.url = url;
		this.callback = callback;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

}
