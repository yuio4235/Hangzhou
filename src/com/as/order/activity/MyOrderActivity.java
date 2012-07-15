package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.MyOrderDAO;
import com.as.order.sync.FileUploader;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.AsProgressDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.CommonQueryUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.FileUtils;

public class MyOrderActivity extends AbstractActivity implements OnTouchListener{
	
	private static final String TAG = "MyOrderActivity";

	private LinearLayout myOrder;
	private GridView mGridView;
	
	private List<MyOrderDAO> mDataSet = new ArrayList<MyOrderDAO>();
	BaseAdapter mAdapter;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	int pageNum = 0;
	int totalPage = 0;
	
	private Button prevPageBtn;
	private Button nextPageBtn;
	
	private Button orderListBtn;
	
	private EditText zhutiEt;
	private EditText boduanEt;
	private EditText daleiEt;
	
	private TextView zongjineTv;
	private TextView jineZbTv;
	private TextView jineRateTv;
	private TextView zongDlTv;
	private TextView dlZbTv;
	private TextView dlRateTv;
	
	private boolean isZhutiDialogShow = false;
	private boolean isBoduanDialogShow = false;
	private boolean isDaleiDialogShow = false;
	
	private static final int OPT_YD = 1001;
	private static final int OPT_WD = 1002;
	private static final int OPT_ALL = 1003;
	
	private int totalAmount = 0;
	private int totalPrice = 0;
	
	private Button orderQueryBtn;
	private Button unOrderQueryBtn;
	
	private AsProgressDialog mUpdatingDialog;
	
	private static final int DIALOG_ID_UPLOADING_ORDER = 1001;
	
	private static final int MSG_UPDATE_PROGRESS = 2001;
	private static final int MSG_UPLOAD_ORDER = 2002;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case MSG_UPDATE_PROGRESS:
				mUpdatingDialog.updateProgress((Integer)msg.obj);
				
				if((Integer)msg.obj == 100) {
					dismissDialog(DIALOG_ID_UPLOADING_ORDER);
					AlertUtils.toastMsg(MyOrderActivity.this, "订单已经提交");
				}
				break;
				
			case MSG_UPLOAD_ORDER:
				showDialog(DIALOG_ID_UPLOADING_ORDER);
				mUpdatingDialog.setMax(100);
				mUpdatingDialog.updateProgress(3);
				if(FileUploader.createSaIndentFile(MyOrderActivity.this)) {
					Message msg1 = mHandler.obtainMessage();
					msg1.what = MSG_UPDATE_PROGRESS;
					msg1.obj = 50;
					msg1.sendToTarget();

					if(FileUploader.uploadSaIndent(MyOrderActivity.this)) {
						Message msg2 = mHandler.obtainMessage();
						msg2.what = MSG_UPDATE_PROGRESS;
						msg2.obj = 100;
						msg2.sendToTarget();
					}
				} 
				
				break;
				
				default:
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myOrder = (LinearLayout) layoutInflater.inflate(R.layout.my_order, null);
		mRootView.addView(myOrder, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		orderQueryBtn = (Button) findViewById(R.id.my_order_order_query);
		unOrderQueryBtn = (Button) findViewById(R.id.my_order_un_order_query);
		
		orderQueryBtn.setOnClickListener(this);
		unOrderQueryBtn.setOnClickListener(this);
		
		zongjineTv = (TextView) findViewById(R.id.my_order_zongjine);
		jineZbTv = (TextView ) findViewById(R.id.my_order_jine_zhibiao);
		jineRateTv = (TextView) findViewById(R.id.my_order_jine_rate);
		zongDlTv = (TextView) findViewById(R.id.my_order_zdl);
		dlZbTv = (TextView) findViewById(R.id.my_order_dl_zb);
		dlRateTv = (TextView) findViewById(R.id.my_order_dl_rate);
		
		prevPageBtn = (Button) findViewById(R.id.prev_page);
		nextPageBtn = (Button) findViewById(R.id.next_page);
		
		orderListBtn = (Button) findViewById(R.id.my_order_order_list_btn);
		orderListBtn.setOnClickListener(this);
		
		prevPageBtn.setOnClickListener(this);
		nextPageBtn.setOnClickListener(this);
		
		zhutiEt = (EditText) findViewById(R.id.must_order_theme_et);
		boduanEt = (EditText) findViewById(R.id.must_order_boduan_et);
		daleiEt = (EditText) findViewById(R.id.must_order_pinlei_et);
		
		zhutiEt.setOnTouchListener(this);
		boduanEt.setOnTouchListener(this);
		daleiEt.setOnTouchListener(this);
		
		setTextForLeftTitleBtn(getString(R.string.title_back));
		setTextForTitle("我的订单");
		setTextForTitleRightBtn("提交订单");
		
		mGridView = (GridView) findViewById(R.id.my_order_grid_view);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_my_order));
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = layoutInflater.inflate(R.layout.my_order_item, null);
				ImageView iv = (ImageView) v.findViewById(R.id.my_order_item_iv);
				TextView sepcNoTv = (TextView) v.findViewById(R.id.my_order_item_specno);
				TextView wareNumTv = (TextView) v.findViewById(R.id.my_order_item_warenum);
				MyOrderDAO dao = mDataSet.get(8*pageNum + position);
				sepcNoTv.setText("货号:"+dao.getSpecNo());
				wareNumTv.setText("数量:" + dao.getWareNum());
				Bitmap[] imgs = FileUtils.getBitmapsFileCode(MyOrderActivity.this, dao.getSpecNo());
				if(imgs != null && imgs.length > 0) {
					iv.setImageBitmap(imgs[0]);
				}
				return v;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return position;
			}
			
			@Override
			public int getCount() {
				int count = 0;
				if(pageNum == 0) {
					count = mDataSet.size() < 8 ? mDataSet.size() : 8;
				}
				
				if(pageNum > 0) {
					if((pageNum + 1)*8 > mDataSet.size()) {
						
						count = mDataSet.size()%8;
					} else {
						count = 8;
					}
				}
				return count;
			}
		};
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				MyOrderDAO dao = mDataSet.get(8*pageNum + position);
				Intent intent = new Intent(MyOrderActivity.this, OrderByStyleActivity.class);
				intent.putExtra("style_code", dao.getSpecNo()+"");
				startActivity(intent);
			}
		});
	}
	
	private void initData(String where, int opt) {
		if(mDataSet == null) {
			mDataSet = new ArrayList<MyOrderDAO>();
		}
		
		String wareNumCon = "";
		if(opt == OPT_ALL) {
			wareNumCon = "";
		} else if(opt == OPT_YD) {
			wareNumCon = " saindent.warenum > 0  and ";
		} else if(opt == OPT_WD) {
			wareNumCon = " saindent.warenum = 0 and ";
		}
//		totalAmount = 0;
//		totalPrice = 0;
		mDataSet.clear();
		String sql = " select saindent.[warecode], sum(saindent.[warenum]) wareNum, sawarecode.[specification], sawarecode.retailprice "
				+ " from saindent, sawarecode "
				+ " where "+wareNumCon+" saindent.[warecode] = sawarecode.[warecode] and saindent.[departcode] = ? " + (TextUtils.isEmpty(where) ? "" : where)
				+ " group by saindent.[warecode] ";
		Log.e(TAG, "sql: " + sql);
		SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(this);
		Cursor cursor = db.rawQuery(sql, new String[]{sp.getString("user_account", "")});
		try {
			if(cursor != null && cursor.moveToFirst()) {
				totalPage = (cursor.getCount()%10 == 0)? cursor.getCount()/8 : cursor.getCount()/8 + 1;
				while(!cursor.isAfterLast()) {
					MyOrderDAO dao = new MyOrderDAO();
					dao.setSpecNo(cursor.getString(2));
					dao.setWarecode(cursor.getString(0));
					dao.setWareNum(cursor.getInt(1));
					mDataSet.add(dao);
//					totalAmount += cursor.getInt(1);
//					totalPrice += (cursor.getInt(1)*cursor.getInt(3));
					cursor.moveToNext();
				}
//				zongjineTv.setText("总订额: " + totalPrice+"");
//				zongDlTv.setText("总订量: " + totalAmount+"");
//				
//				SharedPreferences spp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
//				jineZbTv.setText("指标: " + spp.getString("jinge", "0")+"");
//				dlZbTv.setText("指标: " +spp.getString("shuliang", "0")+"");
//				
//				jineRateTv.setText("完成率: "+(formatter.format(Double.valueOf(totalPrice)/Integer.parseInt(spp.getString("jinge", "0"))*100) + "%"));
//				dlRateTv.setText("完成率: "+ formatter.format((Double.valueOf(totalAmount)/Integer.parseInt(spp.getString("shuliang", "0")))*100) + "%");
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	
	
	private void initInfo(String where, int opt) {
		String wareNumCon = "";
		if(opt == OPT_ALL) {
			wareNumCon = "";
		} else if(opt == OPT_YD) {
			wareNumCon = " saindent.warenum > 0  and ";
		} else if(opt == OPT_WD) {
			wareNumCon = " saindent.warenum = 0 and ";
		}
		totalAmount = 0;
		totalPrice = 0;
		String sql = " select saindent.[warecode], sum(saindent.[warenum]) wareNum, sawarecode.[specification], sawarecode.retailprice "
				+ " from saindent, sawarecode "
				+ " where "+wareNumCon+" saindent.[warecode] = sawarecode.[warecode] and saindent.[departcode] = ? " + (TextUtils.isEmpty(where) ? "" : where)
				+ " group by saindent.[warecode] ";
		SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(this);
		Log.e(TAG, "=================== myorder: user_account: " + sp.getString("user_account", ""));
		Cursor cursor = db.rawQuery(sql, new String[]{sp.getString("user_account", "")});
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					totalAmount += cursor.getInt(1);
					totalPrice += (cursor.getInt(1)*cursor.getInt(3));
					cursor.moveToNext();
				}
				zongjineTv.setText("总订额: " + totalPrice+"");
				zongDlTv.setText("总订量: " + totalAmount+"");
				
				SharedPreferences spp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
				jineZbTv.setText("指标: " + spp.getString("jinge", "0")+"");
				dlZbTv.setText("指标: " +spp.getString("shuliang", "0")+"");
				
				jineRateTv.setText("完成率: "+(formatter.format(Double.valueOf(totalPrice)/Integer.parseInt(spp.getString("jinge", "0"))*100) + "%"));
				dlRateTv.setText("完成率: "+ formatter.format((Double.valueOf(totalAmount)/Integer.parseInt(spp.getString("shuliang", "0")))*100) + "%");
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
	protected void onResume() {
		super.onResume();
		initData("", OPT_ALL);
		initInfo("", OPT_ALL);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_UPLOAD_ORDER;
			msg.sendToTarget();
			break;
			
		case R.id.prev_page:
			if(pageNum <= 0) {
				return;
			} else {
				pageNum --;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(pageNum>=totalPage-1) {
				return;
			} else {
				pageNum ++;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.my_order_order_list_btn:
			Intent listIntent = new Intent(MyOrderActivity.this, MyOrderListActivity.class);
			startActivity(listIntent);
			break;
			
		case R.id.my_order_order_query:
			initData(getWhere(), OPT_YD);
			break;
			
		case R.id.my_order_un_order_query:
			initData(getWhere(), OPT_WD);
			break;
			
			default:
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.must_order_theme_et:
			if(!isZhutiDialogShow) {
				final AsListDialog zhutiListDialog = DialogUtils.makeListDialog(MyOrderActivity.this, zhutiEt, CommonDataUtils.getThemes(MyOrderActivity.this));
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
				final AsListDialog boduanDialog = DialogUtils.makeListDialog(MyOrderActivity.this, boduanEt, CommonDataUtils.getBoduan(MyOrderActivity.this));
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
				final AsListDialog daleiDialog = DialogUtils.makeListDialog(MyOrderActivity.this, daleiEt, CommonDataUtils.getWareTypes(MyOrderActivity.this));
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

	private String getWhere() {
		StringBuffer sb = new StringBuffer();
		if(!TextUtils.isEmpty(zhutiEt.getText().toString().trim()) && !zhutiEt.getText().toString().contains("全部")) {
			sb.append(" and  sawarecode.style = '"+ zhutiEt.getText().toString().trim() +"' ");
		}
		
		if(!TextUtils.isEmpty(boduanEt.getText().toString().trim()) && !boduanEt.getText().toString().contains("全部")) {
			sb.append(" and sawarecode.state = '"+ CommonQueryUtils.getStateByName(MyOrderActivity.this, boduanEt.getText().toString().trim())+"' ");
		}
		
		if(!TextUtils.isEmpty(daleiEt.getText().toString().trim())  && !daleiEt.getText().toString().contains("全部")) {
			sb.append(" and sawarecode.waretypeid = '"+CommonQueryUtils.getWareTypeIdByName(MyOrderActivity.this, daleiEt.getText().toString().trim())+"'" );
		}
		
		return sb.toString();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_ID_UPLOADING_ORDER:
			mUpdatingDialog = new AsProgressDialog(MyOrderActivity.this, R.style.AsDialog, "提交订单");
			return mUpdatingDialog;
		}
		return null;
	}
}
