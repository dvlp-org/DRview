package news.dvlp.drview;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;

import news.dvlp.dcarview.CardInfoBean;
import news.dvlp.dcarview.MyCardList;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 添加卡信息
		List<CardInfoBean> cbs = new ArrayList<CardInfoBean>();
		for (int i = 0; i < 10; i++) {
			CardInfoBean cardBean = new CardInfoBean("card" + (i % 2),
					"callback");
			cbs.add(cardBean);
		}

		AbsoluteLayout rl = (AbsoluteLayout) findViewById(R.id.rl);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 将卡列表添加到当前视图
		rl.addView(new MyCardList(this, cbs), params);
	}

}
