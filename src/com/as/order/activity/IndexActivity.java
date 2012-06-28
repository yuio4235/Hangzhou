package com.as.order.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.as.db.provider.AsContent.User;
import com.as.order.R;
import com.as.order.net.JsonAsyncTask;
import com.as.order.net.ResultListener;
import com.as.order.net.ServerResponse;
import com.as.ui.utils.AlertUtils;

public class IndexActivity extends Activity implements OnClickListener{
	
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
        
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
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
		new JsonAsyncTask("http://dlndl.vicp.cc/WS-Order/OrderRegister.ashx?macip=" + macAddr, new ResultListener() {
			
			@Override
			public void onProgressMessage(String message) {
				
			}
			
			@Override
			public void onPostDataSuccess(ServerResponse response) {
				JSONObject result = response.result;
				Log.e("=", "json: " + result);
				try {
					User user = new User();
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
					user.deptcode = result.getString("DeptCode");
					user.deptname = result.getString("DeptName");
					user.logpwd = result.getString("PassWord");
					user.macid = result.getString("MacID");
					user.indentname = result.getString("Indentname");
					user.maxord = result.getInt("MaxSizeNum");
					user.inttime = result.getInt("IntTime");
					user.upip = result.getString("FtpIp");
					user.ftpuser = result.getString("FtpUser");
					user.ftppwd = result.getString("FtpPwd");
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
		}).execute(new JSONObject());
	}
    
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_NETWORK:
			mLoading = ProgressDialog.show(IndexActivity.this, "注册","");
			mLoading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mLoading.setCancelable(true);
			return mLoading;
			
			default:
				break;
		}
		return null;
	}
}