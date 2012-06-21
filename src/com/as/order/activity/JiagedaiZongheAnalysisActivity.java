package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import com.as.order.dao.JiagedaiFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.UserUtils;

public class JiagedaiZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "JiagedaiZongheAnalysisActivity";
	
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<JiagedaiFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private int currPage=0;
	private int totalPage=0;
	
	//订货会总款数
	private int totalWareCnt = 0;
	//订货会总订量
	private int totalWareNum = 0;
	//订货会订货总额
	private int totalPrice = 0;
	//订货会一定款式
	private int totalOrderedWareCnt = 0;
	
	private int sumWareAll = 0;
	private int sumWareCnt = 0;
	private int sumAmount = 0;
	private int sumPrice = 0;
	
	public static final int INDEX_JIAGEDAI = 0;
	public static final int INDEX_AMOUNT = 1;
	public static final int INDEX_PRICE = 2;
	public static final int INDEX_WARECNT = 3;
	public static final int INDEX_WAREALL = 4;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.jiagedai_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"价格带",
				"总款数",
				"总款数占比",
				"订货款",
				"占款总比",
				"占已订比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, JiagedaiZongheAnalysisActivity.this));
		mDataSet = new ArrayList<JiagedaiFenxiDAO>();
		
		
		setTextForTitle("价格带综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
	}

	private void initTotalData() {
		totalWareCnt = AnaUtils.getTotalWareCnt(JiagedaiZongheAnalysisActivity.this);
		totalWareNum = AnaUtils.getTotalWareNum(JiagedaiZongheAnalysisActivity.this);
		totalPrice = AnaUtils.getTotalPrice(JiagedaiZongheAnalysisActivity.this);
		totalOrderedWareCnt = AnaUtils.getTotalOrderedWareCnt(JiagedaiZongheAnalysisActivity.this);
	}
	
	private void initData() {
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				JiagedaiFenxiDAO dao = mDataSet.get(currPage*15+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getJiagedai(),
						dao.getWareAll()+"",
						formatter.format((((double)dao.getWareAll()/sumWareAll)*100))+"%",
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/sumWareCnt)*100)) +"%",
						formatter.format((((double)dao.getWareCnt()/dao.getWareAll())*100))+"%",
						dao.getAmount()+"",
						formatter.format((((double)dao.getAmount()/sumAmount)*100))+"%",
						dao.getPrice()+"",
						formatter.format((((double)dao.getPrice()/sumPrice)*100))+"%"
				}, JiagedaiZongheAnalysisActivity.this);
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
				if(mDataSet.size() < 10) {
					return mDataSet.size();
				} else if((currPage+1)*10 > mDataSet.size()) {
					return mDataSet.size()%10;
				}
				return 10;
			}
		};
		
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		initTotalData();
		getJiagedaiFenxiData("");
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
			
			default:
				break;
		}
	}

	private void getJiagedaiFenxiData(String where) {
		String sql = " SELECT "
			+ " sawarecode.pricecomment, "
			+ " sum(saindent.warenum) amount, "
			+ " sum(saindent.warenum * sawarecode.retailprice) price, "
			+ " count(distinct saindent.warecode ) ware_cnt "
			+ " (select count(warecode) from sawarecode b where rtrim(sawarecode.pricecomment) = rtrim(b.pricecomment)) ware_all "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode ) "
			+ " and saindent.departcode = '"+UserUtils.getUserAccount(this)+"' "
			+ " and saindent.warenum > 0 "
			+ " and " + where 
			+ " group by sawarecode.pricecomment ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(this);
		Cursor cursor = db.rawQuery(sql, null);
		try  {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				if(cursor.getCount()%10 == 0) {
					totalPage = cursor.getCount()/10;
				} else {
					totalPage = cursor.getCount()/10 + 1;
				}
				while(!cursor.isAfterLast()) {
					sumWareAll += cursor.getInt(INDEX_WAREALL);
					sumWareCnt += cursor.getInt(INDEX_WARECNT);
					sumAmount += cursor.getInt(INDEX_AMOUNT);
					sumPrice += cursor.getInt(INDEX_PRICE);
					JiagedaiFenxiDAO dao = new JiagedaiFenxiDAO();
					dao.setJiagedai(cursor.getString(INDEX_JIAGEDAI));
					dao.setWareAll(cursor.getInt(INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(INDEX_WARECNT));
					dao.setAmount(cursor.getInt(INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(INDEX_PRICE));
					mDataSet.add(dao);
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if( db != null) {
				db.close();
			}
		}
	}
}
