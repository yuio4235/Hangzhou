package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import com.as.order.dao.ChimaFenxiDAO;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.PagerUtils;
import com.as.ui.utils.UserUtils;

public class ChimaZongheAnalysisActivity extends AbstractActivity {

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<ChimaFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private Button chartsBtn;
	
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
	
	public static final int INDEX_CHIMAZU = 0;
	public static final int INDEX_SIZECODE = 1;
	public static final int INDEX_AMOUNT = 2;
	public static final int INDEX_PRICE = 3;
	public static final int INDEX_WARECNT = 4;
	public static final int INDEX_WAREALL = 5;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.chima_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		chartsBtn = (Button) findViewById(R.id.charts_btn);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		chartsBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"尺码组",
				"尺码",
				"总款数",
				"总款占比",
				"订货款",
				"订货款占比",
				"已定占比",
				"订量",
				"订量占比",
				"金额",
				"金额占比"
		}, this));
		mDataSet = new ArrayList<ChimaFenxiDAO>();
		
		setTextForTitle("尺码综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
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
			
		case R.id.charts_btn:
			Intent chartsIntent = new Intent(this, DaleiPipeChartActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(DaleiPipeChartActivity.ANA_TYPE, DaleiPipeChartActivity.ANATYPE_CHIMA);
			bundle.putInt(DaleiPipeChartActivity.OPT_TYPE, CommonDataUtils.ZKZB);
			bundle.putString(DaleiPipeChartActivity.ANA_TITLE, "尺码分析-总款占比");
			chartsIntent.putExtras(bundle);
			startActivity(chartsIntent);
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getChimaData("");
		initData();
	}

	private void initData()	{
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ChimaFenxiDAO dao = mDataSet.get(currPage*PagerUtils.PAGE_AMOUNT + position);
				return ListViewUtils.generateRow(new String[]{
						dao.getChimazu(),
						dao.getChima(),
						dao.getWareAll() + "",
						formatter.format((((double)dao.getWareAll()/sumWareAll)*100))+"%",
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/sumWareCnt)*100)) +"%",
						formatter.format((((double)dao.getWareCnt()/dao.getWareAll())*100))+"%",
						dao.getAmount()+"",
						formatter.format((((double)dao.getAmount()/sumAmount)*100))+"%",
						dao.getPrice()+"",
						formatter.format((((double)dao.getPrice()/sumPrice)*100))+"%"
				}, ChimaZongheAnalysisActivity.this);
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
				} else if((currPage + 1)*10 > mDataSet.size()) {
					return mDataSet.size()%10;
				}
				return 10;
			}
		};
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	private void getChimaData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<ChimaFenxiDAO>();
		}
		String sql = " SELECT "
			+ " (select paraconnent from sapara, sawarecode b, showsize where sapara.[paratype] = 'CM' and sapara.[para] = showsize.[type] and showsize.[type] = b.[flag] and b.[warecode] = sawarecode.warecode) chimazu, "
			+ "        view_ord_list.[sizecode] sizecode,       "
			+ "        sum(view_ord_list.[amount]) amount, "
			+ "        sum(view_ord_list.[money]) price,   "
			+ "        count(distinct view_ord_list.[warecode]) ware_cnt, "
			+ " (select count(warecode) from sawarecode c where rtrim(c.flag) = rtrim(sawarecode.flag)) ware_all "
			+" from "
			+ "     sawarecode, view_ord_list "
			+ " where  "
			+ "       sawarecode.[warecode] = view_ord_list.[warecode] "
			+ " and "
			+ "    view_ord_list.[amount] > 0 "
			+ " and view_ord_list.departcode = '"+UserUtils.getUserAccount(this)+"'"
			+ (TextUtils.isEmpty(where) ? "" : " and " + where)
			+ " group by sawarecode.[flag], view_ord_list.[sizecode] ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(ChimaZongheAnalysisActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mDataSet.clear();
				if(cursor.getCount()%10 ==0) {
					totalPage = cursor.getCount()/10;
				} else  {
					totalPage = cursor.getCount()/10 +1;
				}
				while(!cursor.isAfterLast()) {
					sumWareAll += cursor.getInt(INDEX_WAREALL);
					sumWareCnt += cursor.getInt(INDEX_WARECNT);
					sumAmount += cursor.getInt(INDEX_AMOUNT);
					sumPrice += cursor.getInt(INDEX_PRICE);
					ChimaFenxiDAO dao = new ChimaFenxiDAO();
					dao.setChimazu(cursor.getString(INDEX_CHIMAZU));
					dao.setChima(cursor.getString(INDEX_SIZECODE));
					dao.setWareAll(cursor.getInt(INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(INDEX_WARECNT));
					dao.setPrice(cursor.getInt(INDEX_PRICE));
					dao.setAmount(cursor.getInt(INDEX_AMOUNT));
					mDataSet.add(dao);
					cursor.moveToNext();
				}
			}
		} finally  {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
}
