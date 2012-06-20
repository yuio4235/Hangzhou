package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.BoduanFenxiDAO;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.ListViewUtils;

public class BoduanZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "BoduanZongheAnalysisActivity";
	
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<BoduanFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private int currPage = 0;
	private int totalPage = 0;
	
	private int totalWareCnt = 0;
	private int totalWareNum =0;
	private int totalPrice = 0;
	private int totalOrderedWareCnt =0;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.boduan_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"波段",
				"总款数",
				"总款数占比",
				"订货款",
				"占款总比",
				"占已订比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, BoduanZongheAnalysisActivity.this));
		mDataSet = new ArrayList<BoduanFenxiDAO>();
		
		setTextForTitle("波段综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
	}
	
	private void initTotalData() {
		totalWareCnt = AnaUtils.getTotalWareCnt(BoduanZongheAnalysisActivity.this);
		totalWareNum = AnaUtils.getTotalWareNum(BoduanZongheAnalysisActivity.this);
		totalPrice = AnaUtils.getTotalPrice(BoduanZongheAnalysisActivity.this);
		totalOrderedWareCnt = AnaUtils.getTotalOrderedWareCnt(BoduanZongheAnalysisActivity.this);
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				BoduanFenxiDAO dao = mDataSet.get(currPage*15+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getBoduan(),
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/totalWareCnt)*100))+"%",
						dao.getOrderedWareCnt()+"",
						formatter.format((((double)dao.getOrderedWareCnt()/totalWareCnt)*100)) +"%",
						formatter.format((((double)dao.getOrderedWareCnt()/totalOrderedWareCnt)*100))+"%",
						dao.getWarenum()+"",
						formatter.format((((double)dao.getWarenum()/totalWareNum)*100))+"%",
						dao.getOrderedPrice()+"",
						formatter.format((((double)dao.getOrderedPrice()/totalPrice)*100))+"%"
				}, BoduanZongheAnalysisActivity.this);
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
				if(mDataSet.size() < 15) {
					return mDataSet.size();
				} else if((currPage + 1)*15 > mDataSet.size()) {
					return mDataSet.size()%15;
				}
				return 15;
			}
		};
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initTotalData();
		getBoduanFenxiData("");
		initData();
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
			if(currPage <= 0) {
				return;
			}
			currPage --;
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			if(currPage >= totalPage -1) {
				return;
			}
			currPage ++;
			mAdapter.notifyDataSetChanged();
			break;
		}
	}

	private void getBoduanFenxiData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<BoduanFenxiDAO>();
		}
		
		SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String account = sp.getString("user_account", "A100");
		String sql = " SELECT (select rtrim(paraconnent) from sapara Where Rtrim(para) = Rtrim(sawarecode.state) And Trim(paratype) = 'PD') boduan, "
			+ " saindent.[warenum]* Retailprice  price, "
			+ " count( distinct saindent.warecode) ware_cnt, "
			+ " (Select count(warecode) From sawarecode B where rtrim(B.state) = Rtrim(sawarecode.state)) ware_all "
			+ " FROM saindent,sawarecode "
			+ " WHERE  Rtrim(saindent.warecode)=Rtrim(sawarecode.warecode) " 
			+ " And saindent.departcode='" + account + "'"
			+ " And saindent.[warenum]> 0 "
			+ " GROUP BY  sawarecode.state ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(BoduanZongheAnalysisActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				if(cursor.getCount()%15 ==0) {
					totalPage = cursor.getCount()/15;
				} else {
					totalPage = cursor.getCount()/15+1;
				}
				while(!cursor.isAfterLast()) {
					BoduanFenxiDAO dao = new BoduanFenxiDAO();
					dao.setBoduan(cursor.getString(0));
					dao.setWareCnt(cursor.getInt(3));
					dao.setOrderedWareCnt(cursor.getInt(1));
					dao.setWarenum(cursor.getInt(3));
					dao.setOrderedPrice(cursor.getInt(1));  
					
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
}
