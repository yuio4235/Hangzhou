package com.as.order.adapter;

import android.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.db.provider.AsContent.SaIndent;

public class SaIndentListItem extends LinearLayout {

	private static final LinearLayout.LayoutParams cellLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
	private static final LinearLayout.LayoutParams dLp = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
	
	private Context mContext;
	private String[] mForm;
	private SaIndent mData;
	
	public SaIndentListItem(Context context, SaIndent data, String[] form) {
		super(context);
		this.mContext = context;
		this.mData = data;
		this.mForm = form;
		
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		this.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.list_selector_background));
		this.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(com.as.order.R.layout.table_item_divider, null);
		this.addView(fv, dLp);
		
		for(int i=0; i<form.length-2; i++) {
			TextView tv = new TextView(mContext);
			tv.setTextSize(25);
		}
	}

}
