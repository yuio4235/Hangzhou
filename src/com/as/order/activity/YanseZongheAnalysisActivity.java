package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.as.order.R;
import com.as.order.dao.BoduanFenxiDAO;
import com.as.order.dao.YanseFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.ListViewUtils;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class YanseZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "YanseZongheAnalysisActivity";

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<YanseFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
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
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.boduan_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
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
				YanseFenxiDAO dao = mDataSet.get(currPage*15+position);
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
		}
	}

	private void getYanseFenxiData(String where) {
		
	}
}
