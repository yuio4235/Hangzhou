package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.MyOrderDAO;

public class MyOrderActivity extends AbstractActivity {

	private LinearLayout myOrder;
	private GridView mGridView;
	
	private List<MyOrderDAO> mDataSet = new ArrayList<MyOrderDAO>();
	BaseAdapter mAdapter;
	
	int pageNum = 0;
	int totalPage = 0;
	
	private Button prevPageBtn;
	private Button nextPageBtn;
	
	private Button orderListBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myOrder = (LinearLayout) layoutInflater.inflate(R.layout.my_order, null);
		mRootView.addView(myOrder, FF);
		
		prevPageBtn = (Button) findViewById(R.id.prev_page);
		nextPageBtn = (Button) findViewById(R.id.next_page);
		
		orderListBtn = (Button) findViewById(R.id.my_order_order_list_btn);
		orderListBtn.setOnClickListener(this);
		
		prevPageBtn.setOnClickListener(this);
		nextPageBtn.setOnClickListener(this);
		
		setTextForLeftTitleBtn(getString(R.string.title_back));
		setTextForTitle("我的订单");
		
		mGridView = (GridView) findViewById(R.id.my_order_grid_view);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_my_order));
	}
	
	private void initData() {
		String sql = " select saindent.[warecode], sum(saindent.[warenum]) wareNum, sawarecode.[specification] "
				+ " from saindent, sawarecode "
				+ " where saindent.[warecode] = sawarecode.[warecode] and saindent.[departcode] = ? "
				+ " group by saindent.[warecode] ";
		SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(this);
		Cursor cursor = db.rawQuery(sql, new String[]{sp.getString("user_account", "")});
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				totalPage = (cursor.getCount()%10 == 0)? cursor.getCount()/10 : cursor.getCount()/10 + 1;
				while(!cursor.isAfterLast()) {
					MyOrderDAO dao = new MyOrderDAO();
					dao.setSpecNo(cursor.getString(2));
					dao.setWarecode(cursor.getString(0));
					dao.setWareNum(cursor.getInt(1));
					mDataSet.add(dao);
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = layoutInflater.inflate(R.layout.my_order_item, null);
				TextView sepcNoTv = (TextView) v.findViewById(R.id.my_order_item_specno);
				TextView wareNumTv = (TextView) v.findViewById(R.id.my_order_item_warenum);
				MyOrderDAO dao = mDataSet.get(10*pageNum + position);
				sepcNoTv.setText("编号:"+dao.getSpecNo());
				wareNumTv.setText("数量:" + dao.getWareNum());
				return v;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return position;
			}
			
			@Override
			public int getCount() {
				int count = 0;
				if(pageNum == 0) {
					count = mDataSet.size() < 10 ? mDataSet.size() : 10;
				}
				
				if(pageNum > 0) {
					if((pageNum + 1)*10 > mDataSet.size()) {
						
						count = mDataSet.size()%10;
					} else {
						count = 10;
					}
				}
				return count;
			}
		};
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				MyOrderDAO dao = mDataSet.get(10*pageNum + position);
				Intent intent = new Intent(MyOrderActivity.this, OrderByStyleActivity.class);
				intent.putExtra("style_code", dao.getSpecNo()+"");
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.prev_page:
			if(pageNum <= 0) {
				return;
			} else {
				pageNum --;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(pageNum>=totalPage-1) {
				return;
			} else {
				pageNum ++;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.my_order_order_list_btn:
			
			break;
			
			default:
				break;
		}
	}

}
