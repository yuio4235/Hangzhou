package com.as.order.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.order.R;

public abstract class AbstractActivity extends Activity implements OnClickListener{

	protected LayoutParams FF = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
	protected LayoutParams FW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	protected LayoutInflater layoutInflater;
	protected LinearLayout mmTitle;
	protected LinearLayout mRootView;
	protected Button titleLeftBtn;
	protected Button titleRightBtn;
	protected TextView titleTextView;
	protected ImageView titleNetwrokIv;
	protected ImageView titleActivityLogoIv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mm_activity);
		
		layoutInflater = LayoutInflater.from(this);
		
		mRootView = (LinearLayout) findViewById(R.id.mm_root_view);
		
		mmTitle = (LinearLayout) layoutInflater.inflate(com.as.order.R.layout.mm_title, null);
		mRootView.addView(mmTitle, FW);
		
		init();
	}
	
	protected void init() {
		titleLeftBtn = (Button) findViewById(R.id.title_btn_left);
		titleRightBtn = (Button) findViewById(R.id.title_btn_right);
		titleNetwrokIv = (ImageView) findViewById(R.id.title_network_sts);
		titleActivityLogoIv = (ImageView) findViewById(R.id.title_logo);
		titleTextView = (TextView) findViewById(R.id.title);
		
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
	}
	
	protected void setTextForLeftTitleBtn(String text) {
		titleLeftBtn.setText(text);
		titleLeftBtn.setVisibility(Button.VISIBLE);
	}
	
	protected void setTextForTitleRightBtn(String text) {
		titleRightBtn.setText(text);
		titleRightBtn.setVisibility(Button.VISIBLE);
	}
	
	protected void setTextForTitle(String title) {
		titleTextView.setText(title);
	}
}
