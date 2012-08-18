package com.as.order.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.as.order.sync.FileUploader;
import com.as.order.sync.SaIndentSyncThread;
import com.as.ui.utils.AlertUtils;

public class IndentSyncService extends Service {

	private static final String TAG = "IndentSyncService";
	
	private int p = 0;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Timer timer;
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			Log.e(TAG, "============================= syncing ===============================");
//			AlertUtils.toastMsg(getApplicationContext(), "后台于 " + formatter.format(new Date()) +" 开始同步订单,同步过程不影响您订货");
			if(FileUploader.createSaIndentFile(getApplicationContext())) {
//				AlertUtils.toastMsg(getApplicationContext(), "后台与" + formatter.format(new Date()) + " 开始向服务器传送订单");
				if(FileUploader.uploadSaIndent(getApplicationContext())) {
//					AlertUtils.toastMsg(getApplicationContext(), "本地订单已经于 " + formatter.format(new Date()) + " 传送到服务器");
				}
			} else {
//				AlertUtils.toastMsg(getApplicationContext(), formatter.format(new Date()) +  " 保存订单成功");
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(this);
		p = Integer.valueOf(spp.getString("saindent_upload_time", "20"));
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(timer != null) {
//			timer.cancel();
			return super.onStartCommand(intent, flags, startId);
		}
		timer = new Timer("saindentservice");
		timer.scheduleAtFixedRate(task, 2000*60, p*60*1000);
//		timer.scheduleAtFixedRate(task, 2000, 20000);
		Log.e(TAG, "===================== service started  ============================");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		timer.cancel();
		timer = null;
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "======================= service detroyed ===========================");
		timer.cancel();
	}

}
