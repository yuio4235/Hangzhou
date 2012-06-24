package com.as.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.order.R;

public class OrderAnalysisActivity extends AbstractActivity {

	private LinearLayout orderAnalysis;
	
	private Button dinghuozonghefenxiBtn;
	private Button unOrderBtn;
	private Button zhibiaoBtn;
	private Button paimingBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orderAnalysis = (LinearLayout) layoutInflater.inflate(R.layout.order_analysis, null);
		mRootView.addView(orderAnalysis, FF);
		
		dinghuozonghefenxiBtn = (Button) findViewById(R.id.dinghuozonghefenxi);
		unOrderBtn = (Button) findViewById(R.id.weidingkuanfenxi);
		zhibiaoBtn = (Button) findViewById(R.id.zhibioafenxi);
		paimingBtn = (Button) findViewById(R.id.paimingfenxi);
		
		dinghuozonghefenxiBtn.setOnClickListener(this);
		unOrderBtn.setOnClickListener(this);
		zhibiaoBtn.setOnClickListener(this);
		paimingBtn.setOnClickListener(this);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_order_analysis));
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.dinghuozonghefenxi:
			Intent dinghuozongheIntent = new Intent(OrderAnalysisActivity.this, DinghuoZongheFenxiActivity.class);
			startActivity(dinghuozongheIntent);
			break;
			
		case R.id.weidingkuanfenxi:
			Intent weidingIntent = new Intent(OrderAnalysisActivity.this, UnOrderedListActivity.class);
			startActivity(weidingIntent);
			break;
			
		case R.id.paimingfenxi:
			Intent paimingIntent = new Intent(OrderAnalysisActivity.this, PaimingFenxiAcitivity.class);
			startActivity(paimingIntent);
			break;
			
		case R.id.zhibioafenxi:
			Intent zhibiaoIntent = new Intent(OrderAnalysisActivity.this, ZhibiaoFenxiActivity.class);
			startActivity(zhibiaoIntent);
			break;
			
			default:
				break;
		}
	}

}
