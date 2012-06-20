package com.as.ui.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.as.db.provider.AsContent;
import com.as.db.provider.AsContent.SaPara;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareType;
import com.as.db.provider.AsContent.SaWareTypeColumns;
import com.as.db.provider.AsContent.SawarecodeColumns;
import com.as.db.provider.AsContent.Type1;
import com.as.db.provider.AsContent.Type1Columns;

public class CommonDataUtils {

	public static String[] getBoduan(Context context) {
		ArrayList<String> boduan = new ArrayList<String>();
		Uri u = SaPara.CONTENT_URI;
		Cursor cursor = context.getContentResolver().query(u, SaPara.CONTENT_PROJECTION, SaPara.PARATYPE + " = ?", new String[]{"PD"}, SaPara.RECORD_ID + " asc ");
		if(cursor != null && cursor.moveToFirst()) {
			try {
				while(!cursor.isAfterLast()) {
					boduan.add(cursor.getString(SaPara.CONTENT_PARACONNENT_COLUMN));
					cursor.moveToNext();
				}
			} finally {
				cursor.close();
			}
		}
		String[] boduanArr = new String[boduan.size()];
		return boduan.toArray(boduanArr);
	}
	
	//大类   SaWareType
	public static String[] getWareTypes(Context context) {
		List<String> waretypeids = new ArrayList<String>();
		Cursor cursor = context.getContentResolver().query(SaWareType.CONTENT_URI, SaWareType.CONTENT_PROJECTION, null, null, SaWareTypeColumns.WARETYPEID + " asc ");
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					waretypeids.add(cursor.getString(SaWareType.CONTENT_WARETYPENAME_COLUMN));
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		String[] names = new String[waretypeids.size()];
		return waretypeids.toArray(names);
	}
	
	//小类 type1
	public static String[] getType1s(Context context) {
		List<String> type1s = new ArrayList<String>();
		Cursor cursor = context.getContentResolver().query(AsContent.Type1.CONTENT_URI, Type1.CONTENT_PROJECTION, null, null, Type1Columns.IID + " asc ");
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					type1s.add(cursor.getString(Type1.CONTENT_TYPE1_COLUMN));
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		String[] type1names = new String[type1s.size()];
		return type1s.toArray(type1names);
	}
	
	// theme
	public static String[] getThemes(Context context) {
		List<String> themes = new ArrayList<String>();
		Cursor cursor = context.getContentResolver().query(AsContent.SaWareCode.CONTENT_URI, SaWareCode.CONTENT_PROJECTION, null, null, SawarecodeColumns.ID + " asc ");
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					String theme = cursor.getString(SaWareCode.CONTENT_STYLE_COLUMN);
					if(!TextUtils.isEmpty(theme)) {
						themes.add(theme);
					}
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		String[] themesArr = new String[themes.size()];
		return themes.toArray(themesArr);
	}
}
