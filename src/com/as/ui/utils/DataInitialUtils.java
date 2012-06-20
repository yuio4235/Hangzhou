package com.as.ui.utils;

import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaSizeSet;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataInitialUtils {

	public static void initSaIndent(Context context, String user) {
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		int i=0;
		String sql = " INSERT into saIndent(indentno,DepartCode,WareCode,ColorCode,InputDate) "
			+" SELECT '"+(System.currentTimeMillis() + (++i))+"', '"+user+"', saWareCode.warecode,colorcode,datetime('now', 'localtime') "
			+" FROM saWareCode,saware_color "
			+" WHERE rTrim(saWareCode.WareCode) = rTrim(saWare_Color.WareCode) "
			+" AND NOT EXISTS(SELECT saWareCode.WareCode "
			+" FROM saIndent "
			+" WHERE rTrim(saIndent.warecode) = rTrim(saWareCode.WareCode) "
			+" AND rTrim(saWareCode.WareCode) = rTrim(saWare_Color.WareCode) "
			+" 	AND rtrim(saindent.colorcode) = rTrim(saWare_color.colorcode) "
			+" 	AND rTrim(saIndent.DepartCode) = '"+user+"')"
			+"	ORDER BY saWareCode.warecode,colorcode ";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}
	
	public static void InitSaSizeSet(Context context) {
		SaSizeSet sasizeset = new SaSizeSet();
		sasizeset.sizeGroup = "11";
		sasizeset.s01 = 10;
		sasizeset.s02 = 10;
		sasizeset.s03 = 10;
		sasizeset.s04 = 10;
		sasizeset.s05 = 10;
		sasizeset.s06 = 10;
		sasizeset.s07 = 10;
		sasizeset.s08 = 10;
		sasizeset.s09 = 10;
		sasizeset.s10 = 10;
		sasizeset.s11 = 10;
		sasizeset.s12 = 10;
		sasizeset.s13 = 10;
		sasizeset.s14 = 10;
		sasizeset.s15 = 10;
		sasizeset.s16 = 10;
		sasizeset.s17 = 10;
		sasizeset.s18 = 10;
		sasizeset.s19 = 10;
		sasizeset.s20 = 10;
		context.getContentResolver().insert(SaSizeSet.CONTENT_URI, sasizeset.toContentValues());
	}
}
