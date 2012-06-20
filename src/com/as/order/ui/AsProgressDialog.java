package com.as.order.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.as.order.R;

public class AsProgressDialog extends Dialog{

	Context context;
	
	private String mTitle;
	
	ProgressBar mPorgressBar;
	TextView mProgressText;
	TextView mTextView;
	
	public AsProgressDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public AsProgressDialog(Context context, int theme, String title) {
		super(context, theme);
		this.context = context;
		this.mTitle = title;
	}
	
	public void setTitle(String text) {
		mTextView.setText(text);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.progressdialog);
		mPorgressBar = (ProgressBar) findViewById(R.id.dialog_progress_bar);
		mProgressText = (TextView) findViewById(R.id.progress_dialog_text);
		mTextView = (TextView) findViewById(R.id.dialog_title);
		mTextView.setText(mTitle);
	}

	public void setMax(int value) {
		mPorgressBar.setMax(value);
	}
	
	public void updatePorgressText(String text) {
		mProgressText.setText(text);
	}

	public void updateProgress(int value) {
		mPorgressBar.setProgress(value);
	}
	
}
