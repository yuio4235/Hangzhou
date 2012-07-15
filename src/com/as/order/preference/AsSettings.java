package com.as.order.preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaIndent;
import com.as.order.R;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.Constant;
import com.as.ui.utils.SaIndentUtils;
import com.as.ui.utils.UserUtils;

public class AsSettings extends PreferenceActivity implements
		OnPreferenceClickListener, OnPreferenceChangeListener {
	
	private static final String TAG = "AsSetttings";
	
	private CheckBoxPreference orderLockedP;
	private Preference downloadSaIndentP;
//	private Preference updateImgsP;
//	private Preference updateInfosP;
	private CheckBoxPreference viewOrderP;
	private Preference eraseOrder;
	
	SharedPreferences spp ;
	ProgressDialog mLoading;
	AlertDialog at;
	
	private static final int DIALOG_DOWNLOAD_SAINDENT = 2001;
	
	private static final int MSG_DOWNLOAD_SAINDNET = 1001;
	private static final int MSG_DOWNLOAD = 1002;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case MSG_DOWNLOAD_SAINDNET:
				showDialog(DIALOG_DOWNLOAD_SAINDENT);
				break;
			case MSG_DOWNLOAD:
				downloadSaIndent();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		orderLockedP = (CheckBoxPreference) findPreference("order_locked");
		downloadSaIndentP = (Preference) findPreference("download_saindent");
//		updateImgsP = (Preference) findPreference("update_imgs");
//		updateInfosP = (Preference) findPreference("update_info");
		viewOrderP = (CheckBoxPreference) findPreference("order_view");
		eraseOrder = (Preference) findPreference("erase_order");
		
		orderLockedP.setOnPreferenceClickListener(this);
		orderLockedP.setOnPreferenceChangeListener(this);
		downloadSaIndentP.setOnPreferenceClickListener(this);
//		updateImgsP.setOnPreferenceClickListener(this);
//		updateInfosP.setOnPreferenceClickListener(this);
		viewOrderP.setOnPreferenceClickListener(this);
		viewOrderP.setOnPreferenceChangeListener(this);
		eraseOrder.setOnPreferenceClickListener(this);
//		eraseOrder.setOnPreferenceChangeListener(this);
		
		spp = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
//		final Editor et = spp.edit();
//		if(preference == viewOrderP) {
//			if(!"dln".equals(UserUtils.getUserAccount(AsSettings.this))) {
//				AlertUtils.toastMsg(AsSettings.this, "您没有管理员权限");
//				return false;
//			}
//			et.putBoolean("order_view", true);
//			et.commit();
//			AlertUtils.toastMsg(AsSettings.this, "查看已经解锁");
//		}
		
//		if(preference == orderUnLockP) {
//			mLoading = ProgressDialog.show(AsSettings.this, "提示", "正在解锁");
//			if(!("dln".equals(UserUtils.getUserAccount(this))) &&  spp.getBoolean("order_unlock", false)) {
//				mLoading.dismiss();
//				AlertUtils.toastMsg(AsSettings.this, "权限不足,请联系管理员解锁订单");
//				return false;
//			}
//			et.putBoolean("order_unlock", false);
//			et.commit();
//			mLoading.dismiss();
//			AlertUtils.toastMsg(AsSettings.this, "订单已经解锁");
//		}
//		
//		if(preference == downloadSaIndentP) {
//			AlertUtils.toastMsg(AsSettings.this, "开始下载订单，未提示成功之前，请不要进行订货操作");
//		}
		if(preference == eraseOrder) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AsSettings.this);
			builder.setIcon(R.drawable.logo);
			builder.setTitle("删除订单");
			builder.setMessage("所有订单数据将会清零,确定删除订单？");
			builder.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SQLiteDatabase db = AsProvider.getWriteableDatabase(AsSettings.this);
					ContentValues values = new ContentValues();
					for(int i=1; i<=20; i++) {
						values.put("s" + (i < 10 ? ("0"+i) : i), 0);
					}
					values.put("warenum", 0);
					if(db!=null) {
						try {
//							db.beginTransaction();
							int rows = db.update("saindent", values, null, null);
							Log.e(TAG, "========================================= rows: " + rows);
//							db.endTransaction();
						} finally {
							db.close();
						}
					}
					AlertUtils.toastMsg(AsSettings.this, "订单已删除");
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog dlg = builder.create();
			dlg.show();
			return true;
		}
		
		if(preference == downloadSaIndentP) {
//			if(mLoading == null) {
//				mLoading =  ProgressDialog.show(AsSettings.this, "下载订单", "订单下载中，请稍后", true);	
//				mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				mLoading.setCancelable(false);
//			}
//			mLoading.show();
//			Message msg = mHandler.obtainMessage();
//			msg.what = MSG_DOWNLOAD_SAINDNET;
//			msg.sendToTarget();
			showDialog(DIALOG_DOWNLOAD_SAINDENT);
//			Message msg1 = mHandler.obtainMessage();
//			msg1.what = MSG_DOWNLOAD;
//			msg1.sendToTarget();
			new DownloadThread().start();
//			mLoading.dismiss();
//			downloadSaIndent();
//			Log.e(TAG, "===================== download =======================");
////			new InsertData().start();
//			addSaIndent();
//			dismissDialog(DIALOG_DOWNLOAD_SAINDENT);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		SharedPreferences wp = PreferenceManager.getDefaultSharedPreferences(AsSettings.this);
		Editor et = wp.edit();
		if(orderLockedP == preference) {
			boolean val = (Boolean) newValue;
			if(val) {
				et.putBoolean("order_locked", false);
			} else {
				et.putBoolean("order_locked", true);
			}
			et.commit();
			return true;
		}
		
		if(viewOrderP == preference) {
			boolean val = (Boolean) newValue;
			if(val) {
				et.putBoolean("order_view", true);
			} else {
				et.putBoolean("order_view", false);
			}
			et.commit();
			return true;
		}
		
		
		return false;
	}

	public static int getTotalWareNum(Context context) {
		String sql = " select sum(warenum) from saindent ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor!=null && cursor.moveToFirst()) {
				return cursor.getInt(0);
			}
		} finally {
			if(db != null) {
				db.close();
			}
			
			if(cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}
	
	public static int getTotalPrice(Context context) {
		String sql = " select sum(saindent.[warenum]*sawarecode.[retailprice]) from  saindent, sawarecode where saindent.[warecode]  = sawarecode.[warecode]";
		
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor!=null&&cursor.moveToFirst()) {
				return cursor.getInt(0);
			}
		} finally {
			if(db!=null) {
				db.close();
			}
			if(cursor!=null) {
				cursor.close();
			}
		}
		return 0;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_DOWNLOAD_SAINDENT:
			mLoading =  ProgressDialog.show(AsSettings.this, "下载订单", "订单下载中，请稍后", true);	
			mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mLoading.setCancelable(false);
			return mLoading;
			
			default:
				break;
		}
		return null;
	}
	
	private void downloadSaIndent() {
		String REMOTE_HOST = spp.getString(Constant.SP_FTP_HOST, "");
		String USER_NAME = spp.getString(Constant.SP_FTP_USERNAME, "");
		String PASSWORD = spp.getString(Constant.SP_FTP_PASSWORD, "");
		
		Log.e(TAG, "===========================");
		
		SharedPreferences spp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String departCode = spp.getString("departcode", "");
		
		FTPClient ftp = null;
		
		try {
			ftp = new FTPClient();
			ftp.connect(REMOTE_HOST);
			boolean isLogined = ftp.login(USER_NAME, PASSWORD);
			if(isLogined) {
				ftp.setControlEncoding("UTF-16LE");
				ftp.changeWorkingDirectory("/ORD/downdata");
				String fileName = departCode + ".txt";
				Log.e(TAG, "=========== fileName: " + fileName);
				InputStream is = ftp.retrieveFileStream(fileName);
				if(is != null) {
					File infoDir = new File(getCacheDir() + "/info");
					if(!infoDir.exists()) {
						infoDir.mkdirs();
					}
					File localFile = new File(getCacheDir() + "/info/" + fileName);
					if(!localFile.exists()) {
						localFile.createNewFile();
					}
					OutputStream os = new FileOutputStream(localFile);
					byte[] buff = new byte[1024];
					int len;
					while((len = is.read(buff)) != -1) {
						os.write(buff, 0, len);
					}
					os.close();
					is.close();
					
					ftp.completePendingCommand();
					ftp.disconnect();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addSaIndent() {
		SQLiteDatabase db = AsProvider.getWriteableDatabase(AsSettings.this);
		
		try {
			AsProvider.updateSaIndet(db);
		} catch (SQLException e2) {
			e2.printStackTrace();
		} finally {
			if(db != null) {
				db.close();
			}
		}
		
		SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String departCode = sp.getString("departcode", "");
		
		File localFile = new File(getCacheDir()+"/info/" + departCode + ".txt");
		
		Log.e(TAG, "====== file name: " + localFile.getName());
		if(!localFile.exists()) {
			return;
		}
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile)));
			String line;
			while((line = br.readLine()) != null) {
				Log.e(TAG, "=====================================================================================");
				String[] arr = line.split("\t");
				SaIndent indent = new SaIndent();
				indent.indentNo = arr[0];
				indent.departCode = arr[1];
				indent.wareCode = arr[2];
				indent.colorCode = arr[3];
				indent.s01 = Integer.parseInt(arr[4]);
				indent.s02 = Integer.parseInt(arr[5]);
				indent.s03 = Integer.parseInt(arr[6]);
				indent.s04 = Integer.parseInt(arr[7]);
				indent.s05 = Integer.parseInt(arr[8]);
				indent.s06 = Integer.parseInt(arr[9]);
				indent.s07 = Integer.parseInt(arr[10]);
				indent.s08 = Integer.parseInt(arr[11]);
				indent.s09 = Integer.parseInt(arr[12]);
				indent.s10 = Integer.parseInt(arr[13]);
				indent.s11 = Integer.parseInt(arr[14]);
				indent.s12 = Integer.parseInt(arr[15]);
				indent.s13 = Integer.parseInt(arr[16]);
				indent.s14 = Integer.parseInt(arr[17]);
				indent.s15 = Integer.parseInt(arr[18]);
				indent.s16 = Integer.parseInt(arr[19]);
				indent.s17 = Integer.parseInt(arr[20]);
				indent.s18 = Integer.parseInt(arr[21]);
				indent.s19 = Integer.parseInt(arr[22]);
				indent.s20 = Integer.parseInt(arr[23]);
				indent.inputDate = arr[24];
				indent.inputMan = arr[25];
				indent.wareNum = Integer.parseInt(arr[26]);
				indent.remark = arr[27];
				indent.oFlag = arr[28];
				getContentResolver().insert(SaIndent.CONTENT_URI, indent.toContentValues());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class InsertData extends Thread  {
		@Override
		public void run() {
			super.run();
			addSaIndent();
			SaIndentUtils.checkSaIndents(AsSettings.this);
			dismissDialog(DIALOG_DOWNLOAD_SAINDENT);
		}
	}
	
	public class DownloadThread extends Thread {
		@Override
		public void run() {
			super.run();
			downloadSaIndent();
			addSaIndent();
			dismissDialog(DIALOG_DOWNLOAD_SAINDENT);
		}
	}
}
