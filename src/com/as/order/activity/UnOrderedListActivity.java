package com.as.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.order.R;

public class UnOrderedListActivity extends AbstractActivity {
	
	private Button daleiWdBtn;
	private Button xiaoleiWdBtn;
	private Button sxzWdBtn;
	private Button zhutiWdBtn;
	private Button boduanWdBtn;

	private LinearLayout mLayout;
	private static final int wdids[] = new int[]{
		R.id.dalei_wd,
		R.id.xiaolei_wd,
		R.id.sxz_wd,
		R.id.zhuti_wd,
		R.id.boduan_wd
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.unordered_list, null);
		mRootView.addView(mLayout, FF);
		
//		for(int i=0; i<wdids.length; i++) {
//			Button btn = (Button) findViewById(wdids[i]);
//			btn.setOnClickListener(this);
//		}
		
		daleiWdBtn = (Button) findViewById(R.id.dalei_wd);
		xiaoleiWdBtn = (Button) findViewById(R.id.xiaolei_wd);
		sxzWdBtn = (Button) findViewById(R.id.sxz_wd);
		zhutiWdBtn = (Button) findViewById(R.id.zhuti_wd);
		boduanWdBtn = (Button) findViewById(R.id.boduan_wd);
		
		daleiWdBtn.setOnClickListener(this);
		xiaoleiWdBtn.setOnClickListener(this);
		sxzWdBtn.setOnClickListener(this);
		zhutiWdBtn.setOnClickListener(this);
		boduanWdBtn.setOnClickListener(this);
		
		setTextForTitle("未定综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			break;
			
		case R.id.dalei_wd:
			Intent daleiwdIntent = new Intent(UnOrderedListActivity.this, DaleiWdFenxi.class);
			startActivity(daleiwdIntent);
			break;
			
		case R.id.xiaolei_wd:
			Intent xiaoleiwdIntent = new Intent(UnOrderedListActivity.this, XiaoleiWdFenxi.class);
			startActivity(xiaoleiwdIntent);
			break;
			
		case R.id.boduan_wd:
			Intent boduanIntent = new Intent(UnOrderedListActivity.this, BoduanWdFenxi.class);
			startActivity(boduanIntent);
			break;
			
		case R.id.sxz_wd:
			Intent sxzIntent = new Intent(UnOrderedListActivity.this, SxzWdFenxi.class);
			startActivity(sxzIntent);
			break;
			
		case R.id.zhuti_wd:
			Intent zhutiIntent = new Intent(UnOrderedListActivity.this, ZhutiWdFenxi.class);
			startActivity(zhutiIntent);
			break;
		}
	}

}
