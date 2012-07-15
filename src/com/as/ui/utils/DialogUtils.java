package com.as.ui.utils;

import android.content.Context;
import android.widget.EditText;

import com.as.order.R;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.AsMultiSelectDialog;
import com.as.order.ui.ListDialogListener;

public class DialogUtils {
	
	public static AsListDialog makeListDialog(Context context, EditText et, String[] data) {
		AsListDialog dialog = new AsListDialog(context, R.style.AsDialog, data);
		return dialog;
	}
	
	public static AsMultiSelectDialog makeMultiSelectDialog(Context context, EditText et, String[] data) {
		AsMultiSelectDialog dialog = new AsMultiSelectDialog(context, R.style.AsDialog, data);
		return dialog;
	}
	
}
