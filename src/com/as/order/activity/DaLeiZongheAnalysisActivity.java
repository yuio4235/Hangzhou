package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.as.order.dao.DaleiFenxiDAO;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.PagerUtils;
import com.as.ui.utils.UserUtils;

public class DaLeiZongheAnalysisActivity extends AbstractActivity implements OnTouchListener{
	
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
				"总款占比",
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
				DaleiFenxiDAO dao = mDataSet.get(currPage*PagerUtils.PAGE_AMOUNT+position);
//				Log.e(TAG, "current ware_all: " + dao.getWareAll() + " sumWareAll: " + sumWareAll);
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
		getDaleiFenxiData("");
		initData();
	}
	
	private void queryByCond(String where) {
		getDaleiFenxiData(where);
		mAdapter.notifyDataSetChanged();
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
				if(currPage >= totalPage-1) {
					return;
				}
				currPage ++;
				mAdapter.notifyDataSetChanged();
				break;
				
			case R.id.charts_btn:
//				DaleiFenxiPipeChart mChart = new DaleiFenxiPipeChart(mDataSet);
//				startActivity(mChart.execute(DaLeiZongheAnalysisActivity.this));
				Intent intent = new Intent(DaLeiZongheAnalysisActivity.this, DaleiPipeChartActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(DaleiPipeChartActivity.ANA_TYPE, DaleiPipeChartActivity.ANATYPE_DALEI);
				bundle.putInt(DaleiPipeChartActivity.OPT_TYPE, CommonDataUtils.ZKZB);
				bundle.putString(DaleiPipeChartActivity.ANA_TITLE, "大类分析-总款占比");
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			
			default:
				break;
		}
	}
	
	private void getDaleiFenxiData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<DaleiFenxiDAO>();
		}
		mDataSet.clear();
//		String sql = " SELECT "
//			+ " (select waretypename From sawaretype Where rtrim(sawaretype.waretypeid) = rtrim(sawarecode.waretypeid)) dalei, "
//			/*+ " (Select type1 From type1 Where rtrim(id) = trim(sawarecode.id)) xiaolei, "*/
//			+ " sum(saindent.[warenum]) amount, "
//			+ " Sum(saindent.[warenum] * Retailprice ) price, "
//			+ " count( distinct saindent.warecode) ware_cnt, "
//			+ " (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all "
//			+ " FROM saindent,sawarecode "
//			+ " WHERE  Rtrim(saindent.warecode)=Rtrim(sawarecode.warecode) "
//			+ " And Rtrim(saindent.departcode) = 'A100' "
//		    + where
//		    + " And		saindent.[warenum] > 0 "
//			+ " GROUP BY  sawarecode.waretypeid";
		String sql = ""
			+" SELECT "
			+" (select waretypename from sawaretype where rtrim(sawaretype.[waretypeid])  = rtrim(sawarecode.[waretypeid])) dalei, "
			+" sum(saindent.[warenum]) amount, "
			+" sum(saindent.[warenum]* retailprice) price,  "
			+" count(distinct saindent.[warecode]) ware_cnt,  "
			+" (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all "
			+" from saindent, sawarecode " 
			+" where rtrim(saindent.[warecode]) = rtrim(sawarecode.[warecode]) "
			+" and rtrim(saindent.[departcode]) = '"+UserUtils.getUserAccount(this)+"' "
			+" and saindent.[warenum] > 0 "
			+(TextUtils.isEmpty(where) ? "" : " and " + where)
			+" group by sawarecode.[waretypeid] ";
		
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DaLeiZongheAnalysisActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				if(cursor.getCount()%10 ==0) {
					totalPage = cursor.getCount()/10;
				} else {
					totalPage = cursor.getCount()/10+1;
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_boduan_et:
			if(!isBoduanListDialogShow) {
				final AsListDialog boduanListDialog = 
					DialogUtils.makeListDialog(
							DaLeiZongheAnalysisActivity.this, 
							boduanEt, 
							CommonDataUtils.getBoduan(DaLeiZongheAnalysisActivity.this)
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
							DaLeiZongheAnalysisActivity.this, 
							daleiEt, 
							CommonDataUtils.getWareTypes(DaLeiZongheAnalysisActivity.this)
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
							DaLeiZongheAnalysisActivity.this, 
						xiaoleiEt, 
						CommonDataUtils.getType1s(DaLeiZongheAnalysisActivity.this)
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
						DialogUtils.makeListDialog(DaLeiZongheAnalysisActivity.this, zhutiEt, CommonDataUtils.getThemes(DaLeiZongheAnalysisActivity.this));
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
		
		if(!TextUtils.isEmpty(zhutiStr) && !("=====全部=====".equals(zhutiStr))) {
			where.append(" and type = '"+zhutiStr+"' ");
		}
		
		if(!TextUtils.isEmpty(boduanStr) && !("=====全部=====".equals(boduanStr))) {
			where.append(" and state = '"+ CommonQueryUtils.getStateByName(DaLeiZongheAnalysisActivity.this, boduanStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiStr) && !(CommonDataUtils.ALL_OPT.equals(daleiStr))) {
			where.append(" and waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(DaLeiZongheAnalysisActivity.this, daleiStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(xiaoleiStr) && !(CommonDataUtils.ALL_OPT.equals(xiaoleiStr))) {
			where.append(" and id = '"+CommonQueryUtils.getIdByType1(DaLeiZongheAnalysisActivity.this, xiaoleiStr)+"' ");
		}
		
		return where.toString();
	}
}
