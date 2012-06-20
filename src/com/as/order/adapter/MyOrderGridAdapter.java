package com.as.order.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.as.order.R;
import com.as.order.dao.MyOrderDAO;

public class MyOrderGridAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater layoutInflater;
	private List<MyOrderDAO> mDataSet;
	
	public MyOrderGridAdapter(Context context, List<MyOrderDAO> data) {
		this.mContext = context;
		layoutInflater = LayoutInflater.from(mContext);
		this.mDataSet = data;
	}
	
	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = layoutInflater.inflate(R.layout.my_order_item, null);
		TextView specNoTv = (TextView) v.findViewById(R.id.my_order_item_specno);
		TextView wareNumTv = (TextView) v.findViewById(R.id.my_order_item_warenum);
		MyOrderDAO dao = mDataSet.get(position);
		specNoTv.setText(dao.getSpecNo());
		wareNumTv.setText(dao.getWareNum());
		
		return v;
	}

	public void setData(List<MyOrderDAO> data) {
		this.mDataSet = data;
	}
}
