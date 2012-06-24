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
import com.as.order.dao.WdDetailDAO;
import com.as.order.pager.PageDao;
import com.as.order.pager.PageDaoImplAll;
import com.as.ui.utils.ListViewUtils;

public class WdDetailActivity extends AbstractActivity {

	private LinearLayout mLayout;
	private ListView mList;
	private List<WdDetailDAO> mDataSet;
	private List<WdDetailDAO> mCurrentDataSet;
	private PageDao pager;
	private BaseAdapter mAdapter;
	private Button prevBtn;
	private Button nextBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.wd_detail, null);
		mRootView.addView(mLayout, FF);
		mList = (ListView) findViewById(R.id.as_list);
		
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"Ãû³Æ",
				"±àºÅ",
				"¿îºÅ"
		}, WdDetailActivity.this));
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		setTextForTitle("Î´¶©Ã÷Ï¸");
		setTextForLeftTitleBtn("·µ»Ø");
	}
	
	private void initData(String where) {
		getData(where);
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				WdDetailDAO dao = mCurrentDataSet.get(position);
				return ListViewUtils.generateRow(new String[]{
						dao.getWarename(),
						dao.getPageNum(),
						dao.getSpecification()
				}, WdDetailActivity.this);
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
				WdDetailDAO dao = mCurrentDataSet.get(position-1);
				Intent intent = new Intent(WdDetailActivity.this, OrderByStyleActivity.class);
				intent.putExtra("style_code", dao.getSpecification());
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String where = intent.getStringExtra("where");
		initData(where);
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
			default:
				break;
		}
	}

	private void getData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<WdDetailDAO>();
		}
		String sql = " select warename, pagenum, specification from sawarecode where 1=1 "
			+ (TextUtils.isEmpty(where) ? "" : " and " + where);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(WdDetailActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try  {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				while(!cursor.isAfterLast()) {
					WdDetailDAO dao = new WdDetailDAO();
					dao.setWarename(cursor.getString(0));
					dao.setPageNum(cursor.getString(1));
					dao.setSpecification(cursor.getString(2));
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
			mCurrentDataSet = (List<WdDetailDAO>) pager.getCurrentList();
		}
	}
}
