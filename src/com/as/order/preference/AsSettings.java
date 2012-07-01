package com.as.order.preference;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
}
