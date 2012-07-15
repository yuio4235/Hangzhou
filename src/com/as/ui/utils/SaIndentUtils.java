package com.as.ui.utils;

import com.as.db.provider.AsProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

public class SaIndentUtils {

	public static void checkSaIndents(Context context) {
		SharedPreferences sp = context.getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String userAccount = sp.getString("user_account", "");
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		if(db != null) {
			try {
				db.beginTransaction();
				removeSaIndentNotInSaWareCode(db);
				addSaWareCodeNotInSaIndent(db, userAccount);
			}finally {
				db.close();
			}
		}
	}
	
	private static void removeSaIndentNotInSaWareCode(SQLiteDatabase db) {
		String sql = " delete from saindent where warecode not in ( select distinct warecode from sawarecode )";
		db.execSQL(sql);
	}
	
	private static void addSaWareCodeNotInSaIndent(SQLiteDatabase db, String departCode) {
		String sql = " insert into saindent(departcode, warecode, colorcode) "
			+ " select '"+departCode+"', warecode, colorcode "
			+ " from saware_color "
			+ " where   RTRIM(LTRIM(warecode))+RTRIM(LTRIM(colorcode)) not in "
			+ "         (select RTRIM(LTRIM(warecode))+RTRIM(LTRIM(colorcode)) from saindent) ";
		db.execSQL(sql);
	}
}
