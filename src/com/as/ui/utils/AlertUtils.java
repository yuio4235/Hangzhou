package com.as.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.as.order.R;

public class AlertUtils {

	public static void toastMsg(Context context, String msg) {
		View toastV = LayoutInflater.from(context).inflate(R.layout.toast, null);
		Toast toast = new Toast(context);
		toast.setView(toastV);
		TextView tv = (TextView) toastV.findViewById(R.id.toast_txt);
		tv.setText(msg);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
