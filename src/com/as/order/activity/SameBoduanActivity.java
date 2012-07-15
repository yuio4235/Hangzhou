package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.as.ui.utils.UserUtils;

public class SameBoduanActivity extends AbstractActivity {
	
	private static final String TAG = "SameBoduanActivity";

	private LinearLayout mLayout;
	private Button prevPage;
	private Button nextPage;
	
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<MustOrderDAO> mData = new ArrayList<MustOrderDAO>();
	
	private int sumPrice = 0;
	private int sumWareNum = 0;
	
	private int pageNum = 0;
	private int totalPage = 0;
	private int pageSize = 10;
	
	private View listFooter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.same_boduan, null);
		mRootView.addView(mLayout, FF);
		
		prevPage = (Button) findViewById(R.id.prev_page);
		nextPage = (Button) findViewById(R.id.next_page);
		
		prevPage.setOnClickListener(this);
		nextPage.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"序号", "编号", "上柜日期", "波段", "品类", "主题", "货号", "价格", "已订量"
		}, SameBoduanActivity.this));
		
		setTextForTitle("同波段明细");
		setTextForLeftTitleBtn("返回");
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generateMustOrderItem(mData.get(pageNum*pageSize+position), SameBoduanActivity.this);
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
				} else if((pageNum +1)*pageSize > mData.size()) {
					return mData.size()%pageSize;
				}
				return pageSize;
			}
		};
		
		if(listFooter != null) {
			mList.removeFooterView(listFooter);
		}
		
		listFooter = ListViewUtils.generateRow(new String[]{
				"合计", "", "", "", "", "", "", "" + sumPrice, "" + sumWareNum
		}, SameBoduanActivity.this);
		
		mList.setAdapter(mAdapter);
		
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				MustOrderDAO dao = mData.get(position-1);
				Intent intent = new Intent(SameBoduanActivity.this, OrderByStyleActivity.class);
				intent.putExtra("style_code", dao.getSpecNo());
				startActivity(intent);
			}
		});
		
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent  = getIntent();
		String boduan = intent.getStringExtra("boduan");
		Log.e(TAG, "==== boduan: " + boduan);
		getData(boduan);
		initData();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.prev_page:
			if(pageNum <=0) {
				return;
			}
			pageNum --;
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			if(pageNum >= totalPage-1) {
				return;
			}
			pageNum ++;
			mAdapter.notifyDataSetChanged();
			break;
			
			default:
				break;
		}
	}
	
	private void getData(String boduan) {
		mData.clear();
		
		String sql = " select a.specification, a.date3, a.state, a.paraconnent, a.waretypeid, a.waretypename, a.style, a.specification, a.retailprice, b.wareNum, a.pagenum "
			+ " from "
			+ " ( select sawarecode.[warecode], sawarecode.[specification], sawarecode.[date3], sawarecode.[state], sapara.[paraconnent], sawarecode.[waretypeid], sawaretype.[waretypename], sawarecode.[style], sawarecode.[specification], sawarecode.[retailprice], sawarecode.pagenum "
			+ " from sawarecode "
			+ " left join sapara on sawarecode.[state] = sapara.[para] "
			+ " left join sawaretype on sawarecode.[waretypeid] = sawaretype.[waretypeid] "
			+ " where sapara.[paratype] = 'PD' and sapara.paraconnent = '"+boduan+"' ) a "
			+ " left join "
			+ " (select warecode,  sum(wareNum) wareNum from saindent where saindent.departcode = '"+UserUtils.getUserAccount(SameBoduanActivity.this)+"' group by warecode) b "
			+ "  on a.warecode = b.warecode ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(SameBoduanActivity.this);
		if( db != null) {
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor != null) {
				try {
					if(cursor.moveToFirst()) {
						totalPage = (cursor.getCount()%pageSize == 0) ? cursor.getCount()/pageSize : (cursor.getCount()/pageSize + 1);
						int serial = 0;
						while(!cursor.isAfterLast()) {
							MustOrderDAO dao = new MustOrderDAO();
							dao.setSerialNo(++serial);
							dao.setSpecNo(TextUtils.isEmpty(cursor.getString(0)) ? "" : cursor.getString(0));
							dao.setDate3(TextUtils.isEmpty(cursor.getString(1)) ? "" : cursor.getString(1));
							dao.setWave(TextUtils.isEmpty(cursor.getString(3)) ? "" : cursor.getString(3));
							dao.setWareType(TextUtils.isEmpty(cursor.getString(5)) ? "" : cursor.getString(5));
							dao.setTheme(TextUtils.isEmpty(cursor.getString(6)) ? "" : cursor.getString(6));
							dao.setHuohao(TextUtils.isEmpty(cursor.getString(10)) ? "" : cursor.getString(10));
							dao.setRetailPrice(TextUtils.isEmpty(cursor.getString(8)) ? "" : cursor.getString(8));
							dao.setWareNum(cursor.getInt(9));
							sumWareNum += cursor.getInt(9);
							sumPrice += cursor.getInt(8)*cursor.getInt(9);
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
