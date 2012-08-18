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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.DaleiWdDAO;
import com.as.order.pager.PageDao;
import com.as.order.pager.PageDaoImplAll;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.DialogListener;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.UserUtils;

public class DaleiWdFenxi extends AbstractActivity implements OnTouchListener{

	private static final String TAG = "DaleiWdFenxi";
	
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<DaleiWdDAO> mDataSet;
	private List<DaleiWdDAO> mCurrentDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private int totalWare = 0;
	
	private EditText zhutiEt;
	private EditText boduanEt;
	private EditText daleiEt;
	private EditText xiaoleiEt;
	
	private boolean isBoduanListDialogShow = false;
	private boolean isZhutiListDialogShow = false;
	private boolean isDaleiListDialogShow = false;
	private boolean isXiaoleiListDialogShow = false;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	PageDao pager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.dalei_wd_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"大类",
				"未订款",
				"已订款",
				"总款数",
				"未定占比"
		}, DaleiWdFenxi.this));
		
		setTextForLeftTitleBtn("返回");
		setTextForTitle("大类未定分析");
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
	protected void onResume() {
		super.onResume();
		initData(getWhere());
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	private void queryByCond(String where) {
		getData(where);
		mCurrentDataSet = (List<DaleiWdDAO>)pager.getCurrentList();
		mAdapter.notifyDataSetChanged();
	}
	
	private void initData(String where) {
		getData(where);
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				DaleiWdDAO dao = mCurrentDataSet.get(position);
				return ListViewUtils.generateRow(new String[]{
						dao.getDalei(),
						dao.getWd()+"",
						dao.getYd()+"",
						dao.getTotal()+"",
						formatter.format(((double)dao.getWd()/dao.getTotal())*100)+"%"
				}, DaleiWdFenxi.this);
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
				return mCurrentDataSet.size();
			}
		};
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				DaleiWdDAO dao = mCurrentDataSet.get(position-1);
//				Log.e(TAG, "current position: " + (pager.getPerPage()*(pager.getCurrentPage()-1) + position) + ", dalei: " + dao.getDalei() + " waretypeid: " + dao.getWaretypeid());
				Intent intent = new Intent(DaleiWdFenxi.this, WdDetailActivity.class);
				intent.putExtra("where", " waretypeid = '" + dao.getWaretypeid() + "'");
				startActivity(intent);
			}
		});
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
			pager.prevPage();
			mCurrentDataSet = (List<DaleiWdDAO>)pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			pager.nextPage();
			mCurrentDataSet = (List<DaleiWdDAO>) pager.getCurrentList();
			mAdapter.notifyDataSetChanged();
			break;
			
			default:
				break;
		}
	}

	private void getData(String where) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<DaleiWdDAO>();
		}
		mDataSet.clear();
		String sql = " select "
			+ " sawarecode.[waretypeid], "
			+ "   (select waretypename from sawaretype where rtrim(sawaretype.waretypeid) = rtrim(sawarecode.waretypeid)) dalei, "
			+ "         count(distinct b.warecode) ware_order, "
			+ "         count(distinct c.warecode) ware_unorder, "
			+ "         count(distinct sawarecode.warecode) ware_all "
			+ " from sawarecode  "
			+ "  left join saindent b on sawarecode.warecode = b.warecode and b.warenum > 0 "
			+ " left join saindent c on sawarecode.warecode = c.warecode and c.warenum = 0 "
			+ (TextUtils.isEmpty(where) ? "" : " where 1=1 " + where)
			+ " group by sawarecode.waretypeid  ";
		
		String sql1 = " Select sawaretype.waretypeid,  sawaretype.WareTypeName,sum(unorder) zongkuanshu,sum(orderware) yd,sum(unorder)-sum(orderware) wd "
			+ " From (SELECT  sawarecode.waretypeid, "
			+ "             count(distinct sawarecode.warecode) unorder, "
			+ "            0 orderware "
			+ " FROM saindent,   sawarecode "
			+ " WHERE ( saindent.warecode = sawarecode.warecode ) "
			+ " Group By    sawarecode.waretypeid,saindent.warecode "
			+ " Union All "
			+ " SELECT  sawarecode.waretypeid, "
			+ "            0, "
			+ "            count(distinct sawarecode.warecode) orderware "
			+ " FROM saindent,   sawarecode  "
			+ " WHERE ( saindent.warecode = sawarecode.warecode ) "
			+ " and saindent.departcode = '"+UserUtils.getUserAccount(DaleiWdFenxi.this)+"' "
			+ " And    saindent.WARENUM > 0 "
			+ " Group By    sawarecode.waretypeid) A,sawaretype "
			+ " where    sawaretype.WareTypeID = A.waretypeid "
			+ " Group By sawaretype.WareTypeName ";
		
//		String sql1 = " Select Type1.Type1,sum(allorder) zongkuanshu ,sum(orderware) yiding ,sum(allorder)-sum(orderware) weid "
//			+ " From(SELECT   sawarecode.id, "
//			+ " count(distinct sawarecode.warecode) allorder, "
//			+ "  0 orderware " 
//			+ " FROM saindent,sawarecode  "
//			+ " WHERE ( saindent.warecode = sawarecode.warecode )   "
//			+ " Group By  sawarecode.id "
//			+ " Union All "
//			+ " SELECT   sawarecode.id,   "
//			+ "  0, "
//		    + "  count(distinct sawarecode.warecode) "
//		    + " FROM saindent,sawarecode "
//		    + " WHERE ( rtrim(saindent.warecode) = rtrim(sawarecode.warecode )) "
//		    + " and saindent.warenum > 0 "
//		    + " Group By  sawarecode.id) A,type1 "
//		    + " where   A.id = type1.ID "
//		    + " Group By type1.Type1 ";
		Log.e(TAG, "sql: " + sql1);
		
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DaleiWdFenxi.this);
		Cursor cursor = db.rawQuery(sql1, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				totalWare = 0;
				while(!cursor.isAfterLast()) {
					DaleiWdDAO dao = new DaleiWdDAO();
					dao.setWaretypeid(cursor.getString(0));
					dao.setDalei(cursor.getString(1));
					dao.setYd(cursor.getInt(3));
					dao.setWd(cursor.getInt(4));
					dao.setTotal(cursor.getInt(2));
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
			pager = new PageDaoImplAll(mDataSet, 15, mDataSet.size());
			mCurrentDataSet = (List<DaleiWdDAO>)pager.getCurrentList();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_boduan_et:
			if(!isBoduanListDialogShow) {
				final AsListDialog boduanListDialog = 
					DialogUtils.makeListDialog(
							DaleiWdFenxi.this, 
							boduanEt, 
							CommonDataUtils.getBoduan(DaleiWdFenxi.this)
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
							DaleiWdFenxi.this, 
							daleiEt, 
							CommonDataUtils.getWareTypes(DaleiWdFenxi.this)
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
						DaleiWdFenxi.this, 
						xiaoleiEt, 
						CommonDataUtils.getType1s(DaleiWdFenxi.this)
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
						DialogUtils.makeListDialog(DaleiWdFenxi.this, zhutiEt, CommonDataUtils.getThemes(DaleiWdFenxi.this));
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
			where.append(" and style = '"+zhutiStr+"' ");
		}
		
		if(!TextUtils.isEmpty(boduanStr) && !("=====全部=====".equals(boduanStr))) {
			where.append(" and state = '"+ CommonQueryUtils.getStateByName(DaleiWdFenxi.this, boduanStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiStr) && !("=====全部=====".equals(daleiStr))) {
			where.append(" and waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(DaleiWdFenxi.this, daleiStr)+"' ");
		}
		
		if(!TextUtils.isEmpty(xiaoleiStr) && !("=====全部=====".equals(xiaoleiStr))) {
			where.append(" and id = '"+CommonQueryUtils.getIdByType1(DaleiWdFenxi.this, xiaoleiStr)+"' ");
		}
		
		return where.toString();
	}
}
