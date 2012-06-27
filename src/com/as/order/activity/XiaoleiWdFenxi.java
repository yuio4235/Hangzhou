package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.as.order.dao.XiaoleiWdDAO;
import com.as.order.pager.PageDao;
import com.as.order.pager.PageDaoImplAll;
import com.as.ui.utils.ListViewUtils;

public class XiaoleiWdFenxi extends AbstractActivity {

	private static final String TAG = "XiaoleiWdFenxi";
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<XiaoleiWdDAO> mDataSet;
	private List<XiaoleiWdDAO> mCurrentDataSet;
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
				"小类",
				"未订款",
				"已订款",
				"总款数"
		}, XiaoleiWdFenxi.this));
		
		setTextForLeftTitleBtn("返回");
		setTextForTitle("小类未定分析");
		setTextForTitleRightBtn("查询");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
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
			mCurrentDataSet = (List<XiaoleiWdDAO>) pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			pager.nextPage();
			mCurrentDataSet = (List<XiaoleiWdDAO>) pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
			default:
				break;
		}
	}

	private void initData()	{
		getData("");
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				XiaoleiWdDAO dao = mCurrentDataSet.get(position);
				return ListViewUtils.generateRow(new String[]{
						dao.getXiaolei(),
						dao.getWd()+"",
						dao.getYd()+"",
						dao.getTotal()+""
				}, XiaoleiWdFenxi.this);
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
					return ;
				}
				XiaoleiWdDAO dao = mCurrentDataSet.get(position-1);
				Intent intent = new Intent(XiaoleiWdFenxi.this, WdDetailActivity.class);
				intent.putExtra("where", " id = '" + dao.getId() + "'");
				startActivity(intent);
			}
		});
	}
	
	private void getData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<XiaoleiWdDAO>();
		}
		
		String sql = " select "
			+ " sawarecode.id,  "
			+ " (select type1.type1 from type1 where rtrim(type1.id) = rtrim(sawarecode.id)) xiaolei, "
			+ " count(distinct b.warecode) ware_order, "
			+ " count(distinct c.warecode) ware_unorder, "
			+ " count(distinct sawarecode.[warecode]) ware_all "
			+ " from sawarecode     "
			+ " left join saindent b on sawarecode.[warecode] = b.warecode and b.warenum > 0 "
			+ " left join saindent c on sawarecode.[warecode] = c.warecode and c.warenum = 0 "
			+ (TextUtils.isEmpty(where) ? "" : " where " + where)
			+ " group by sawarecode.[id] ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(XiaoleiWdFenxi.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				while(!cursor.isAfterLast()) {
					XiaoleiWdDAO dao = new XiaoleiWdDAO();
					dao.setId(cursor.getString(0));
					dao.setXiaolei(cursor.getString(1));
					dao.setYd(cursor.getInt(2));
					dao.setWd(cursor.getInt(3));
					dao.setTotal(cursor.getInt(4));
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
		pager = new PageDaoImplAll(mDataSet, 15, mDataSet.size());
		mCurrentDataSet = (List<XiaoleiWdDAO>) pager.getCurrentList();
	}
}
