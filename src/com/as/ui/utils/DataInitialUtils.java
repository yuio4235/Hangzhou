package com.as.ui.utils;

import com.as.db.provider.AsProvider;

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
}
