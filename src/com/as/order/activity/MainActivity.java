package com.as.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.order.R;
import com.as.order.preference.AsSettings;

public class MainActivity extends AbstractActivity {

	private LinearLayout main;
	private static final int ID_LOGOUT = R.id.title_btn_right;
	private static final int ID_ORDER_BY_STYLE = R.id.order_by_style;
	private static final int ID_CHENPIN_CHENLIE = R.id.chenpin_chenlie;
	private static final int ID_MUST_ORDER = R.id.must_order;
	private static final int ID_STYLE_REVIEW = R.id.style_review;
	private static final int ID_DAPEI_ORDER = R.id.dapei_order;
	private static final int ID_ORDER_ANALYSIS = R.id.order_analysis ;
	private static final int ID_THEME_ORDER = R.id.theme_order;
	private static final int ID_SUMMRIZE_ANALYSIS = R.id.summrize_analysis;
	private static final int ID_MY_ORDER = R.id.my_order;
	private static final int ID_ORDER_SETTING = R.id.order_setting;
	
	private TextView customerName;
	private TextView customerOrderAmount;
	
	private Button orderByStyleBtn;
	private Button chenpinChenlieBtn;
	private Button mustOrderBtn;
	private Button styleRevicewBtn;
	private Button dapeiOrderBtn;
	private Button orderAnalysisBtn;
	private Button themeOrderBtn;
	private Button summrizeAnalysisBtn;
	private Button myOrderBtn;
	private Button orderSetttingBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (LinearLayout) layoutInflater.inflate(R.layout.main, null);
		mRootView.addView(main, FF);
		
		customerName = (TextView) findViewById(R.id.main_customer_name);
		customerOrderAmount = (TextView) findViewById(R.id.main_customer_order_amount);
	
		customerName.setText(Html.fromHtml("尊敬的&nbsp;<u><i><font color=\"blue\">测试</font></i></u>&nbsp;用户"));
		customerOrderAmount.setText(Html.fromHtml("您的订货指标是:<u><i><font color=\"red\">100000</font></i></u>元"));
		setTextForTitleRightBtn(this.getString(R.string.app_logout));
		
		orderByStyleBtn = (Button) findViewById(R.id.order_by_style);
		chenpinChenlieBtn = (Button) findViewById(R.id.chenpin_chenlie);
		mustOrderBtn = (Button) findViewById(R.id.must_order);
		styleRevicewBtn = (Button) findViewById(R.id.style_review);
		dapeiOrderBtn = (Button) findViewById(R.id.dapei_order);
		orderAnalysisBtn = (Button) findViewById(R.id.order_analysis);
		themeOrderBtn = (Button) findViewById(R.id.theme_order);
		summrizeAnalysisBtn = (Button) findViewById(R.id.summrize_analysis);
		myOrderBtn = (Button) findViewById(R.id.my_order);
		orderSetttingBtn = (Button) findViewById(R.id.order_setting);
		
		orderByStyleBtn.setOnClickListener(this);
		chenpinChenlieBtn.setOnClickListener(this);
		mustOrderBtn.setOnClickListener(this);
		styleRevicewBtn.setOnClickListener(this);
		dapeiOrderBtn.setOnClickListener(this);
		orderAnalysisBtn.setOnClickListener(this);
		themeOrderBtn.setOnClickListener(this);
		myOrderBtn.setOnClickListener(this);
		summrizeAnalysisBtn.setOnClickListener(this);
		orderSetttingBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case ID_LOGOUT:
			finish();
			 break;
			 
		case ID_ORDER_BY_STYLE:
			Intent orderByStyleIntent = new Intent(MainActivity.this, OrderByStyleActivity.class);
			startActivity(orderByStyleIntent);
			break;
			
		case ID_CHENPIN_CHENLIE:
			Intent orderChenpinChenlieIntent = new Intent(MainActivity.this, ChenpinChenlieActivity.class);
			startActivity(orderChenpinChenlieIntent);
			break;
			
		case ID_MUST_ORDER:
			Intent mustOrderIntent = new Intent(MainActivity.this, MustOrderActivity.class);
			startActivity(mustOrderIntent);
			break;
			
		case ID_STYLE_REVIEW:
			Intent styleReviewIntent = new Intent(MainActivity.this, StyleReviewActivity.class);
			startActivity(styleReviewIntent);
			break;
			
		case ID_DAPEI_ORDER:
			Intent dapeiIntent = new Intent(MainActivity.this, DapeiOrderActivity.class);
			startActivity(dapeiIntent);
			break;
			
		case ID_ORDER_ANALYSIS:
			Intent orderAnalysisIntent = new Intent(MainActivity.this, OrderAnalysisActivity.class);
			startActivity(orderAnalysisIntent);
			break;
			
		case ID_THEME_ORDER:
			Intent themeOrderIntent = new Intent(MainActivity.this, ThemeOrderActivity.class);
			startActivity(themeOrderIntent);
			break;
			
		case ID_SUMMRIZE_ANALYSIS:
			Intent summrizeAnalysisIntent = new Intent(MainActivity.this, SummrizeAnalysisActivity.class);
			startActivity(summrizeAnalysisIntent);
			break;
			
		case ID_MY_ORDER:
			Intent myOrderIntent = new Intent(MainActivity.this, MyOrderActivity.class);
			startActivity(myOrderIntent);
			break;
			
		case ID_ORDER_SETTING:
			Intent orderSetttingIntent = new Intent(MainActivity.this, AsSettings.class);
			startActivity(orderSetttingIntent);
			break;
			 
			 default:
				 break;
		}
	}

}
