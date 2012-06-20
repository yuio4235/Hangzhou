package com.as.ui.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.as.db.provider.AsProvider;
import com.as.order.R;

public class AnaUtils {

	/**
	 * 获取订货总的款数
	 * @param context
	 * @return
	 */
	public static int getTotalWareCnt(Context context) {
		int totalWareCnt = 0;
		String sql = context.getString(R.string.total_ware_cnt);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				totalWareCnt = cursor.getInt(0);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		return totalWareCnt;
	}
	
	/**
	 * 获取总的订量
	 * @param context
	 * @return
	 */
	public static int getTotalWareNum(Context context) {
		int totalWareNum = 0;
		String sql = context.getString(R.string.total_ware_num);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				totalWareNum = cursor.getInt(0);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		return totalWareNum;
	}
	
	/**
	 * 获取订货总额
	 * @param context
	 * @return
	 */
	public static int getTotalPrice(Context context) {
		int totalPrice = 0;
		String sql = context.getString(R.string.total_price);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				totalPrice = cursor.getInt(0);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		return totalPrice;
	}
	
	public static int getTotalOrderedWareCnt(Context context) {
		int totalOrderedWareCnt = 0;
		String sql = context.getString(R.string.total_ordered_ware_cnt);
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor!=null && cursor.moveToFirst()) {
				totalOrderedWareCnt = cursor.getInt(0);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		return totalOrderedWareCnt;
	}
}
