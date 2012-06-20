package com.as.order.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.as.db.provider.AsContent.SaIndent;
import com.as.db.provider.AsContent.SaIndentColumns;
import com.as.order.R;

public class AsDialog extends Dialog {

	Context context;
	DialogListener mListener;
	LinearLayout mHeaderContent;
	LinearLayout mItemContent;
	SaIndent mSaIndent;
	
	private Button okBtn;
	private Button cancelBtn;
	
	public AsDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public AsDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	public AsDialog(Context context, int theme, DialogListener listener) {
		super(context, theme);
		this.context = context;
		this.mListener = listener;
	}
	
	public AsDialog(SaIndent saIndent, Context context, int theme, LinearLayout headerLayout, LinearLayout itemLayout) {
		super(context, theme);
		this.mSaIndent = saIndent;
		this.context = context;
		this.mHeaderContent = headerLayout;
		this.mItemContent = itemLayout;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);
		final LinearLayout mConentRoot = (LinearLayout)findViewById(R.id.dialog_content_root);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		if(mContent != null) {
//			mConentRoot.addView(mContent, lp);
//			mConentRoot.setVisibility(LinearLayout.VISIBLE);
//		}
		if(mHeaderContent != null) {
			mConentRoot.addView(mHeaderContent, lp);
			mConentRoot.setVisibility(LinearLayout.VISIBLE);
		}
		if(mItemContent != null) {
			mConentRoot.addView(mItemContent, lp);
			mConentRoot.setVisibility(LinearLayout.VISIBLE);
		}
		okBtn = (Button)findViewById(R.id.dialog_button_ok);
		cancelBtn = (Button)findViewById(R.id.dialog_button_cancel);
		
		okBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LinearLayout myItemContent = mItemContent;
				int childCount = myItemContent.getChildCount();
				int totalEditTextCount = 0;
				List<String> dataList = new ArrayList<String>();
				for(int i=0; i<childCount; i++) {
					if(myItemContent.getChildAt(i) instanceof EditText) {
						totalEditTextCount ++;
						EditText et = (EditText) myItemContent.getChildAt(i);
						dataList.add(et.getText().toString().trim());
					}
				}
				dataList.add(0, "");
				String[] dataArr = new String[dataList.size()];
				dataList.toArray(dataArr);
				
				int wareNum = 0;
				
				for(int m=1;m<dataArr.length; m++) {
					Log.e("============", "dataArr[" + m + "]: " + dataArr[m]);
					try {
						Field field = mSaIndent.getClass().getDeclaredField("s"+(m<10? "0"+m : m));
						field.setInt(mSaIndent, Integer.parseInt(dataArr[m]));
						wareNum += Integer.parseInt(dataArr[m]);
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				try {
					Field wareNumFiled = mSaIndent.getClass().getDeclaredField("wareNum");
					wareNumFiled.setInt(mSaIndent, wareNum);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				if(TextUtils.isEmpty(mSaIndent.indentNo)) {
					mSaIndent.indentNo = System.currentTimeMillis() + "";
					context.getContentResolver().insert(SaIndent.CONTENT_URI, mSaIndent.toContentValues());					
				} else {
					context.getContentResolver().update(SaIndent.CONTENT_URI, mSaIndent.toContentValues(), SaIndentColumns.INDENTNO + " = ?", new String[]{mSaIndent.indentNo});
				}
				
				Log.e("TAG", "Count of EditText: " + totalEditTextCount);
				mListener.onOk();
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onCancel();
			}
		});
	}
	
	public void setDialogListener(DialogListener listener) {
		mListener = listener;
	}

}
