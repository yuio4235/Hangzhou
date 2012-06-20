package com.as.order.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.as.db.provider.AsContent.SaIndent;

public class SaIndentListAdapter extends BaseAdapter {

	private Context mContext;
	private String[] mHeader;
	private List<SaIndent> mDataSet;
	
	public SaIndentListAdapter(Context context, String[] header, List<SaIndent> dataset) {
		this.mContext = context;
		this.mHeader = header;
		this.mDataSet = dataset;
	}
	
	@Override
	public int getCount() {
		return mDataSet.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
