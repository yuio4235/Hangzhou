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
import com.as.order.activity.BoduanZongheAnalysisActivity;
import com.as.order.activity.ChimaZongheAnalysisActivity;
import com.as.order.activity.DaLeiZongheAnalysisActivity;
import com.as.order.activity.JiagedaiZongheAnalysisActivity;
import com.as.order.activity.ShangxiazuangZongheAnalysisAcitivity;
import com.as.order.activity.XiaoleiZongheAnalysisActivity;
import com.as.order.activity.YanseZongheAnalysisActivity;
import com.as.order.activity.ZhutiZongheAnalysisActivity;
import com.as.order.dao.BoduanFenxiDAO;
import com.as.order.dao.ChimaFenxiDAO;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.order.dao.JiagedaiFenxiDAO;
import com.as.order.dao.SxzFenxiDAO;
import com.as.order.dao.XiaoleiFenxiDAO;
import com.as.order.dao.YanseFenxiDAO;
import com.as.order.dao.ZhutiFenxiDAO;

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
			+(TextUtils.isEmpty(where) ? "" : " and " + where)
			+" group by sawarecode.[waretypeid] ";
		List<DaleiFenxiDAO> data = new ArrayList<DaleiFenxiDAO>();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					DaleiFenxiDAO dao = new DaleiFenxiDAO();
					dao.setDalei(cursor.getString(DaLeiZongheAnalysisActivity.INDEX_DALEI));
					dao.setAmount(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_PRICE));
					dao.setWareCnt(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_CNT));
					dao.setWareAll(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_ALL));
					data.add(dao);
					cursor.moveToNext();
				}
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
	public static Map<String, Double> chartXiaoleiFenxi(Context context, String where, int opt) {
		String sql = 
			" select "
			+ " (select type1 from type1 where rtrim(id) = rtrim(sawarecode.id)) xiaolei, "
			+ " sum(saindent.warenum) amount, "
			+ " sum(saindent.warenum*sawarecode.retailprice) price, "
			+ " count(distinct sawarecode.warecode) ware_cnt, "
			+ " (select count(warecode) from sawarecode b where rtrim(b.id) = rtrim(sawarecode.id)) ware_all "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode) "
			+ " and rtrim(saindent.departcode) = '"+getUserAccount(context)+"' "
			+ " and saindent.warenum > 0 "
			+ (TextUtils.isEmpty(where) ? "" : " and " + where )
			+ " group by sawarecode.id ";
		List<XiaoleiFenxiDAO> data = new ArrayList<XiaoleiFenxiDAO>();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					XiaoleiFenxiDAO dao = new XiaoleiFenxiDAO();
					dao.setXiaolei(cursor.getString(XiaoleiZongheAnalysisActivity.INDEX_XIAOLEI));
					dao.setAmount(cursor.getInt(XiaoleiZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(XiaoleiZongheAnalysisActivity.INDEX_PRICE));
					dao.setWareCnt(cursor.getInt(XiaoleiZongheAnalysisActivity.INDEX_WARECNT));
					dao.setWareAll(cursor.getInt(XiaoleiZongheAnalysisActivity.INDEX_WAREALL));
					data.add(dao);
					cursor.moveToNext();
				}
				
			}
		} finally {
			if(cursor != null)  {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		Map<String, Double> returnData = new HashMap<String, Double>();
		if(opt == ZKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getXiaolei(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getXiaolei(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getXiaolei(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getXiaolei(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getXiaolei(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
	
	/**
	 * 主题分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartZhutiFenxi(Context context, String where, int opt) {
		String sql = 
		      " select sawarecode.style, "
			+ " sum(saindent.warenum) amount ,"
			+ " sum(saindent.warenum*sawarecode.retailprice) price, "
			+ " count(distinct saindent.warecode) ware_cnt, "
			+ " (select count(warecode) from sawarecode b where rtrim(sawarecode.style) = rtrim(b.style)) ware_all "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode) "
			+ " and saindent.departcode = '"+getUserAccount(context)+"' "
			+ " and saindent.warenum > 0 "
			+ (TextUtils.isEmpty(where) ? "" : " and " + where) 
			+ " group by sawarecode.style ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		List<ZhutiFenxiDAO> data = new ArrayList<ZhutiFenxiDAO>();
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					ZhutiFenxiDAO dao = new ZhutiFenxiDAO();
					dao.setZhuti(cursor.getString(ZhutiZongheAnalysisActivity.INDEX_ZHUTI));
					dao.setWareAll(cursor.getInt(ZhutiZongheAnalysisActivity.INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(ZhutiZongheAnalysisActivity.INDEX_WARECNT));
					dao.setAmount(cursor.getInt(ZhutiZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(ZhutiZongheAnalysisActivity.INDEX_PRICE));
					data.add(dao);
					cursor.moveToNext();
				}
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
				returnData.put(data.get(i).getZhuti(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getZhuti(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getZhuti(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getZhuti(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getZhuti(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
	
	/**
	 * 波段分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartBoduanFenxi(Context context, String where, int opt) {
		String sql = 
			"select (select rtrim(paraconnent) from sapara where rtrim(para) = rtrim(sawarecode.state) AND trim(paratype) = 'PD') boduan, "
			+ " sum(saindent.warenum) amount, "
			+ " sum(saindent.warenum*retailprice) price, "
			+ " count(distinct saindent.warecode) ware_cnt, "
			+ " (select count(warecode) from sawarecode b where rtrim(b.state) = rtrim(sawarecode.state)) ware_all "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode) "
			+ " and saindent.warenum > 0 "
			+ " and saindent.departcode = '"+getUserAccount(context)+"'"
			+ (TextUtils.isEmpty(where) ? "" : " and " + where )
			+ " group by sawarecode.state";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		List<BoduanFenxiDAO> data = new ArrayList<BoduanFenxiDAO>();
		try  {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					BoduanFenxiDAO dao = new BoduanFenxiDAO();
					dao.setBoduan(cursor.getString(BoduanZongheAnalysisActivity.INDEX_BODUAN));
					dao.setAmount(cursor.getInt(BoduanZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(BoduanZongheAnalysisActivity.INDEX_PRICE));
					dao.setWareCnt(cursor.getInt(BoduanZongheAnalysisActivity.INDEX_WARECNT));
					dao.setWareAll(cursor.getInt(BoduanZongheAnalysisActivity.INDEX_WAREALL));
					data.add(dao);
					cursor.moveToNext();
				}
			}
		} finally {
			if(db != null) {
				db.close();
			}
			if(cursor != null) {
				cursor.close();
			}
		}
		Map<String, Double> returnData = new HashMap<String, Double>();
		if(opt == ZKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getBoduan(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getBoduan(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getBoduan(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getBoduan(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getBoduan(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
	
	/**
	 * 颜色分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartYanseFenxi(Context context, String where, int opt) {
		String sql = " select sacolorcode.[colorname] yanse, "
			+" sum(saindent.[warenum]) amount, "
			+" sum(saindent.[warenum]*sawarecode.[retailprice]) price, "
			+" count(distinct saindent.[warecode]) ware_cnt, "
			+" (Select count(B.warecode) From saindent  B,sawarecode C "
			+" where Rtrim(B.colorcode) = Rtrim(saindent.colorcode) "
			+" And Rtrim(B.departcode) = '"+getUserAccount(context)+"' "
			+" And Rtrim(B.warecode) = Rtrim(C.warecode)) ware_all "
			+" from saindent, sawarecode, sacolorcode "
			+" where "
			+"  Rtrim(saindent.colorcode)= Rtrim(sacolorcode.colorcode)  "
			+" and "
			+"  Rtrim(saindent.warecode)= Rtrim(sawarecode.warecode) "
			+" and "
			+"  saindent.departcode= '"+getUserAccount(context)+"' "
			+" and "
			+" saindent.[warenum] > 0 "
			+(TextUtils.isEmpty(where) ? "" : " and " + where)
			+" group by saindent.[colorcode], sacolorcode.[colorname] ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		List<YanseFenxiDAO> data = new ArrayList<YanseFenxiDAO>();
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					YanseFenxiDAO dao = new YanseFenxiDAO();
					dao.setYanse(cursor.getString(YanseZongheAnalysisActivity.INDEX_YANSE));
					dao.setAmount(cursor.getInt(YanseZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(YanseZongheAnalysisActivity.INDEX_PRICE));
					dao.setWareAll(cursor.getInt(YanseZongheAnalysisActivity.INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(YanseZongheAnalysisActivity.INDEX_WARECNT));
					data.add(dao);
					cursor.moveToNext();
				}
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
				returnData.put(data.get(i).getYanse(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getYanse(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getYanse(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getYanse(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getYanse(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
	
	/**
	 * 尺码分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartChaimaFenxi(Context context, String where, int opt) {
		String sql = " SELECT "
			+ " (select paraconnent from sapara, sawarecode b, showsize where sapara.[paratype] = 'CM' and sapara.[para] = showsize.[type] and showsize.[type] = b.[flag] and b.[warecode] = sawarecode.warecode) chimazu, "
			+ "        view_ord_list.[sizecode] sizecode,       "
			+ "        sum(view_ord_list.[amount]) amount, "
			+ "        sum(view_ord_list.[money]) price,   "
			+ "        count(distinct view_ord_list.[warecode]) ware_cnt, "
			+ " (select count(warecode) from sawarecode c where rtrim(c.flag) = rtrim(sawarecode.flag)) ware_all "
			+" from "
			+ "     sawarecode, view_ord_list "
			+ " where  "
			+ "       sawarecode.[warecode] = view_ord_list.[warecode] "
			+ " and "
			+ "    view_ord_list.[amount] > 0 "
			+ " and view_ord_list.departcode = '"+getUserAccount(context)+"'"
			+ (TextUtils.isEmpty(where) ? "" : " and " + where)
			+ " group by sawarecode.[flag],  view_ord_list.[sizecode] ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		List<ChimaFenxiDAO> data = new ArrayList<ChimaFenxiDAO>();
		try  {
			if(cursor != null && cursor.moveToNext()) {
				while(!cursor.isAfterLast()) {
					ChimaFenxiDAO dao = new ChimaFenxiDAO();
					dao.setChimazu(cursor.getString(ChimaZongheAnalysisActivity.INDEX_CHIMAZU));
					dao.setChima(cursor.getString(ChimaZongheAnalysisActivity.INDEX_SIZECODE));
					dao.setAmount(cursor.getInt(ChimaZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setWareAll(cursor.getInt(ChimaZongheAnalysisActivity.INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(ChimaZongheAnalysisActivity.INDEX_WARECNT));
					dao.setPrice(cursor.getInt(ChimaZongheAnalysisActivity.INDEX_PRICE));
					data.add(dao);
					cursor.moveToNext();
				}
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
				returnData.put(data.get(i).getChima(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getChima(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getChima(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getChima(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getChima(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
	
	/**
	 * 价格带分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartJiagedaiFenxi(Context context, String where, int opt)  {
		String sql = " SELECT "
			+ " sawarecode.pricecomment, "
			+ " sum(saindent.warenum) amount, "
			+ " sum(saindent.warenum * sawarecode.retailprice) price, "
			+ " count(distinct saindent.warecode ) ware_cnt, "
			+ " (select count(warecode) from sawarecode b where rtrim(sawarecode.pricecomment) = rtrim(b.pricecomment)) ware_all "
			+ " from saindent, sawarecode "
			+ " where rtrim(saindent.warecode) = rtrim(sawarecode.warecode ) "
			+ " and saindent.departcode = '"+getUserAccount(context)+"' "
			+ " and saindent.warenum > 0 "
			+ (TextUtils.isEmpty(where) ? "" : " and " + where)
			+ " group by sawarecode.pricecomment ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		List<JiagedaiFenxiDAO> data = new ArrayList<JiagedaiFenxiDAO>();
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					JiagedaiFenxiDAO dao = new JiagedaiFenxiDAO();
					dao.setJiagedai(cursor.getString(JiagedaiZongheAnalysisActivity.INDEX_JIAGEDAI));
					dao.setWareAll(cursor.getInt(JiagedaiZongheAnalysisActivity.INDEX_WAREALL));
					dao.setWareCnt(cursor.getInt(JiagedaiZongheAnalysisActivity.INDEX_WARECNT));
					dao.setAmount(cursor.getInt(JiagedaiZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(JiagedaiZongheAnalysisActivity.INDEX_PRICE));
					data.add(dao);
					cursor.moveToNext();
				}
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
				returnData.put(data.get(i).getJiagedai(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getJiagedai(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getJiagedai(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getJiagedai(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getJiagedai(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
	
	/**
	 * 上下装分析
	 * @param context
	 * @param where
	 * @return
	 */
	public static Map<String, Double> chartSxzFenxi(Context context, String where, int opt)  {
		String sql = " SELECT sawarecode.sxz, "
			+" sum(saindent.warenum) amount, "
			+" sum(saindent.warenum*sawarecode.retailprice) price, "
			+" count(distinct saindent.warecode) ware_cnt, "
			+" (select count(warecode) from sawarecode b where rtrim(sawarecode.sxz) = rtrim(b.sxz)) ware_all "
			+" from saindent, sawarecode "
			+" where rtrim(saindent.warecode) = rtrim(sawarecode.warecode) "
			+" and "
			+" saindent.departcode = '"+getUserAccount(context)+"'"
			+" and "
			+" saindent.warenum > 0 "
			+(TextUtils.isEmpty(where) ? "" : " and " + where)
			+" group by sawarecode.sxz ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		Cursor cursor = db.rawQuery(sql, null);
		List<SxzFenxiDAO> data = new ArrayList<SxzFenxiDAO>();
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					SxzFenxiDAO dao = new SxzFenxiDAO();
					dao.setSxz(cursor.getString(ShangxiazuangZongheAnalysisAcitivity.INDEX_SXZ));
					dao.setAmount(cursor.getInt(ShangxiazuangZongheAnalysisAcitivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(ShangxiazuangZongheAnalysisAcitivity.INDEX_PRICE));
					dao.setWareCnt(cursor.getInt(ShangxiazuangZongheAnalysisAcitivity.INDEX_WARECNT));
					dao.setWareAll(cursor.getInt(ShangxiazuangZongheAnalysisAcitivity.INDEX_WAREALL));
					data.add(dao);
					cursor.moveToNext();
				}
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
				returnData.put(data.get(i).getSxz(), (double)data.get(i).getWareAll());
			}
		} else if(opt == DHKZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getSxz(), (double)data.get(i).getWareCnt());
			}
		} else if(opt == DLZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getSxz(), (double)data.get(i).getAmount());
			}
		} else if(opt == JEZB) {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getSxz(), (double)data.get(i).getPrice());
			}
		} else {
			for(int i=0; i<data.size(); i++) {
				returnData.put(data.get(i).getSxz(), (double)data.get(i).getWareAll());
			}
		}		
		return returnData;
	}
}
