package com.as.order.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.as.order.sync.FileUploader;
import com.as.order.sync.SaIndentSyncThread;
import com.as.ui.utils.AlertUtils;

public class IndentSyncService extends Service {

	private static final String TAG = "IndentSyncService";
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	private Handler mHandler;
	private Timer timer;
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			mHandler.post(new Runnable(){

				@Override
				public void run() {
					AlertUtils.toastMsg(getApplicationContext(), "��̨�� " + formatter.format(new Date()) +" ��ʼͬ������,ͬ�����̲�Ӱ��������");
					if(FileUploader.createSaIndentFile(getApplicationContext())) {
						AlertUtils.toastMsg(getApplicationContext(), "��̨��" + formatter.format(new Date()) + " ��ʼ����������Ͷ���");
						if(FileUploader.uploadSaIndent(getApplicationContext())) {
							AlertUtils.toastMsg(getApplicationContext(), "���ض����Ѿ��� " + formatter.format(new Date()) + " ���͵�������");
						}
					} else {
						AlertUtils.toastMsg(getApplicationContext(), formatter.format(new Date()) +  " ���涩���ɹ�");
					}
				}});
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler(Looper.getMainLooper());
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
		timer = new Timer("saindentservice");
		timer.scheduleAtFixedRate(task, 2000*60, 1000*60);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
	}

}
