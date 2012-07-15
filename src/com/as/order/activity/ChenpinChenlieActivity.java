package com.as.order.activity;

import java.util.Collections;
import java.util.Currency;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.order.R;
import com.as.order.sync.FileUploader;
import com.as.ui.utils.FileUtils;

public class ChenpinChenlieActivity extends AbstractActivity {
	
	private LinearLayout mLayout;
	private Button prevPage;
	private Button nextPage;
	private TextView pageInd;
	private EditText pageIndexEt;
	private Button goPage;
	
	private Bitmap[] imgs;
	private ImageView contentImgView;
	
	private int currIndex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.cpcl, null);
		
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		prevPage = (Button) findViewById(R.id.prev_page);
		nextPage = (Button) findViewById(R.id.next_page);
		pageInd = (TextView) findViewById(R.id.page_indicator);
		pageIndexEt = (EditText) findViewById(R.id.pageIndex);
		pageIndexEt.setSelectAllOnFocus(true);
		pageIndexEt.setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		goPage = (Button) findViewById(R.id.goPage);
		contentImgView = (ImageView) findViewById(R.id.cpclImg);
		
		prevPage.setText("上一张");
		nextPage.setText("下一张");
		
		prevPage.setOnClickListener(this);
		nextPage.setOnClickListener(this);
		goPage.setOnClickListener(this);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle("产品陈列");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		imgs = FileUtils.getBitmapsFileCode(ChenpinChenlieActivity.this, "CPCL");
		
		if(imgs != null && imgs.length > 0) {
			pageInd.setText(1+"/"+imgs.length);
			contentImgView.setImageBitmap(imgs[0]);
			pageIndexEt.setText(1+"");
		} 
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.prev_page:
			if(currIndex <=1) {
				return;
			}
			currIndex--;
			pageInd.setText(currIndex+"/"+imgs.length);
			pageIndexEt.setText(currIndex+"");
			updateImg(currIndex);
			break;
			
		case R.id.next_page:
			if(currIndex >= imgs.length) {
				return;
			}
			currIndex ++;
			pageInd.setText(currIndex+"/"+imgs.length);
			pageIndexEt.setText(currIndex+"");
			updateImg(currIndex);
			break;
			
		case R.id.goPage:
			try {
				updateImg(Integer.parseInt(pageIndexEt.getText().toString().trim()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			break;
			
			default:
				break;
		}
	}

	private void updateImg(int imgIndex) {
		if(imgs !=null && imgs.length>=imgIndex) {
			contentImgView.setImageBitmap(imgs[imgIndex-1]);
		}
	}
}
