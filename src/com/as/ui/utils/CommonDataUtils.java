package com.as.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.as.db.provider.AsContent;
import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaPara;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareType;
import com.as.db.provider.AsContent.SaWareTypeColumns;
import com.as.db.provider.AsContent.SawarecodeColumns;
import com.as.db.provider.AsContent.Type1;
import com.as.db.provider.AsContent.Type1Columns;
import com.as.order.activity.DaLeiZongheAnalysisActivity;
import com.as.order.dao.DaleiFenxiDAO;

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
	
	private static String getUserAccount(Context context) {
		SharedPreferences sp = context.getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String user_account = sp.getString("user_account", "");
		return user_account;
	}
	
	public static final int ZKZB = 1001;
	public static final int DHKZB = 1002;
	public static final int DLZB = 1003;
	public static final int JEZB = 1004;
	
	/**
	 * 大类分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartDaleiFenxi(Context context, String where, int opt) {
		String sql = ""
			+" SELECT "
			+" (select waretypename from sawaretype where rtrim(sawaretype.[waretypeid])  = rtrim(sawarecode.[waretypeid])) dalei, "
			+" sum(saindent.[warenum]) amount, "
			+" sum(saindent.[warenum]* retailprice) price,  "
			+" count(distinct saindent.[warecode]) ware_cnt,  "
			+" (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all "
			+" from saindent, sawarecode " 
			+" where rtrim(saindent.[warecode]) = rtrim(sawarecode.[warecode]) "
			+" and rtrim(saindent.[departcode]) = '"+getUserAccount(context)+"' "
			+" and saindent.[warenum] > 0 "
			+" and " + where
			+" group by sawarecode.[waretypeid] ";
		List<DaleiFenxiDAO> data = new ArrayList<DaleiFenxiDAO>();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				DaleiFenxiDAO dao = new DaleiFenxiDAO();
				dao.setDalei(cursor.getString(DaLeiZongheAnalysisActivity.INDEX_DALEI));
				dao.setAmount(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_AMOUNT));
				dao.setPrice(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_PRICE));
				dao.setWareCnt(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_CNT));
				dao.setWareAll(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_ALL));
				data.add(dao);
				cursor.moveToNext();
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			
			if(db != null) {
				db.close();
			}
		}
		Map<String, Double> returnData = new HashMap<String, Double>();
		if(opt == ZKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getDalei(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getDalei(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getDalei(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getDalei(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getDalei(), (double)data.get(i).getWareAll());
			}
		}
		return returnData;
	}
	
	/**
	 * 小类分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartXiaoleiFenxi(Context context, String where) {
		return null;
	}
	
	/**
	 * 主题分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartZhutiFenxi(Context context, String where) {
		return null;
	}
	
	/**
	 * 波段分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartBoduanFenxi(Context context, String where) {
		return null;
	}
	
	/**
	 * 颜色分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartYanseFenxi(Context context, String where) {
		return null;
	}
	
	/**
	 * 尺码分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartChaimaFenxi(Context context, String where) {
		return null;
	}
	
	/**
	 * 价格带分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartJiagedaiFenxi(Context context, String where)  {
		return null;
	}
	
	/**
	 * 上下装分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static double[] chartSxzFenxi(Context context, String where)  {
		return null;
	}
}
