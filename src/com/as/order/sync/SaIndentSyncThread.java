package com.as.order.sync;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.as.ui.utils.AlertUtils;

public class SaIndentSyncThread extends Thread {

	private static final String REMOTE_HOST = "dlndl.vicp.cc";
	private static final String USER_NAME = "dln";
	private static final String PASSWORD = "dlnfeiyang";
	
	private Context mContext;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd MM:ss:hh");
	
	public SaIndentSyncThread(Context context) {
		mContext = context;
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			String info = (String)msg.obj;
			AlertUtils.toastMsg(mContext, info);
		};
	};
	
	@Override
	public void run() {
		super.run();
//		AlertUtils.toastMsg(mContext, "后台于 " + formatter.format(new Date()) +" 开始同步订单,同步过程不影响您订货");
//		Looper.loop();
		Message msg = mHandler.obtainMessage();
		msg.obj = "后台于 " + formatter.format(new Date()) +" 开始同步订单,同步过程不影响您订货";
		msg.sendToTarget();
		if(FileUploader.createSaIndentFile(mContext)) {
//			AlertUtils.toastMsg(mContext, "后台与" + formatter.format(new Date()) + " 开始向服务器传送订单");
			Message msg1 = mHandler.obtainMessage();
			msg1.obj = "后台与" + formatter.format(new Date()) + " 开始向服务器传送订单";
			msg1.sendToTarget();
			if(FileUploader.uploadSaIndent(mContext)) {
//				AlertUtils.toastMsg(mContext, "本地订单已经于 " + formatter.format(new Date()) + " 传送到服务器");
				Message msg2 = mHandler.obtainMessage();
				msg2.obj = "本地订单已经于 " + formatter.format(new Date()) + " 传送到服务器";
				msg2.sendToTarget();
			}
		} else {
//			AlertUtils.toastMsg(mContext, formatter.format(new Date()) +  " 保存订单成功");
			Message msg3 = mHandler.obtainMessage();
			msg3.obj = formatter.format(new Date()) +  " 保存订单成功";
			msg3.sendToTarget();
		}
	}
}
