package com.as.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.order.R;

public class UnOrderedListActivity extends AbstractActivity {

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
		
		for(int i=0; i<wdids.length; i++) {
			Button btn = (Button) findViewById(wdids[i]);
			btn.setOnClickListener(this);
		}
		
		setTextForTitle("大类未定分析");
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
		}
	}

}
