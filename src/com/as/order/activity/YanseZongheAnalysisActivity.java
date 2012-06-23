package com.as.order.activity;

import java.text.DecimalFormat;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.YanseFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.PagerUtils;
import com.as.ui.utils.UserUtils;

public class YanseZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "YanseZongheAnalysisActivity";

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<YanseFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private Button chartsBtn;
	
	private int currPage = 0;
	private int totalPage = 0;
	
	private int totalWareCnt = 0;
	private int totalWareNum =0;
	private int totalPrice = 0;
	private int totalOrderedWareCnt =0;
	
	private int sumWareAll = 0;
	private int sumWareCnt = 0;
	private int sumAmount = 0;
	private int sumPrice = 0;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	public static final int INDEX_YANSE = 0;
	public static final int INDEX_AMOUNT = 1;
	public static final int INDEX_PRICE = 2;
	public static final int INDEX_WARECNT = 3;
	public static final int INDEX_WAREALL = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.yanse_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		chartsBtn = (Button) findViewById(R.id.charts_btn);
		chartsBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"颜色",
				"总款数",
				"总款数占比",
				"订货款",
				"占款总比",
				"占已订比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, YanseZongheAnalysisActivity.this));
		mDataSet = new ArrayList<YanseFenxiDAO>();
		
		setTextForTitle("颜色综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");		
	}
	
	private void initTotalData() {
		totalWareCnt = AnaUtils.getTotalWareCnt(YanseZongheAnalysisActivity.this);
		totalWareNum = AnaUtils.getTotalWareNum(YanseZongheAnalysisActivity.this);
		totalPrice = AnaUtils.getTotalPrice(YanseZongheAnalysisActivity.this);
		totalOrderedWareCnt = AnaUtils.getTotalOrderedWareCnt(YanseZongheAnalysisActivity.this);
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				YanseFenxiDAO dao = mDataSet.get(currPage*PagerUtils.PAGE_AMOUNT+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getYanse(),
						dao.getWareAll()+"",
						formatter.format((((double)dao.getWareAll()/sumWareAll)*100))+"%",
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/sumWareCnt)*100)) +"%",
						formatter.format((((double)dao.getWareCnt()/dao.getWareAll())*100))+"%",
						dao.getAmount()+"",
						formatter.format((((double)dao.getAmount()/sumAmount)*100))+"%",
						dao.getPrice()+"",
						formatter.format((((double)dao.getPrice()/sumPrice)*100))+"%"
				}, YanseZongheAnalysisActivity.this);
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
	
	@Override
	protected void onResume() {
		super.onResume();
		initTotalData();
		getYanseFenxiData("");
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
			
		case R.id.charts_btn:
			Intent chartsIntent = new Intent(YanseZongheAnalysisActivity.this, DaleiPipeChartActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(DaleiPipeChartActivity.ANA_TYPE, DaleiPipeChartActivity.ANATYPE_YANSE);
			bundle.putInt(DaleiPipeChartActivity.OPT_TYPE, CommonDataUtils.ZKZB);
			bundle.putString(DaleiPipeChartActivity.ANA_TITLE, "颜色分析-总款占比");
			chartsIntent.putExtras(bundle);
			startActivity(chartsIntent);
			break;
		}
	}

	private void getYanseFenxiData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<YanseFenxiDAO>();
		}
		String sql = " select sacolorcode.[colorname] yanse, "
			+" sum(saindent.[warenum]) amount, "
			+" sum(saindent.[warenum]*sawarecode.[retailprice]) price, "
			+" count(distinct saindent.[warecode]) ware_cnt, "
			+" (Select count(B.warecode) From saindent  B,sawarecode C "
			+" where Rtrim(B.colorcode) = Rtrim(saindent.colorcode) "
			+" And Rtrim(B.departcode) = '"+UserUtils.getUserAccount(this)+"' "
			+" And Rtrim(B.warecode) = Rtrim(C.warecode)) ware_all "
			+" from saindent, sawarecode, sacolorcode "
			+" where "
			+"  Rtrim(saindent.colorcode)= Rtrim(sacolorcode.colorcode)  "
			+" and "
			+"  Rtrim(saindent.warecode)= Rtrim(sawarecode.warecode) "
			+" and "
			+"  saindent.departcode= '"+UserUtils.getUserAccount(this)+"' "
			+" and "
			+" saindent.[warenum] > 0 "
			+(TextUtils.isEmpty(where) ? "" : " and " + where)
			+" group by saindent.[colorcode], sacolorcode.[colorname] ";
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
					Log.e(TAG, "current sumWareAll: " + sumWareAll);
					sumWareAll += cursor.getInt(INDEX_WAREALL);
					sumWareCnt += cursor.getInt(INDEX_WARECNT);
					sumAmount += cursor.getInt(INDEX_AMOUNT);
					sumPrice += cursor.getInt(INDEX_PRICE);
					YanseFenxiDAO dao = new YanseFenxiDAO();
					dao.setYanse(cursor.getString(INDEX_YANSE));
					dao.setAmount(cursor.getInt(INDEX_AMOUNT));
					dao.setWareAll(cursor.getInt(INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(INDEX_WARECNT));
					dao.setPrice(cursor.getInt(INDEX_PRICE));
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
