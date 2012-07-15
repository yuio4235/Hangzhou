package com.as.order.activity;

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
import com.as.order.dao.OrderListDAO;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;

public class MyOrderListActivity extends AbstractActivity implements OnTouchListener{

	private static final String TAG = "MyOrderListActivity";
	
	private LinearLayout mLayout;
	
	private Button prevPage;
	private Button nextPage;
	private ListView mList;
	
	private EditText zhutiEt;
	private EditText boduanEt;
	private EditText daleiEt;
	
	private boolean isZhutiDialogShow = false;
	private boolean isBoduanDialogShow = false;
	private boolean isDaleiDialogShow = false;
	
	private BaseAdapter mAdapter;
	private List<OrderListDAO> mData = new ArrayList<OrderListDAO>() ;
	private OrderListDAO[] mDataArr;
	
	private int pageSize = 15;
	private int totalPage = 0;
	private int pageNum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.my_order_list, null);
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		prevPage = (Button) findViewById(R.id.prev_page);
		nextPage =(Button) findViewById(R.id.next_page);
		
		prevPage.setOnClickListener(this);
		nextPage.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"大类", "小类", "波段", "编号", "款号", "零售价", "订量", "总金额"
		}, MyOrderListActivity.this));
		
		
		zhutiEt = (EditText) findViewById(R.id.must_order_theme_et);
		boduanEt = (EditText) findViewById(R.id.must_order_boduan_et);
		daleiEt = (EditText) findViewById(R.id.must_order_pinlei_et);
		
		zhutiEt.setOnTouchListener(this);
		boduanEt.setOnTouchListener(this);
		daleiEt.setOnTouchListener(this);
		
		setTextForLeftTitleBtn("返回");
		setTextForTitle("订单列表");
		setTextForTitleRightBtn("查询");
	}
	
	private void initData() {
		if(mData != null) {
			mDataArr = new OrderListDAO[mData.size()];
			mData.toArray(mDataArr);
		}
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generateRow(new String[]{
						mDataArr[pageNum*pageSize+position].dalei,
						mDataArr[pageNum*pageSize+position].xiaolei,
						mDataArr[pageNum*pageSize+position].boduan,
						mDataArr[pageNum*pageSize+position].bianhao,
						mDataArr[pageNum*pageSize+position].kuanhao,
						mDataArr[pageNum*pageSize+position].price,
						mDataArr[pageNum*pageSize+position].dingliang,
						mDataArr[pageNum*pageSize+position].sumPrice
				}, MyOrderListActivity.this);
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
				if(mData.size() < pageSize) {
					return mData.size();
				} else if((pageNum+1)*pageSize > mData.size()) {
					return mData.size()%pageSize;
				}
				return pageSize;
			}
		};
		
		mList.setAdapter(mAdapter);
		
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position ==0) {
					return;
				}
				
				OrderListDAO dao = mData.get(pageNum*pageSize + position-1);
				Intent in = new Intent(MyOrderListActivity.this, OrderByStyleActivity.class);
				in.putExtra("style_code", dao.kuanhao);
				startActivity(in);
			}
		});
		
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			getData(getWhere());
			initData();
			break;
			
		case R.id.prev_page:
			if(pageNum == 0) {
				return;
			} else {
				pageNum --;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(pageNum >= totalPage -1) {
				return;
			} else {
				pageNum ++;
				mAdapter.notifyDataSetChanged();
			}
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData("");
		initData();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_theme_et:
			if(!isZhutiDialogShow) {
				final AsListDialog zhutiListDialog = DialogUtils.makeListDialog(MyOrderListActivity.this, zhutiEt, CommonDataUtils.getThemes(MyOrderListActivity.this));
				zhutiListDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						zhutiEt.setText(text);
						zhutiListDialog.dismiss();
						isZhutiDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						zhutiEt.setText("");
						zhutiListDialog.dismiss();
						isZhutiDialogShow = false;
					}
				});
				zhutiListDialog.show();
				isZhutiDialogShow = true;
			}
			break;
			
		case R.id.must_order_boduan_et:
			if(!isBoduanDialogShow) {
				final AsListDialog boduanDialog = DialogUtils.makeListDialog(MyOrderListActivity.this, boduanEt, CommonDataUtils.getBoduan(MyOrderListActivity.this));
				boduanDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						boduanEt.setText(text);
						boduanDialog.dismiss();
						isBoduanDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						boduanEt.setText("");
						boduanDialog.dismiss();
						isBoduanDialogShow = false;
					}
				});
				boduanDialog.show();
				isBoduanDialogShow = true;
			}
			break;
			
		case R.id.must_order_pinlei_et:
			if(!isDaleiDialogShow) {
				final AsListDialog daleiDialog = DialogUtils.makeListDialog(MyOrderListActivity.this, daleiEt, CommonDataUtils.getWareTypes(MyOrderListActivity.this));
				daleiDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						daleiEt.setText(text);
						daleiDialog.dismiss();
						isDaleiDialogShow = false;
					}
					
					@Override
					public void onCancel() {
						daleiEt.setText("");
						daleiDialog.dismiss();
						isDaleiDialogShow = false;
					}
				});
				daleiDialog.show();
				isDaleiDialogShow = true;
			}
			break;
		}
		return false;
	}

	private void getData(String where) {
		mData.clear();
		mDataArr = null;
		String sql = " SELECT sawaretype.WareTypeName,type1.Type1,saPara.ParaConnent,pagenum,specification,retailprice,SUM(warenum),retailprice*SUM(warenum) "
			+ " from   sawarecode,saindent,sawaretype,type1,saPara "
			+ " where   sawarecode.warecode =saindent.warecode  and "
			+ "         sawarecode.waretypeid = sawaretype.WareTypeID and "
			+ "         sawarecode.id = type1.ID                      and "
			+ "         sawarecode.state = saPara.Para         and "
			+ "         saPara.ParaType =  'PD' "
			+ (TextUtils.isEmpty(where) ? "" : " " + where)
			+ " group by sawaretype.WareTypeName,type1.Type1,saPara.ParaConnent,pagenum,specification,retailprice "
			+ " order by  specification ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(MyOrderListActivity.this);
		if(db!=null) {
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor != null) {
				Log.e(TAG, "======= count: " + cursor.getCount());
				totalPage = (cursor.getCount()%pageSize==0) ? cursor.getCount()/pageSize : (cursor.getCount()/pageSize + 1);
				try {
					if(cursor.moveToFirst()) {
						while(!cursor.isAfterLast()) {
							OrderListDAO dao = new OrderListDAO();
							dao.setDalei(cursor.getString(0));
							dao.setXiaolei(cursor.getString(1));
							dao.setBoduan(cursor.getString(2));
							dao.setBianhao(cursor.getString(3));
							dao.setKuanhao(cursor.getString(4));
							dao.setPrice(cursor.getString(5));
							dao.setDingliang(cursor.getString(6));
							dao.setSumPrice(cursor.getString(7));
							mData.add(dao);
							cursor.moveToNext();							
						}
					}
					Log.e(TAG, "=== mData size: " + mData.size());
				} finally {
					cursor.close();
					db.close();
				}
			}
		}
	}
	
	private String getWhere() {
		StringBuffer sb = new StringBuffer();
		
		if(!TextUtils.isEmpty(zhutiEt.getText().toString().trim())) {
			sb.append(" and  sawarecode.style = '"+ zhutiEt.getText().toString().trim() +"' ");
		}
		
		if(!TextUtils.isEmpty(boduanEt.getText().toString().trim())) {
			sb.append(" and sawarecode.state = '"+ CommonQueryUtils.getStateByName(MyOrderListActivity.this, boduanEt.getText().toString().trim())+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiEt.getText().toString().trim())) {
			sb.append(" and sawarecode.waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(MyOrderListActivity.this, daleiEt.getText().toString().trim())+"'" );
		}
		
		return sb.toString();
	}
}
