package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.as.order.dao.MustOrderDAO;
import com.as.order.ui.AsDialog;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.DialogListener;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;

public class MustOrderActivity extends AbstractActivity implements OnTouchListener{

	private LinearLayout mustOrder;
	private ListView mustOrderList;
	
	private EditText boduanEt;
	private EditText themeEt;
	private EditText pinleiEt;
	private EditText xiaoleiEt;
	private EditText xilieEt;
	private EditText bianhaoEt;
	
	private boolean isBoduanListDialogShow = false;
	private boolean isThemeListDialogShow = false;
	private boolean isPinleiListDialogShow = false;
	private boolean isXiaoleiListDialogShow = false;
	private boolean isXilieListDialogShow = false;
	private boolean isBianhaoListDialogShow = false;
	
	private static final int ID_DATA_LOADING_DIALOG = 1001;
	
	ProgressDialog mDataLoading;
	final List<MustOrderDAO> dataset = new ArrayList<MustOrderDAO>();
	BaseAdapter mAdapter;
	
	int pageNum = 0;
	int totalPage = 0;
	
	private Button prevPageBtn;
	private Button nextPageBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mustOrder = (LinearLayout)layoutInflater.inflate(R.layout.must_order, null);
		mRootView.addView(mustOrder, FF);
		mustOrderList = (ListView) findViewById(R.id.must_order_list);
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_must_order));
		setTextForTitleRightBtn("查询");
		
		prevPageBtn = (Button) findViewById(R.id.prev_page);
		nextPageBtn = (Button) findViewById(R.id.next_page);
		
		prevPageBtn.setOnClickListener(this);
		nextPageBtn.setOnClickListener(this);
		
		boduanEt = (EditText) findViewById(R.id.must_order_boduan_et);
		themeEt = (EditText) findViewById(R.id.must_order_theme_et);
		pinleiEt = (EditText) findViewById(R.id.must_order_pinlei_et);
		xiaoleiEt = (EditText) findViewById(R.id.must_order_xiaolei_et);
		xilieEt = (EditText) findViewById(R.id.must_order_xilie_et);
		bianhaoEt = (EditText) findViewById(R.id.must_order_bianhao_et);

		boduanEt.setOnTouchListener(this);
		themeEt.setOnTouchListener(this);
		pinleiEt.setOnTouchListener(this);
		xiaoleiEt.setOnTouchListener(this);
		xilieEt.setOnTouchListener(this);
		bianhaoEt.setOnTouchListener(this);
		
		mustOrderList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"序号", "编号", "上柜日期", "波段", "品类", "主题", "货号", "价格", "已订量"
		}, MustOrderActivity.this));
//		dataset.add(new String[]{"1", "002", "2012-3-1", "第一波", "裤子", "都市女人", "2103118", "198", "50"});
//		dataset.add(new String[]{"2", "005", "2012-3-5", "第一波", "毛衣", "都市女人", "2103108", "258", "35"});
//		dataset.add(new String[]{"3", "008", "2012-4-5", "第二波", "上衣", "都市女人", "2103008", "238", "32"});
//		dataset.add(new String[]{"4", "012", "2012-4-5", "第五波", "外套", "步行者", "2113118", "458", "21"});
//		dataset.add(new String[]{"5", "015", "2012-5-5", "第五波", "大衣", "步行者", "2203119", "519", "23"});
//		dataset.add(new String[]{"6", "018", "2012-5-5", "第六波", "皮衣", "步行者", "2123115", "698", "12"});
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generateMustOrderItem(dataset.get(pageNum*15 + position), MustOrderActivity.this);
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
				if(dataset.size() < 15) {
					return dataset.size();
				} else if((pageNum + 1)*15 > dataset.size()) {
					return dataset.size()%15;
				}
				return 15;
			}
		};
		mustOrderList.setAdapter(mAdapter);
		mustOrderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				Intent intent = new Intent(MustOrderActivity.this, OrderByStyleActivity.class);
				MustOrderDAO dao = dataset.get(pageNum*15 + (position -1));
				intent.putExtra("style_code", dao.getSpecNo()+"");
				startActivity(intent);
			}
		});
//		initData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData(getWhere());
	}
	
	private void queryByCond(String where) {
		initData(where);
		mAdapter.notifyDataSetChanged();
	}
	
	private void initData(String where )	 {
		showDialog(ID_DATA_LOADING_DIALOG);
		String SQL = " select a.specification, a.date3, a.state, a.paraconnent, a.waretypeid, a.waretypename, a.style, a.specification, a.retailprice, b.wareNum, a.pagenum"
				+ " from "
				+ " ( select sawarecode.[warecode], sawarecode.[specification], sawarecode.[date3], sawarecode.[state], sapara.[paraconnent], sawarecode.[waretypeid], sawaretype.[waretypename], sawarecode.[style], sawarecode.[specification], sawarecode.[retailprice], sawarecode.pagenum "
				+ " from sawarecode "
				+ " left join sapara on sawarecode.[state] = sapara.[para] "
				+ " left join sawaretype on sawarecode.[waretypeid] = sawaretype.[waretypeid] "
				+ " where sapara.[paratype] = 'PD' "+ (TextUtils.isEmpty(where) ? "" :  where )+") a "
				+ " left join "
				+ " (select warecode,  sum(wareNum) wareNum from saindent group by warecode) b "
				+ " on a.warecode = b.warecode ";
		Log.e("==", "sql: " + SQL);
		SQLiteDatabase db  = AsProvider.getWriteableDatabase(MustOrderActivity.this);
		Cursor cursor = db.rawQuery(SQL, null);
		if(dataset != null) {
			dataset.clear();
		}
		try {
			if(cursor != null && cursor.moveToFirst()) {
				totalPage = (cursor.getCount()%15 == 0) ? cursor.getCount()/15 : (cursor.getCount()/15 + 1);
				int serial = 0;
				while(!cursor.isAfterLast()) {
					MustOrderDAO dao = new MustOrderDAO();
					dao.setSerialNo(++serial);
					if(serial <= 10) {
						Log.e("", "========== spec no: " + cursor.getString(0));
					}
					dao.setSpecNo(TextUtils.isEmpty(cursor.getString(0)) ? "" : cursor.getString(0));
					dao.setDate3(TextUtils.isEmpty(cursor.getString(1)) ? "" : cursor.getString(1));
					dao.setWave(TextUtils.isEmpty(cursor.getString(3)) ? "" : cursor.getString(3));
					dao.setWareType(TextUtils.isEmpty(cursor.getString(5)) ? "" : cursor.getString(5));
					dao.setTheme(TextUtils.isEmpty(cursor.getString(6)) ? "" : cursor.getString(6));
					dao.setHuohao(TextUtils.isEmpty(cursor.getString(10)) ? "" : cursor.getString(10));
					dao.setRetailPrice(TextUtils.isEmpty(cursor.getString(8)) ? "" : cursor.getString(8));
					dao.setWareNum(cursor.getInt(9));
					dataset.add(dao);
					cursor.moveToNext();
				}
				mAdapter.notifyDataSetChanged();
				dismissDialog(ID_DATA_LOADING_DIALOG);
			} else {
				dismissDialog(ID_DATA_LOADING_DIALOG);
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
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.must_order_boduan_et:
			final AsDialog dialog = new AsDialog(MustOrderActivity.this, R.style.AsDialog);
			dialog.setDialogListener(new DialogListener() {
				
				@Override
				public void onOk() {
					dialog.dismiss();
				}
				
				@Override
				public void onCancel() {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
			
//		case R.id.must_order_theme_et:
//			final AsListDialog listDialog = new AsListDialog(MustOrderActivity.this, R.style.AsDialog, new String[]{
//					"测试1", "测试2", "测试3", "测试4"
//			});
//			listDialog.setDialogListener(new ListDialogListener() {
//				
//				@Override
//				public void onClick(String text) {
//					themeEt.setText(text);
//					listDialog.dismiss();
//				}
//				
//				@Override
//				public void onCancel() {
//					listDialog.dismiss();
//				}
//			});
//			listDialog.show();
//			break;
			
		case R.id.prev_page:
			if(pageNum == 0) {
				return;
			} else {
				pageNum--;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(pageNum >= totalPage-1) {
				return;
			} else {
				pageNum ++;
				mAdapter.notifyDataSetChanged(); 
			}
			break;
			
		case R.id.title_btn_right:
			queryByCond(getWhere());
			break;
			
			default:
				break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case ID_DATA_LOADING_DIALOG:
			mDataLoading = new ProgressDialog(MustOrderActivity.this);
			mDataLoading.setMessage("正在加载数据...");
			return mDataLoading;
			
			default:
				break;
		}
		return null;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_boduan_et:
			if(!isBoduanListDialogShow) {
				final AsListDialog boduanListDialog = DialogUtils.makeListDialog(MustOrderActivity.this, boduanEt, CommonDataUtils.getBoduan(MustOrderActivity.this));
				boduanListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						boduanEt.setText(text);
						boduanListDialog.dismiss();
						isBoduanListDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						boduanListDialog.dismiss();
						isBoduanListDialogShow = false;
					}
				});
				boduanListDialog.show();
				isBoduanListDialogShow = true;
			}
			break;
			
		case R.id.must_order_pinlei_et:
			if(!isPinleiListDialogShow) {
				final AsListDialog pinleiListDialog = DialogUtils.makeListDialog(MustOrderActivity.this, themeEt, CommonDataUtils.getWareTypes(MustOrderActivity.this));
				pinleiListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						pinleiEt.setText(text);
						pinleiListDialog.dismiss();
						isPinleiListDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						pinleiListDialog.dismiss();
						isPinleiListDialogShow = false;
					}
				});	
				pinleiListDialog.show();
				isPinleiListDialogShow = true;
			}
			break;
			
		case R.id.must_order_xiaolei_et:
			if(!isXiaoleiListDialogShow) {
				final AsListDialog xiaoleiListDialog = DialogUtils.makeListDialog(MustOrderActivity.this, xiaoleiEt, CommonDataUtils.getType1s(MustOrderActivity.this));
				xiaoleiListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						xiaoleiEt.setText(text);
						xiaoleiListDialog.dismiss();
						isXiaoleiListDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						xiaoleiListDialog.dismiss();
						isXiaoleiListDialogShow = false;
					}
				});
				xiaoleiListDialog.show();
				isXiaoleiListDialogShow = true;
			}
			break;
			
		case R.id.must_order_theme_et:
			Log.e("==", "======= must order theme et ========");
			if(!isThemeListDialogShow) {
				final AsListDialog themeListDialog = DialogUtils.makeListDialog(MustOrderActivity.this, themeEt, CommonDataUtils.getThemes(MustOrderActivity.this));
				themeListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						themeEt.setText(text);
						themeListDialog.dismiss();
						isThemeListDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						themeListDialog.dismiss();
						isThemeListDialogShow = false;
					}
				});
				themeListDialog.show();
				isThemeListDialogShow = true;
			}
			break;
			
		case R.id.must_order_xilie_et:
			if(!isXilieListDialogShow) {
				final AsListDialog xilieListDialog = 
					DialogUtils.makeListDialog(MustOrderActivity.this, xilieEt, CommonDataUtils.getXilie(MustOrderActivity.this));
				xilieListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						xilieEt.setText(text);
						xilieListDialog.dismiss();
						isXilieListDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						xilieListDialog.dismiss();
						isXilieListDialogShow = false;
					}
				});
				xilieListDialog.show();
				isXilieListDialogShow = true;
			}
			break;
			
		case R.id.must_order_bianhao_et:
			if(!isBianhaoListDialogShow) {
				final AsListDialog bianhaoListDialog = 
					DialogUtils.makeListDialog(MustOrderActivity.this, bianhaoEt, CommonDataUtils.getPagenum(MustOrderActivity.this));
				bianhaoListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						bianhaoEt.setText(text);
						bianhaoListDialog.dismiss();
						isBianhaoListDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						bianhaoListDialog.dismiss();
						isBianhaoListDialogShow = false;
					}
				});
				bianhaoListDialog.show();
				isBianhaoListDialogShow = true;
			}
			break;
			
			default:
				break;
		}
		return false;
	}

//	private String getWhere() {
//		StringBuilder where = new StringBuilder();
//		String zhutiStr = themeEt.getText().toString().trim();
//		String boduanStr = boduanEt.getText().toString().trim();
//		String daleiStr = pinleiEt.getText().toString().trim();
//		String xiaoleiStr = xiaoleiEt.getText().toString().trim();
//		
//		if(!TextUtils.isEmpty(zhutiStr)) {
//			where.append(" and type = '"+zhutiStr+"' ");
//		}
//		
//		if(!TextUtils.isEmpty(boduanStr)) {
//			where.append(" and state = '"+ CommonQueryUtils.getStateByName(MustOrderActivity.this, boduanStr)+"' ");
//		}
//		
//		if(!TextUtils.isEmpty(daleiStr)) {
//			where.append(" and waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(MustOrderActivity.this, daleiStr)+"' ");
//		}
//		
//		if(!TextUtils.isEmpty(xiaoleiStr)) {
//			where.append(" and id = '"+CommonQueryUtils.getIdByType1(MustOrderActivity.this, xiaoleiStr)+"' ");
//		}
//		
//		return where.toString();
//	}
	
	private String getWhere() {
		StringBuilder where = new StringBuilder();
		String stateStr = boduanEt.getText().toString().trim();
		String styleStr = themeEt.getText().toString().trim();
		String sawaretypeStr = pinleiEt.getText().toString().trim();
		String idStr = xiaoleiEt.getText().toString().trim();
		String specdefStr = xilieEt.getText().toString().trim();
		String pagenumStr = bianhaoEt.getText().toString().trim();
		
		if(!TextUtils.isEmpty(stateStr) && !(CommonDataUtils.ALL_OPT.equals(stateStr))) {
			where.append(" and sawarecode.state = '"+CommonQueryUtils.getStateByName(MustOrderActivity.this, stateStr)+"' ");
		}
		

		if(!TextUtils.isEmpty(styleStr) && !(CommonDataUtils.ALL_OPT.equals(styleStr))) {
			where.append(" and sawarecode.type = '"+styleStr+"' ");
		}

		if(!TextUtils.isEmpty(sawaretypeStr) && !(CommonDataUtils.ALL_OPT.equals(sawaretypeStr))) {
			where.append(" and sawarecode.waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(MustOrderActivity.this, sawaretypeStr)+"' ");
		}

		if(!TextUtils.isEmpty(idStr) && !(CommonDataUtils.ALL_OPT.equals(idStr))) {
			where.append(" and sawarecode.id = '"+CommonQueryUtils.getIdByType1(MustOrderActivity.this, idStr)+"' ");
		}

		if(!TextUtils.isEmpty(specdefStr) && !(CommonDataUtils.ALL_OPT.equals(specdefStr))) {
			where.append(" and sawarecode.specdef = '"+specdefStr+"' ");
		}

		if(!TextUtils.isEmpty(pagenumStr) && !(CommonDataUtils.ALL_OPT.equals(pagenumStr))) {
			where.append(" and sawarecode.pagenum = '"+ pagenumStr+"' ");
		}

		return where.toString();
	}
}
