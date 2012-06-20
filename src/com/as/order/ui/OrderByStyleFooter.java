package com.as.order.ui;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.db.provider.AsContent.SaIndent;
import com.as.order.R;
import com.as.ui.utils.ListViewUtils;

public class OrderByStyleFooter extends LinearLayout {

	private int mSize;
	public OrderByStyleFooter(Context context, int size, List<SaIndent> allIndents) {
		super(context);
		this.mSize = size;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_selector_background));
		this.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		this.addView(fv, ListViewUtils.dLp);
		//first textview
		TextView firstTv = new TextView(context);
		firstTv.setTextSize(25);
		firstTv.setText("ºÏ¼Æ");
		firstTv.setTextColor(context.getResources().getColor(R.color.ConversationVoiceTextColor));
		firstTv.setGravity(Gravity.CENTER);
		View v1 = layoutInflater.inflate(R.layout.table_item_divider, null);
		this.addView(firstTv, ListViewUtils.cellLp);
		this.addView(v1, ListViewUtils.dLp);
		
		int allTotal = 0;
		for(int i=1; i<mSize-1; i++) {
			int currentTotal = 0;
			for(SaIndent si : allIndents) {
				try {
					Field field = si.getClass().getDeclaredField("s" + (i<10?"0"+i:i));
					currentTotal += field.getInt(si);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			allTotal += currentTotal;
			TextView tv = new TextView(context);
			tv.setTextSize(25);
			tv.setText(currentTotal+"");
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER);
			
			View v = layoutInflater.inflate(R.layout.table_item_divider, null);
			this.addView(tv, ListViewUtils.cellLp);
			this.addView(v, ListViewUtils.dLp);
		}
		
		TextView allTv = new TextView(context);
		allTv.setTextSize(25);
		allTv.setText(allTotal + "");
		allTv.setTextColor(Color.BLACK);
		allTv.setGravity(Gravity.CENTER);
		View allV = layoutInflater.inflate(R.layout.table_item_divider, null);
		this.addView(allTv, ListViewUtils.cellLp);
		this.addView(allV, ListViewUtils.dLp);
	}
	
	public void setTextForItem(int index, String text) {
		TextView tv = (TextView)this.getChildAt(index);
		tv.setText(text);
	}

}
