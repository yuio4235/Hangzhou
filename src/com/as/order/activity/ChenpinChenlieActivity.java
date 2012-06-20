package com.as.order.activity;

import com.as.order.R;

import android.os.Bundle;
import android.view.View;

public class ChenpinChenlieActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_chenpin_chenlie));
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
			default:
				break;
		}
	}

}
