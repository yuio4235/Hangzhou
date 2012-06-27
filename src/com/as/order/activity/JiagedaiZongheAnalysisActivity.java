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
import com.as.order.dao.JiagedaiFenxiDAO;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.UserUtils;

public class JiagedaiZongheAnalysisActivity extends AbstractActivity implements OnTouchListener{
	
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
	
	private Button chartsBtn;
	
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
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.jiagedai_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		chartsBtn = (Button) findViewById(R.id.charts_btn);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		chartsBtn.setOnClickListener(this);
		
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
	
	private void initConditionEts() {
		zhutiEt = (EditText) findViewById(R.id.must_order_theme_et);
		boduanEt = (EditText) findViewById(R.id.must_order_boduan_et);
		daleiEt = (EditText) findViewById(R.id.must_order_pinlei_et);
		xiaoleiEt = (EditText) findViewById(R.id.must_order_xiaolei_et);
		
		zhutiEt.setOnTouchListener(this);
		boduanEt.setOnTouchListener(this);
		daleiEt.setOnTouchListener(this);
		xiaoleiEt.setOnClickListener(this);
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
	
	private void queryByCond(String where) {
		getJiagedaiFenxiData(where);
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
			if(currPage >= totalPage -1) {
				return;
			}
			currPage ++;
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.charts_btn:
			Intent chartIntent = new Intent(this, DaleiPipeChartActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(DaleiPipeChartActivity.ANA_TYPE, DaleiPipeChartActivity.ANATYPE_JIAGEDAI);
			bundle.putInt(DaleiPipeChartActivity.OPT_TYPE, CommonDataUtils.ZKZB);
			bundle.putString(DaleiPipeChartActivity.ANA_TITLE, "价格带分析-总款占比");
			chartIntent.putExtras(bundle);
			startActivity(chartIntent);
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
			+ " count(distinct saindent.warecode ) ware_cnt, "
			+ " (select count(warecode) from sawarecode b where rtrim(sawarecode.pricecomment) = rtrim(b.pricecomment)) ware_all "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode ) "
			+ " and saindent.departcode = '"+UserUtils.getUserAccount(this)+"' "
			+ " and saindent.warenum > 0 "
			+ (TextUtils.isEmpty(where) ? "" : " and " + where )
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_boduan_et:
			if(!isBoduanListDialogShow) {
				final AsListDialog boduanListDialog = 
					DialogUtils.makeListDialog(
							JiagedaiZongheAnalysisActivity.this, 
							boduanEt, 
							CommonDataUtils.getBoduan(JiagedaiZongheAnalysisActivity.this)
						);
				boduanListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
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
							JiagedaiZongheAnalysisActivity.this, 
							daleiEt, 
							CommonDataUtils.getWareTypes(JiagedaiZongheAnalysisActivity.this)
					);
				daleiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
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
				isDaleiListDialogShow = false;
			}
			break;
			
		case R.id.must_order_xiaolei_et:
			if(!isXiaoleiListDialogShow) {
				final AsListDialog xiaoleiListDialog = 
					DialogUtils.makeListDialog(
							JiagedaiZongheAnalysisActivity.this, 
						xiaoleiEt, 
						CommonDataUtils.getType1s(JiagedaiZongheAnalysisActivity.this)
					);
				xiaoleiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
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
						DialogUtils.makeListDialog(JiagedaiZongheAnalysisActivity.this, zhutiEt, CommonDataUtils.getThemes(JiagedaiZongheAnalysisActivity.this));
				zhutiListDialog.setDialogListener(new ListDialogListener(){

					@Override
					public void onCancel() {
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
		
		if(!TextUtils.isEmpty(zhutiStr)) {
			where.append(" and type = '"+zhutiStr+"' ");
		}
		
		if(!TextUtils.isEmpty(boduanStr)) {
			where.append(" and state = '"+ CommonQueryUtils.getStateByName(JiagedaiZongheAnalysisActivity.this, boduanStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiStr)) {
			where.append(" and waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(JiagedaiZongheAnalysisActivity.this, daleiStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(xiaoleiStr)) {
			where.append(" and id = '"+CommonQueryUtils.getIdByType1(JiagedaiZongheAnalysisActivity.this, xiaoleiStr)+"' ");
		}
		
		return where.toString();
	}
}
