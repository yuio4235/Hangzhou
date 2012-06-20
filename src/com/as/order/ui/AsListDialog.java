package com.as.order.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.order.R;

public class AsListDialog extends Dialog {

	Context context;
	ListDialogListener mListener;
	
	Button cancelBtn;
	ListView data;
	String[] mDataSet;
	
	public AsListDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AsListDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	public AsListDialog(Context context, int theme, ListDialogListener listener) {
		super(context, theme);
		this.context = context;
		this.mListener = listener;
	}
	
	public AsListDialog(Context context, int theme, ListDialogListener listener, String[] dataSet) {
		super(context, theme);
		this.context = context;
		this.mListener = listener;
		this.mDataSet = dataSet;
	}
	
	public AsListDialog(Context context, int theme, String[] dataSet) {
		super(context, theme);
		this.context = context;
		this.mDataSet = dataSet;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.listdialog);
		cancelBtn = (Button) findViewById(R.id.dialog_button_cancel);
		data = (ListView) findViewById(R.id.dialog_content_list);
		
		data.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv = new TextView(context);
				tv.setGravity(Gravity.CENTER);
				tv.setText(mDataSet[position]);
				return tv;
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
		
		data.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				mListener.onClick(mDataSet[position]);
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onCancel();
			}
		});
	}
	
	public void setDialogListener(ListDialogListener listener) {
		mListener = listener;
	}
}
