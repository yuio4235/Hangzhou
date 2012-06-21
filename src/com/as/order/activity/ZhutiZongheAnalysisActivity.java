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
import com.as.order.dao.ZhutiFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.UserUtils;

public class ZhutiZongheAnalysisActivity extends AbstractActivity {

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<ZhutiFenxiDAO> mDataSet;
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
	
	public static final int INDEX_ZHUTI = 0;
	public static final int INDEX_AMOUNT = 1;
	public static final int INDEX_PRICE = 2;
	public static final int INDEX_WARECNT = 3;
	public static final int INDEX_WAREALL = 4;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.zhuti_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"主题",
				"总款数",
				"总款数占比",
				"订货款",
				"占款总比",
				"占已订比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, ZhutiZongheAnalysisActivity.this));
		mDataSet = new ArrayList<ZhutiFenxiDAO>();
		
		
		setTextForTitle("主题综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
	}

	private void initTotalData() {
		totalWareCnt = AnaUtils.getTotalWareCnt(ZhutiZongheAnalysisActivity.this);
		totalWareNum = AnaUtils.getTotalWareNum(ZhutiZongheAnalysisActivity.this);
		totalPrice = AnaUtils.getTotalPrice(ZhutiZongheAnalysisActivity.this);
		totalOrderedWareCnt = AnaUtils.getTotalOrderedWareCnt(ZhutiZongheAnalysisActivity.this);
	}
	
	private void initData() {
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ZhutiFenxiDAO dao = mDataSet.get(currPage*15+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getZhuti(),
						dao.getWareAll()+"",
						formatter.format((((double)dao.getWareAll()/sumWareAll)*100))+"%",
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/sumWareCnt)*100)) +"%",
						formatter.format((((double)dao.getWareCnt()/dao.getWareAll())*100))+"%",
						dao.getAmount()+"",
						formatter.format((((double)dao.getAmount()/sumAmount)*100))+"%",
						dao.getPrice()+"",
						formatter.format((((double)dao.getPrice()/sumPrice)*100))+"%"
				}, ZhutiZongheAnalysisActivity.this);
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
		getZhutiFenxiData("");
		initData();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
			case R.id.prev_page:
				if(currPage <= 0) {
					return;
				}
				currPage --;
				mAdapter.notifyDataSetChanged();
				break;
				
			case R.id.next_page:
				if(currPage >= totalPage-1) {
					return;
				}
				currPage ++;
				mAdapter.notifyDataSetChanged();
				break;
			
			default:
				break;
		}
	}

	private void getZhutiFenxiData(String where) {
		String sql = 
		      " select sawarecode.style, "
			+ " sum(saindent.warenum) amount ,"
			+ " sum(saindent.warenum*sawarecode.retailprice) price, "
			+ " count(distinct saindent.warecode) ware_cnt, "
			+ " (select count(warecode) from sawarecode b where rtrim(sawarecode.style) = rtrim(b.style)) ware_all, "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode) "
			+ " and saindent.departcode = '"+UserUtils.getUserAccount(this)+"' "
			+ " and saindent.warenum > 0 "
			+ " and " + where 
			+ " group by sawarecode.style ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
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
					ZhutiFenxiDAO dao = new ZhutiFenxiDAO();
					dao.setZhuti(cursor.getString(INDEX_ZHUTI));
					dao.setWareAll(cursor.getInt(INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(INDEX_WARECNT));
					dao.setAmount(cursor.getInt(INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(INDEX_PRICE));
					mDataSet.add(dao);
					cursor.moveToNext();
				}
			}
		} finally {
			if(db != null) {
				db.close();
			}
			if(cursor != null) {
				cursor.close();
			}
		}
	}
}
