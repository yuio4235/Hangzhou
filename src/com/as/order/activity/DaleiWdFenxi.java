package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.order.dao.DaleiWdDAO;
import com.as.order.pager.PageDao;
import com.as.order.pager.PageDaoImplAll;
import com.as.ui.utils.ListViewUtils;

public class DaleiWdFenxi extends AbstractActivity {

	private static final String TAG = "DaleiWdFenxi";
	
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<DaleiWdDAO> mDataSet;
	private List<DaleiWdDAO> mCurrentDataSet;
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
				"����",
				"δ����",
				"�Ѷ���",
				"�ܿ���"
		}, DaleiWdFenxi.this));
		
		setTextForLeftTitleBtn("����");
		setTextForTitle("����δ������");
		setTextForTitleRightBtn("��ѯ");
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				DaleiWdDAO dao = mCurrentDataSet.get(position);
				return ListViewUtils.generateRow(new String[]{
						dao.getDalei(),
						dao.getWd()+"",
						dao.getYd()+"",
						dao.getTotal()+""
				}, DaleiWdFenxi.this);
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
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			
		case R.id.title_btn_right:
			break;
			
		case R.id.prev_page:
			pager.prevPage();
			mCurrentDataSet = (List<DaleiWdDAO>)pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			pager.nextPage();
			mCurrentDataSet = (List<DaleiWdDAO>) pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			default:
				break;
		}
	}

	private void getData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<DaleiWdDAO>();
		}
		String sql = " select "
			+ "   (select waretypename from sawaretype where rtrim(sawaretype.waretypeid) = rtrim(sawarecode.waretypeid)) dalei, "
			+ "         count(distinct b.warecode) ware_order, "
			+ "         count(distinct c.warecode) ware_unorder, "
			+ "         count(distinct sawarecode.warecode) ware_all "
			+ " from sawarecode  "
			+ "  left join saindent b on sawarecode.warecode = b.warecode and b.warenum > 0 "
			+ " left join saindent c on sawarecode.warecode = c.warecode and c.warenum = 0 "
			+ (TextUtils.isEmpty(where) ? "" : " and " + where)
			+ " group by sawarecode.waretypeid  ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DaleiWdFenxi.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					DaleiWdDAO dao = new DaleiWdDAO();
					dao.setDalei(cursor.getString(0));
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
			mCurrentDataSet = (List<DaleiWdDAO>)pager.getCurrentList();
		}
	}
}
