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
//		AlertUtils.toastMsg(mContext, "��̨�� " + formatter.format(new Date()) +" ��ʼͬ������,ͬ�����̲�Ӱ��������");
//		Looper.loop();
		Message msg = mHandler.obtainMessage();
		msg.obj = "��̨�� " + formatter.format(new Date()) +" ��ʼͬ������,ͬ�����̲�Ӱ��������";
		msg.sendToTarget();
		if(FileUploader.createSaIndentFile(mContext)) {
//			AlertUtils.toastMsg(mContext, "��̨��" + formatter.format(new Date()) + " ��ʼ����������Ͷ���");
			Message msg1 = mHandler.obtainMessage();
			msg1.obj = "��̨��" + formatter.format(new Date()) + " ��ʼ����������Ͷ���";
			msg1.sendToTarget();
			if(FileUploader.uploadSaIndent(mContext)) {
//				AlertUtils.toastMsg(mContext, "���ض����Ѿ��� " + formatter.format(new Date()) + " ���͵�������");
				Message msg2 = mHandler.obtainMessage();
				msg2.obj = "���ض����Ѿ��� " + formatter.format(new Date()) + " ���͵�������";
				msg2.sendToTarget();
			}
		} else {
//			AlertUtils.toastMsg(mContext, formatter.format(new Date()) +  " ���涩���ɹ�");
			Message msg3 = mHandler.obtainMessage();
			msg3.obj = formatter.format(new Date()) +  " ���涩���ɹ�";
			msg3.sendToTarget();
		}
	}
}
