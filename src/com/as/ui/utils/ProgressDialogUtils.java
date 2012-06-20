package com.as.ui.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtils {

	public static final ProgressDialog newProgressDialog(Context ctx, String title, String message) {
		ProgressDialog dlg = new ProgressDialog(ctx);
		dlg.setTitle(title);
		dlg.setMessage(message);
		return dlg;
	}
}
