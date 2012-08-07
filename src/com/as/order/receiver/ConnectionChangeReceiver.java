package com.as.order.receiver;

import com.as.ui.utils.NetWorkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if(activeNetInfo != null) {
			Toast.makeText(context, "网络已连接", Toast.LENGTH_LONG).show();
			NetWorkUtils.netWorkStatus = true;
		} else {
			Toast.makeText(context, "网络已断开", Toast.LENGTH_LONG).show();
			NetWorkUtils.netWorkStatus = false;
		}
	}

}
