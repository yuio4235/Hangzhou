package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.MustOrderDAO;
import com.as.ui.utils.ListViewUtils;

public class ThemeOrderDetail extends AbstractActivity {

	private static final String TAG = "ThemeOrderDetail";
	
	private LinearLayout mLayout;
	private Button prevPage;
	private Button nextPage;
	private ListView mList;
	
	private int totalPage = 0;
	private int pageNum = 0;
	private int pageSize = 15;
	
	private List<MustOrderDAO> mData = new ArrayList<MustOrderDAO>();
	private BaseAdapter mAdapter;
	
	private View listHeader;
	private View listFooter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.theme_order_detail, null);
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		prevPage = (Button) findViewById(R.id.prev_page);
		nextPage = (Button) findViewById(R.id.next_page);
		prevPage.setOnClickListener(this);
		nextPage.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		
		setTextForLeftTitleBtn("返回");
		setTextForTitle("主题明细");
		
		initData();
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generateMustOrderItem(mData.get(pageNum*pageSize+position), ThemeOrderDetail.this);
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
				if(mData.size() < pageSize) {
					return mData.size();
				} else if((pageNum+1)*pageSize > mData.size()) {
					return mData.size()%pageSize;
				}
				return pageSize;
			}
		};
		
		if(listHeader != null) {
			mList.removeHeaderView(listHeader);
		}
		
		if(listFooter != null) {
			mList.removeFooterView(listFooter);
		}
		
		listHeader = ListViewUtils.generateListViewHeader(new String[]{
				"序号", "编号", "上柜日期", "波段", "品类", "主题", "货号", "价格", "订量"
		}, ThemeOrderDetail.this);
		
		mList.addHeaderView(listHeader);
		mList.setAdapter(mAdapter);
		
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				Intent intent = new Intent(ThemeOrderDetail.this, OrderByStyleActivity.class);
				MustOrderDAO dao = mData.get(pageNum*pageSize + (position-1));
				intent.putExtra("style_code", dao.getSpecNo()+"");
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		String style = intent.getStringExtra("style");
		getData(style);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.prev_page:
			if(pageNum == 0) {
				return;
			} else {
				pageNum --;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(pageNum >= totalPage -1) {
				return;
			} else {
				pageNum ++;
				mAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	private void getData(String style) {
		String SQL = " select a.specification, a.date3, a.state, a.paraconnent, a.waretypeid, a.waretypename, a.style, a.specification, a.retailprice, b.wareNum, a.pagenum"
			+ " from "
			+ " ( select sawarecode.[warecode], sawarecode.[specification], sawarecode.[date3], sawarecode.[state], sapara.[paraconnent], sawarecode.[waretypeid], sawaretype.[waretypename], sawarecode.[style], sawarecode.[specification], sawarecode.[retailprice], sawarecode.pagenum "
			+ " from sawarecode "
			+ " left join sapara on sawarecode.[state] = sapara.[para] "
			+ " left join sawaretype on sawarecode.[waretypeid] = sawaretype.[waretypeid] "
			+ " where sawarecode.style = '"+style+"' and  sapara.[paratype] = 'PD' ) a "
			+ " left join "
			+ " (select warecode,  sum(wareNum) wareNum from saindent group by warecode) b "
			+ " on a.warecode = b.warecode ";
		Log.e(TAG, "sql: " + SQL);
		mData.clear();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(ThemeOrderDetail.this);
		if(db != null) {
			Cursor cursor = db.rawQuery(SQL, null);
			if(cursor != null) {
				totalPage = (cursor.getCount()%pageSize ==0) ? cursor.getCount()/pageSize : (cursor.getCount()/pageSize + 1);
				try {
					if(cursor.moveToFirst()) {
						int serial = 0;
						while(!cursor.isAfterLast()) {
							MustOrderDAO dao = new MustOrderDAO();
							dao.setSerialNo(++serial);
							dao.setSpecNo(cursor.getString(0));
							dao.setDate3(cursor.getString(1));
							dao.setWave(cursor.getString(3));
							dao.setWareType(cursor.getString(5));
							dao.setTheme(cursor.getString(6));
							dao.setHuohao(cursor.getString(10));
							dao.setRetailPrice(cursor.getString(8));
							dao.setWareNum(cursor.getInt(9));
							mData.add(dao);
							cursor.moveToNext();
						}
					}
				} finally {
					cursor.close();
					db.close();
				}
			}
		}
	}
}
