package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.order.dao.XiaoleiFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.ListViewUtils;

public class XiaoleiZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "XiaoleiZongheAnalysisActivity";

	private LinearLayout mLayout;
	private ListView mList;
	private List<XiaoleiFenxiDAO> mDataSet;
	private BaseAdapter mAdapter;
	
	private Button prevBtn;
	private Button nextBtn;
	
	private int currPage = 0;
	private int totalPage = 0;
	
	private int totalWareCnt = 0;
	private int totalWareNum = 0;
	private int totalPrice = 0;
	private int totalOrderedWareCnt = 0;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	public static final int INDEX_XIAOLEI = 0;
	public static final int INDEX_AMOUNT = 1;
	public static final int INDEX_PRICE = 2;
	public static final int INDEX_WARECNT = 3;
	public static final int INDEX_WAREALL = 4;
	
	//总款数
	private int sumWareAll = 0;
	//总订货款数
	private int sumWareCnt = 0;
	//总定量
	private int sumAmount = 0;
	//总金额
	private int sumPrice = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.xiaolei_fenxi, null);
		mRootView.addView(mLayout, FF);
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"小类",
				"总款数",
				"总款数占比",
				"订货款",
				"占款总比",
				"占已订比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, XiaoleiZongheAnalysisActivity.this));
		mDataSet = new ArrayList<XiaoleiFenxiDAO>();
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		setTextForTitle("小类综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				XiaoleiFenxiDAO dao = mDataSet.get(currPage*15+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getXiaolei(),
						dao.getWareAll()+"",
						formatter.format((((double)dao.getWareAll()/sumWareAll)*100))+"%",
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/sumWareCnt)*100)) +"%",
						formatter.format((((double)dao.getWareCnt()/dao.getWareAll())*100))+"%",
						dao.getAmount()+"",
						formatter.format((((double)dao.getAmount()/sumAmount)*100))+"%",
						dao.getPrice()+"",
						formatter.format((((double)dao.getPrice()/sumPrice)*100))+"%"
				}, XiaoleiZongheAnalysisActivity.this);
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
				} else if((currPage+1)*15 > mDataSet.size()) {
					return mDataSet.size()%15;
				}
				return 15;
			}
		};
		
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();		
	}
	
	private void initTotalData() {
		totalWareCnt = AnaUtils.getTotalWareCnt(XiaoleiZongheAnalysisActivity.this);
		totalWareNum = AnaUtils.getTotalWareNum(XiaoleiZongheAnalysisActivity.this);
		totalPrice = AnaUtils.getTotalPrice(XiaoleiZongheAnalysisActivity.this);
		totalOrderedWareCnt = AnaUtils.getTotalOrderedWareCnt(XiaoleiZongheAnalysisActivity.this);
		
		Log.e(TAG, "totalWareCnt: " + totalWareCnt + ", totalWareNum: " + totalWareNum + ", totalPrice: " + totalPrice + ", totalOrderedWareCnt: " + totalOrderedWareCnt);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initTotalData();
		getXiaoleiFenxiData("");
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
			if(currPage >= (totalPage -1)) {
				return;
			}
			currPage ++;
			mAdapter.notifyDataSetChanged();
			break;
		}
	}

	private void getXiaoleiFenxiData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<XiaoleiFenxiDAO>();
		}
		String sql = " SELECT "
			+ " (select type1 from type1 where rtrim(id) = rtrim(sawarecode.[id])) xiaolei, "
			+ " (select count(distinct warecode ) from sawarecode b where rtrim(b.id) = rtrim(sawarecode.[id])) ware_all, "
			+ " count(distinct saindent.[warecode]) ware_cnt, "
			+ " sum(saindent.[warenum]) ware_num, "
			+ " sum(sawarecode.[retailprice]*saindent.[warenum]) price "
			+ " from sawarecode "
			+ " left join saindent "
			+ " on sawarecode.[warecode] = saindent.warecode "
			+ " where 1=1 " + where
			+ " group by sawarecode.id ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(XiaoleiZongheAnalysisActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				if(cursor.getCount()%15 == 0) {
					totalPage = cursor.getCount()/15;
				} else {
					totalPage = cursor.getCount()/15 + 1;
				}
				while(!cursor.isAfterLast()) {
					sumWareAll += cursor.getInt(INDEX_WAREALL);
					sumWareCnt += cursor.getInt(INDEX_WARECNT);
					sumAmount += cursor.getInt(INDEX_AMOUNT);
					sumPrice += cursor.getInt(INDEX_PRICE);
					XiaoleiFenxiDAO dao = new XiaoleiFenxiDAO();
					dao.setXiaolei(cursor.getString(INDEX_XIAOLEI));
					dao.setWareAll(cursor.getInt(INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(INDEX_WARECNT));
					dao.setAmount(cursor.getInt(INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(INDEX_PRICE));
					mDataSet.add(dao);
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null ) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
	}
}
