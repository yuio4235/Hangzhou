package com.as.ui.utils;

import com.as.db.provider.AsProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
				addNewSawarecodeIntoSaIndent(db, userAccount);
				db.setTransactionSuccessful();
				db.endTransaction();
			}finally {
				db.close();
			}
		}
	}
	
	private static void removeSaIndentNotInSaWareCode(SQLiteDatabase db) {
		Log.e("==", "====================== remove =========================");
//		String sql = " delete from saindent where  RTRIM(LTRIM(warecode))+RTRIM(LTRIM(colorcode)) not in  (select RTRIM(LTRIM(warecode))+RTRIM(LTRIM(colorcode)) from saware_color) ";
		String sql = " delete from saindent where trim(warecode)||trim(colorcode) not in (select trim(warecode)||trim(colorcode) from saware_color)";
		db.execSQL(sql);
	}
	
	private static void addSaWareCodeNotInSaIndent(SQLiteDatabase db, String departCode) {
		Log.e("==", "====================== add =========================");
		String sql = " insert into saindent(departcode, warecode, colorcode) "
			+ " select '"+departCode+"', warecode, colorcode "
			+ " from saware_color "
//			+ " where   RTRIM(LTRIM(warecode))+RTRIM(LTRIM(colorcode)) not in "
			+ " where trim(warecode)||trim(colorcode) not in "
//			+ "         (select RTRIM(LTRIM(warecode))+RTRIM(LTRIM(colorcode)) from saindent) "
			+ " (select trim(warecode)||trim(colorcode) from saindent)  ";
		Log.e("==", "sql: " + sql);
		db.execSQL(sql);
	}
	
	private static void addNewSawarecodeIntoSaIndent(SQLiteDatabase db, String departCode) {
		int i =0;
		String sql = " INSERT into saIndent(indentno,DepartCode,WareCode,ColorCode,InputDate) "
			+" SELECT '"+(System.currentTimeMillis() + (++i))+"', '"+departCode+"', saWareCode.warecode,colorcode,datetime('now', 'localtime') "
			+" FROM saWareCode,saware_color "
			+" WHERE rTrim(saWareCode.WareCode) = rTrim(saWare_Color.WareCode) "
			+" AND NOT EXISTS(SELECT saWareCode.WareCode "
			+" FROM saIndent "
			+" WHERE rTrim(saIndent.warecode) = rTrim(saWareCode.WareCode) "
			+" AND rTrim(saWareCode.WareCode) = rTrim(saWare_Color.WareCode) "
			+" 	AND rtrim(saindent.colorcode) = rTrim(saWare_color.colorcode) "
			+" 	AND rTrim(saIndent.DepartCode) = '"+departCode+"')"
			+"	ORDER BY saWareCode.warecode,colorcode ";
		
		db.execSQL(sql);
	}
}
