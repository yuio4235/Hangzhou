package com.as.order.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.order.R;
import com.as.order.preference.AsSettings;
import com.as.order.sync.FileUploader;
import com.as.ui.utils.UserUtils;

public class MainActivity extends AbstractActivity {

	private static final String TAG = "MainActivity";
	private LinearLayout main;
	private static final int ID_LOGOUT = R.id.title_btn_right;
	private static final int ID_ORDER_BY_STYLE = R.id.order_by_style;
	private static final int ID_CHENPIN_CHENLIE = R.id.chenpin_chenlie;
	private static final int ID_MUST_ORDER = R.id.must_order;
	private static final int ID_STYLE_REVIEW = R.id.style_review;
	private static final int ID_DAPEI_ORDER = R.id.dapei_order;
	private static final int ID_ORDER_ANALYSIS = R.id.order_analysis ;
	private static final int ID_THEME_ORDER = R.id.theme_order;
	private static final int ID_SUMMRIZE_ANALYSIS = R.id.summrize_analysis;
	private static final int ID_MY_ORDER = R.id.my_order;
	private static final int ID_ORDER_SETTING = R.id.order_setting;
	
	private TextView customerName;
	private TextView customerOrderAmount;
	
	private Button orderByStyleBtn;
	private Button chenpinChenlieBtn;
	private Button mustOrderBtn;
	private Button styleRevicewBtn;
	private Button dapeiOrderBtn;
	private Button orderAnalysisBtn;
	private Button themeOrderBtn;
	private Button summrizeAnalysisBtn;
	private Button myOrderBtn;
	private Button orderSetttingBtn;
	
	private Button orderCommitBtn;
	private Button peimaSetting;
	
	private AlertDialog ad;
	
	private static final int MSG_UPLOAD_SUCC = 1;
	private static final int MSG_CREATE_SAINDENT_ERR = 2;
	private static final int MSG_NETWORK_ERR = 3;
	private static final int MSG_UPLOAD_SAINDNET = 4;
	private static final int MSG_ADMIN_LOGIN = 5;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case MSG_UPLOAD_SUCC:
				AlertDialog.Builder succAb = new AlertDialog.Builder(MainActivity.this);
				succAb.setIcon(R.drawable.logo);
				succAb.setTitle("提示");
				succAb.setMessage("订单已提交");
				succAb.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
						Editor et = spp.edit();
						et.putBoolean("order_commit", true);
						et.putBoolean("order_locked", true);
						et.commit();
						dialog.dismiss();
						finish();
					}
				});
				AlertDialog succDialog = succAb.create();
				succDialog.show();
				break;
				
			case MSG_CREATE_SAINDENT_ERR:
			case MSG_NETWORK_ERR:
				AlertDialog.Builder errAb = new AlertDialog.Builder(MainActivity.this);
				errAb.setIcon(R.drawable.logo);
				errAb.setTitle("提示");
				errAb.setMessage((String)msg.obj);
				errAb.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog errDlg = errAb.create();
				errDlg.show();				
				break;
				
			case MSG_UPLOAD_SAINDNET:
				AlertDialog.Builder adb1 = new AlertDialog.Builder(MainActivity.this);
				adb1.setIcon(R.drawable.logo);
				adb1.setTitle("提交订单");
				adb1.setMessage("总订量: " + AsSettings.getTotalWareNum(MainActivity.this) + "  " + " 总金额: " + AsSettings.getTotalPrice(MainActivity.this));
				adb1.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(FileUploader.createSaIndentFile(MainActivity.this)) {
							
							if(FileUploader.uploadSaIndent(MainActivity.this)) {
								Message msg = mHandler.obtainMessage();
								msg.what = MSG_UPLOAD_SUCC;
								msg.sendToTarget();
							} else {
								Message msg = mHandler.obtainMessage();
								msg.what = MSG_NETWORK_ERR;
								msg.obj = "网络出错";
								msg.sendToTarget();
							}
						} else {
							Message msg = mHandler.obtainMessage();
							msg.what = MSG_CREATE_SAINDENT_ERR;
							msg.obj = "导出订单出错";
							msg.sendToTarget();
						}
						dialog.dismiss();
					}
				});
				adb1.setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog add = adb1.create();
				add.show();
				break;
				
			case MSG_ADMIN_LOGIN:
				AlertDialog.Builder adminB = new AlertDialog.Builder(MainActivity.this);
				adminB.setIcon(R.drawable.logo);
				adminB.setTitle("提示");
				adminB.setMessage("设置功能只能由管理员使用");
				adminB.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog adminDlg = adminB.create();
				adminDlg.show();
				break;
				
				default:
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (LinearLayout) layoutInflater.inflate(R.layout.main, null);
		mRootView.addView(main, FF);
		
		customerName = (TextView) findViewById(R.id.main_customer_name);
		customerOrderAmount = (TextView) findViewById(R.id.main_customer_order_amount);
	
		customerName.setText(Html.fromHtml("尊敬的&nbsp;<u><i><font color=\"blue\">测试</font></i></u>&nbsp;用户"));
		customerOrderAmount.setText(Html.fromHtml("您的订货指标是:<u><i><font color=\"red\">100000</font></i></u>元"));
		setTextForTitleRightBtn(this.getString(R.string.app_logout));
		
		orderByStyleBtn = (Button) findViewById(R.id.order_by_style);
		chenpinChenlieBtn = (Button) findViewById(R.id.chenpin_chenlie);
		mustOrderBtn = (Button) findViewById(R.id.must_order);
		styleRevicewBtn = (Button) findViewById(R.id.style_review);
		dapeiOrderBtn = (Button) findViewById(R.id.dapei_order);
		orderAnalysisBtn = (Button) findViewById(R.id.order_analysis);
		themeOrderBtn = (Button) findViewById(R.id.theme_order);
		summrizeAnalysisBtn = (Button) findViewById(R.id.summrize_analysis);
		myOrderBtn = (Button) findViewById(R.id.my_order);
		orderSetttingBtn = (Button) findViewById(R.id.order_setting);
		
		orderCommitBtn = (Button) findViewById(R.id.order_commit);
		peimaSetting = (Button) findViewById(R.id.main_peima_setting);
		
		orderByStyleBtn.setOnClickListener(this);
		chenpinChenlieBtn.setOnClickListener(this);
		mustOrderBtn.setOnClickListener(this);
		styleRevicewBtn.setOnClickListener(this);
		dapeiOrderBtn.setOnClickListener(this);
		orderAnalysisBtn.setOnClickListener(this);
		themeOrderBtn.setOnClickListener(this);
		myOrderBtn.setOnClickListener(this);
		summrizeAnalysisBtn.setOnClickListener(this);
		orderSetttingBtn.setOnClickListener(this);
		orderCommitBtn.setOnClickListener(this);
		peimaSetting.setOnClickListener(this);
		
		
		SharedPreferences sppp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		boolean order_locked = sppp.getBoolean("order_locked", false);
		if(order_locked) {
			orderCommitBtn.setClickable(false);
		} else {
			orderCommitBtn.setClickable(true);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case ID_LOGOUT:
			finish();
			 break;
			 
		case ID_ORDER_BY_STYLE:
			Intent orderByStyleIntent = new Intent(MainActivity.this, OrderByStyleActivity.class);
			startActivity(orderByStyleIntent);
			break;
			
		case ID_CHENPIN_CHENLIE:
			Intent orderChenpinChenlieIntent = new Intent(MainActivity.this, ChenpinChenlieActivity.class);
			startActivity(orderChenpinChenlieIntent);
			break;
			
		case ID_MUST_ORDER:
			Intent mustOrderIntent = new Intent(MainActivity.this, MustOrderActivity.class);
			startActivity(mustOrderIntent);
			break;
			
		case ID_STYLE_REVIEW:
			Intent styleReviewIntent = new Intent(MainActivity.this, StyleReviewActivity.class);
			startActivity(styleReviewIntent);
			break;
			
		case ID_DAPEI_ORDER:
			Intent dapeiIntent = new Intent(MainActivity.this, DapeiOrderActivity.class);
			startActivity(dapeiIntent);
			break;
			
		case ID_ORDER_ANALYSIS:
			Intent orderAnalysisIntent = new Intent(MainActivity.this, OrderAnalysisActivity.class);
			startActivity(orderAnalysisIntent);
			break;
			
		case ID_THEME_ORDER:
			Intent themeOrderIntent = new Intent(MainActivity.this, ThemeOrderActivity.class);
			startActivity(themeOrderIntent);
			break;
			
		case ID_SUMMRIZE_ANALYSIS:
			Intent summrizeAnalysisIntent = new Intent(MainActivity.this, SummrizeAnalysisActivity.class);
			startActivity(summrizeAnalysisIntent);
			break;
			
		case ID_MY_ORDER:
			Intent myOrderIntent = new Intent(MainActivity.this, MyOrderActivity.class);
			startActivity(myOrderIntent);
			break;
			
		case ID_ORDER_SETTING:
			if(!"dln".equals(UserUtils.getUserAccount(MainActivity.this))) {
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_ADMIN_LOGIN;
				msg.sendToTarget();
				return;
			}
			Intent orderSetttingIntent = new Intent(MainActivity.this, AsSettings.class);
			startActivity(orderSetttingIntent);
			break;
			
		case R.id.order_commit:
			AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
			adb.setIcon(R.drawable.logo);
			adb.setTitle("提交订单");
			adb.setMessage("提交订单后，不能修改订单，确认提交?");
			adb.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_UPLOAD_SAINDNET;
					msg.sendToTarget();
					dialog.dismiss();
				}
			});
			adb.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			ad = adb.create();
			ad.show();
			break;
			
		case R.id.main_peima_setting:
			Intent peimaSettingInent = new Intent(MainActivity.this, PeimaSetting.class);
			startActivity(peimaSettingInent);
			break;
			 
			 default:
				 break;
		}
	}

}
