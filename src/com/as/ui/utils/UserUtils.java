package com.as.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtils {

	public static String getUserAccount(Context context) {
		SharedPreferences sp = context.getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String userAccount = sp.getString("user_account", "");
		return userAccount;
	}
}
