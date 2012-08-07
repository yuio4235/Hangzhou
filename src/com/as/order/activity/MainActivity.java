package com.as.order.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.db.provider.AsContent;
import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaColorCode;
import com.as.db.provider.AsContent.SaPara;
import com.as.db.provider.AsContent.SaSizeSet;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareColor;
import com.as.db.provider.AsContent.SaWareGroup;
import com.as.db.provider.AsContent.SaWareSize;
import com.as.db.provider.AsContent.SaWareType;
import com.as.db.provider.AsContent.ShowSize;
import com.as.order.R;
import com.as.order.activity.LoginActivity.DialogMessage;
import com.as.order.preference.AsSettings;
import com.as.order.preference.IndextSetting;
import com.as.order.service.IndentSyncService;
import com.as.order.sync.FileUploader;
import com.as.order.ui.AsProgressDialog;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.Constant;
import com.as.ui.utils.ImageSyncUtils;
import com.as.ui.utils.NetWorkUtils;
import com.as.ui.utils.SaIndentUtils;
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
	
	private ProgressDialog mLoading;
	
	private Timer mTimer;
	
	private TimerTask mTimerTask = new TimerTask() {
		
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = MSG_SYNC;
			mHandler.sendMessage(msg);
		}
	};
	
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
	private Button updatePics;
	private Button updateInfos;
	
	private AlertDialog ad;
	
	private static final int MSG_UPLOAD_SUCC = 1;
	private static final int MSG_CREATE_SAINDENT_ERR = 2;
	private static final int MSG_NETWORK_ERR = 3;
	private static final int MSG_UPLOAD_SAINDNET = 4;
	private static final int MSG_ADMIN_LOGIN = 5;
	private static final int MSG_UPDATE_DOWNLOAD_FILE_PROGRESS = 2001;
	private static final int MSG_UPDATE_IMG = 2002;
	private static final int MSG_UPDATE_INFO = 2003;
	private static final int MSG_INSERT_PROGRESS_DIALG = 2004;
	private static final int MSG_INSERT_DATA = 2005;
	private static final int MSG_SYNC = 2006;
	private static final int MSG_UPLOAD = 2007;
	private static final int MSG_SHOW_LOADING = 2008;
	
	private AsProgressDialog mUpdatingDialog;
	private AsProgressDialog mUpdatingDataDialog;
	
	private static final int DIALOG_ID_DOWNLOADING = 1001;
	private static final int DIALOG_ID_UPDATING_DATA = 1002;
	private static final int DIALOG_ID_COMMIT = 1003;
	
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
				
			case MSG_UPLOAD:
//				showDialog(DIALOG_ID_COMMIT);
//				mLoading = ProgressDialog.show(MainActivity.this, "确认订单", "正在提交订单", true);
//				mLoading.show();
//				Log.e("==", "==========================================================");
				if(FileUploader.createSaIndentFile(MainActivity.this)) {
//					Log.e("==", "======================================================1");
//					mLoading.setProgress(50);
				} else {
//					Log.e("==", "======================================================2");
					dismissDialog(DIALOG_ID_COMMIT);
					AlertUtils.toastMsg(MainActivity.this, "提交订单失败，导出文件出错");
				}
				
				if(FileUploader.uploadSaIndent(MainActivity.this)) {
//					mLoading.setProgress(100);
//					Log.e("==", "=======================================================3");
					dismissDialog(DIALOG_ID_COMMIT);
					SharedPreferences sppp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
					Editor ett = sppp.edit();
					ett.putBoolean("order_locked", true);
//					ett.putBoolean("order_commit", true);
					ett.putBoolean("order_view", false);
					ett.commit();
					AlertUtils.toastMsg(MainActivity.this, "订单已经提交");
					finish();
//					mLoading.dismiss();
					stopSync();
				} else {
//					Log.e("==", "=======================================================4");
//					dismissDialog(DIALOG_ID_COMMIT);
//					AlertUtils.toastMsg(MainActivity.this, "提交订单失败，网络出错");
				}
				break;
				
			case MSG_SHOW_LOADING:
				showDialog(DIALOG_ID_COMMIT);
				Message msgg = mHandler.obtainMessage();
				msgg.what = MSG_UPLOAD;
				msgg.sendToTarget();
				break;
				
			case MSG_UPLOAD_SAINDNET:
				AlertDialog.Builder adb1 = new AlertDialog.Builder(MainActivity.this);
				adb1.setIcon(R.drawable.logo);
				adb1.setTitle("提交订单");
				adb1.setMessage("总订量: " + AsSettings.getTotalWareNum(MainActivity.this) + "件  " + " 总金额: " + AsSettings.getTotalPrice(MainActivity.this));
				adb1.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						if(FileUploader.createSaIndentFile(MainActivity.this)) {
//							
//							if(FileUploader.uploadSaIndent(MainActivity.this)) {
//								Message msg = mHandler.obtainMessage();
//								msg.what = MSG_UPLOAD_SUCC;
//								msg.sendToTarget();
//							} else {
//								Message msg = mHandler.obtainMessage();
//								msg.what = MSG_NETWORK_ERR;
//								msg.obj = "网络出错";
//								msg.sendToTarget();
//							}
//						} else {
//							Message msg = mHandler.obtainMessage();
//							msg.what = MSG_CREATE_SAINDENT_ERR;
//							msg.obj = "导出订单出错";
//							msg.sendToTarget();
//						}
						dialog.dismiss();
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_SHOW_LOADING;
						msg.sendToTarget();
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
				
			case MSG_UPDATE_DOWNLOAD_FILE_PROGRESS:
				DownloadFileInfo fileInfo = (DownloadFileInfo)msg.obj;
				if(mUpdatingDialog == null) {
					Log.e(TAG, "============== dialog null ==========");
				}
				mUpdatingDialog.updateProgress(fileInfo.currSize);
				mUpdatingDialog.updatePorgressText(fileInfo.fileName + ", " + fileInfo.currSize + "/" + fileInfo.totalSize);
				break;
				
			case MSG_UPDATE_IMG:
				showDialog(DIALOG_ID_DOWNLOADING);
				mUpdatingDialog.updatePorgressText("");
				Log.e(TAG, "================= start");
//				ImageSyncUtils.clearUpLocalImgs(MainActivity.this);
				Log.e(TAG, "================= end");
				Thread downImg = new Thread() {
					public void run(){
						Looper.prepare();
						updatePic();
						dismissDialog(DIALOG_ID_DOWNLOADING);
					}
				};
				downImg.start();
				break;
				
			case MSG_UPDATE_INFO:
				showDialog(DIALOG_ID_DOWNLOADING);
				mUpdatingDialog.updatePorgressText("");
				SQLiteDatabase db = AsProvider.getWriteableDatabase(MainActivity.this);
				if( db != null) {
					try {
						AsProvider.updateInfo(db);
					} finally {
						db.close();
					}
				}
				ImageSyncUtils.cleanUpLocalInfoFiles(MainActivity.this, updateFiles);
				Thread downInfo =  new Thread() {
					public void run() {
						for (String f : updateFiles) {
							updateInfo(f, "/ORD/downinfo");
						}
//						SaIndentUtils.checkSaIndents(MainActivity.this);
						dismissDialog(DIALOG_ID_DOWNLOADING);
						Message mg = mHandler.obtainMessage();
						mg.what = MSG_INSERT_DATA;
						mg.sendToTarget();
					}
				};
				downInfo.start();
				break;
				
			case MSG_INSERT_PROGRESS_DIALG:
				DialogMessage dm = (DialogMessage)msg.obj;
				mUpdatingDataDialog.updateProgress((int)dm.value);
				mUpdatingDataDialog.updatePorgressText(dm.text);
				break;
				
			case MSG_INSERT_DATA:
				showDialog(DIALOG_ID_UPDATING_DATA);
				Thread updateData = new UpdateData();
				updateData.start();				
				break;
				
			case MSG_SYNC:
				sync();
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
		
		SharedPreferences accountSp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		
	
		customerName.setText(Html.fromHtml("尊敬的&nbsp;<u><i><font color=\"blue\">"+accountSp.getString("deptname", "")+"</font></i></u>&nbsp;用户"));
		customerOrderAmount.setText(Html.fromHtml("欢迎参加"+accountSp.getString("indentname", "")+", 您的订货指标, 订量: <u><i><font color=\"red\">"+accountSp.getString("shuliang", "0")+"</font></i></u>件, 金额:<u><i><font color=\"red\">"+accountSp.getString("jinge", "0")+"</font></i></u> 元"));
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
		updatePics = (Button) findViewById(R.id.main_update_pics);
		updateInfos = (Button) findViewById(R.id.main_update_infos);
		
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
		updatePics.setOnClickListener(this);
		updateInfos.setOnClickListener(this);
		
		
		SharedPreferences sppp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		boolean order_locked = sppp.getBoolean("order_locked", false);
//		if(order_locked) {
//			orderCommitBtn.setClickable(false);
//			AlertUtils.toastMsg(MainActivity.this, "订单已经确认");
//		} else {
//			orderCommitBtn.setClickable(true);
//		}
		
////		mHandler.postDelayed(mRunnable, 1000);
//		mTimer = new Timer();
//		mTimer.schedule(mTimerTask, 10000);
		Intent syncService = new Intent(MainActivity.this, IndentSyncService.class);
		startService(syncService);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case ID_LOGOUT:
			stopSync();
			finish();
			SharedPreferences sppp1 = getSharedPreferences("user_account", Context.MODE_PRIVATE);
			Editor ett = sppp1.edit();
			ett.putString("admin_user", "1000");
			ett.commit();
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
			Intent orderAnalysisIntent = new Intent(MainActivity.this, DinghuoZongheFenxiActivity.class);
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
//			SharedPreferences sp2 = getSharedPreferences("user_account", Context.MODE_PRIVATE);
//			String admin = sp2.getString("admin_user", "");
//			if(!"1001".equals(admin)) {
//				Message msg = mHandler.obtainMessage();
//				msg.what = MSG_ADMIN_LOGIN;
//				msg.sendToTarget();
//				return;	
//			}
//			if(!"dln".equals(UserUtils.getUserAccount(MainActivity.this))) {
//
//			}
			AlertDialog.Builder dlgBuiler = new AlertDialog.Builder(MainActivity.this);
			dlgBuiler.setTitle("管理员验证");
			dlgBuiler.setIcon(R.drawable.logo);
			LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);
			final LinearLayout dlgLayout = (LinearLayout)mInflater.inflate(R.layout.dialog_layout, null);
			dlgBuiler.setView(dlgLayout);
			dlgBuiler.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText adminUsername = (EditText) dlgLayout.findViewById(R.id.admin_username);
					EditText adminPassword = (EditText) dlgLayout.findViewById(R.id.admin_password);
					if(adminUsername.getText().toString().trim().equals("dln") && adminPassword.getText().toString().trim().equals("dln87751870")) {
//						Log.e(TAG, "=======                 admin login                  ===================");
//						dialog.dismiss();
//						Intent settingIntent = new Intent(MainActivity.this, IndextSetting.class);
//						startActivity(settingIntent);
						Intent orderSetttingIntent = new Intent(MainActivity.this, AsSettings.class);
						startActivity(orderSetttingIntent);
					} else {
						dialog.dismiss();
					}
				}
			});
			dlgBuiler.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog dlg = dlgBuiler.create();
			dlg.show();
			break;
			
		case R.id.order_commit:
			SharedPreferences sppp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			boolean order_locked = sppp.getBoolean("order_locked", false);
			if(order_locked) {
				AlertUtils.toastMsg(MainActivity.this, "订单已经确认");
				return;
			}
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
					stopSync();
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
			
		case R.id.main_update_pics:
			AlertDialog.Builder updatePicBuilder = new AlertDialog.Builder(MainActivity.this);
			updatePicBuilder.setIcon(R.drawable.logo);
			updatePicBuilder.setTitle("更新图片");
			updatePicBuilder.setMessage("确定更新图片吗？");
			updatePicBuilder.setPositiveButton("确定",  new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_UPDATE_IMG;
					msg.sendToTarget();
					dialog.dismiss();
				}
			});
			updatePicBuilder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			AlertDialog updatePicDialog = updatePicBuilder.create();
			updatePicBuilder.show();
			break;
			
		case R.id.main_update_infos:
			AlertDialog.Builder updateInfoBuilder = new AlertDialog.Builder(MainActivity.this);
			updateInfoBuilder.setIcon(R.drawable.logo);
			updateInfoBuilder.setTitle("更新资料");
			updateInfoBuilder.setMessage("确定更新资料吗？");
			updateInfoBuilder.setPositiveButton("确定",  new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Message msg1 = mHandler.obtainMessage();
					msg1.what = MSG_UPDATE_INFO;
					msg1.sendToTarget();
					dialog.dismiss();
				}
			});
			updateInfoBuilder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			AlertDialog updateInfoDialog = updateInfoBuilder.create();
			updateInfoDialog.show();
			break;
			 
			 default:
				 break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_ID_DOWNLOADING:
			mUpdatingDialog = new AsProgressDialog(MainActivity.this, R.style.AsDialog, "下载文件");
//			mUpdatingDialog.setCancelable(false);
			return mUpdatingDialog;
			
		case DIALOG_ID_UPDATING_DATA:
			return mUpdatingDataDialog = new AsProgressDialog(MainActivity.this, R.style.AsDialog, "更新数据");
			
		case DIALOG_ID_COMMIT:
//			mLoading = new ProgressDialog(this);
//			mLoading.setMax(100);
//			mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			mLoading.setTitle("订单确认");
			mLoading = ProgressDialog.show(MainActivity.this, "", "", true);
			mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return mLoading;
		}
		return null;
	}

	private void updatePic() {
		SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		FTPClient ftp = new FTPClient();
		String host = spp.getString(Constant.SP_FTP_HOST, "");
		String username = spp.getString(Constant.SP_FTP_USERNAME, "");
		String password = spp.getString(Constant.SP_FTP_PASSWORD, "");
		try {
			ftp.connect(host);
			boolean isLogined = ftp.login(username, password);
			ftp.changeWorkingDirectory("/ORD/downpic");
			FTPFile[] files = ftp.listFiles();
			File picDir = new File(getCacheDir() + "/pic");
			if(!picDir.exists()) {
				picDir.mkdirs();
			}
			for(FTPFile file : files) {
				File pic = new File(picDir + "/" + file.getName());
				Log.e(TAG, "filename: " + pic.getName());
				InputStream is = ftp.retrieveFileStream(file.getName());
				OutputStream os = new FileOutputStream(pic);
				int currSize = 0;
				int totalSize = is.available();
				mUpdatingDialog.setMax(totalSize);
				
				byte[] buff = new byte[1024];
				int len;
				while((len = is.read(buff)) != -1) {
					os.write(buff, 0, len);
					Message msg = mHandler.obtainMessage();
					currSize += len;
					DownloadFileInfo fileInfo = new DownloadFileInfo(pic.getName(), "", totalSize, currSize);
					msg.obj = fileInfo;
					msg.what = MSG_UPDATE_DOWNLOAD_FILE_PROGRESS;
					msg.sendToTarget();
				}
				os.close();
				is.close();
				ftp.completePendingCommand();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(ftp != null) {
					ftp.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final class DownloadFileInfo {
		String fileName;
		String path;
		int totalSize;
		int currSize;
		public DownloadFileInfo(String fileName, String path, int totalSize, int currSize) {
			this.fileName = fileName;
			this.path = path;
			this.totalSize = totalSize;
			this.currSize = currSize;
		}
	}
	
	private static final String[] updateFiles = new String[]{
		"sawarecode.txt",
		"sacolorcode.txt",
		"saware_color.txt",
		"saware_size.txt",
		"stpara.txt"
	};
	
	private void updateInfo(String fileName, String path) {
		SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		String host= spp.getString(Constant.SP_FTP_HOST, Constant.DEFAULT_SP_FTP_HOST);
		String username = spp.getString(Constant.SP_FTP_USERNAME, Constant.DEFAULT_FTP_USERNAME);
		String password = spp.getString(Constant.SP_FTP_PASSWORD, Constant.DEFAULT_FTP_PASSWORD);
		FTPClient ftp = null;
		try {
			ftp = new FTPClient();
			ftp.connect(host);
			boolean isLogined = ftp.login(username, password);
			if(isLogined) {
				ftp.setControlEncoding("UTF-16LE");
				ftp.changeWorkingDirectory(path);
					Log.e(TAG, "filename: " + fileName);
					InputStream is = ftp.retrieveFileStream(fileName);
					if(is!=null) {
						int currSize = 0;
						mUpdatingDialog.setMax(is.available());
						File infoDir = new File(getCacheDir() + "/downinfo");
						if(!infoDir.exists()) {
							infoDir.mkdirs();
						}
						File localFile = new File(getCacheDir() + "/downinfo/" + fileName);
						if(!localFile.exists()) {
							localFile.createNewFile();
						}
						OutputStream os = new FileOutputStream(localFile);
						byte[] buff = new byte[1024];
						int len;
						while((len = is.read(buff)) != -1) {
							os.write(buff, 0, len);
							currSize += len;
							Message msg = mHandler.obtainMessage();
							DownloadFileInfo fileInfo = new DownloadFileInfo(fileName, "/ORD/info", is.available(), currSize);
							msg.obj = fileInfo;
							msg.what = MSG_UPDATE_DOWNLOAD_FILE_PROGRESS;
							msg.sendToTarget();
						}
						os.close();
						is.close();
//						ftp.disconnect();
						ftp.completePendingCommand();
						ftp.disconnect();
					}
				}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ftp != null) {
					ftp.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class UpdateData extends Thread {
		private DialogMessage dm;
		private File localFile;
		
		int totalLines = 0;
		int currLine = 0;
		
		private void addSaWareCode() throws Exception{
			localFile = new File(getCacheDir() + "/downinfo/sawarecode.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaWareCode.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareCode sawarecode = new SaWareCode();
					sawarecode.setWarecode(TextUtils.isEmpty(arr[0]) ? "" : arr[0]);
					sawarecode.setTrademarkcode(TextUtils.isEmpty(arr[1]) ? "" : arr[1]);
					sawarecode.setWaretypeid(TextUtils.isEmpty(arr[2]) ? "" : arr[2]);
					sawarecode.setId(TextUtils.isEmpty(arr[3]) ? "" : arr[3]);
					sawarecode.setSpecification(TextUtils.isEmpty(arr[4]) ? "" : arr[4]);
					sawarecode.setWarename(TextUtils.isEmpty(arr[5]) ? "" : arr[5]);
					sawarecode.setAdutunit(TextUtils.isEmpty(arr[6]) ? "" : arr[6]);
					sawarecode.setRetailprice(TextUtils.isEmpty(arr[7]) ? 0 : Double.parseDouble(arr[7]));
					sawarecode.setDate1(TextUtils.isEmpty(arr[8]) ? 0 : Long.parseLong(arr[8]));
					sawarecode.setType2(TextUtils.isEmpty(arr[9]) ? "" : arr[9]);
					sawarecode.setState(TextUtils.isEmpty(arr[10]) ? "" : arr[10]);
					sawarecode.setPy("");
					sawarecode.setFlag(TextUtils.isEmpty(arr[11]) ? "" : arr[11]);
					sawarecode.setSxz(TextUtils.isEmpty(arr[12]) ? "" : arr[12]);
					sawarecode.setPagenum(TextUtils.isEmpty(arr[13]) ? "" : arr[13]);
					sawarecode.setSpecdef(TextUtils.isEmpty(arr[14]) ? "" : arr[14]);
					sawarecode.setSex(TextUtils.isEmpty(arr[15]) ? "" : arr[15]);
					sawarecode.setStyle(TextUtils.isEmpty(arr[16]) ? "" : arr[16]);
					sawarecode.setTrait(TextUtils.isEmpty(arr[17]) ? "" : arr[17]);
					sawarecode.setPricecomment(TextUtils.isEmpty(arr[18]) ? "" : arr[18]);
					sawarecode.setPlandate(TextUtils.isEmpty(arr[19]) ? "" : arr[19]);
					sawarecode.setWaregoto(TextUtils.isEmpty(arr[20]) ? "" : arr[20]);
					sawarecode.setProdarea(TextUtils.isEmpty(arr[21]) ? "" : arr[21]);
					sawarecode.setPatten(TextUtils.isEmpty(arr[22]) ? "" : arr[22]);
					sawarecode.setSizeorder(TextUtils.isEmpty(arr[23]) ? "" : arr[23]);
					sawarecode.setRemark(TextUtils.isEmpty(arr[24]) ? "" : arr[24]);
					sawarecode.setDate3(TextUtils.isEmpty(arr[25]) ? 0 : Long.parseLong(arr[25]));
					sawarecode.setDate4(TextUtils.isEmpty(arr[26]) ? 0 : Long.parseLong(arr[26]));
					sawarecode.setProcuredate(TextUtils.isEmpty(arr[27]) ? 0 : Long.parseLong(arr[27]));
					sawarecode.setStylespec(TextUtils.isEmpty(arr[28]) ? "" : arr[28]);
					sawarecode.setFactorycode(TextUtils.isEmpty(arr[29]) ? "" : arr[29]);
					sawarecode.setDirection(TextUtils.isEmpty(arr[30]) ? "" : arr[30]);
					sawarecode.setCliencode(TextUtils.isEmpty(arr[31]) ? "" : arr[31]);
					sawarecode.setStdname(TextUtils.isEmpty(arr[32]) ? "" : arr[32]);
					sawarecode.setCtype(TextUtils.isEmpty(arr[33]) ? "" : arr[33]);
					sawarecode.setWaredegree(TextUtils.isEmpty(arr[34]) ? "" : arr[34]);
					sawarecode.setSaleprice(TextUtils.isEmpty(arr[35]) ? 0 : Double.parseDouble(arr[35]));
//					sawarecode.setAvgpurhprice(TextUtils.isEmpty(arr[36]) ? 0 : Double.parseDouble(arr[36]));
					
					ContentValues values =  sawarecode.toContentValues();
					getContentResolver().insert(SaWareCode.CONTENT_URI, sawarecode.toContentValues());
					
					DialogMessage dm = new DialogMessage(++currLine, "sawarecode, warecode: " + arr[0]);
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();					
				}
			} catch (Exception e) {
				e.printStackTrace();
				AlertUtils.toastMsg(MainActivity.this, "更新货品资料出错,可能导致输入没有入库,请再次更新");
				throw new Exception();
			} 
		}
		
		private void addStPara() throws Exception{
			localFile = new File(getCacheDir() + "/downinfo/stpara.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaPara.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaPara para =new SaPara();
					if(arr.length==0) {
						continue;					
					} else if(arr.length == 1) {
						para.paraType = arr[0];
						para.para = "";
						para.paraConnent = "";
						para.connent = "";
					} else if(arr.length == 2) {
						para.paraType = arr[0];
						para.para = arr[1];
						para.paraConnent = "";
						para.connent = "";
					} else if(arr.length == 3) {
						para.paraType = arr[0];
						para.para = arr[1];
						para.paraConnent = arr[2];
						para.connent = "";
					} else {
						para.paraType = arr[0];
						para.para = arr[1];
						para.paraConnent = arr[2];
						para.connent = arr[3];
					}
					
					ContentValues values = para.toContentValues();
					Uri u = getContentResolver().insert(SaPara.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "stpara, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();						
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaColorCode() throws Exception{
			localFile = new File(getCacheDir() + "/downinfo/sacolorcode.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaColorCode.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaColorCode sacolorcode = new SaColorCode();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = "";
						sacolorcode.py = "";
						sacolorcode.code = "";
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 2) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = "";
						sacolorcode.code = "";
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 3) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = "";
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";						
					} else if(arr.length == 4) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 5) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = arr[4];
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 6) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = arr[4];
						sacolorcode.pb = arr[5];
						sacolorcode.trade = "";						
					} else {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = arr[4];
						sacolorcode.pb = arr[5];
						sacolorcode.trade = arr[6];
					}
					ContentValues values = sacolorcode.toContentValues();
					Uri u = getContentResolver().insert(SaColorCode.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "sacolorcode, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();		
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addShowSize() throws Exception{
			localFile = new File(getCacheDir() + "/info/showsize.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(ShowSize.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					ShowSize showsize = new ShowSize();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						showsize.size = arr[0];
						showsize.type = "";
						showsize.show = "";
						showsize.title = "";
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 2) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = "";
						showsize.title = "";
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 3) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = "";
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 4) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 5) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = arr[4];
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 6) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = arr[4];
						showsize.sizeGroup = arr[5];
						showsize.scale = "";
					} else {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = arr[4];
						showsize.sizeGroup = arr[5];
						showsize.scale = arr[6];
					}
					ContentValues values = showsize.toContentValues();
					Uri u = getContentResolver().insert(ShowSize.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "showsize, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSawareColor() throws Exception{
			localFile = new File(getCacheDir() + "/downinfo/saware_color.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaWareColor.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareColor warecolor = new SaWareColor();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						warecolor.wareCode = arr[0];
						warecolor.colorCode = "";
						warecolor.colorComment = "";						
					} else if(arr.length == 2) {
						warecolor.wareCode = arr[0];
						warecolor.colorCode = arr[1];
						warecolor.colorComment = "";
					} else {
						warecolor.wareCode = arr[0];
						warecolor.colorCode = arr[1];
						warecolor.colorComment = arr[2];
					}
					ContentValues values = warecolor.toContentValues();
					Uri u = getContentResolver().insert(SaWareColor.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "saware_color, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaWareSize() throws Exception{
			localFile = new File(getCacheDir() + "/downinfo/saware_size.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaWareSize.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareSize waresize = new SaWareSize();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						waresize.wareCode = arr[0];
						waresize.size = "";
						waresize.showSort = "";
						waresize.stand = "";
					} else if(arr.length == 2) {
						waresize.wareCode = arr[0];
						waresize.size = arr[1];
						waresize.showSort = "";
						waresize.stand = "";
					} else if(arr.length == 3) {
						waresize.wareCode = arr[0];
						waresize.size = arr[1];
						waresize.showSort = arr[2];
						waresize.stand = "";
					} else {
						waresize.wareCode = arr[0];
						waresize.size = arr[1];
						waresize.showSort = arr[2];
						waresize.stand = arr[3];
					}
					ContentValues values = waresize.toContentValues();
					Uri u = getContentResolver().insert(SaWareSize.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "saware_size, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();						
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addType1() throws Exception{
			localFile = new File(getCacheDir() + "/info/type1.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(AsContent.Type1.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					AsContent.Type1 type = new AsContent.Type1();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						type.iid = arr[0];
						type.type1 = "";
						type.wareTypeId = "";
						type.standard = "";
						type.remark = "";
					} else if(arr.length == 2) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = "";
						type.standard = "";
						type.remark = "";
					} else if(arr.length == 3) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = arr[2];
						type.standard = "";
						type.remark = "";
					} else if(arr.length == 4) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = arr[2];
						type.standard = arr[3];
						type.remark = "";
					} else if(arr.length == 5) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = arr[2];
						type.standard = arr[3];
						type.remark = arr[4];
					}
					ContentValues values = type.toContentValues();
					Uri u = getContentResolver().insert(AsContent.Type1.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "type1, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaWareType() throws Exception{
			localFile = new File(getCacheDir() + "/info/sawaretype.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(AsContent.SaWareType.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareType waretype = new SaWareType();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = "";
						waretype.taxRate = "";
						waretype.standard = "";
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 2) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = "";
						waretype.standard = "";
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 3) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = "";
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 4) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = arr[3];
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 5) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = arr[3];
						waretype.flag = arr[4];
						waretype.sizeFlag = "";
					} else {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = arr[3];
						waretype.flag = arr[4];
						waretype.sizeFlag = arr[5];
					}
					ContentValues values = waretype.toContentValues();
					Uri u = getContentResolver().insert(AsContent.SaWareType.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "sawaretype, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaWareGroup() throws Exception{
			localFile = new File(getCacheDir() + "/info/sawaregroup.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(AsContent.SaWareGroup.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareGroup group = new SaWareGroup();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						group.itemCode = arr[0];
						group.groupName = "";
						group.wareCode = "";
						group.colorCode = "";
						group.remark = "";
					} else if(arr.length == 2) {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = "";
						group.colorCode = "";
						group.remark = "";
					} else if(arr.length == 3) {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = arr[2];
						group.colorCode = "";
						group.remark = "";
					} else if(arr.length == 4) {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = arr[2];
						group.colorCode = arr[3];
						group.remark = "";
					} else {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = arr[2];
						group.colorCode = arr[3];
						group.remark = arr[4];
					}
					ContentValues values = group.toContentValues();
					Uri u = getContentResolver().insert(AsContent.SaWareGroup.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "sawaregroup, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaSizeSet() throws Exception {
			localFile = new File(getCacheDir() + "/info/saSizeSet.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaSizeSet.CONTENT_URI, null, null);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
			String line;
			while((line = br.readLine()) != null) {
				String[] arr = line.split("\t");
				SaSizeSet sizeSet = new SaSizeSet();
				sizeSet.sizeGroup = arr[0];
				for(int i=1; i<arr.length; i++) {
					if(!TextUtils.isEmpty(arr[i])) {
						Field field = sizeSet.getClass().getField("s" + (i<10 ? ("0" + i) : i));
						field.setInt(sizeSet, Integer.parseInt(arr[i]));
					}
				}
				ContentValues values = sizeSet.toContentValues();
				Uri u = getContentResolver().insert(AsContent.SaSizeSet.CONTENT_URI, values);
				
				DialogMessage dm = new DialogMessage(++currLine, "sasizeset, id: " + u.getPathSegments().get(1));
				
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_INSERT_PROGRESS_DIALG;
				msg.obj = dm;
				msg.sendToTarget();	
			}
		}
		
		@Override
		public void run() {
			Looper.prepare();
			showDialog(DIALOG_ID_UPDATING_DATA);
			try {
				addSaWareCode();
				addStPara();
				addSaColorCode();
//				addShowSize();
				addSawareColor();
				addSaWareSize();
//				addType1();
//				addSaWareType();
//				addSaWareGroup();
//				addSaSizeSet();
//				ImageSyncUtils.checkWareCodeInSaIndent(MainActivity.this);
//				ImageSyncUtils.checkSaOrderScore(MainActivity.this);
				SaIndentUtils.checkSaIndents(MainActivity.this);
				dismissDialog(DIALOG_ID_UPDATING_DATA);
				AlertUtils.toastMsg(MainActivity.this, "资料已经更新");
			} catch (Exception e) {
				e.printStackTrace();
				AlertUtils.toastMsg(MainActivity.this, "更新资料出错，请再次更新");
				dismissDialog(DIALOG_ID_UPDATING_DATA);
			}

		}
	}
	
	private int getLinesForFile(File f) {
		int lines = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF16-LE"));
			String line;
			while((line = br.readLine()) != null) {
				lines ++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			SharedPreferences spe = getSharedPreferences("data_downloaded", 0);
//			SharedPreferences.Editor editor = spe.edit();
//			editor.putBoolean("data_downloaded", false);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
//			SharedPreferences spe = getSharedPreferences("data_downloaded", 0);
//			SharedPreferences.Editor editor = spe.edit();
//			editor.putBoolean("data_downloaded", false);			
		} catch (IOException e) {
			e.printStackTrace();
//			SharedPreferences spe = getSharedPreferences("data_downloaded", 0);
//			SharedPreferences.Editor editor = spe.edit();
//			editor.putBoolean("data_downloaded", false);			
		}
		return lines;
	}
	
	private class IndentSyncTimerTask extends TimerTask {
		
		private Context mContext;
		
		public IndentSyncTimerTask(Context context) {
			this.mContext = context;
		}

		@Override
		public void run() {
			Log.e(TAG, "===================== start sync =============================");
			try {
				FileUploader.createSaIndentFile(mContext);
				FileUploader.uploadSaIndent(mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e(TAG, "===================== end sync =============================");
		}
		
	}
	
	private void startSync() {
		IndentSyncTimerTask timerTask = new IndentSyncTimerTask(MainActivity.this);
		mTimer = new Timer(true);
		mTimer.scheduleAtFixedRate(timerTask, 1000, 5000);
	}
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			Log.e(TAG, "====================== sync sync ================================");
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_SYNC;
			msg.sendToTarget();
			mHandler.postDelayed(this, 100000);
		}
	};
	
	private void stopSync() {
		Intent service = new Intent(MainActivity.this, IndentSyncService.class);
		stopService(service);
	}
	
	private void sync() {
		FileUploader.createSaIndentFile(MainActivity.this);
		FileUploader.uploadSaIndent(MainActivity.this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(NetWorkUtils.isNetAvailable(MainActivity.this)) {
			NetWorkUtils.netWorkStatus = true;
		} else {
			NetWorkUtils.netWorkStatus = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(mRunnable);
		Log.e(TAG, "============================== stop sync ==================================");
		super.onDestroy();
	}
}
