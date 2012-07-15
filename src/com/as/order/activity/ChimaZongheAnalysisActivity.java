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
import com.as.order.dao.ChimaFenxiDAO;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.PagerUtils;
import com.as.ui.utils.UserUtils;

public class ChimaZongheAnalysisActivity extends AbstractActivity implements OnTouchListener{

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<ChimaFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private Button chartsBtn;
	
	private View listFooter;
	
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
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.chima_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
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
				"已订占比",
				"订量",
				"订量占比",
				"金额",
				"金额占比"
		}, this));
		mDataSet = new ArrayList<ChimaFenxiDAO>();
		
		setTextForTitle("尺码综合分析");
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
	
	private void queryByCond(String where) {
		getChimaData(where);
		initData();
//		mAdapter.notifyDataSetChanged();
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
		
		if(listFooter != null) {
			mList.removeFooterView(listFooter);
		}
		
		listFooter = ListViewUtils.generateRow(new String[]{
				"合计", "",  sumWareAll+"", "100%", sumWareCnt+"", "100%", formatter.format((((double)sumWareCnt/sumWareAll)*100))+"%", sumAmount+"", "100%", sumPrice+"", "100%"
		}, ChimaZongheAnalysisActivity.this);
		
		mList.addFooterView(listFooter);
		
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	private void getChimaData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<ChimaFenxiDAO>();
		}
		mDataSet.clear();
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
			+ (TextUtils.isEmpty(where) ? "" :  " " + where)
			+ " group by sawarecode.[flag], view_ord_list.[sizecode] ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(ChimaZongheAnalysisActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				sumWareAll = 0;
				sumWareCnt = 0;
				sumAmount = 0;
				sumPrice = 0;
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_boduan_et:
			if(!isBoduanListDialogShow) {
				final AsListDialog boduanListDialog = 
					DialogUtils.makeListDialog(
							ChimaZongheAnalysisActivity.this, 
							boduanEt, 
							CommonDataUtils.getBoduan(ChimaZongheAnalysisActivity.this)
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
							ChimaZongheAnalysisActivity.this, 
							daleiEt, 
							CommonDataUtils.getWareTypes(ChimaZongheAnalysisActivity.this)
					);
				daleiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
						daleiEt.setText("");
						daleiListDialog.dismiss();
						isDaleiListDialogShow = true;
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
							ChimaZongheAnalysisActivity.this, 
						xiaoleiEt, 
						CommonDataUtils.getType1s(ChimaZongheAnalysisActivity.this)
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
				isXiaoleiListDialogShow = false;
			}
			break;
			
		case R.id.must_order_theme_et:
			if(!isZhutiListDialogShow) {
				final AsListDialog
					zhutiListDialog = 
						DialogUtils.makeListDialog(ChimaZongheAnalysisActivity.this, zhutiEt, CommonDataUtils.getThemes(ChimaZongheAnalysisActivity.this));
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
			where.append(" and state = '"+ CommonQueryUtils.getStateByName(ChimaZongheAnalysisActivity.this, boduanStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiStr) && !(CommonDataUtils.ALL_OPT.equals(daleiStr))) {
			where.append(" and waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(ChimaZongheAnalysisActivity.this, daleiStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(xiaoleiStr) && !(CommonDataUtils.ALL_OPT.equals(xiaoleiStr))) {
			where.append(" and id = '"+CommonQueryUtils.getIdByType1(ChimaZongheAnalysisActivity.this, xiaoleiStr)+"' ");
		}
		
		return where.toString();
	}
}
