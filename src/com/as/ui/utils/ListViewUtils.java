package com.as.ui.utils;


import java.lang.reflect.Field;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.db.provider.AsContent.SaIndent;
import com.as.db.provider.AsContent.SaIndentColumns;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.order.R;
import com.as.order.dao.DapeiOrderDAO;
import com.as.order.dao.MustOrderDAO;

public class ListViewUtils {
	
	private static final String TAG = "ListViewUtils";
	
	public static final LinearLayout.LayoutParams cellLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
	public static final LinearLayout.LayoutParams dLp = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);

	public static LinearLayout generateListViewHeader(String[] header, Context ctx) {
		LinearLayout mLayout = new LinearLayout(ctx);
		LayoutInflater layoutInflater = LayoutInflater.from(ctx);
		
		mLayout.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.table_header_normal));
		for(int i=0; i<header.length-1; i++) {
			TextView tv = new TextView(ctx);
			tv.setTextSize(25);
			tv.setText(header[i]);
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER);
			tv.setEllipsize(TruncateAt.MARQUEE);
			tv.setMarqueeRepeatLimit(-1);
			
			View v = layoutInflater.inflate(R.layout.table_item_divider, null);
			
			mLayout.addView(tv, cellLp);
			mLayout.addView(v, dLp);
		}
		if(header.length >=1) {
			TextView tv = new TextView(ctx);			
			tv.setTextSize(25);
			tv.setText(header[header.length-1]);
			tv.setGravity(Gravity.CENTER);

			tv.setTextColor(Color.BLACK);
			mLayout.addView(tv, cellLp);
		}
		return mLayout;
	}
	
	public static LinearLayout generateRow(String[] row, Context ctx) {
		LayoutInflater layoutInflater = LayoutInflater.from(ctx);
		LinearLayout mLayout = new LinearLayout(ctx);
		mLayout.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.list_selector_background));
		mLayout.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(fv, dLp);
		for(int i=0; i<row.length -1; i++) {
			TextView tv = new TextView(ctx);
			tv.setTextSize(25);
			tv.setText(row[i]);
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER);
			
			View v = layoutInflater.inflate(R.layout.table_item_divider, null);
			
			mLayout.addView(tv, cellLp);
			mLayout.addView(v, dLp);
		}
		
		if(row.length >=1) {
			TextView tv = new TextView(ctx);			
			tv.setTextSize(25);
			tv.setText(row[row.length-1]);
			tv.setGravity(Gravity.CENTER);

			tv.setTextColor(Color.BLACK);
			mLayout.addView(tv, cellLp);			
		}
		View lv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(lv, dLp);
		return mLayout;
	}
	
	public static LinearLayout generateDapeiOrderRow(DapeiOrderDAO dao, Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		LinearLayout mLayout = new LinearLayout(context);
		mLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_selector_background));
		mLayout.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(fv, dLp);
		
		TextView itemCodeTv = new TextView(context);
		itemCodeTv.setTextSize(25);
		itemCodeTv.setText(dao.getItemCode());
		itemCodeTv.setTextColor(Color.BLACK);
		itemCodeTv.setGravity(Gravity.CENTER);
		
		View v = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(itemCodeTv, cellLp);
		mLayout.addView(v, dLp);
		
		TextView groupNameTv = new TextView(context);
		groupNameTv.setTextSize(25);
		groupNameTv.setText(dao.getGroupName());
		groupNameTv.setTextColor(Color.BLACK);
		groupNameTv.setGravity(Gravity.CENTER);
		View gv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(groupNameTv, cellLp);
		mLayout.addView(gv, dLp);
		
		int totalCount =0;
		int i;
		Log.e("==", "====== warecodes size: " + dao.getWareCodes().size());
		for(i=0; i<dao.getWareCodes().size() && i<3; i++) {
			TextView tv = new TextView(context);
			tv.setTextSize(25);
			tv.setText(dao.getWareCodes().get(i).specification);
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER);
			
			View dv = layoutInflater.inflate(R.layout.table_item_divider, null);
			mLayout.addView(tv, cellLp);
			mLayout.addView(dv, dLp);
			
//			Cursor cursor = context.getContentResolver().query(SaIndent.CONTENT_URI, SaIndent.CONTENT_PROJECTION, SaIndentColumns.WARECODE + "=?", new String[]{dao.getWareCodes().get(i).warecode}, null);
//			try {
//				if(cursor != null && cursor.moveToFirst()) {
//					while(!cursor.isAfterLast()) {
//						totalCount += cursor.getInt(SaIndent.CONTENT_WARENUM_COLUMN);
//						cursor.moveToNext();
//					}
//				}
//			} finally {
//				if(cursor != null) {
//					cursor.close();
//				}
//			}
			totalCount += SaWareCode.getWareNum(context, dao.getWareCodes().get(i).warecode);
		}
		
		if(!(i>=3)) {
			for(i=dao.getWareCodes().size();i<3;i++) {
				TextView tv = new TextView(context);
				tv.setTextSize(25);
				tv.setText("");
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
				
				View dv = layoutInflater.inflate(R.layout.table_item_divider, null);
				mLayout.addView(tv, cellLp);
				mLayout.addView(dv, dLp);
			}
		}
		
		TextView totalTv = new TextView(context);
		totalTv.setTextSize(25);
		totalTv.setText(totalCount+"");
		totalTv.setTextColor(Color.BLACK);
		totalTv.setGravity(Gravity.CENTER);
		mLayout.addView(totalTv, cellLp);
		View toV = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(toV, dLp);
		
		return mLayout;
	}
	
	private static TextView makeTextView(String text, Context ctx) {
		TextView tv = new TextView(ctx);
		tv.setTextSize(25);
		tv.setText(text);
		tv.setTextColor(Color.BLACK);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}
	
	private static View makeDivideView(Context ctx) {
		LayoutInflater layoutInflater = LayoutInflater.from(ctx);
		View v = layoutInflater.inflate(R.layout.table_item_divider, null);
		return v;
	}
	
	public static LinearLayout generateMustOrderItem(MustOrderDAO dao, Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		LinearLayout mLayout = new LinearLayout(context);
		mLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_selector_background));
		mLayout.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(fv, dLp);
		
		TextView serialNoTv = makeTextView(dao.getSerialNo()+"", context);
		mLayout.addView(serialNoTv, cellLp);
		View v1 = makeDivideView(context);
		mLayout.addView(v1, dLp);
		
		TextView specNoTv = makeTextView(dao.getSpecNo(), context);
		mLayout.addView(specNoTv, cellLp);
		View v2 = makeDivideView(context);
		mLayout.addView(v2, dLp);
		
		TextView date3Tv = makeTextView(dao.getDate3(), context);
		mLayout.addView(date3Tv, cellLp);
		View v3 = makeDivideView(context);
		mLayout.addView(v3, dLp);
		
		TextView waveTv = makeTextView(dao.getWave(), context);
		mLayout.addView(waveTv, cellLp);
		View v4 = makeDivideView(context);
		mLayout.addView(v4, dLp);
		
		TextView pinleiTv = makeTextView(dao.getWareType(), context);
		mLayout.addView(pinleiTv, cellLp);
		View v5 = makeDivideView(context);
		mLayout.addView(v5, dLp);
		
		TextView themeTv = makeTextView(dao.getTheme(), context);
		mLayout.addView(themeTv, cellLp);
		View v6 = makeDivideView(context);
		mLayout.addView(v6, dLp);
		
		TextView huohaoTv = makeTextView(dao.getHuohao(), context);
		mLayout.addView(huohaoTv, cellLp);
		View v7 = makeDivideView(context);
		mLayout.addView(v7, dLp);
		
		TextView priceTv = makeTextView(dao.getRetailPrice(), context);
		mLayout.addView(priceTv, cellLp);
		View v8 = makeDivideView(context);
		mLayout.addView(v8, dLp);
		
		TextView wareNumTv = makeTextView(dao.getWareNum()+"", context);
		mLayout.addView(wareNumTv, cellLp);
		View v9 = makeDivideView(context);
		mLayout.addView(v9, dLp);
		return mLayout;
	}
	
	public static LinearLayout generateRow(SaIndent saIndent, int sizeCount, Context ctx) {
		LayoutInflater layoutInflater = LayoutInflater.from(ctx);
		LinearLayout mLayout = new LinearLayout(ctx);
		mLayout.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.list_selector_background));
		mLayout.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(fv, dLp);
		//add color name
		TextView colorTv = new TextView(ctx);
		colorTv.setTextSize(25);
		colorTv.setText(saIndent.colorName);
		colorTv.setTextColor(Color.BLACK);
		colorTv.setGravity(Gravity.CENTER);
		
		View v1 = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(colorTv, cellLp);
		mLayout.addView(v1, dLp);
		
		for(int i=1; i<=sizeCount; i++) {
			try {
				TextView tv = new TextView(ctx);
				tv.setTextSize(25);
				Field field = saIndent.getClass().getDeclaredField("s" + (i<10?"0"+i:i));
				tv.setText(field.getInt(saIndent) +"");
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
				
				View v = layoutInflater.inflate(R.layout.table_item_divider, null);
				
				mLayout.addView(tv, cellLp);
				mLayout.addView(v, dLp);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		TextView totalTv = new TextView(ctx);
		totalTv.setTextSize(25);
		totalTv.setText(saIndent.wareNum + "");
		totalTv.setTextColor(Color.BLACK);
		totalTv.setGravity(Gravity.CENTER);
		
		mLayout.addView(totalTv, cellLp);
		View lv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(lv, dLp);
		
		return mLayout;
	}
	
	public static  LinearLayout generateEditText(SaIndent saIndent, int sizeCount, Context ctx) {
		LayoutInflater layoutInflater = LayoutInflater.from(ctx);
		LinearLayout mLayout = new LinearLayout(ctx);
		mLayout.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.list_selector_background));
		mLayout.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(fv, dLp);
		//add color name
		TextView colorTv = new TextView(ctx);
		colorTv.setTextSize(25);
		colorTv.setText(saIndent.colorName);
		colorTv.setTextColor(ctx.getResources().getColor(R.color.ConversationVoiceTextColor));
		colorTv.setGravity(Gravity.CENTER);
		
		View v1 = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(colorTv, cellLp);
		mLayout.addView(v1, dLp);
		
		for(int i=1; i<=sizeCount; i++) {
			try {
				EditText tv = new EditText(ctx);
				tv.setTextSize(25);
				Field field = saIndent.getClass().getDeclaredField("s" + (i<10?"0"+i:i));
				tv.setText(field.getInt(saIndent) +"");
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
				tv.setKeyListener(new DigitsKeyListener(false, false));
				
				View v = layoutInflater.inflate(R.layout.table_item_divider, null);
				
				mLayout.addView(tv, cellLp);
				mLayout.addView(v, dLp);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		TextView totalTv = new TextView(ctx);
		totalTv.setTextSize(25);
		totalTv.setText(saIndent.wareNum + "");
		totalTv.setTextColor(Color.BLACK);
		totalTv.setGravity(Gravity.CENTER);
		
		mLayout.addView(totalTv, cellLp);
		View lv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(lv, dLp);
		
		return mLayout;		
	}
	
	
	public static LinearLayout generateEditTextRow(SaIndent saIndent, int sizeCount, Context ctx) {
		LayoutInflater layoutInflater = LayoutInflater.from(ctx);
		LinearLayout mLayout = new LinearLayout(ctx);
		mLayout.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.list_selector_background));
		mLayout.setPadding(0, 0, 0, 0);
		View fv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(fv, dLp);
		//add color name
		TextView colorTv = new TextView(ctx);
		colorTv.setTextSize(25);
		colorTv.setText(saIndent.colorName);
		colorTv.setTextColor(Color.BLACK);
		colorTv.setGravity(Gravity.CENTER);
		
		View v1 = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(colorTv, cellLp);
		mLayout.addView(v1, dLp);
		
		int totalCount = 0;
		for(int i=1; i<=sizeCount; i++) {
			try {
				EditText tv = new EditText(ctx);
				tv.setTextSize(25);
				Field field = saIndent.getClass().getDeclaredField("s" + ( i<10 ? "0" + i : i));
				tv.setText(field.getInt(saIndent)+"");
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
				tv.setKeyListener(new DigitsKeyListener(false, false));
				
				View v = layoutInflater.inflate(R.layout.table_item_divider, null);
				
				mLayout.addView(tv, cellLp);
				mLayout.addView(v, dLp);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
//		TextView totalTv = new TextView(ctx);
//		totalTv.setTextSize(25);
//		totalTv.setText(totalCount + "");
//		totalTv.setTextColor(Color.BLACK);
//		totalTv.setGravity(Gravity.CENTER);
//		
//		mLayout.addView(totalTv, cellLp);
		View lv = layoutInflater.inflate(R.layout.table_item_divider, null);
		mLayout.addView(lv, dLp);
		
		return mLayout;
	}
	
	public static LinearLayout generateFooter(LayoutInflater layoutInflater) {
		return (LinearLayout) layoutInflater.inflate(R.layout.table_fotter, null);
	}
}
