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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.XiaoleiFenxiDAO;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.PagerUtils;
import com.as.ui.utils.UserUtils;

public class XiaoleiZongheAnalysisActivity extends AbstractActivity implements OnTouchListener {
	
	private static final String TAG = "XiaoleiZongheAnalysisActivity";

	private LinearLayout mLayout;
	private ListView mList;
	private List<XiaoleiFenxiDAO> mDataSet;
	private BaseAdapter mAdapter;
	private View listFooter;
	
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
	
	private Button chartBtn;
	
	private EditText zhutiEt;
	private EditText boduanEt;
	private EditText daleiEt;
	private EditText xiaoleiEt;
	
	private boolean isBoduanListDialogShow = false;
	private boolean isZhutiListDialogShow = false;
	private boolean isDaleiListDialogShow = false;
	private boolean isXiaoleiListDialogShow = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.xiaolei_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"小类",
				"总款数",
				"总款数占比",
				"订货款",
				"占款总比",
				"已订占比",
				"订量",
				"订量占比",
				"订货金额",
				"金额占比"
		}, XiaoleiZongheAnalysisActivity.this));
		mDataSet = new ArrayList<XiaoleiFenxiDAO>();
		
		chartBtn = (Button) findViewById(R.id.charts_btn);
		chartBtn.setOnClickListener(this);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		setTextForTitle("小类综合分析");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
		
		initConditionEts();
	}
	
	private void initConditionEts() {
		zhutiEt = (EditText) findViewById(R.id.must_order_theme_et);
		boduanEt = (EditText) findViewById(R.id.must_order_boduan_et);
		daleiEt = (EditText) findViewById(R.id.must_order_pinlei_et);
		xiaoleiEt = (EditText) findViewById(R.id.must_order_xiaolei_et);
		
		zhutiEt.setOnTouchListener(this);
		boduanEt.setOnTouchListener(this);
		daleiEt.setOnTouchListener(this);
		xiaoleiEt.setOnTouchListener(this);
	}
	
	private void initData() {
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				XiaoleiFenxiDAO dao = mDataSet.get(currPage*PagerUtils.PAGE_AMOUNT+position);
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
				if(mDataSet.size() < 10) {
					return mDataSet.size();
				} else if((currPage+1)*10 > mDataSet.size()) {
					return mDataSet.size()%10;
				}
				return 10;
			}
		};
		
		if(listFooter != null) {
			mList.removeFooterView(listFooter);
		}
		
		listFooter = ListViewUtils.generateRow(new String[]{
				"合计", sumWareAll+ "", "100%", sumWareCnt+"", "100%", formatter.format((((double)sumWareCnt/sumWareAll)*100))+"%", sumAmount+"", "100%",sumPrice+"", "100%"
		}, XiaoleiZongheAnalysisActivity.this);
		
		mList.addFooterView(listFooter);
		
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
	
	private void queryByCond(String where) {
		getXiaoleiFenxiData(where);
		initData();
//		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			queryByCond(getWhere());
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
			
		case R.id.charts_btn:
			Intent chartIntent = new Intent(XiaoleiZongheAnalysisActivity.this, DaleiPipeChartActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(DaleiPipeChartActivity.ANA_TYPE, DaleiPipeChartActivity.ANATYPE_XIAOLEI);
			bundle.putInt(DaleiPipeChartActivity.OPT_TYPE, CommonDataUtils.ZKZB);
			bundle.putString(DaleiPipeChartActivity.ANA_TITLE, "小类分析-总款占比");
			chartIntent.putExtras(bundle);
			startActivity(chartIntent);
			break;
		}
	}

	private void getXiaoleiFenxiData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<XiaoleiFenxiDAO>();
		}
		mDataSet.clear();
		String sql = " SELECT "
			+ " (select type1 from type1 where rtrim(id) = rtrim(sawarecode.[id])) xiaolei, "
			+ " (select count(distinct warecode ) from sawarecode b where rtrim(b.id) = rtrim(sawarecode.[id])) ware_all, "
			+ " count(distinct saindent.[warecode]) ware_cnt, "
			+ " sum(saindent.[warenum]) ware_num, "
			+ " sum(sawarecode.[retailprice]*saindent.[warenum]) price "
			+ " from sawarecode "
			+ " left join saindent "
			+ " on sawarecode.[warecode] = saindent.warecode and rtrim(saindent.departcode) = '"+UserUtils.getUserAccount(XiaoleiZongheAnalysisActivity.this)+"' and saindent.warenum >  0 "
			+ (TextUtils.isEmpty(where) ? "" : " where 1=1 and saindent.warenum > 0 " + where)
			+ " group by sawarecode.id ";

		String sql1 = " SELECT "
			+ "   (select rtrim(type1) from type1 where rtrim(type1.id) = rtrim(sawarecode.id)) type1, "
			+ "    Sum(saindent.warenum) amount,                            "
			+ "    Sum(saindent.warenum*sawarecode.retailprice) totalmoney, "
			+ "    count( distinct saindent.warecode) ware_cnt,            "
			+ "     (Select count(warecode) From sawarecode B where Rtrim(sawarecode.id)= Rtrim(B.id)) ware_all "
			+ " FROM  saindent,sawarecode "
			+ " WHERE saindent.departcode = '"+UserUtils.getUserAccount(XiaoleiZongheAnalysisActivity.this)+"'                and "
			+ "       saindent.[warenum]> 0                       and "
			+ "       saindent.warecode =sawarecode.warecode "
			+ (TextUtils.isEmpty(where) ? "" : where)
			+ " GROUP BY sawarecode.id  ";
		
		Log.e(TAG, "sql: " + sql1);
		
		SQLiteDatabase db = AsProvider.getWriteableDatabase(XiaoleiZongheAnalysisActivity.this);
		Cursor cursor = db.rawQuery(sql1, null);
		
		try {
			if(cursor != null && cursor.moveToFirst()) {
				sumWareAll = 0;
				sumWareCnt = 0;
				sumAmount = 0;
				sumPrice = 0;
				if(cursor.getCount()%10 == 0) {
					totalPage = cursor.getCount()/10;
				} else {
					totalPage = cursor.getCount()/10 + 1;
				}
				while(!cursor.isAfterLast()) {
					sumWareAll += cursor.getInt(4);
					sumWareCnt += cursor.getInt(3);
					sumAmount += cursor.getInt(1);
					sumPrice += cursor.getInt(2);
					XiaoleiFenxiDAO dao = new XiaoleiFenxiDAO();
					dao.setXiaolei(cursor.getString(INDEX_XIAOLEI));
					dao.setWareAll(cursor.getInt(4));
					dao.setWareCnt(cursor.getInt(3));
					dao.setAmount(cursor.getInt(1));
					dao.setPrice(cursor.getInt(2));
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_boduan_et:
			if(!isBoduanListDialogShow) {
				final AsListDialog boduanListDialog = 
					DialogUtils.makeListDialog(
							XiaoleiZongheAnalysisActivity.this, 
							boduanEt, 
							CommonDataUtils.getBoduan(XiaoleiZongheAnalysisActivity.this)
						);
				boduanListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
						boduanEt.setText("");
						boduanListDialog.dismiss();
						isBoduanListDialogShow = false;
					}

					@Override
					public void onClick(String text) {
						boduanEt.setText(text.trim());
						boduanListDialog.dismiss();
						isBoduanListDialogShow = false;
					}});
				boduanListDialog.show();
				isBoduanListDialogShow = true;
			}
			break;
			
		case R.id.must_order_pinlei_et:
			if(!isDaleiListDialogShow) {
				final AsListDialog daleiListDialog = 
					DialogUtils.makeListDialog(
							XiaoleiZongheAnalysisActivity.this, 
							daleiEt, 
							CommonDataUtils.getWareTypes(XiaoleiZongheAnalysisActivity.this)
					);
				daleiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
						daleiEt.setText("");
						daleiListDialog.dismiss();
						isDaleiListDialogShow = false;
					}

					@Override
					public void onClick(String text) {
						daleiEt.setText(text);
						daleiListDialog.dismiss();
						isDaleiListDialogShow = false;
					}});
				daleiListDialog.show();
				isDaleiListDialogShow = true;
			}
			break;
			
		case R.id.must_order_xiaolei_et:
			if(!isXiaoleiListDialogShow) {
				final AsListDialog xiaoleiListDialog = 
					DialogUtils.makeListDialog(
							XiaoleiZongheAnalysisActivity.this, 
						xiaoleiEt, 
						CommonDataUtils.getType1s(XiaoleiZongheAnalysisActivity.this)
					);
				xiaoleiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
						xiaoleiEt.setText("");
						xiaoleiListDialog.dismiss();
						isXiaoleiListDialogShow = false;
					}

					@Override
					public void onClick(String text) {
						xiaoleiEt.setText(text);
						xiaoleiListDialog.dismiss();
						isXiaoleiListDialogShow = false;
					}});
				xiaoleiListDialog.show();
				isXiaoleiListDialogShow = true;
			}
			break;
			
		case R.id.must_order_theme_et:
			if(!isZhutiListDialogShow) {
				final AsListDialog
					zhutiListDialog = 
						DialogUtils.makeListDialog(XiaoleiZongheAnalysisActivity.this, zhutiEt, CommonDataUtils.getThemes(XiaoleiZongheAnalysisActivity.this));
				zhutiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
						zhutiEt.setText("");
						zhutiListDialog.dismiss();
						isZhutiListDialogShow = false;
					}

					@Override
					public void onClick(String text) {
						zhutiEt.setText(text);
						zhutiListDialog.dismiss();
						isZhutiListDialogShow = false;
					}});
				zhutiListDialog.show();
				isZhutiListDialogShow = true;
			}
			break;
			
			default:
				break;
		}
		return false;
	}
	
	private String getWhere() {
		StringBuilder where = new StringBuilder();
		String zhutiStr = zhutiEt.getText().toString().trim();
		String boduanStr = boduanEt.getText().toString().trim();
		String daleiStr = daleiEt.getText().toString().trim();
		String xiaoleiStr = xiaoleiEt.getText().toString().trim();
		
		if(!TextUtils.isEmpty(zhutiStr) && !(CommonDataUtils.ALL_OPT.equals(zhutiStr))) {
			where.append(" and style = '"+zhutiStr+"' ");
		}
		
		if(!TextUtils.isEmpty(boduanStr) && !(CommonDataUtils.ALL_OPT.equals(boduanStr))) {
			where.append(" and state = '"+ CommonQueryUtils.getStateByName(XiaoleiZongheAnalysisActivity.this, boduanStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiStr) && !(CommonDataUtils.ALL_OPT.equals(daleiStr))) {
			where.append(" and waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(XiaoleiZongheAnalysisActivity.this, daleiStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(xiaoleiStr) && !(CommonDataUtils.ALL_OPT.equals(xiaoleiStr))) {
			where.append(" and id = '"+CommonQueryUtils.getIdByType1(XiaoleiZongheAnalysisActivity.this, xiaoleiStr)+"' ");
		}
		
		return where.toString();
	}
}
