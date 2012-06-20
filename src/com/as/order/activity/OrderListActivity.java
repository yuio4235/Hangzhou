package com.as.order.activity;

import com.as.order.R;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class OrderListActivity extends AbstractActivity {

	private LinearLayout orderList;
	private ListView orderListLv;
	
	@Override
	public void onClick(View v) {
		orderList = (LinearLayout) layoutInflater.inflate(R.layout.order_list, null);
		mRootView.addView(orderList, FF);
		
		
	}

}
