package com.as.db.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.ToggleButton;

import com.as.db.provider.AsContent.SaColorCode;
import com.as.db.provider.AsContent.SaColorCodeColumns;
import com.as.db.provider.AsContent.SaIndent;
import com.as.db.provider.AsContent.SaIndentColumns;
import com.as.db.provider.AsContent.SaOrderTrget;
import com.as.db.provider.AsContent.SaOrderTrgetColumns;
import com.as.db.provider.AsContent.SaPara;
import com.as.db.provider.AsContent.SaParaColumns;
import com.as.db.provider.AsContent.SaSizeSet;
import com.as.db.provider.AsContent.SaSizeSetColumns;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareColor;
import com.as.db.provider.AsContent.SaWareColorColumns;
import com.as.db.provider.AsContent.SaWareGroup;
import com.as.db.provider.AsContent.SaWareGroupColumns;
import com.as.db.provider.AsContent.SaWareSize;
import com.as.db.provider.AsContent.SaWareSizeColumns;
import com.as.db.provider.AsContent.SaWareType;
import com.as.db.provider.AsContent.SaWareTypeColumns;
import com.as.db.provider.AsContent.SawarecodeColumns;
import com.as.db.provider.AsContent.ShowSize;
import com.as.db.provider.AsContent.ShowSizeColumns;
import com.as.db.provider.AsContent.Type1;
import com.as.db.provider.AsContent.Type1Columns;

public class AsProvider extends ContentProvider{
	
	private static final String TAG = "AsProvider";
	
	protected static final String DATABASE_NAME = "as.db";
	
	//version 3 add stPara
	public static final int DATABASE_VERSION = 3;
	
	public static final String AS_AUTHORITY = "com.as.order.provider";
	
	private static final int SAWARECODE_BASE = 0;
	private static final int SAWARECODE = SAWARECODE_BASE;
	private static final int SAWARECODE_ID = SAWARECODE_BASE + 1;
	private static final int SAWARECODE_WARECODE = SAWARECODE_BASE + 2;
	
	private static final int SACOLORCODE_BASE = 0x1000;
	private static final int SACOLORCODE = SACOLORCODE_BASE;
	private static final int SACOLORCODE_ID = SACOLORCODE_BASE + 1;
	
	private static final int SAWARECOLOR_BASE = 0x2000;
	private static final int SAWARECOLOR = SAWARECOLOR_BASE;
	private static final int SAWARECOLOR_ID = SAWARECOLOR_BASE + 1;
	
	private static final int SHOWSIZE_BASE = 0x3000;
	private static final int SHOWSIZE = SHOWSIZE_BASE;
	private static final int SHOWSIZE_ID = SHOWSIZE_BASE + 1;
	
	private static final int SAWARESIZE_BASE = 0x4000;
	private static final int SAWARESIZE = SAWARESIZE_BASE;
	private static final int SAWARESIZE_ID = SAWARESIZE_BASE + 1;
	
	private static final int SAPARA_BASE = 0x5000;
	private static final int SAPARA = SAPARA_BASE;
	private static final int SAPARA_ID = SAPARA_BASE + 1;
	
	private static final int SAWARETYPE_BASE = 0x6000;
	private static final int SAWARETYPE = SAWARETYPE_BASE;
	private static final int SAWARETYPE_ID = SAWARETYPE_BASE + 1;
	
	private static final int TYPE1_BASE = 0x7000;
	private static final int TYPE1 = TYPE1_BASE;
	private static final int TYPE1_ID = TYPE1_BASE + 1;
	
	private static final int SAINDENT_BASE = 0x8000;
	private static final int SAINDENT = SAINDENT_BASE;
	private static final int SAINDENT_ID = SAINDENT_BASE + 1;
	
	private static final int SAWAREGROUP_BASE = 0x9000;
	private static final int SAWAREGROUP = SAWAREGROUP_BASE;
	private static final int SAWAREGROUP_ID = SAWAREGROUP_BASE + 1;
	
	private static final int SAORDERTRGET_BASE = 0xA000;
	private static final int SAORDERTRGET = 0xA000;
	private static final int SAORDERTRGET_ID = 0xA000 + 1;
	
	private static final int SASIZESET_BASE = 0xB000;
	private static final int SASIZESET = SASIZESET_BASE;
	private static final int SASIZESET_SIZEGROUP = SASIZESET_BASE + 1;
	
	private static final int BASE_SHIFT = 12;
	
	private static final String[] TABLE_NAMES = {
		AsContent.SaWareCode.TABLE_NAME,
		AsContent.SaColorCode.TABLE_NAME,
		AsContent.SaWareColor.TABLE_NAME,
		AsContent.ShowSize.TABLE_NAME,
		AsContent.SaWareSize.TABLE_NAME,
		AsContent.SaPara.TABLE_NAME,
		AsContent.SaWareType.TABLE_NAME,
		AsContent.Type1.TABLE_NAME,
		AsContent.SaIndent.TABLE_NAME,
		SaWareGroup.TABLE_NAME,
		SaOrderTrget.TABLE_NAME,
		SaSizeSet.TABLE_NAME
	};
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		UriMatcher matcher = sURIMatcher;
		
		//All sawarecodes
		matcher.addURI(AS_AUTHORITY, "sawarecode", SAWARECODE);
		matcher.addURI(AS_AUTHORITY, "sawarecode/#", SAWARECODE_ID);
		matcher.addURI(AS_AUTHORITY, "sawarecode/warecode/*", SAWARECODE_WARECODE);
		
		//All sacolorcode
		matcher.addURI(AS_AUTHORITY, "sacolorcode", SACOLORCODE);
		matcher.addURI(AS_AUTHORITY, "sacolorcode/#", SACOLORCODE_ID);
		
		//All sawarecolor
		matcher.addURI(AS_AUTHORITY, "sawarecolor", SAWARECOLOR);
		matcher.addURI(AS_AUTHORITY, "sawarecolor/#", SAWARECOLOR_ID);
		
		//All showsize
		matcher.addURI(AS_AUTHORITY, "showsize", SHOWSIZE);
		matcher.addURI(AS_AUTHORITY, "showsize/#", SHOWSIZE_ID);
		
		//All SaWareSize
		matcher.addURI(AS_AUTHORITY, "sawaresize", SAWARESIZE);
		matcher.addURI(AS_AUTHORITY, "sawaresize/#", SAWARESIZE_ID);
		
		//All BoDuan
		matcher.addURI(AS_AUTHORITY, "sapara", SAPARA);
		matcher.addURI(AS_AUTHORITY, "sapara/#", SAPARA_ID);
		
		//All SaWareType
		matcher.addURI(AS_AUTHORITY, "sawaretype", SAWARETYPE);
		matcher.addURI(AS_AUTHORITY, "sawaretype/#", SAWARETYPE_ID);
		
		//All Type1
		matcher.addURI(AS_AUTHORITY, "type1", TYPE1);
		matcher.addURI(AS_AUTHORITY, "type1/#", TYPE1_ID);
		
		//All SaIndent
		matcher.addURI(AS_AUTHORITY, "saindent", SAINDENT);
		matcher.addURI(AS_AUTHORITY, "saindent/#", SAINDENT_ID);
		
		//All SaWareGroups
		matcher.addURI(AS_AUTHORITY, "sawaregroup", SAWAREGROUP);
		matcher.addURI(AS_AUTHORITY, "sawaregroup/#", SAWAREGROUP_ID);
		
		//All SaOrderTrget
		matcher.addURI(AS_AUTHORITY, "saordertrget", SAORDERTRGET);
		matcher.addURI(AS_AUTHORITY, "saordertrget/#", SAORDERTRGET_ID);
		
		//All SaSizeSet
		matcher.addURI(AS_AUTHORITY, "sasizeset", SASIZESET);
		matcher.addURI(AS_AUTHORITY, "sasizeset/sizegroup/#", SASIZESET_SIZEGROUP);
	}
	
	/**
	 * Internal helper method for index creation.
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	static String createIndex(String tableName, String columnName) {
		return "create index " + tableName.toLowerCase() + "_" + columnName
			+ " on " + tableName + " ( " + columnName + " ); ";
	}
	
	static void createSawareCodeTable(SQLiteDatabase db) {
		String sawareCodeColumns = SawarecodeColumns.WARECODE + " text, "
			+ SawarecodeColumns.TRADEMARKCODE + " text, "
			+ SawarecodeColumns.WARETYPEID + " text, "
			+ SawarecodeColumns.IID + " text, "
			+ SawarecodeColumns.SPECIFICATION + " text, "
			+ SawarecodeColumns.WARENAME + " text, "
			+ SawarecodeColumns.ADUTUNIT + " text, "
			+ SawarecodeColumns.RETAILPRICE + " numeric(20,4), "
			+ SawarecodeColumns.DATE1 + " integer, "
			+ SawarecodeColumns.TYPE2 + " text, "
			+ SawarecodeColumns.STATE + " text, "
			+ SawarecodeColumns.PY + " text, "
			+ SawarecodeColumns.FLAG + " text, "
			+ SawarecodeColumns.SXZ + " text, "
			+ SawarecodeColumns.PAGENUM + " text, "
			+ SawarecodeColumns.SPECDEF + " text, "
			+ SawarecodeColumns.SEX + " text, "
			+ SawarecodeColumns.STYLE + " text, "
			+ SawarecodeColumns.TRAIT + " text, "
			+ SawarecodeColumns.PRICECOMMENT + " text, "
			+ SawarecodeColumns.PLANDATE + " integer, "
			+ SawarecodeColumns.WAREGOTO + " text, "
			+ SawarecodeColumns.PRODAREA + " text, "
			+ SawarecodeColumns.PATTEN + " text, "
			+ SawarecodeColumns.SIZEORDER + " text, "
			+ SawarecodeColumns.REMARK + " text, "
			+ SawarecodeColumns.DATE3 + " integer, "
			+ SawarecodeColumns.DATE4 + " integer, "
			+ SawarecodeColumns.PROCUREDATE + " integer, "
			+ SawarecodeColumns.STYLESPEC + " text, "
			+ SawarecodeColumns.FACTORYCODE + " text, "
			+ SawarecodeColumns.DIRECTION + " text, "
			+ SawarecodeColumns.CLIENCODE + " text, "
			+ SawarecodeColumns.STDNAME + " text, "
			+ SawarecodeColumns.CTYPE + " text, "
			+ SawarecodeColumns.WAREDEGREE + " text, "
			+ SawarecodeColumns.SALEPRICE + " text, "
			+ SawarecodeColumns.AVGPURTHPRICE + " text);";
		
		String createString = " ( " + AsContent.RECORD_ID + " integer primary key autoincrement, "
			+ sawareCodeColumns;
		
		db.execSQL(" create table " + AsContent.SaWareCode.TABLE_NAME + createString);
	}
	
	static void resetSawarecodeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaWareCode.TABLE_NAME);
		} catch(SQLException e) {
			
		}
		createSawareCodeTable(db);
	}
	
	static void createSaColorCodeTable(SQLiteDatabase db) {
		String saColorCodeColumns = SaColorCodeColumns.COLORCODE + " text, "
			+ SaColorCodeColumns.COLORNAME + " text, "
			+ SaColorCodeColumns.PY + " text, "
			+ SaColorCodeColumns.CODE + " text, "
			+ SaColorCodeColumns.REMARK + " text, "
			+ SaColorCodeColumns.PB + " text, "
			+ SaColorCodeColumns.TRADE + " text );";
		
		String createString = " ( " + AsContent.RECORD_ID + " integer primary key autoincrement, "
			+ saColorCodeColumns;
		
		db.execSQL(" create table " + AsContent.SaColorCode.TABLE_NAME + createString);
	}
	
	static void resetSaColorCodeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaColorCode.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaColorCodeTable(db);
	}
	
	static void createSaWareColorTable(SQLiteDatabase db) {
		String saWareColorColumns = SaWareColorColumns.WARECODE + " text, "
			+ SaWareColorColumns.COLORCODE + " text, "
			+ SaWareColorColumns.COLORCOMMENT + " text );";
		
		String createString = " ( " + AsContent.RECORD_ID + " integer primary key autoincrement, "
			+ saWareColorColumns;
		
		db.execSQL(" create table  " + AsContent.SaWareColor.TABLE_NAME + createString);
	}
	
	static void resetSaWareColorTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaWareColor.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaWareColorTable(db);
	}
	
	static void createShowSizeTable(SQLiteDatabase db) {
		String showSizeColumns = ShowSizeColumns.SIZE + " text, "
			+ ShowSizeColumns.TYPE + " text, "
			+ ShowSizeColumns.SHOW + " text, "
			+ ShowSizeColumns.TITLE + " text, "
			+ ShowSizeColumns.PRINTTITLE + " text, "
			+ ShowSizeColumns.SIZEGROUP + " text, "
			+ ShowSizeColumns.SCALE + " text ) ;";
		
		String createString = " ( " + AsContent.RECORD_ID + " integer primary key autoincrement, "
			+ showSizeColumns;
		
		db.execSQL(" create table " + AsContent.ShowSize.TABLE_NAME + createString);
	}
	
	static void resetShowSizeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.ShowSize.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createShowSizeTable(db);
	}
	
	static void createSaWareSizeTable(SQLiteDatabase db) {
		String columns = SaWareSizeColumns.WARECODE + " text, "
			+ SaWareSizeColumns.SIZE + " text, "
			+ SaWareSizeColumns.SHOWSORT + " text, "
			+ SaWareSizeColumns.STAND + " text );";
		
		String createString = " ( " + AsContent.RECORD_ID + " integer primary key autoincrement, "
			+ columns;
		
		db.execSQL(" create table " + AsContent.SaWareSize.TABLE_NAME + createString);
	}
	
	static void resetSaWareSizeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaWareSize.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaWareSizeTable(db);
	}
	
	static void createSaParaTable(SQLiteDatabase db) {
		String columns = SaParaColumns.PARATYPE + " text, "
			+ SaParaColumns.PARA + " text, "
			+ SaParaColumns.PARACONNENT + " text, "
			+ SaParaColumns.CONNENT + " text);";
		
		String createString = " ( " + AsContent.SaPara.RECORD_ID + " integer primary key autoincrement, " + columns;
		
		db.execSQL(" create table " + AsContent.SaPara.TABLE_NAME + createString);
	}
	
	static void resetSaParaTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaPara.TABLE_NAME );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaParaTable(db);
	}
	
	static void createSaWareTypeTable(SQLiteDatabase db) {
		String waretypeColumns = SaWareTypeColumns.WARETYPEID + " text, "
			+ SaWareTypeColumns.WARETYPENAME + " text, "
			+ SaWareTypeColumns.TAXRATE + " text, "
			+ SaWareTypeColumns.STANDARD + " text, "
			+ SaWareTypeColumns.FLAG + " text, "
			+ SaWareTypeColumns.SIZEFLAG + " text );";
		
		String createString = " ( " + AsContent.SaWareType.RECORD_ID + " integer primary key autoincrement, "
			+ waretypeColumns;
		
		db.execSQL(" create table " + AsContent.SaWareType.TABLE_NAME + createString);
	}
	
	static void resetSaWareTypeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaWareType.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaWareTypeTable(db);
	}
	
	static void createType1Table(SQLiteDatabase db) {
		String type1Columns = Type1Columns.IID + " text, "
			+ Type1Columns.TYPE1 + " text, "
			+ Type1Columns.WARETYPEID + " text, "
			+ Type1Columns.STANDARD + " text, "
			+ Type1Columns.REMARK + " text);";
		
		String createString = " ( " + AsContent.Type1.RECORD_ID + " integer primary key autoincrement, " + type1Columns;
		
		db.execSQL(" create table " + AsContent.Type1.TABLE_NAME + createString);
	}
	
	static void resetType1Table(SQLiteDatabase db, int oldVerion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.Type1.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createType1Table(db);
	}
	
	static void createSaIndentTable(SQLiteDatabase db) {
		String columns = SaIndentColumns.INDENTNO + " text, "
			+ SaIndentColumns.DEPARTCODE + " text, "
			+ SaIndentColumns.WARECODE + " text, "
			+ SaIndentColumns.COLORCODE + " text, "
			+ SaIndentColumns.S01 + " integer default 0, "
			+ SaIndentColumns.S02 + " integer default 0, "
			+ SaIndentColumns.S03 + " integer default 0, "
			+ SaIndentColumns.S04 + " integer default 0, "
			+ SaIndentColumns.S05 + " integer default 0, "
			+ SaIndentColumns.S06 + " integer default 0, "
			+ SaIndentColumns.S07 + " integer default 0, "
			+ SaIndentColumns.S08 + " integer default 0, "
			+ SaIndentColumns.S09 + " integer default 0, "
			+ SaIndentColumns.S10 + " integer default 0, "
			+ SaIndentColumns.S11 + " integer default 0, "
			+ SaIndentColumns.S12 + " integer default 0, "
			+ SaIndentColumns.S13 + " integer default 0, "
			+ SaIndentColumns.S14 + " integer default 0, "
			+ SaIndentColumns.S15 + " integer default 0, "
			+ SaIndentColumns.S16 + " integer default 0, "
			+ SaIndentColumns.S17 + " integer default 0, "
			+ SaIndentColumns.S18 + " integer default 0, "
			+ SaIndentColumns.S19 + " integer default 0, "
			+ SaIndentColumns.S20 + " integer default 0, "
			+ SaIndentColumns.INPUTDATE + " text, "
			+ SaIndentColumns.INPUTMAN + " text, "
			+ SaIndentColumns.WARENUM + " integer, "
			+ SaIndentColumns.REMARK + " text, "
			+ SaIndentColumns.OFLAG + " text);";
		
		String createString =  " ( " + AsContent.SaIndent.RECORD_ID + " integer primary key autoincrement, "
			+ columns;
		db.execSQL(" create table " + AsContent.SaIndent.TABLE_NAME + createString);
	}
	
	static void resetSaIndentTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaIndent.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaIndentTable(db);
	}
	
	static void createSaWareGroupTable(SQLiteDatabase db) {
		String columns = SaWareGroupColumns.ITEMCODE + " text, "
			+ SaWareGroupColumns.GROUPNAME + " text, "
			+ SaWareGroupColumns.WARECODE + " text, "
			+ SaWareGroupColumns.COLORCODE + " text, "
			+ SaWareGroupColumns.REMARK + " text);";
		String createString = " ( " + AsContent.SaWareGroup.RECORD_ID + " integer primary key autoincrement, " + columns;
		db.execSQL(" create table " + AsContent.SaWareGroup.TABLE_NAME + createString);
	}
	
	static void resetSaWareGroupTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaWareGroup.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaWareGroupTable(db);
	}
	
	static void createSaOrderTrgetTable(SQLiteDatabase db) {
		String columns = SaOrderTrgetColumns.DEPARTCODE + " text,"
			+ SaOrderTrgetColumns.ITEM1 + " text, "
			+ SaOrderTrgetColumns.ITEM2 + " text, "
			+ SaOrderTrgetColumns.ITEM3 + " text, "
			+ SaOrderTrgetColumns.ITEM4 + " text, "
			+ SaOrderTrgetColumns.NUM1 + " integer, "
			+ SaOrderTrgetColumns.NUM2 + " integer, "
			+ SaOrderTrgetColumns.NUM3 + " integer, "
			+ SaOrderTrgetColumns.TYPE + " text, "
			+ SaOrderTrgetColumns.TYPE1 + " text);";
		String createString = " ( " + AsContent.SaOrderTrget.RECORD_ID + " integer primary key autoincrement, " + columns;
		db.execSQL(" create table " + AsContent.SaOrderTrget.TABLE_NAME + createString);
	}
	
	static void resetSaOrderTrgetTable(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaOrderTrget.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaOrderTrgetTable(db);
	}
	
	static void createSaSizeSetTable(SQLiteDatabase db) {
		String columns = SaSizeSetColumns.SIZEGROUP + " text not null, "
			+ SaSizeSetColumns.S01 + " integer default 0, "
			+ SaSizeSetColumns.S02 + " integer default 0, "
			+ SaSizeSetColumns.S03 + " integer default 0, "
			+ SaSizeSetColumns.S04 + " integer default 0, "
			+ SaSizeSetColumns.S05 + " integer default 0, "
			+ SaSizeSetColumns.S06 + " integer default 0, "
			+ SaSizeSetColumns.S07 + " integer default 0, "
			+ SaSizeSetColumns.S08 + " integer default 0, "
			+ SaSizeSetColumns.S09 + " integer default 0, "
			+ SaSizeSetColumns.S10 + " integer default 0, "
			+ SaSizeSetColumns.S11 + " integer default 0, "
			+ SaSizeSetColumns.S12 + " integer default 0, "
			+ SaSizeSetColumns.S13 + " integer default 0, "
			+ SaSizeSetColumns.S14 + " integer default 0, "
			+ SaSizeSetColumns.S15 + " integer default 0, "
			+ SaSizeSetColumns.S16 + " integer default 0, "
			+ SaSizeSetColumns.S17 + " integer default 0, "
			+ SaSizeSetColumns.S18 + " integer default 0, "
			+ SaSizeSetColumns.S19 + " integer default 0, "
			+ SaSizeSetColumns.S20 + " integer default 0); ";
		String createString = " ( " + AsContent.SaSizeSet.RECORD_ID + " integer primary key autoincrement, " + columns;
		db.execSQL(" create table " + AsContent.SaSizeSet.TABLE_NAME + createString);
	}
	
	static void resetSaSizeSetTable(SQLiteDatabase db , int oldVersion, int newVersion) {
		try {
			db.execSQL(" drop table " + AsContent.SaSizeSet.TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createSaSizeSetTable(db);
	}
	
	static void createSizeView(SQLiteDatabase db) {
		String viewSql = "";
	}
	
	private SQLiteDatabase mDatabase;
	
	public synchronized SQLiteDatabase getDatabase(Context context) {
		if(mDatabase != null) {
			return mDatabase;
		}
		
		DatabaseHelper helper = new DatabaseHelper(context, DATABASE_NAME);
		mDatabase = helper.getWritableDatabase();
		
		return mDatabase;
	}
	
	static SQLiteDatabase getReadableDatabase(Context context) {
		DatabaseHelper helper = new AsProvider().new DatabaseHelper(context, DATABASE_NAME);
		return helper.getReadableDatabase();
	}
	
	public static SQLiteDatabase getWriteableDatabase(Context context) {
		DatabaseHelper helper = new AsProvider().new DatabaseHelper(context, DATABASE_NAME);
		return helper.getWritableDatabase();
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper {

		Context mContext;
		
		DatabaseHelper(Context context, String name) {
			super(context, name, null, DATABASE_VERSION);
			mContext = context;
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			createSawareCodeTable(db);
			createSaColorCodeTable(db);
			createSaWareColorTable(db);
			createShowSizeTable(db);
			createSaWareSizeTable(db);
			createSaParaTable(db);
			createSaWareTypeTable(db);
			createType1Table(db);
			createSaIndentTable(db);
			createSaWareGroupTable(db);
			createSaOrderTrgetTable(db);
			createSaSizeSetTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(newVersion > oldVersion) {
				resetSawarecodeTable(db, oldVersion, newVersion);
				resetSaColorCodeTable(db, oldVersion, newVersion);
				resetShowSizeTable(db, oldVersion, newVersion);
				resetSaWareSizeTable(db, oldVersion, newVersion);
				resetSaParaTable(db, oldVersion, newVersion);
				resetSaWareTypeTable(db, oldVersion, newVersion);
				resetType1Table(db, oldVersion, newVersion);
				resetSaIndentTable(db, oldVersion, newVersion);
				resetSaWareGroupTable(db, oldVersion, newVersion);
				resetSaOrderTrgetTable(db, oldVersion, newVersion);
				resetSaSizeSetTable(db, oldVersion, newVersion);
			}
		}
		
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final int match = sURIMatcher.match(uri);
		Context context = getContext();
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		String id = "0";
		int result = -1;
		
		try {
			switch(match) {
			case SAWARECODE:
			case SAWARECODE_ID:
			case SACOLORCODE:
			case SACOLORCODE_ID:
			case SAWARECOLOR:
			case SAWARECOLOR_ID:
			case SHOWSIZE:
			case SHOWSIZE_ID:
			case SAWARESIZE:
			case SAWARESIZE_ID:
			case SAPARA:
			case SAPARA_ID:
			case SAWARETYPE_ID:
			case SAWARETYPE:
			case TYPE1:
			case TYPE1_ID:
			case SAINDENT:
			case SAINDENT_ID:
			case SAWAREGROUP:
			case SAWAREGROUP_ID:
			case SAORDERTRGET:
			case SAORDERTRGET_ID:
			case SASIZESET_SIZEGROUP:
			case SASIZESET:
				db.beginTransaction();
				break;
			}
			
			switch(match) {
			case SAWARECODE_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWARECODE:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAWARECODE_WARECODE:
				String warecode = uri.getPathSegments().get(2);
				result = db.delete(TABLE_NAMES[table], whereWithWareCode(warecode, selection), selectionArgs);
				break;
				
			case SACOLORCODE_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SACOLORCODE:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAWARECOLOR:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAWARECOLOR_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SHOWSIZE:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SHOWSIZE_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWARESIZE:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAWARESIZE_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAPARA:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAPARA_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWARETYPE:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAWARETYPE_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case TYPE1:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case TYPE1_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAINDENT:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAINDENT_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWAREGROUP:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAWAREGROUP_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SAORDERTRGET:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SAORDERTRGET_ID:
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
				break;
				
			case SASIZESET:
				result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
				break;
				
			case SASIZESET_SIZEGROUP:
				String sizeGroup = uri.getPathSegments().get(2);
				result = db.delete(TABLE_NAMES[table], whereWithSizeGroup(sizeGroup, selection), selectionArgs);
				break;
				
				default:
					throw new IllegalArgumentException("Unknow URI" + uri);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public String getType(Uri uri) {
		int match = sURIMatcher.match(uri);
		switch(match) {
		case SAWARECODE_ID:
			return "vnd.android.cursor.item/sawarecode";
			
		case SAWARECODE:
			return "vnd.android.cursor.dir/sawarecode";
			
		case SAWARECODE_WARECODE:
			return "vnd.android.cursor.item/warecode";
			
		case SACOLORCODE_ID:
			return "vnd.android.cursor.item/sacolorcode";
			
		case SACOLORCODE:
			return "vnd.android.cursor.dir/sacolorcode";
			
		case SAWARECOLOR:
			return "vnd.android.dir/sawarecolor";
			
		case SAWARECOLOR_ID:
			return "vnd.android.item/sawarecolor";
			
		case SHOWSIZE:
			return "vnd.android.dir/showsize";
			
		case SHOWSIZE_ID:
			return "vnd.android.item/showsize";
			
		case SAWARESIZE:
			return "vnd.android.dir/sawaresize";
			
		case SAWARESIZE_ID:
			return "vnd.android.item/sawaresize";
			
		case SAPARA:
			return "vnd.android.dir/sapara";
			
		case SAPARA_ID:
			return "vnd.android.item/sapara";
			
		case SAWARETYPE:
			return "vnd.android.dir/sawaretype";
			
		case SAWARETYPE_ID:
			return "vnd.android.item/sawaretype";
			
		case TYPE1:
			return "vnd.android.dir/type1";
			
		case TYPE1_ID:
			return "vnd.android.item/type1";
			
		case SAINDENT:
			return "vnd.android.dir/saindent";
			
		case SAINDENT_ID:
			return "vnd.android.item/saindent";
			
		case SAWAREGROUP:
			return "vnd.android.dir/sawaregroup";
			
		case SAWAREGROUP_ID:
			return "vnd.android.item/sawaregroup";
			
		case SAORDERTRGET:
			return "vnd.android.dir/saordertrget";
			
		case SAORDERTRGET_ID:
			return "vnd.android.item/saordertrget";
			
		case SASIZESET:
			return "vnd.android.dir/sasizeset";
			
		case SASIZESET_SIZEGROUP:
			return "vnd.android.item/sasizeset";
			
			default:
				throw new IllegalArgumentException("Unknow URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = sURIMatcher.match(uri);
		Context context = getContext();
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		long id;
		
		Uri resultUri = null;
		try {
			switch(match) {
			case SAWARECODE:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaWareCode.CONTENT_URI, id);
				break;
				
			case SACOLORCODE:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaColorCode.CONTENT_URI, id);
				break;
				
			case SAWARECOLOR:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaWareColor.CONTENT_URI, id);
				break;
				
			case SHOWSIZE:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(ShowSize.CONTENT_URI, id);
				break;
				
			case SAWARESIZE:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaWareSize.CONTENT_URI, id);
				break;
				
			case SAPARA:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaPara.CONTENT_URI, id);
				break;
				
			case SAWARETYPE:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaWareType.CONTENT_URI, id);
				break;
				
			case TYPE1:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(Type1.CONTENT_URI, id);
				break;
				
			case SAINDENT:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaIndent.CONTENT_URI, id);
				break;
				
			case SAWAREGROUP:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaWareGroup.CONTENT_URI, id);
				break;
				
			case SAORDERTRGET:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaOrderTrget.CONTENT_URI, id);
				break;
				
			case SASIZESET:
				id = db.insert(TABLE_NAMES[table], "foo", values);
				resultUri = ContentUris.withAppendedId(SaSizeSet.CONTENT_URI, id);
				break;
				
				default:
					throw new IllegalArgumentException("UnKnown URL " + uri);
			}
		} catch(SQLiteException e) {
			e.printStackTrace();
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return resultUri;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		int match = sURIMatcher.match(uri);
		Context context = getContext();
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		String id;
		
		try {
			switch(match) {
			case SAWARECODE:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARECODE_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARECODE_WARECODE:
				String warecode = uri.getPathSegments().get(2);
				c = db.query(TABLE_NAMES[table], projection, whereWithWareCode(warecode, selection), selectionArgs, null, null, sortOrder);
				
			case SACOLORCODE:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SACOLORCODE_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), null, null, null, sortOrder);
				break;
				
			case SAWARECOLOR:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARECOLOR_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SHOWSIZE:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SHOWSIZE_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARESIZE:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARESIZE_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAPARA:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAPARA_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARETYPE:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAWARETYPE_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case TYPE1:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case TYPE1_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAINDENT:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAINDENT_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAWAREGROUP:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAWAREGROUP_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SAORDERTRGET:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SAORDERTRGET_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null, sortOrder);
				break;
				
			case SASIZESET:
				c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
				break;
				
			case SASIZESET_SIZEGROUP:
				String sizeGroup = uri.getPathSegments().get(2);
				c = db.query(TABLE_NAMES[table], projection, whereWithSizeGroup(sizeGroup, selection), selectionArgs, null, null, sortOrder);
				break;
				
				default:
					throw new IllegalArgumentException("UnKnown URI " + uri);
			}
		} catch(SQLiteException e) {
			e.printStackTrace();
		}
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = sURIMatcher.match(uri);
		Context context = getContext();
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		int result = 0;
		
		String id;
		try {
			switch(match) {
			case SAWARECODE_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWARECODE:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SACOLORCODE_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SACOLORCODE:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAWARECOLOR:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAWARECOLOR_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SHOWSIZE:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SHOWSIZE_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWARESIZE:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAWARESIZE_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAPARA:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAPARA_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWARETYPE:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAWARETYPE_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case TYPE1:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case TYPE1_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAINDENT:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAINDENT_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAWAREGROUP:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAWAREGROUP_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SAORDERTRGET:
				result= db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SAORDERTRGET_ID:
				id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
				break;
				
			case SASIZESET:
				result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
				break;
				
			case SASIZESET_SIZEGROUP:
				String sizeGroup = uri.getPathSegments().get(2);
				result = db.update(TABLE_NAMES[table], values, whereWithSizeGroup(sizeGroup, selection), selectionArgs);
				break;
				
				default:
					throw new IllegalArgumentException("UnKnown URI " + uri);
			}
		} catch(SQLiteException e) {
			e.printStackTrace();
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	private String whereWithId(String id, String selection) {
		StringBuilder sb = new StringBuilder();
		sb.append("_id=");
		sb.append(id);
		if(selection != null) {
			sb.append(" AND (");
			sb.append(selection);
			sb.append(" ) ");
		}
		return sb.toString();
	}
	
	private String whereWithSizeGroup(String sizeGroup, String selection) {
		StringBuilder sb = new StringBuilder();
		sb.append(SaSizeSetColumns.SIZEGROUP + " = '");
		sb.append(sizeGroup);
		sb.append("' ");
		if(selection != null) {
			sb.append(" AND ( ");
			sb.append( selection );
			sb.append(" ) "); 
		}
		return sb.toString();
	}
	
	private String whereWithWareCode(String warecode, String selection) {
		StringBuilder sb = new StringBuilder();
		sb.append(SawarecodeColumns.WARECODE + " = '");
		sb.append(warecode);
		sb.append("' ");
		if(selection != null) {
			sb.append(" AND ( ");
			sb.append(selection);
			sb.append(" )  ");
		}
		return sb.toString();
	}
}
