package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareGroup;
import com.as.order.R;
import com.as.order.dao.DapeiOrderDAO;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.FileUtils;
import com.as.ui.utils.ListViewUtils;

public class DapeiOrderDetailActivity extends AbstractActivity {
	
	private static final String TAG = "DapeiOrderDetailActivity";

	private LinearLayout detailLayout;
	private BaseAdapter mAdapter;
	//上一个Activity穿过来的dao，从中获取搭配组号，搭配的所有款式
	private DapeiOrderDAO mDao;
	//左侧大图
	private ImageView dapeiImageView;
	//右侧显示的所有款式的ListView
	private ListView dapeiOrderLv;
	
	private EditText searchText;
	
	private String savedItemCode = "";
	private String savedGroupName = "";
	
	private Button prevBtn;
	private Button nextBtn;
	
	private TextView mDapeiOrderGroupIndex;
	
	List<String> itemCodes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		detailLayout = (LinearLayout) layoutInflater.inflate(R.layout.dapei_order_detail, null);
		mRootView.addView(detailLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
//		if(savedInstanceState != null) {
//			savedItemCode = savedInstanceState.getString("itemCode");
//			savedGroupName = savedInstanceState.getString("groupName");
//		}
		
		searchText = (EditText) findViewById(R.id.dapei_order_query_text);
		
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("查询");
		dapeiImageView = (ImageView) findViewById(R.id.order_by_style_image_iv);
		dapeiOrderLv = (ListView) findViewById(R.id.dapei_order_lv);
	
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		prevBtn.setText("上一组");
		nextBtn.setText("下一组");
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mDapeiOrderGroupIndex = (TextView) findViewById(R.id.dapei_order_group_index);
	}
	
	private void initData(String itemCode, String groupName) {
		setTextForTitle(itemCode + "  "+mDao.getGroupName());
		
		Log.e(TAG, "======= init Data: " + itemCode);
		Bitmap[] itemImgs = FileUtils.getBitmapsFileCode(DapeiOrderDetailActivity.this, itemCode);
		if(itemImgs != null && itemImgs.length > 0) {
			dapeiImageView.setImageBitmap(itemImgs[0]);
		}
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout itemLayout = (LinearLayout) layoutInflater.inflate(R.layout.dapei_order_detail_item, null);
				ImageView iv = (ImageView) itemLayout.findViewById(R.id.dapei_order_detail_item_iv);
				Log.e(TAG, "current item image: " + mDao.getWareCodes().get(position).getWarecode());
				Bitmap[] imgs = FileUtils.getBitmapsFileCode(DapeiOrderDetailActivity.this, mDao.getWareCodes().get(position).getSpecification());
				if(imgs != null && imgs.length>0) {
					iv.setImageBitmap(imgs[0]);
				}
				ListView itemLv = (ListView) itemLayout.findViewById(R.id.dapei_order_detail_item_lv);
				itemLv.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
						"编号", "款号", "类别", "零售价", "订量"
				}, DapeiOrderDetailActivity.this));
				final int currPos = position;
				itemLv.setAdapter(new BaseAdapter() {
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						Log.e(TAG, "current ============== pos: " + currPos);
						SaWareCode sawarecode = mDao.getWareCodes().get(currPos);
						LinearLayout insideItemRow = (LinearLayout) ListViewUtils.generateRow(new String[]{
								sawarecode.warecode, 
								sawarecode.specification, 
								SaWareCode.getSaWareTypeName(DapeiOrderDetailActivity.this, sawarecode.waretypeid), 
								sawarecode.retailprice+"", 
								SaWareCode.getWareNum(DapeiOrderDetailActivity.this, sawarecode.warecode)+""
						}, DapeiOrderDetailActivity.this);
						return insideItemRow;
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
						return 1;
					}
				});
				itemLv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						SaWareCode sawarecode = mDao.getWareCodes().get(currPos);
						Intent intent = new Intent(DapeiOrderDetailActivity.this, OrderByStyleActivity.class);
						intent.putExtra("style_code", sawarecode.specification);
						startActivity(intent);
					}
				});
				return itemLayout;
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
				return mDao.getWareCodes().size();
//				return 3;
			}
		};
		
		dapeiOrderLv.setAdapter(mAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String from = intent.getStringExtra("from");
		if("o".equals(from)) {
			getItemCodesByWareCode(intent.getStringExtra("warecode"));
			if(itemCodes.size() > 0) {
				searchText.setText(itemCodes.get(0));
			}
			mDapeiOrderGroupIndex.setText((itemCodes.indexOf(searchText.getText().toString().trim()) + 1)+ "/" + itemCodes.size());
			createDaopeiOrderDao(searchText.getText().toString().trim());
			initData(searchText.getText().toString().trim(), "");
		} 
		if("d".equals(from)) {
			getAllItemCodes();
			searchText.setText(intent.getStringExtra("itemCode"));
			mDapeiOrderGroupIndex.setText((itemCodes.indexOf(searchText.getText().toString().trim()) + 1)+ "/" + itemCodes.size());
			createDaopeiOrderDao(searchText.getText().toString().trim());
			initData(searchText.getText().toString().trim(), "");
		}
//		if(TextUtils.isEmpty(savedItemCode)) {
//			savedItemCode = intent.getStringExtra("itemCode");
//			savedGroupName = intent.getStringExtra("groupName");
//		} 
//		createDaopeiOrderDao(savedItemCode);
//		initData(savedItemCode, savedGroupName);
	}
	
	private void getItemCodesByWareCode(String warecode) {
		String sql = " select distinct itemcode from sawaregroup where warecode = '"+warecode+"' order by itemcode ";
		List<String> itemCode = new ArrayList<String>();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DapeiOrderDetailActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					itemCode.add(cursor.getString(0));
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
		itemCodes = itemCode;
	}
	
	private void getAllItemCodes() {
		String sql = " select distinct itemcode from sawaregroup order by itemcode ";
		List<String> itemCode = new ArrayList<String>();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DapeiOrderDetailActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					itemCode.add(cursor.getString(0));
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
		itemCodes = itemCode;
	}
	
	@Override
	protected void onPause() {
		savedItemCode = mDao.getItemCode();
		savedGroupName = mDao.getGroupName();
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("itemCode", mDao.getItemCode());
		outState.putString("groupName", mDao.getGroupName());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		savedItemCode = savedInstanceState.getString("itemCode");
		savedGroupName = savedInstanceState.getString("groupName");
		Log.e(TAG, "== savedItemCode: " + savedItemCode + ", savedGroupName: " + savedGroupName);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			if(TextUtils.isEmpty(searchText.getText().toString().trim())) {
				AlertUtils.toastMsg(DapeiOrderDetailActivity.this, "请输入搭配组编号");
				return;
			} else {
				createDaopeiOrderDao(searchText.getText().toString().trim());
				initData(mDao.getItemCode(), mDao.getGroupName());
			}
			break;
			
		case R.id.prev_page:
			if(itemCodes.indexOf(searchText.getText().toString().trim()) <=0) {
				return;
			}
			searchText.setText(itemCodes.get(itemCodes.indexOf(searchText.getText().toString().trim()) -1));
			
			mDapeiOrderGroupIndex.setText( (itemCodes.indexOf(searchText.getText().toString().trim()) + 1)+ "/" + itemCodes.size());
			
			createDaopeiOrderDao(searchText.getText().toString().trim());
			initData(searchText.getText().toString().trim(), "");
			break;
			
		case R.id.next_page:
			if(itemCodes.indexOf(searchText.getText().toString().trim()) >= (itemCodes.size()-1)) {
				return;
			}
			int index = itemCodes.indexOf(searchText.getText().toString().trim());
			searchText.setText(itemCodes.get(/*itemCodes.indexOf(searchText.getText().toString().trim())*/ index +1));
			
			mDapeiOrderGroupIndex.setText((itemCodes.indexOf(searchText.getText().toString().trim()) + 1)+ "/" + itemCodes.size());
			
			createDaopeiOrderDao(searchText.getText().toString().trim());
			initData(searchText.getText().toString().trim(), "");			
			break;
			
			default:
				break;
		}
	}

	private void createDaopeiOrderDao(String itemCode) {
		String sql = " select itemcode, groupname, warecode from sawaregroup where itemcode = '" + itemCode + "' ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DapeiOrderDetailActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		if(mDao == null) {
			mDao = new DapeiOrderDAO();
		}
		mDao.setItemCode(itemCode);
		mDao.setGroupName(SaWareGroup.getGroupName(DapeiOrderDetailActivity.this, itemCode));
		List<SaWareCode> sawarecodes = mDao.getWareCodes();
		if(sawarecodes == null) {
			sawarecodes = new ArrayList<SaWareCode>();
			mDao.setWareCodes(sawarecodes);
		} else {
			sawarecodes.clear();
		}
		try  {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					SaWareCode sawarecode = SaWareCode.restoreSaWareCodeWithWareCode(DapeiOrderDetailActivity.this, cursor.getString(2));
					sawarecodes.add(sawarecode);
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
