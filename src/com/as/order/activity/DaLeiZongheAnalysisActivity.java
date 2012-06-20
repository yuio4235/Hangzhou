package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import com.as.order.charts.DaleiFenxiPipeChart;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.ui.utils.ListViewUtils;

public class DaLeiZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "DaLeiZongheAnalysisActivity";

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<DaleiFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private Button chartsBtn;
	
	private int currPage=0;
	private int totalPage=0;
	
	//总款数
	private int sumWareAll = 0;
	//总订货款数
	private int sumWareCnt = 0;
	//总订量
	private int sumAmount = 0;
	//总金额
	private int sumPrice = 0;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	public static final int INDEX_DALEI = 0;
	public static final int INDEX_AMOUNT = 1;
	public static final int INDEX_PRICE = 2;
	public static final int INDEX_WARE_CNT = 3;
	public static final int INDEX_WARE_ALL = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.dalei_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		chartsBtn = (Button) findViewById(R.id.charts_btn);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		chartsBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"大类",
				"总款数",
				"总款数占比",
				"订货款",
				"订货款占比",
				"已订占比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, DaLeiZongheAnalysisActivity.this));
		mDataSet = new ArrayList<DaleiFenxiDAO>();
		
		
		setTextForTitle("大类综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
	}
	
	private void initData() {
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				DaleiFenxiDAO dao = mDataSet.get(currPage*15+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getDalei(),
						dao.getWareAll()+"",
						formatter.format((((double)dao.getWareAll()/sumWareAll)*100))+"%",
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/sumWareCnt)*100)) +"%",
						formatter.format((((double)dao.getWareCnt()/dao.getWareAll())*100))+"%",
						dao.getAmount()+"",
						formatter.format((((double)dao.getAmount()/sumAmount)*100))+"%",
						dao.getPrice()+"",
						formatter.format((((double)dao.getPrice()/sumPrice)*100))+"%"
				}, DaLeiZongheAnalysisActivity.this);
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
	
	@Override
	protected void onResume() {
		super.onResume();
		getDaleiFenxiData("");
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
				
			case R.id.charts_btn:
//				DaleiFenxiPipeChart mChart = new DaleiFenxiPipeChart(mDataSet);
//				startActivity(mChart.execute(DaLeiZongheAnalysisActivity.this));
				Intent inent = new Intent(DaLeiZongheAnalysisActivity.this, DaleiPipeChartActivity.class);
				startActivity(inent);
				break;
			
			default:
				break;
		}
	}
	
	private void getDaleiFenxiData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<DaleiFenxiDAO>();
		}
		String sql = " SELECT "
			+ " (select waretypename From sawaretype Where rtrim(sawaretype.waretypeid) = rtrim(sawarecode.waretypeid)) dalei, "
			/*+ " (Select type1 From type1 Where rtrim(id) = trim(sawarecode.id)) xiaolei, "*/
			+ " sum(saindent.[warenum]) amount, "
			+ " Sum(saindent.[warenum] * Retailprice ) price, "
			+ " count( distinct saindent.warecode) ware_cnt, "
			+ " (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all "
			+ " FROM saindent,sawarecode "
			+ " WHERE  Rtrim(saindent.warecode)=Rtrim(sawarecode.warecode) "
			+ " And Rtrim(saindent.departcode) = 'A100' "
		    + where
		    + " And		saindent.[warenum] > 0 "
			+ " GROUP BY  sawarecode.waretypeid";
		
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DaLeiZongheAnalysisActivity.this);
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
					sumWareAll += cursor.getInt(INDEX_WARE_ALL);
					sumWareCnt += cursor.getInt(INDEX_WARE_CNT);
					sumAmount += cursor.getInt(INDEX_AMOUNT);
					sumPrice += cursor.getInt(INDEX_PRICE);
					DaleiFenxiDAO dao = new DaleiFenxiDAO();
					dao.setDalei(cursor.getString(INDEX_DALEI));
					dao.setWareAll(cursor.getInt(INDEX_WARE_ALL));
					dao.setWareCnt(cursor.getInt(INDEX_WARE_CNT));
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
			if(db != null) {
				db.close();
			}
		}
	}

}
