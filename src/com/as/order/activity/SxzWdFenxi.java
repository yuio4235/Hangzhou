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
import com.as.order.dao.BoduanWdDAO;
import com.as.order.dao.DaleiWdDAO;
import com.as.order.dao.SxzWdDAO;
import com.as.order.pager.PageDao;
import com.as.order.pager.PageDaoImplAll;
import com.as.ui.utils.ListViewUtils;

public class SxzWdFenxi extends AbstractActivity {

	private static final String TAG = "SxzWdFenxi";
	
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<SxzWdDAO> mDataSet;
	private List<SxzWdDAO> mCurrentDataSet;
	private Button prevBtn;
	private Button nextBtn;
	PageDao pager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.dalei_wd_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"上下装",
				"未订款",
				"已订款",
				"总款数"
		}, SxzWdFenxi.this));
		
		setTextForLeftTitleBtn("返回");
		setTextForTitle("上下装未定分析");
		setTextForTitleRightBtn("查询");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	private void initData() {
		getData("");
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				SxzWdDAO dao = mCurrentDataSet.get(position);
				return ListViewUtils.generateRow(new String[]{
						dao.getSxz(),
						dao.getWd()+"",
						dao.getYd()+"",
						dao.getTotal()+""
				}, SxzWdFenxi.this);
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
				return mCurrentDataSet.size();
			}
		};
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				SxzWdDAO dao = mCurrentDataSet.get(position-1);
//				Log.e(TAG, "current position: " + (pager.getPerPage()*(pager.getCurrentPage()-1) + position) + ", dalei: " + dao.getDalei() + " waretypeid: " + dao.getWaretypeid());
				Intent intent = new Intent(SxzWdFenxi.this, WdDetailActivity.class);
				intent.putExtra("where", " sxz = '" + dao.getSxz() + "'");
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
			
		case R.id.title_btn_right:
			break;
			
		case R.id.prev_page:
			pager.prevPage();
			mCurrentDataSet = (List<SxzWdDAO>)pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			pager.nextPage();
			mCurrentDataSet = (List<SxzWdDAO>) pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
			default:
				break;
		}
	}

	private void getData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<SxzWdDAO>();
		}
		String sql = " select "
			+ "      sawarecode.[sxz] sxz,      "
			+ "       count(distinct b.warecode) ware_order, "
			+ "       count(distinct c.warecode) ware_unorder, "
			+ "       count(distinct sawarecode.[warecode]) ware_all "
			+ " from sawarecode "
			+ " left join saindent b on sawarecode.[warecode] = b.warecode and b.warenum > 0 "
			+ " left join saindent c on sawarecode.[warecode] = c.warecode and c.warenum = 0 "
			+ (TextUtils.isEmpty(where) ? "" : " where " + where)
			+ " group by sawarecode.[sxz] ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(SxzWdFenxi.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				while(!cursor.isAfterLast()) {
					SxzWdDAO dao = new SxzWdDAO();
					dao.setSxz(cursor.getString(0));
					dao.setYd(cursor.getInt(1));
					dao.setWd(cursor.getInt(2));
					dao.setTotal(cursor.getInt(3));
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
			pager = new PageDaoImplAll(mDataSet, 15, mDataSet.size());
			mCurrentDataSet = (List<SxzWdDAO>)pager.getCurrentList();
		}
	}
}
