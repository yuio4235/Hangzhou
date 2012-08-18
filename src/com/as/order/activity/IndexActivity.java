package com.as.order.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.as.db.provider.AsContent.User;
import com.as.order.R;
import com.as.order.net.JsonAsyncTask;
import com.as.order.net.ResultListener;
import com.as.order.net.ServerResponse;
import com.as.order.preference.IndextSetting;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.Constant;

public class IndexActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "IndexActivity";
	
	private static final int DIALOG_NETWORK = 1001;
	private ProgressDialog mLoading;
    /** Called when the activity is first created. */
	
	private static final int MSG_REG = 2001;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Looper.loop();
			Log.e("==", "============sdfsdfs");
			switch(msg.what) {
			case MSG_REG:
				Log.e("==", "============before reg");
				register();
				Log.e("==", "============after reg");
				break;
				
				default:
					break;
			}
		};
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        
        Button loginBtn = (Button) findViewById(R.id.login_with_phone_num);
        Button regBtn = (Button) findViewById(R.id.reg_with_phone_num);
        Button settingBtn = (Button) findViewById(R.id.index_setting);
        
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.login_with_phone_num:
			Intent loginActivityIntent = new Intent(IndexActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
			break;
			
		case R.id.reg_with_phone_num:
//			Message msg = mHandler.obtainMessage();
//			msg.what = MSG_REG;
//			msg.sendToTarget();
			register();
			break;
			
		case R.id.index_setting:
			AlertDialog.Builder dlgBuiler = new AlertDialog.Builder(IndexActivity.this);
			dlgBuiler.setTitle("管理员验证");
			dlgBuiler.setIcon(R.drawable.logo);
			LayoutInflater mInflater = LayoutInflater.from(IndexActivity.this);
			final LinearLayout dlgLayout = (LinearLayout)mInflater.inflate(R.layout.dialog_layout, null);
			dlgBuiler.setView(dlgLayout);
			dlgBuiler.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText adminUsername = (EditText) dlgLayout.findViewById(R.id.admin_username);
					EditText adminPassword = (EditText) dlgLayout.findViewById(R.id.admin_password);
					if(adminUsername.getText().toString().trim().equals("dln") && adminPassword.getText().toString().trim().equals("dln87751870")) {
						Log.e(TAG, "=======                 admin login                  ===================");
						dialog.dismiss();
						Intent settingIntent = new Intent(IndexActivity.this, IndextSetting.class);
						startActivity(settingIntent);
						
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
			
			default:
				break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void register() {
//		Looper.prepare();
//		Looper.myLooper();
//		Looper.loop();
		Cursor cursor = getContentResolver().query(User.CONTENT_URI, User.CONTENT_PROJECTION, null, null, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				if(cursor.getCount() > 0) {
					AlertUtils.toastMsg(IndexActivity.this, "已经存在用户，不能注册");
					return;
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String macAddr = info.getMacAddress();
		
		Log.e("===", "================================= mac addr: " + macAddr);
		
		showDialog(DIALOG_NETWORK);
		SharedPreferences sppp = PreferenceManager.getDefaultSharedPreferences(IndexActivity.this);
		String url = sppp.getString(Constant.SP_REG_URL, Constant.DEFAULT_REG_URL);
		Log.e("===", "================================= urll: " + url);
		new JsonAsyncTask(/*"http://dlndl.vicp.cc/WS-Order/OrderRegister.ashx?macip="*/ url + macAddr, new ResultListener() {
			
			@Override
			public void onProgressMessage(String message) {
				
			}
			
			@Override
			public void onPostDataSuccess(ServerResponse response) {
				JSONObject result = response.result;
				Log.e("=", "json: " + result);
				try {
					User user = new User();
					SharedPreferences accountSp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
					Editor et = accountSp.edit();
					SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(IndexActivity.this);
					Editor sppEt = spp.edit();
//					String departCode = result.getString("DeptCode");
//					String deptName = result.getString("DeptName");
//					String passWd = result.getString("PassWord");
//					String macId = result.getString("MacID");
//					String indentName = result.getString("Indentname");
//					String maxSizeNum = result.getString("MaxSizeNum");
//					String intTime = result.getString("IntTime");
//					String ftpip = result.getString("FtpIp");
//					String ftpuser = result.getString("FtpIp");
//					String ftppwd = result.getString("FtpPwd");
					user.deptcode = result.getString("DeptCode").trim();
					user.deptname = result.getString("DeptName").trim();
					user.logpwd = result.getString("PassWord").trim();
					user.macid = result.getString("MacID").trim();
					user.indentname = result.getString("Indentname").trim();
					user.maxord = result.getInt("MaxSizeNum");
					user.inttime = result.getInt("IntTime");
					user.upip = result.getString("FtpIp").trim();
					user.ftpuser = result.getString("FtpUser").trim();
					user.ftppwd = result.getString("FtpPwd").trim();
					sppEt.putString("indentname", result.getString("Indentname").trim());
					sppEt.putString("ftp_url", result.getString("FtpIp").trim());
					sppEt.putString("ftp_username", result.getString("FtpUser").trim());
					sppEt.putString("ftp_password", result.getString("FtpPwd").trim());
					sppEt.putString("report_url", result.getString("HttpAddr").trim());
					sppEt.commit();
					
					et.putString("indentname", result.getString("Indentname").trim());
					et.putString("shuliang", result.getString("shuliang").trim());
					et.putString("jinge", result.getString("jinge").trim());
					et.putString("deptname", result.getString("DeptName").trim());
					et.putString("departcode", result.getString("DeptCode").trim());
					et.commit();
					getContentResolver().delete(User.CONTENT_URI, null, null);
					getContentResolver().insert(User.CONTENT_URI, user.toContentValues());
					mLoading.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
					mLoading.dismiss();
				}
			}
			
			@Override
			public void onPostDataStart() {
				Log.e("==", "+++++++++++++++++++++++++++++++++++++++++++++");
			}
			
			@Override
			public void onPostDataError(ServerResponse response) {
				JSONObject result = response.result;
				try {
					String msg = result.getString("msg");
					AlertUtils.toastMsg(IndexActivity.this, msg);
					mLoading.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
					mLoading.dismiss();
				}
			}
			
			@Override
			public void onPostDataComplete(ServerResponse response) {
			}

			@Override
			public void onUpdateProgressMax(int value) {
				
			}

			@Override
			public void onUpdateProgressValue(int value) {
				// TODO Auto-generated method stub
				
			}
		}).execute(new JSONObject());
	}
    
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_NETWORK:
			mLoading = ProgressDialog.show(IndexActivity.this, "注册","");
			mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mLoading.setCancelable(false);
			return mLoading;
			
			default:
				break;
		}
		return null;
	}
}