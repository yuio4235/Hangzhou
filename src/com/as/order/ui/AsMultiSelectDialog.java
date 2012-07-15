package com.as.order.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.as.order.R;

public class AsMultiSelectDialog extends Dialog {

	Context context;
	MultiSelectListDialogListener mListener;
	
	Button cancelButton;
	Button okButton;
	ListView mList;
	String[] mDataSet;
	String[] selectedData;
	public AsMultiSelectDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AsMultiSelectDialog(Context context, int theme, MultiSelectListDialogListener listener) {
		super(context, theme);
		this.context = context;
		this.mListener = listener;
	}
	
	public AsMultiSelectDialog(Context context, int theme, MultiSelectListDialogListener listener, String[] dataSet) {
		super(context, theme);
		this.context = context;
		this.mListener = listener;
		this.mDataSet = dataSet;
	}
	
	public AsMultiSelectDialog(Context context, int theme, String[] dataSet) {
		super(context, theme);
		this.context = context;
		this.mDataSet = dataSet;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.multi_listdialog);
		final LayoutInflater mLayoutInflater = LayoutInflater.from(context);
		cancelButton = (Button) findViewById(R.id.dialog_button_cancel);
		okButton = (Button) findViewById(R.id.dialog_button_ok);
		mList = (ListView) findViewById(R.id.dialog_content_list);
		mList.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout mItemLayout = (LinearLayout)mLayoutInflater.inflate(R.layout.multi_select_item, null);
				TextView tv = (TextView) mItemLayout.findViewById(R.id.multi_select_item_tv);
				tv.setText(mDataSet[position]);
//				CheckBox checkBox = (CheckBox) mItemLayout.findViewById(R.id.multi_select_item_checkbox);
				
				return mItemLayout;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				return mDataSet.length;
			}
		});
		
		okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<String> selectedList = new ArrayList<String>();
				int childCountOfMList = mList.getChildCount();
				for(int i=0; i<childCountOfMList; i++) {
					LinearLayout mItemLayout = (LinearLayout)mList.getChildAt(i);
					CheckBox checkBox = (CheckBox)mItemLayout.findViewById(R.id.multi_select_item_checkbox);
					if(checkBox.isChecked()) {
						TextView tv = (TextView) mItemLayout.findViewById(R.id.multi_select_item_tv);
						selectedList.add(tv.getText().toString().trim());
					}
				}
				selectedData = new String[selectedList.size()];
				selectedList.toArray(selectedData);
				mListener.onOk();
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onCancel();
			}
		});
	}
	
	public void setDialogListener(MultiSelectListDialogListener listener) {
		mListener = listener;
	}
	
	public String[] getSelectedData() {
		return selectedData;
	}
}
