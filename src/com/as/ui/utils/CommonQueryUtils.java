package com.as.ui.utils;

/**
 * 
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaWareType;
import com.as.db.provider.AsContent.SaWareTypeColumns;

public class CommonQueryUtils {
	
	private static final String TAG = "CommonQueryUtils";
	
	/**
	 * waretypeid relates to waretypeid in waretype table
	 * @param context
	 * @param waretypename
	 * @return
	 */
	public static String getWareTypeIdByName(Context context, String waretypename) {
		String sql = " select "
			+ SaWareTypeColumns.WARETYPEID + " from " + SaWareType.TABLE_NAME + " where " + SaWareTypeColumns.WARETYPENAME
			+ " = '" + waretypename + "' ";
		String waretypeid = commonQuery(context, sql);
		return waretypeid;
	}
	
	/**
	 * get id 
	 * @param context
	 * @param type1
	 * @return
	 */
	public static String getIdByType1(Context context, String type1) {
		String sql = " select id from type1 where type1 = '"+type1+"' ";
		String id = commonQuery(context, sql);
		return id;
	}
	
	/**
	 * para type 'PD' in saparatype
	 * @param context
	 * @param boduan
	 * @return
	 */
	public static String getStateByName(Context context, String boduan) {
		String sql = " select para from sapara where paratype = 'PD' and paraconnent= '"+boduan+"' ";
		String state = commonQuery(context, sql);
		return state;
	}
	
	
	private static String commonQuery(Context context, String sql) {
		Log.e(TAG, "sql: " + sql);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				String value = cursor.getString(0);
				return value;
			}
		} finally {
			if(db != null) {
				db.close();
			}
			if(cursor != null) {
				cursor.close();
			}
		}
		return "";
	}
}
