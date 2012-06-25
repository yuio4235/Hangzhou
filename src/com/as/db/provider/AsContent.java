package com.as.db.provider;

import java.io.Serializable;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public abstract class AsContent {
	public static final String AUTHORITY = AsProvider.AS_AUTHORITY;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	//All classes share this
	public static final String RECORD_ID = "_id";
	
	private static final String[] COUNT_COLUMNS = new String[]{"count(*)"};
	
	/**
	 * This projection can be used with any of the AsConent classes, when all you need 
	 * is a list of id's. Use ID_PROJECTION_COLUMN to access the row data
	 */
	public static final String[] ID_PROJECTION = new String[]{ RECORD_ID };
	
	public static final int ID_PROJECTION_COLUMN = 0;
	
	private static final String ID_SELECTION = RECORD_ID + " = ?";
	
	// Newly created objects get this id
	private static final int NOT_SAVED = -1;
	//The base Uri that this piece of content came from
	public Uri mBaseUri;
	// Lazily initialized uri for this Content
	private Uri mUri = null;
	//The id od the Content
	public long mId = NOT_SAVED;
	
	// Write the content to the ContentValues container
	public abstract ContentValues toContentValues();
	//Read the content from a ContentCursor
	public abstract <T extends AsContent> T restore(Cursor cursor);
	
	//The Uri is lazily initialized
	public Uri getUri() {
		if(mUri == null) {
			mUri = ContentUris.withAppendedId(mBaseUri, mId);
		}
		return mUri;
	}
	
	public boolean isSaved() {
		return mId != NOT_SAVED;
	}
	
	static public <T extends AsContent> T getContent(Cursor cursor, Class<T> klass) {
		try {
			if(cursor != null && cursor.moveToFirst()) {
				T content = klass.newInstance();
				content.mId = cursor.getLong(0);
				return (T)content.restore(cursor);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} finally  {
			if(cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	public Uri save(Context context) {
		if(isSaved()) {
			throw new UnsupportedOperationException();
		}
		Uri res = context.getContentResolver().insert(mBaseUri, toContentValues());
		mId = Long.parseLong(res.getPathSegments().get(1));
		return res;
	}
	
	public int update(Context context, ContentValues contentValues) {
		if(!isSaved()) {
			throw new UnsupportedOperationException();
		}
		return context.getContentResolver().update(getUri(), contentValues, null, null);
	}
	
	static public int update(Context context, Uri baseUri, long id, ContentValues contentValues) {
		return context.getContentResolver().update(ContentUris.withAppendedId(baseUri, id), contentValues, null, null);
	}
	
	static public int count(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = context.getContentResolver().query(uri, COUNT_COLUMNS, selection, selectionArgs, null);
		try {
			if(!cursor.moveToFirst()) {
				return 0;
			}
			return cursor.getInt(0);
		} finally {
			cursor.close();
		}
	}
	
	private AsContent(){}
	
	public interface SawarecodeColumns {
		public static final String ID = "_id";
		//款号代码
		public static final String WARECODE = "warecode";
		//品牌代码
		public static final String TRADEMARKCODE = "trademarkcode";
		//大类代码
		public static final String WARETYPEID = "waretypeid";
		//小类代码
		public static final String IID = "id";
		//款号
		public static final String SPECIFICATION = "specification";
		//商品名称
		public static final String WARENAME = "warename";
		//单位
		public static final String ADUTUNIT = "adutunit";
		//零售价
		public static final String RETAILPRICE = "retailprice";
		//商品年份
		public static final String DATE1 = "date1";
		//季节代码
		public static final String TYPE2 = "type2";
		//波段代码
		public static final String STATE = "state";
		//简拼
		public static final String PY = "py";
		//尺码组
		public static final String FLAG = "flag";
		//上下装
		public static final String SXZ = "sxz";
		//订货会编号
		public static final String PAGENUM = "pagenum";
		//款式定位
		public static final String SPECDEF = "specdef";
		//性别
		public static final String SEX = "sex";
		//产品主题
		public static final String STYLE = "style";
		//卖点说明
		public static final String TRAIT = "trait";
		//价位段
		public static final String PRICECOMMENT = "pricecomment";
		//上市日期
		public static final String PLANDATE = "plandate";
		//产品风格
		public static final String WAREGOTO = "waregoto";
		//面料成分
		public static final String PRODAREA = "prodarea";
		//里料成分
		public static final String PATTEN = "patten";
		//sizeorder
		public static final String SIZEORDER = "sizeorder";
		//remark
		public static final String REMARK = "remark";
		//上架日期
		public static final String DATE3 = "date3";
		//下架日期
		public static final String DATE4 = "date4";
		//工厂生产日期
		public static final String PROCUREDATE = "procuredate";
		//样衣号
		public static final String STYLESPEC = "stylespec";
		//工厂样衣号
		public static final String FACTORYCODE = "factorycode";
		//
		public static final String DIRECTION = "direction";
		//
		public static final String CLIENCODE = "cliencode";
		//标准名称
		public static final String STDNAME = "stdname";
		//
		public static final String CTYPE = "ctype";
		//商品等级
		public static final String WAREDEGREE = "waredegree";
		//
		public static final String SALEPRICE = "saleprice";
		//
		public static final String AVGPURTHPRICE = "avgpurhprice";
	}
	
	public static final class SaWareCode extends AsContent implements SawarecodeColumns, Serializable {
		
		private static final long serialVersionUID = 248904753027183834L;
		public static final String TABLE_NAME = "sawarecode";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sawarecode");
		
		public static final int CONTENT_ID_COLUMN 				= 0;
		public static final int CONTENT_WARECODE_COLUMN 		= 1;
		public static final int CONTENT_TRADEMARKCODE_COLUMN 	= 2;
		public static final int CONTENT_WARETYPEID_COLUMN 		= 3;
		public static final int CONTENT_IID_COLUMN 				= 4;
		public static final int CONTENT_SPECIFICATION_COLUMN 	= 5;
		public static final int CONTENT_WARENAME_COLUMN 		= 6;
		public static final int CONTENT_ADUTUNIT_COLUMN 		= 7;
		public static final int CONTENT_RETAILPRICE_COLUMN 		= 8;
		public static final int CONTENT_DATE1_COLUMN 			= 9;
		public static final int CONTENT_TYPE2_COLUMN 			= 10;
		public static final int CONTENT_STATE_COLUMN 			= 11;
		public static final int CONTENT_PY_COLUMN 				= 12;
		public static final int CONTENT_FLAG_COLUMN 			= 13;
		public static final int CONTENT_SXZ_COLUMN 				= 14;
		public static final int CONTENT_PAGENUM_COLUMN 			= 15;
		public static final int CONTENT_SPECDEF_COLUMN 			= 16;
		public static final int CONTENT_SEX_COLUMN 				= 17;
		public static final int CONTENT_STYLE_COLUMN 			= 18;
		public static final int CONTENT_TRAIT_COLUMN 			= 19;
		public static final int CONTENT_PRICECOMMENT_COLUMN 	= 20;
		public static final int CONTENT_PLANDATE_COLUMN 		= 21;
		public static final int CONTENT_WAREGOTO_COLUMN 		= 22;
		public static final int CONTENT_PRODAREA_COLUMN 		= 23;
		public static final int CONTENT_PATTEN_COLUMN 			= 24;
		public static final int CONTENT_SIZEORDER_COLUMN 		= 25;
		public static final int CONTENT_REMARK_COLUMN 			= 26;
		public static final int CONTENT_DATE3_COLUMN 			= 27;
		public static final int CONTENT_DATE4_COLUMN 			= 28;
		public static final int CONTENT_PROCUREDATE_COLUMN 		= 29;
		public static final int CONTENT_STYLESPEC_COLUMN 		= 30;
		public static final int CONTENT_FACTORYCODE_COLUMN 		= 31;
		public static final int CONTENT_DIRECTION_COLUMN		= 32;
		public static final int CONTENT_CLIENCODE_COLUMN 		= 33;
		public static final int CONTENT_STDNAME_COLUMN 			= 34;
		public static final int CONTENT_CTYPE_COLUMN 			= 35;
		public static final int CONTENT_WAREDEGREE_COLUMN 		= 36;
		public static final int CONTENT_SALEPRICE_COLUMN 		= 37;
		public static final int CONTENT_AVGPURTHPRICE_COLUMN 	= 38;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SawarecodeColumns.WARECODE,
			SawarecodeColumns.TRADEMARKCODE,
			SawarecodeColumns.WARETYPEID,
			SawarecodeColumns.IID,
			SawarecodeColumns.SPECIFICATION,
			SawarecodeColumns.WARENAME,
			SawarecodeColumns.ADUTUNIT,
			SawarecodeColumns.RETAILPRICE,
			SawarecodeColumns.DATE1,
			SawarecodeColumns.STATE,
			SawarecodeColumns.TYPE2,
			SawarecodeColumns.PY,
			SawarecodeColumns.FLAG,
			SawarecodeColumns.SXZ,
			SawarecodeColumns.PAGENUM,
			SawarecodeColumns.SPECDEF,
			SawarecodeColumns.SEX,
			SawarecodeColumns.STYLE,
			SawarecodeColumns.TRAIT,
			SawarecodeColumns.PRICECOMMENT,
			SawarecodeColumns.PLANDATE,
			SawarecodeColumns.WAREGOTO,
			SawarecodeColumns.PRODAREA,
			SawarecodeColumns.PATTEN,
			SawarecodeColumns.SIZEORDER,
			SawarecodeColumns.REMARK,
			SawarecodeColumns.DATE3,
			SawarecodeColumns.DATE4,
			SawarecodeColumns.PROCUREDATE,
			SawarecodeColumns.STYLESPEC,
			SawarecodeColumns.FACTORYCODE,
			SawarecodeColumns.DIRECTION,
			SawarecodeColumns.CLIENCODE,
			SawarecodeColumns.STDNAME,
			SawarecodeColumns.CTYPE,
			SawarecodeColumns.WAREDEGREE,
			SawarecodeColumns.SALEPRICE,
			SawarecodeColumns.AVGPURTHPRICE
		};
		
		public String warecode;
		public String trademarkcode;
		public String waretypeid;
		public String id;
		public String specification;
		public String warename;
		public String adutunit;
		public double retailprice;
		public long   date1;
		public String type2;
		public String state;
		public String py;
		public String flag;
		public String sxz;
		public String pagenum;
		public String specdef;
		public String sex;
		public String style;
		public String trait;
		public String pricecomment;
		public String plandate;
		public String waregoto;
		public String prodarea;
		public String patten;
		public String sizeorder;
		public String remark;
		public long   date3;
		public long   date4;
		public long procuredate;
		public String stylespec;
		public String factorycode;
		public String direction;
		public String cliencode;
		public String stdname;
		public String ctype;
		public String waredegree;
		public double saleprice;
		public double avgpurhprice;
		
		public SaWareCode() {
			mBaseUri = CONTENT_URI;
		}

		public String getWarecode() {
			return warecode;
		}

		public void setWarecode(String warecode) {
			this.warecode = warecode;
		}

		public String getTrademarkcode() {
			return trademarkcode;
		}

		public void setTrademarkcode(String trademarkcode) {
			this.trademarkcode = trademarkcode;
		}

		public String getWaretypeid() {
			return waretypeid;
		}

		public void setWaretypeid(String waretypeid) {
			this.waretypeid = waretypeid;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSpecification() {
			return specification;
		}

		public void setSpecification(String specification) {
			this.specification = specification;
		}

		public String getWarename() {
			return warename;
		}

		public void setWarename(String warename) {
			this.warename = warename;
		}

		public String getAdutunit() {
			return adutunit;
		}

		public void setAdutunit(String adutunit) {
			this.adutunit = adutunit;
		}

		public double getRetailprice() {
			return retailprice;
		}

		public void setRetailprice(double retailprice) {
			this.retailprice = retailprice;
		}

		public long getDate1() {
			return date1;
		}

		public void setDate1(long date1) {
			this.date1 = date1;
		}

		public String getType2() {
			return type2;
		}

		public void setType2(String type2) {
			this.type2 = type2;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getPy() {
			return py;
		}

		public void setPy(String py) {
			this.py = py;
		}

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public String getSxz() {
			return sxz;
		}

		public void setSxz(String sxz) {
			this.sxz = sxz;
		}

		public String getPagenum() {
			return pagenum;
		}

		public void setPagenum(String pagenum) {
			this.pagenum = pagenum;
		}

		public String getSpecdef() {
			return specdef;
		}

		public void setSpecdef(String specdef) {
			this.specdef = specdef;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getStyle() {
			return style;
		}

		public void setStyle(String style) {
			this.style = style;
		}

		public String getTrait() {
			return trait;
		}

		public void setTrait(String trait) {
			this.trait = trait;
		}

		public String getPricecomment() {
			return pricecomment;
		}

		public void setPricecomment(String pricecomment) {
			this.pricecomment = pricecomment;
		}

		public String getPlandate() {
			return plandate;
		}

		public void setPlandate(String plandate) {
			this.plandate = plandate;
		}

		public String getWaregoto() {
			return waregoto;
		}

		public void setWaregoto(String waregoto) {
			this.waregoto = waregoto;
		}

		public String getProdarea() {
			return prodarea;
		}

		public void setProdarea(String prodarea) {
			this.prodarea = prodarea;
		}

		public String getPatten() {
			return patten;
		}

		public void setPatten(String patten) {
			this.patten = patten;
		}

		public String getSizeorder() {
			return sizeorder;
		}

		public void setSizeorder(String sizeorder) {
			this.sizeorder = sizeorder;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public long getDate3() {
			return date3;
		}

		public void setDate3(long date3) {
			this.date3 = date3;
		}

		public long getDate4() {
			return date4;
		}

		public void setDate4(long date4) {
			this.date4 = date4;
		}

		public long getProcuredate() {
			return procuredate;
		}

		public void setProcuredate(long procuredate) {
			this.procuredate = procuredate;
		}

		public String getStylespec() {
			return stylespec;
		}

		public void setStylespec(String stylespec) {
			this.stylespec = stylespec;
		}

		public String getFactorycode() {
			return factorycode;
		}

		public void setFactorycode(String factorycode) {
			this.factorycode = factorycode;
		}

		public String getDirection() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction = direction;
		}

		public String getCliencode() {
			return cliencode;
		}

		public void setCliencode(String cliencode) {
			this.cliencode = cliencode;
		}

		public String getStdname() {
			return stdname;
		}

		public void setStdname(String stdname) {
			this.stdname = stdname;
		}

		public String getCtype() {
			return ctype;
		}

		public void setCtype(String ctype) {
			this.ctype = ctype;
		}

		public String getWaredegree() {
			return waredegree;
		}

		public void setWaredegree(String waredegree) {
			this.waredegree = waredegree;
		}

		public double getSaleprice() {
			return saleprice;
		}

		public void setSaleprice(double saleprice) {
			this.saleprice = saleprice;
		}

		public double getAvgpurhprice() {
			return avgpurhprice;
		}

		public void setAvgpurhprice(double avgpurhprice) {
			this.avgpurhprice = avgpurhprice;
		}

		@Override
		public SaWareCode restore(Cursor cursor) {
			mBaseUri = AsContent.SaWareCode.CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					warecode = cursor.getString(CONTENT_WARECODE_COLUMN);
					trademarkcode = cursor.getString(CONTENT_TRADEMARKCODE_COLUMN);
					waretypeid = cursor.getString(CONTENT_WARETYPEID_COLUMN);
					id = cursor.getString(CONTENT_IID_COLUMN);
					specification = cursor.getString(CONTENT_SPECIFICATION_COLUMN);
					warename = cursor.getString(CONTENT_WARENAME_COLUMN);
					adutunit = cursor.getString(CONTENT_ADUTUNIT_COLUMN);
					retailprice = cursor.getDouble(CONTENT_RETAILPRICE_COLUMN);
					date1 = cursor.getLong(CONTENT_DATE1_COLUMN);
					type2 = cursor.getString(CONTENT_TYPE2_COLUMN);
					state = cursor.getString(CONTENT_STATE_COLUMN);
					py = cursor.getString(CONTENT_PY_COLUMN);
					flag = cursor.getString(CONTENT_FLAG_COLUMN);
					sxz = cursor.getString(CONTENT_SXZ_COLUMN);
					pagenum = cursor.getString(CONTENT_PAGENUM_COLUMN);
					specdef = cursor.getString(CONTENT_SPECDEF_COLUMN);
					sex = cursor.getString(CONTENT_SEX_COLUMN);
					style = cursor.getString(CONTENT_STYLE_COLUMN);
					trait = cursor.getString(CONTENT_TRAIT_COLUMN);
					pricecomment = cursor.getString(CONTENT_PRICECOMMENT_COLUMN);
					plandate = cursor.getString(CONTENT_PLANDATE_COLUMN);
					waregoto = cursor.getString(CONTENT_WAREGOTO_COLUMN);
					prodarea = cursor.getString(CONTENT_PRODAREA_COLUMN);
					patten = cursor.getString(CONTENT_PATTEN_COLUMN);
					sizeorder = cursor.getString(CONTENT_SIZEORDER_COLUMN);
					remark = cursor.getString(CONTENT_REMARK_COLUMN);
					date3 = cursor.getLong(CONTENT_DATE3_COLUMN);
					date4 = cursor.getLong(CONTENT_DATE4_COLUMN);
					procuredate = cursor.getLong(CONTENT_PROCUREDATE_COLUMN);
					stylespec = cursor.getString(CONTENT_STYLESPEC_COLUMN);
					factorycode = cursor.getString(CONTENT_FACTORYCODE_COLUMN);
					direction = cursor.getString(CONTENT_DIRECTION_COLUMN);
					cliencode = cursor.getString(CONTENT_CLIENCODE_COLUMN);
					stdname = cursor.getString(CONTENT_STDNAME_COLUMN);
					ctype = cursor.getString(CONTENT_CTYPE_COLUMN);
					waredegree = cursor.getString(CONTENT_WAREDEGREE_COLUMN);
					saleprice = cursor.getDouble(CONTENT_SALEPRICE_COLUMN);
					avgpurhprice = cursor.getDouble(CONTENT_AVGPURTHPRICE_COLUMN);
				}
			} finally  {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			
			//Assign values for each row
			values.put(SawarecodeColumns.WARECODE, warecode);
			values.put(SawarecodeColumns.TRADEMARKCODE, trademarkcode);
			values.put(SawarecodeColumns.WARETYPEID, waretypeid);
			values.put(SawarecodeColumns.IID, id);
			values.put(SawarecodeColumns.SPECIFICATION, specification);
			values.put(SawarecodeColumns.WARENAME, warename);
			values.put(SawarecodeColumns.ADUTUNIT, adutunit);
			values.put(SawarecodeColumns.RETAILPRICE, retailprice);
			values.put(SawarecodeColumns.DATE1, date1);
			values.put(SawarecodeColumns.TYPE2, type2);
			values.put(SawarecodeColumns.STATE, state);
			values.put(SawarecodeColumns.PY, py);
			values.put(SawarecodeColumns.FLAG, flag);
			values.put(SawarecodeColumns.SXZ, sxz);
			values.put(SawarecodeColumns.PAGENUM, pagenum);
			values.put(SawarecodeColumns.SPECDEF, specdef);
			values.put(SawarecodeColumns.SEX, sex);
			values.put(SawarecodeColumns.STYLE, style);
			values.put(SawarecodeColumns.TRAIT, trait);
			values.put(SawarecodeColumns.PRICECOMMENT, pricecomment);
			values.put(SawarecodeColumns.PLANDATE, plandate);
			values.put(SawarecodeColumns.WAREGOTO, waregoto);
			values.put(SawarecodeColumns.PRODAREA, prodarea);
			values.put(SawarecodeColumns.PATTEN, patten);
			values.put(SawarecodeColumns.SIZEORDER, sizeorder);
			values.put(SawarecodeColumns.REMARK, remark);
			values.put(SawarecodeColumns.DATE3, date3);
			values.put(SawarecodeColumns.DATE4, date4);
			values.put(SawarecodeColumns.PROCUREDATE, procuredate);
			values.put(SawarecodeColumns.STYLESPEC, stylespec);
			values.put(SawarecodeColumns.FACTORYCODE, factorycode);
			values.put(SawarecodeColumns.DIRECTION, direction);
			values.put(SawarecodeColumns.CLIENCODE, cliencode);
			values.put(SawarecodeColumns.STDNAME, stdname);
			values.put(SawarecodeColumns.CTYPE, ctype);
			values.put(SawarecodeColumns.WAREDEGREE, waredegree);
			values.put(SawarecodeColumns.SALEPRICE, saleprice);
			values.put(SawarecodeColumns.AVGPURTHPRICE, avgpurhprice);
			
			return values;
		}
		
		private static SaWareCode restoreSawareCodeWithCursor(Cursor cursor) {
//			try {
//				if(cursor.moveToFirst()) {
//					return getContent(cursor, SaWareCode.class);
//				} else {
//					return null;
//				}
//			} finally {
//				cursor.close();
//			}
			return getContent(cursor, SaWareCode.class);
		}
		
		public static SaWareCode restoreSawareCodeWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(SaWareCode.CONTENT_URI, id);
			Cursor c = context.getContentResolver().query(u, SaWareCode.CONTENT_PROJECTION, null, null, null);
			return restoreSawareCodeWithCursor(c);
		}
		
		public static SaWareCode restoreSaWareCodeWithWareCode(Context context, String wc) {
			Uri u = SaWareCode.CONTENT_URI;
			Cursor c = context.getContentResolver().query(u, SaWareCode.CONTENT_PROJECTION, SawarecodeColumns.WARECODE + " = ?", new String[]{wc}, null);
			return restoreSawareCodeWithCursor(c);
		}
		
		public static String getSaWareTypeName(Context context, String waretypeid) {
			String waretypename = "";
			Cursor c = context.getContentResolver().query(SaWareType.CONTENT_URI, SaWareType.CONTENT_PROJECTION, SaWareTypeColumns.WARETYPEID + "=?", new String[]{waretypeid}, null);
			try {
				if(c != null && c.moveToFirst()) {
					waretypename = c.getString(SaWareType.CONTENT_WARETYPENAME_COLUMN);
				}
			} finally  {
				if(c != null) {
					c.close();
				}
			}
			return waretypename;
		}
		
		public static int getWareNum(Context context, String warecode) {
			int warenum = 0;
			Cursor c = context.getContentResolver().query(SaIndent.CONTENT_URI, SaIndent.CONTENT_PROJECTION, SaIndentColumns.WARECODE + "=?", new String[]{warecode}, null);
			try {
				if(c != null && c.moveToFirst()) {
					while(!c.isAfterLast()) {
						warenum += c.getInt(SaIndent.CONTENT_WARENUM_COLUMN);
						c.moveToNext();
					}
				} 
			} finally {
				if(c != null) {
					c.close();
				}
			}
			return warenum;
		}
		
	}
	
	public interface SaColorCodeColumns {
		public static final String ID = "_id";
		//颜色代码
		public static final String COLORCODE = "colorcode";
		//颜色名称
		public static final String COLORNAME = "colorname";
		//py
		public static final String PY = "py";
		//公司自定义代码
		public static final String CODE = "code";
		//色系说明
		public static final String REMARK = "remark";
		//pb
		public static final String PB = "pb";
		//trade
		public static final String TRADE = "trade";
	}
	
	public static final class SaColorCode extends AsContent implements SaColorCodeColumns {

		public static final String TABLE_NAME = "sacolorcode";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sacolorcode");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_COLORCODE_COLUMN = 1;
		public static final int CONTENT_COLORNAME_COLUMN = 2;
		public static final int CONTENT_PY_COLUMN = 3;
		public static final int CONTENT_CODE_COLUMN = 4;
		public static final int CONTENT_REMARK_COLUMN = 5;
		public static final int CONTENT_PB_COLUMN = 6;
		public static final int CONTENT_TRADE_COLUMN = 7;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaColorCodeColumns.COLORCODE,
			SaColorCodeColumns.COLORNAME,
			SaColorCodeColumns.PY,
			SaColorCodeColumns.CODE,
			SaColorCodeColumns.REMARK,
			SaColorCodeColumns.PB,
			SaColorCodeColumns.TRADE
		};
		
		public String colorCode;
		public String colorName;
		public String py;
		public String code;
		public String remark;
		public String pb;
		public String trade;
		
		public SaColorCode() {
			mBaseUri = CONTENT_URI;
		}
		
		
		
		@Override
		public SaColorCode restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			colorCode = cursor.getString(CONTENT_COLORCODE_COLUMN);
			colorName = cursor.getString(CONTENT_COLORNAME_COLUMN);
			py = cursor.getString(CONTENT_PY_COLUMN);
			code = cursor.getString(CONTENT_CODE_COLUMN);
			remark = cursor.getString(CONTENT_REMARK_COLUMN);
			pb = cursor.getString(CONTENT_PB_COLUMN);
			trade = cursor.getString(CONTENT_TRADE_COLUMN);
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaColorCodeColumns.COLORCODE, colorCode);
			values.put(SaColorCodeColumns.COLORNAME, colorName);
			values.put(SaColorCodeColumns.PY, py);
			values.put(SaColorCodeColumns.CODE, code);
			values.put(SaColorCodeColumns.REMARK, remark);
			values.put(SaColorCodeColumns.PB, pb);
			values.put(SaColorCodeColumns.TRADE, trade);
			return values;
		}
		
		private static SaColorCode restoreSaColorCodeWithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, SaColorCode.class);
				} else {
					return null;
				}
			} finally  {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static SaColorCode restoreColorCodeWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(SaColorCode.CONTENT_URI, id);
			Cursor c = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaColorCodeWithCursor(c);
		}
		
	}
	
	public interface SaWareColorColumns {
		public static final String ID = "_id";
		//商品编号 
		public static final String WARECODE = "warecode";
		//颜色代码
		public static final String COLORCODE = "colorcode";
		//颜色描述说明
		public static final String COLORCOMMENT = "colorcomment";
	}
	
	public static final class SaWareColor extends AsContent implements SaWareColorColumns {

		public static final String TABLE_NAME = "saware_color";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sawarecolor");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_WARECODE_COLUMN = 1;
		public static final int CONTENT_COLORCODE_COLUMN = 2;
		public static final int CONTENT_COLORCOMMENT_COLUMN = 3;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaWareColorColumns.WARECODE,
			SaWareColorColumns.COLORCODE,
			SaWareColorColumns.COLORCOMMENT
		};
		
		public String wareCode;
		public String colorCode;
		public String colorComment;
		
		public SaWareColor() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaWareColor restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					wareCode = cursor.getString(SaWareColor.CONTENT_WARECODE_COLUMN);
					colorCode = cursor.getString(SaWareColor.CONTENT_COLORCODE_COLUMN);
					colorComment = cursor.getString(SaWareColor.CONTENT_COLORCOMMENT_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaWareColorColumns.COLORCODE, colorCode);
			values.put(SaWareColorColumns.WARECODE, wareCode);
			values.put(SaWareColorColumns.COLORCOMMENT, colorComment);
			return values;
		}
		
		private static SaWareColor restoreSaWareColorWithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, SaWareColor.class);
				} else {
					return null;
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static SaWareColor restoreSaWareColorWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(SaWareColor.CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaWareColorWithCursor(cursor);
		}
	}
	
	public interface ShowSizeColumns {
		public static final String ID = "_id";
		//尺码
		public static final String SIZE = "size";
		//尺码组代码
		public static final String TYPE = "type";
		//显示次序
		public static final String SHOW = "show";
		//Title
		public static final String TITLE = "title";
		//PrintTitle
		public static final String PRINTTITLE = "printtitle";
		//SizeGroup
		public static final String SIZEGROUP = "sizegroup";
		//Scale
		public static final String SCALE = "scale";
	}
	
	public static final class ShowSize extends AsContent implements ShowSizeColumns {

		public static final String TABLE_NAME = "showsize";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/showsize");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_SIZE_COLUMN = 1;
		public static final int CONTENT_TYPE_COLUMN = 2;
		public static final int CONTENT_SHOW_COLUMN = 3;
		public static final int CONTENT_TITLE_COLUMN = 4;
		public static final int CONTENT_PRINTTITILE_COLUMN = 5;
		public static final int CONTENT_SIZEGROUP_COLUMN = 6;
		public static final int CONTENT_SCALE_COLUMN = 7;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			ShowSizeColumns.SIZE,
			ShowSizeColumns.TYPE,
			ShowSizeColumns.SHOW,
			ShowSizeColumns.TITLE,
			ShowSizeColumns.PRINTTITLE,
			ShowSizeColumns.SIZEGROUP,
			ShowSizeColumns.SCALE
		};
		
		public String size;
		public String type;
		public String show;
		public String title;
		public String printTitle;
		public String sizeGroup;
		public String scale;
		
		public ShowSize() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public ShowSize restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					size = cursor.getString(CONTENT_SIZE_COLUMN);
					type = cursor.getString(CONTENT_TYPE_COLUMN);
					show = cursor.getString(CONTENT_SHOW_COLUMN);
					title = cursor.getString(CONTENT_TITLE_COLUMN);
					printTitle = cursor.getString(CONTENT_PRINTTITILE_COLUMN);
					sizeGroup = cursor.getString(CONTENT_SIZEGROUP_COLUMN);
					scale = cursor.getString(CONTENT_SCALE_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(ShowSizeColumns.SIZE, size);
			values.put(ShowSizeColumns.TYPE, type);
			values.put(ShowSizeColumns.SHOW, show);
			values.put(ShowSizeColumns.TITLE, title);
			values.put(ShowSizeColumns.PRINTTITLE, printTitle);
			values.put(ShowSizeColumns.SIZEGROUP, sizeGroup);
			values.put(ShowSizeColumns.SCALE, scale);
			return values;
		}
		
		
		private static ShowSize restoreShowSizeWithCursor(Cursor cursor) {
			try {
				return getContent(cursor, ShowSize.class);
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static ShowSize restoreShowSizeWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor c = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreShowSizeWithCursor(c);
		}
	}
	
	public interface SaWareSizeColumns {
		public static final String ID = "_id";
		//款号
		public static final String WARECODE = "warecode";
		//规格
		public static final String SIZE = "size";
		//显示次序
		public static final String SHOWSORT = "showsort";
		//Stand
		public static final String STAND = "stand";
	}
	
	public static final class SaWareSize extends AsContent implements SaWareSizeColumns {
		
		public static final String TABLE_NAME = "saware_size";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sawaresize");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_WARECODE_COLUMN = 1;
		public static final int CONTENT_SIZE_COLUMN = 2;
		public static final int CONTENT_SHOWSORT_COLUMN = 3;
		public static final int CONTENT_STAND_COLUMN = 4;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaWareSizeColumns.WARECODE,
			SaWareSizeColumns.SIZE,
			SaWareSizeColumns.SHOWSORT,
			SaWareSizeColumns.STAND
		};
		
		public String wareCode;
		public String size;
		public String showSort;
		public String stand;
		
		public SaWareSize() {
			mBaseUri = CONTENT_URI;
		}

		@Override
		public SaWareSize restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					wareCode = cursor.getString(CONTENT_WARECODE_COLUMN);
					size = cursor.getString(CONTENT_SIZE_COLUMN);
					showSort = cursor.getString(CONTENT_SHOWSORT_COLUMN);
					stand = cursor.getString(CONTENT_STAND_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaWareSizeColumns.WARECODE, wareCode);
			values.put(SaWareSizeColumns.SIZE, size);
			values.put(SaWareSizeColumns.SHOWSORT, showSort);
			values.put(SaWareSizeColumns.STAND, stand);
			return values;
		}
		
		private static SaWareSize restoreSaWareSizeWithCursor(Cursor cursor) {
			try {
				return getContent(cursor, SaWareSize.class);
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static SaWareSize restoreSaWareSizeWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaWareSizeWithCursor(cursor);
		}
	}
	
	public interface SaParaColumns {
		public static final String ID = "_id";
		//公共参数类型
		public static final String PARATYPE = "paratype";
		//公共参数代码
		public static final String PARA = "para";
		//公共参数名称
		public static final String PARACONNENT = "paraconnent";
		//说明
		public static final String CONNENT = "connent";
	}
	
	public static final class SaPara extends AsContent implements SaParaColumns {

		public static final String TABLE_NAME = "sapara";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sapara");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_PARATYPE_COLUMN = 1;
		public static final int CONTENT_PARA_COLUMN = 2;
		public static final int CONTENT_PARACONNENT_COLUMN = 3;
		public static final int CONTENT_CONNENT_COLUMN = 4;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaParaColumns.PARATYPE,
			SaParaColumns.PARA,
			SaParaColumns.PARACONNENT,
			SaParaColumns.CONNENT
		};
		
		public String paraType;
		public String para;
		public String paraConnent;
		public String connent;
		
		public SaPara() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaPara restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					paraType = cursor.getString(CONTENT_PARATYPE_COLUMN);
					para = cursor.getString(CONTENT_PARA_COLUMN);
					paraConnent = cursor.getString(CONTENT_PARACONNENT_COLUMN);
					connent = cursor.getString(CONTENT_CONNENT_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaParaColumns.PARATYPE, paraType);
			values.put(SaParaColumns.PARA, para);
			values.put(SaParaColumns.PARACONNENT, paraConnent);
			values.put(SaParaColumns.CONNENT, connent);
			return values;
		}
		
		private static SaPara restoreSaParaWithCursor(Cursor cursor) {
			try {
				if(cursor!=null && cursor.moveToFirst()) {
					return getContent(cursor, SaPara.class);
				} else {
					return null;
				}
			} finally {
				if(cursor!=null) {
					cursor.close();
				}
			}
		}
		
		public static SaPara restoreSaParaWithid(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaParaWithCursor(cursor);
		}
	}
	
	public interface SaWareTypeColumns {
		public static final String ID = "_id";
		//大类代码
		public static final String WARETYPEID = "waretypeid";
		//大类名称
		public static final String WARETYPENAME = "waretypename";
		//TaxRate
		public static final String TAXRATE = "taxrate";
		//Standard
		public static final String STANDARD = "standard";
		//Flag
		public static final String FLAG = "flag";
		//SizeFlag
		public static final String SIZEFLAG = "sizeflag";
	}
	
	public static final class SaWareType extends AsContent implements SaWareTypeColumns{

		public static final String TABLE_NAME = "sawaretype";
		
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sawaretype");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_WARETYPEID_COLUMN = 1;
		public static final int CONTENT_WARETYPENAME_COLUMN = 2;
		public static final int CONTENT_TAXRATE_COLUMN = 3;
		public static final int CONTENT_STANDARD_COLUMN = 4;
		public static final int CONTENT_FLAG_COLUMN = 5;
		public static final int CONTENT_SIZEFLAG_COLUMN = 6;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaWareTypeColumns.WARETYPEID,
			SaWareTypeColumns.WARETYPENAME,
			SaWareTypeColumns.TAXRATE,
			SaWareTypeColumns.STANDARD,
			SaWareTypeColumns.FLAG,
			SaWareTypeColumns.SIZEFLAG
		};
		
		public String wareTypeId;
		public String wareTypeName;
		public String taxRate;
		public String standard;
		public String flag;
		public String sizeFlag;
		
		public SaWareType() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaWareType restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					wareTypeId = cursor.getString(CONTENT_WARETYPEID_COLUMN);
					wareTypeName = cursor.getString(CONTENT_WARETYPENAME_COLUMN);
					taxRate = cursor.getString(CONTENT_TAXRATE_COLUMN);
					standard = cursor.getString(CONTENT_STANDARD_COLUMN);
					flag = cursor.getString(CONTENT_FLAG_COLUMN);
					sizeFlag = cursor.getString(CONTENT_SIZEFLAG_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaWareTypeColumns.WARETYPEID, wareTypeId);
			values.put(SaWareTypeColumns.WARETYPENAME, wareTypeName);
			values.put(SaWareTypeColumns.TAXRATE, taxRate);
			values.put(SaWareTypeColumns.STANDARD, standard);
			values.put(SaWareTypeColumns.FLAG, flag);
			values.put(SaWareTypeColumns.SIZEFLAG, sizeFlag);
			return values;
		}
		
		private static SaWareType restoreSaWareTypeWithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, SaWareType.class);
				} else {
					return null;
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static SaWareType restoreSaWareTypeWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaWareTypeWithCursor(cursor);
		}
	}
	
	public interface Type1Columns {
		public static final String ID = "_id";
		//小类代码
		public static final String IID = "id";
		//小类名称
		public static final String TYPE1 = "type1";
		//所属大类
		public static final String WARETYPEID = "waretypeid";
		//Standard
		public static final String STANDARD = "standard";
		//Remark
		public static final String REMARK = "remark";
	}
	
	public static final class Type1 extends AsContent implements Type1Columns {

		public static final String TABLE_NAME = "type1";
		
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/type1");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_IID_COLUMN = 1;
		public static final int CONTENT_TYPE1_COLUMN = 2;
		public static final int CONTENT_WARETYPEID_COLUMN = 3;
		public static final int CONTENT_STANDARD_COLUMN = 4;
		public static final int CONTENT_REMARK_COLUMN = 5;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			Type1Columns.IID,
			Type1Columns.TYPE1,
			Type1Columns.WARETYPEID,
			Type1Columns.STANDARD,
			Type1Columns.REMARK
		};
		
		public String iid;
		public String type1;
		public String wareTypeId;
		public String standard;
		public String remark;
		
		public Type1() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public Type1 restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					iid = cursor.getString(CONTENT_IID_COLUMN);
					type1 = cursor.getString(CONTENT_TYPE1_COLUMN);
					wareTypeId = cursor.getString(CONTENT_WARETYPEID_COLUMN);
					standard = cursor.getString(CONTENT_STANDARD_COLUMN);
					remark = cursor.getString(CONTENT_REMARK_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(Type1Columns.IID, iid);
			values.put(Type1Columns.TYPE1, type1);
			values.put(Type1Columns.WARETYPEID, wareTypeId);
			values.put(Type1Columns.STANDARD, standard);
			values.put(Type1Columns.REMARK, remark);
			return values;
		}
		
		private static Type1 restoreType1WithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, Type1.class);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return null;
		}
		
		public static Type1 restoreType1WithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreType1WithCursor(cursor);
		}
		
	}
	
	public interface SaIndentColumns {
		public static final String ID = "_id";
		//顺序号
		public static final String INDENTNO = "indentno";
		//店铺代码
		public static final String DEPARTCODE = "departcode";
		//款号
		public static final String WARECODE = "warecode";
		//颜色代码
		public static final String COLORCODE = "colorcode";
		//第一位尺码的订货数
		public static final String S01 = "s01";
		//第二位尺码的订货数
		public static final String S02 = "s02";
		public static final String S03 = "s03";
		public static final String S04 = "s04";
		public static final String S05 = "s05";
		public static final String S06 = "s06";
		public static final String S07 = "s07";
		public static final String S08 = "s08";
		public static final String S09 = "s09";
		public static final String S10 = "s10";
		public static final String S11 = "s11";
		public static final String S12 = "s12";
		public static final String S13 = "s13";
		public static final String S14 = "s14";
		public static final String S15 = "s15";
		public static final String S16 = "s16";
		public static final String S17 = "s17";
		public static final String S18 = "s18";
		public static final String S19 = "s19";
		public static final String S20 = "s20";
		public static final String INPUTDATE = "inputdate";
		public static final String INPUTMAN = "inputman";
		public static final String WARENUM = "warenum";
		public static final String REMARK = "remark";
		public static final String OFLAG = "oflag";
	}
	
	public static final class SaIndent extends AsContent implements SaIndentColumns {
		
		public static final String TABLE_NAME = "saindent";
		
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/saindent");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_INDENTNO_COLUMN = 1;
		public static final int CONTENT_DEPARTCODE_COLUMN = 2;
		public static final int CONTENT_WARECODE_COLUMN = 3;
		public static final int CONTENT_COLORCODE_COLUMN = 4;
		public static final int CONTENT_S01_COLUMN = 5;
		public static final int CONTENT_S02_COLUMN = 6;
		public static final int CONTENT_S03_COLUMN = 7;
		public static final int CONTENT_S04_COLUMN = 8;
		public static final int CONTENT_S05_COLUMN = 9;
		public static final int CONTENT_S06_COLUMN = 10;
		public static final int CONTENT_S07_COLUMN = 11;
		public static final int CONTENT_S08_COLUMN = 12;
		public static final int CONTENT_S09_COLUMN = 13;
		public static final int CONTENT_S10_COLUMN = 14;
		public static final int CONTENT_S11_COLUMN = 15;
		public static final int CONTENT_S12_COLUMN = 16;
		public static final int CONTENT_S13_COLUMN = 17;
		public static final int CONTENT_S14_COLUMN = 18;
		public static final int CONTENT_S15_COLUMN = 19;
		public static final int CONTENT_S16_COLUMN = 20;
		public static final int CONTENT_S17_COLUMN = 21;
		public static final int CONTENT_S18_COLUMN = 22;
		public static final int CONTENT_S19_COLUMN = 23;
		public static final int CONTENT_S20_COLUMN = 24;
		public static final int CONTENT_INPUTDATE_COLUMN = 25;
		public static final int CONTENT_INPUTMAN_COLUMN = 26;
		public static final int CONTENT_WARENUM_COLUMN = 27;
		public static final int CONTENT_REMARK_COLUMN = 28;
		public static final int CONTENT_OFLAG_COLUMN = 29;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaIndentColumns.INDENTNO,
			SaIndentColumns.DEPARTCODE,
			SaIndentColumns.WARECODE,
			SaIndentColumns.COLORCODE,
			SaIndentColumns.S01,
			SaIndentColumns.S02,
			SaIndentColumns.S03,
			SaIndentColumns.S04,
			SaIndentColumns.S05,
			SaIndentColumns.S06,
			SaIndentColumns.S07,
			SaIndentColumns.S08,
			SaIndentColumns.S09,
			SaIndentColumns.S10,
			SaIndentColumns.S11,
			SaIndentColumns.S12,
			SaIndentColumns.S13,
			SaIndentColumns.S14,
			SaIndentColumns.S15,
			SaIndentColumns.S16,
			SaIndentColumns.S17,
			SaIndentColumns.S18,
			SaIndentColumns.S19,
			SaIndentColumns.S20,
			SaIndentColumns.INPUTDATE,
			SaIndentColumns.INPUTMAN,
			SaIndentColumns.WARENUM,
			SaIndentColumns.REMARK,
			SaIndentColumns.OFLAG
		};

		public String indentNo;
		public String departCode;
		public String wareCode;
		public String colorCode;
		public int s01;
		public int s02;
		public int s03;
		public int s04;
		public int s05;
		public int s06;
		public int s07;
		public int s08;
		public int s09;
		public int s10;
		public int s11;
		public int s12;
		public int s13;
		public int s14;
		public int s15;
		public int s16;
		public int s17;
		public int s18;
		public int s19;
		public int s20;
		public String inputDate;
		public String inputMan;
		public int wareNum;
		public String remark;
		public String oFlag;
		public String colorName;
		
		public SaIndent() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaIndent restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					indentNo = cursor.getString(CONTENT_INDENTNO_COLUMN);
					departCode = cursor.getString(CONTENT_DEPARTCODE_COLUMN);
					wareCode = cursor.getString(CONTENT_WARECODE_COLUMN);
					colorCode = cursor.getString(CONTENT_COLORCODE_COLUMN);
					s01 = cursor.getInt(CONTENT_S01_COLUMN);
					s02 = cursor.getInt(CONTENT_S02_COLUMN);
					s03 = cursor.getInt(CONTENT_S03_COLUMN);
					s04 = cursor.getInt(CONTENT_S04_COLUMN);
					s05 = cursor.getInt(CONTENT_S05_COLUMN);
					s06 = cursor.getInt(CONTENT_S06_COLUMN);
					s07 = cursor.getInt(CONTENT_S07_COLUMN);
					s08 = cursor.getInt(CONTENT_S08_COLUMN);
					s09 = cursor.getInt(CONTENT_S09_COLUMN);
					s10 = cursor.getInt(CONTENT_S10_COLUMN);
					s11 = cursor.getInt(CONTENT_S11_COLUMN);
					s12 = cursor.getInt(CONTENT_S12_COLUMN);
					s13 = cursor.getInt(CONTENT_S13_COLUMN);
					s14 = cursor.getInt(CONTENT_S14_COLUMN);
					s15 = cursor.getInt(CONTENT_S15_COLUMN);
					s16 = cursor.getInt(CONTENT_S16_COLUMN);
					s17 = cursor.getInt(CONTENT_S17_COLUMN);
					s18 = cursor.getInt(CONTENT_S18_COLUMN);
					s19 = cursor.getInt(CONTENT_S19_COLUMN);
					s20 = cursor.getInt(CONTENT_S20_COLUMN);
					inputDate = cursor.getString(CONTENT_INPUTDATE_COLUMN);
					inputMan = cursor.getString(CONTENT_INPUTMAN_COLUMN);
					wareNum = cursor.getInt(CONTENT_WARENUM_COLUMN);
					remark = cursor.getString(CONTENT_REMARK_COLUMN);
					oFlag = cursor.getString(CONTENT_OFLAG_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaIndentColumns.INDENTNO, indentNo);
			values.put(SaIndentColumns.DEPARTCODE, departCode);
			values.put(SaIndentColumns.WARECODE, wareCode);
			values.put(SaIndentColumns.COLORCODE, colorCode);
			values.put(SaIndentColumns.S01, s01);
			values.put(SaIndentColumns.S02, s02);
			values.put(SaIndentColumns.S03, s03);
			values.put(SaIndentColumns.S04, s04);
			values.put(SaIndentColumns.S05, s05);
			values.put(SaIndentColumns.S06, s06);
			values.put(SaIndentColumns.S07, s07);
			values.put(SaIndentColumns.S08, s08);
			values.put(SaIndentColumns.S09, s09);
			values.put(SaIndentColumns.S10, s10);
			values.put(SaIndentColumns.S11, s11);
			values.put(SaIndentColumns.S12, s12);
			values.put(SaIndentColumns.S13, s13);
			values.put(SaIndentColumns.S14, s14);
			values.put(SaIndentColumns.S15, s15);
			values.put(SaIndentColumns.S16, s16);
			values.put(SaIndentColumns.S17, s17);
			values.put(SaIndentColumns.S18, s18);
			values.put(SaIndentColumns.S19, s19);
			values.put(SaIndentColumns.S20, s20);
			values.put(SaIndentColumns.INPUTDATE, inputDate);
			values.put(SaIndentColumns.INPUTMAN, inputMan);
			values.put(SaIndentColumns.WARENUM, wareNum);
			values.put(SaIndentColumns.REMARK, remark);
			values.put(SaIndentColumns.OFLAG, oFlag);
			return values;
		}
		
		private static SaIndent restoreSaIndentWithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, SaIndent.class);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return null;
		}
		
		public static SaIndent restoreSaIndentWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaIndentWithCursor(cursor);
		}
	}
	
	public interface SaWareGroupColumns {
		public static final String ID = "_id";
		public static final String ITEMCODE = "itemcode";
		public static final String GROUPNAME = "groupname";
		public static final String WARECODE = "warecode";
		public static final String COLORCODE = "colorcode";
		public static final String REMARK = "remark";
	}
	
	public static final class SaWareGroup extends AsContent implements SaWareGroupColumns {

		public static final String TABLE_NAME = "sawaregroup";
		
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sawaregroup");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_ITEMCODE_COLUMN = 1;
		public static final int CONTENT_GROUPNAME_COLUMN = 2;
		public static final int CONTENT_WARECODE_COLUMN = 3;
		public static final int CONTENT_COLORCODE_COLUMN = 4;
		public static final int CONTENT_REMARK_COLUMN = 5;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaWareGroupColumns.ITEMCODE,
			SaWareGroupColumns.GROUPNAME,
			SaWareGroupColumns.WARECODE,
			SaWareGroupColumns.COLORCODE,
			SaWareGroupColumns.REMARK
		};
		
		public String itemCode;
		public String groupName;
		public String wareCode;
		public String colorCode;
		public String remark;
		
		public SaWareGroup() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaWareGroup restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					itemCode = cursor.getString(CONTENT_ITEMCODE_COLUMN);
					groupName = cursor.getString(CONTENT_GROUPNAME_COLUMN);
					wareCode = cursor.getString(CONTENT_WARECODE_COLUMN);
					colorCode = cursor.getString(CONTENT_COLORCODE_COLUMN);
					remark = cursor.getString(CONTENT_REMARK_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaWareGroupColumns.ITEMCODE, itemCode);
			values.put(SaWareGroupColumns.GROUPNAME, groupName);
			values.put(SaWareGroupColumns.WARECODE, wareCode);
			values.put(SaWareGroupColumns.COLORCODE, colorCode);
			values.put(SaWareGroupColumns.REMARK, remark);
			return values;
		}
		
		private static SaWareGroup restoreSaWareGroupWithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, SaWareGroup.class);
				} else {
					return null;
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static SaWareGroup restoreSaWareGroupWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(CONTENT_URI, id);
			Cursor cursor = context.getContentResolver().query(u, CONTENT_PROJECTION, null, null, null);
			return restoreSaWareGroupWithCursor(cursor);
		}
		
		public static String getGroupName(Context context, String itemCode) {
			String groupName = "";
			Cursor c = context.getContentResolver().query(SaWareGroup.CONTENT_URI, SaWareGroup.CONTENT_PROJECTION, SaWareGroupColumns.ITEMCODE + "=?", new String[]{itemCode}, null);
			try {
				if(c!=null && c.moveToFirst()) {
					groupName = c.getString(SaWareGroup.CONTENT_GROUPNAME_COLUMN);
				}
			} finally {
				if(c != null) {
					c.close();
				}
			}
			return groupName;
		}
		
	}
	
	public interface SaOrderTrgetColumns {
		public static final String ID = "_id";
		public static final String DEPARTCODE = "departcode";
		public static final String ITEM1 = "item1";
		public static final String ITEM2 = "item2";
		public static final String ITEM3 = "item3";
		public static final String ITEM4 = "item4";
		//订货量指标
		public static final String NUM1 = "num1";
		//订货额指标
		public static final String NUM2 = "num2";
		//订货款色指标
		public static final String NUM3 = "num3";
		//指标类型
		public static final String TYPE = "type";
		//1统计到款/款色2
		public static final String TYPE1 = "type1";
	}
	
	public static final class SaOrderTrget extends AsContent implements SaOrderTrgetColumns {

		public static final String TABLE_NAME = "saordertrget";
		
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/saordertrget");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_DEPARTCODE_COLUMN = 1;
		public static final int CONTENT_ITEM1_COLUMN = 2;
		public static final int CONTENT_ITEM2_COLUMN = 3;
		public static final int CONTENT_ITEM3_COLUMN = 4;
		public static final int CONTENT_ITEM4_COLUMN = 5;
		public static final int CONTENT_NUM1_COLUMN = 6;
		public static final int CONTENT_NUM2_COLUMN = 7;
		public static final int CONTENT_NUM3_COLUMN = 8;
		public static final int CONTENT_TYPE_COLUMN = 9;
		public static final int CONTENT_TYPE1_COLUMN = 10;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaOrderTrgetColumns.DEPARTCODE,
			SaOrderTrgetColumns.ITEM1,
			SaOrderTrgetColumns.ITEM2,
			SaOrderTrgetColumns.ITEM3,
			SaOrderTrgetColumns.ITEM4,
			SaOrderTrgetColumns.NUM1,
			SaOrderTrgetColumns.NUM2,
			SaOrderTrgetColumns.NUM3,
			SaOrderTrgetColumns.TYPE,
			SaOrderTrgetColumns.TYPE1
		};
		
		public String departCode;
		public String item1;
		public String item2;
		public String item3;
		public String item4;
		public int num1;
		public int num2;
		public int num3;
		public String type;
		public String type1;
		
		public SaOrderTrget() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaOrderTrget restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					departCode = cursor.getString(CONTENT_DEPARTCODE_COLUMN);
					item1 = cursor.getString(CONTENT_ITEM1_COLUMN);
					item2 = cursor.getString(CONTENT_ITEM2_COLUMN);
					item3 = cursor.getString(CONTENT_ITEM3_COLUMN);
					item4 = cursor.getString(CONTENT_ITEM4_COLUMN);
					num1 = cursor.getInt(CONTENT_NUM1_COLUMN);
					num2 = cursor.getInt(CONTENT_NUM2_COLUMN);
					num3 = cursor.getInt(CONTENT_NUM3_COLUMN);
					type = cursor.getString(CONTENT_TYPE_COLUMN);
					type1 = cursor.getString(CONTENT_TYPE1_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaOrderTrget.DEPARTCODE, departCode);
			values.put(SaOrderTrget.ITEM1, item1);
			values.put(SaOrderTrget.ITEM2, item2);
			values.put(SaOrderTrget.ITEM3, item4);
			values.put(SaOrderTrget.ITEM4, item4);
			values.put(SaOrderTrget.NUM1, num1);
			values.put(SaOrderTrget.NUM2, num2);
			values.put(SaOrderTrget.NUM3, num3);
			values.put(SaOrderTrget.TYPE, type);
			values.put(SaOrderTrget.TYPE1, type1);
			return values;
		}
	}
	
	public interface SaSizeSetColumns {
		public static final String ID = "_id";
		public static final String SIZEGROUP = "sizegroup";
		public static final String S01 = "s01";
		public static final String S02 = "s02";
		public static final String S03 = "s03";
		public static final String S04 = "s04";
		public static final String S05 = "s05";
		public static final String S06 = "s06";
		public static final String S07 = "s07";
		public static final String S08 = "s08";
		public static final String S09 = "s09";
		public static final String S10 = "s10";
		public static final String S11 = "s11";
		public static final String S12 = "s12";
		public static final String S13 = "s13";
		public static final String S14 = "s14";
		public static final String S15 = "s15";
		public static final String S16 = "s16";
		public static final String S17 = "s17";
		public static final String S18 = "s18";
		public static final String S19 = "s19";
		public static final String S20 = "s20";
	}
	
	public static final class SaSizeSet extends AsContent implements SaSizeSetColumns {

		public static final String TABLE_NAME = "sasizeset";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/sasizeset");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_SIZEGROUP_COLUMN = 1;
		public static final int CONTENT_S01_COLUMN = 2;
		public static final int CONTENT_S02_COLUMN = 3;
		public static final int CONTENT_S03_COLUMN = 4;
		public static final int CONTENT_S04_COLUMN = 5;
		public static final int CONTENT_S05_COLUMN = 6;
		public static final int CONTENT_S06_COLUMN = 7;
		public static final int CONTENT_S07_COLUMN = 8;
		public static final int CONTENT_S08_COLUMN = 9;
		public static final int CONTENT_S09_COLUMN = 10;
		public static final int CONTENT_S10_COLUMN = 11;
		public static final int CONTENT_S11_COLUMN = 12;
		public static final int CONTENT_S12_COLUMN = 13;
		public static final int CONTENT_S13_COLUMN = 14;
		public static final int CONTENT_S14_COLUMN = 15;
		public static final int CONTENT_S15_COLUMN = 16;
		public static final int CONTENT_S16_COLUMN = 17;
		public static final int CONTENT_S17_COLUMN = 18;
		public static final int CONTENT_S18_COLUMN = 19;
		public static final int CONTENT_S19_COLUMN = 20;
		public static final int CONTENT_S20_COLUMN = 21;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			SaSizeSetColumns.SIZEGROUP,
			SaSizeSetColumns.S01,
			SaSizeSetColumns.S02,
			SaSizeSetColumns.S03,
			SaSizeSetColumns.S04,
			SaSizeSetColumns.S05,
			SaSizeSetColumns.S06,
			SaSizeSetColumns.S07,
			SaSizeSetColumns.S08,
			SaSizeSetColumns.S09,
			SaSizeSetColumns.S10,
			SaSizeSetColumns.S11,
			SaSizeSetColumns.S12,
			SaSizeSetColumns.S13,
			SaSizeSetColumns.S14,
			SaSizeSetColumns.S15,
			SaSizeSetColumns.S16,
			SaSizeSetColumns.S17,
			SaSizeSetColumns.S18,
			SaSizeSetColumns.S19,
			SaSizeSetColumns.S20
		};
		
		public String sizeGroup;
		public int s01;
		public int s02;
		public int s03;
		public int s04;
		public int s05;
		public int s06;
		public int s07;
		public int s08;
		public int s09;
		public int s10;
		public int s11;
		public int s12;
		public int s13;
		public int s14;
		public int s15;
		public int s16;
		public int s17;
		public int s18;
		public int s19;
		public int s20;
		
		public SaSizeSet() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public SaSizeSet restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					sizeGroup = cursor.getString(CONTENT_SIZEGROUP_COLUMN);
					s01 = cursor.getInt(CONTENT_S01_COLUMN);
					s02 = cursor.getInt(CONTENT_S02_COLUMN);
					s03 = cursor.getInt(CONTENT_S03_COLUMN);
					s04 = cursor.getInt(CONTENT_S04_COLUMN);
					s05 = cursor.getInt(CONTENT_S05_COLUMN);
					s06 = cursor.getInt(CONTENT_S06_COLUMN);
					s07 = cursor.getInt(CONTENT_S07_COLUMN);
					s08 = cursor.getInt(CONTENT_S08_COLUMN);
					s09 = cursor.getInt(CONTENT_S09_COLUMN);
					s10 = cursor.getInt(CONTENT_S10_COLUMN);
					s11 = cursor.getInt(CONTENT_S11_COLUMN);
					s12 = cursor.getInt(CONTENT_S13_COLUMN);
					s13 = cursor.getInt(CONTENT_S14_COLUMN);
					s14 = cursor.getInt(CONTENT_S15_COLUMN);
					s16 = cursor.getInt(CONTENT_S16_COLUMN);
					s17 = cursor.getInt(CONTENT_S17_COLUMN);
					s18 = cursor.getInt(CONTENT_S18_COLUMN);
					s19 = cursor.getInt(CONTENT_S19_COLUMN);
					s20 = cursor.getInt(CONTENT_S20_COLUMN);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(SaSizeSetColumns.SIZEGROUP, sizeGroup);
			values.put(SaSizeSetColumns.S01, s01);
			values.put(SaSizeSetColumns.S02, s02);
			values.put(SaSizeSetColumns.S03, s03);
			values.put(SaSizeSetColumns.S04, s04);
			values.put(SaSizeSetColumns.S05, s05);
			values.put(SaSizeSetColumns.S06, s06);
			values.put(SaSizeSetColumns.S07, s07);
			values.put(SaSizeSetColumns.S08, s08);
			values.put(SaSizeSetColumns.S09, s09);
			values.put(SaSizeSetColumns.S10, s10);
			values.put(SaSizeSetColumns.S11, s11);
			values.put(SaSizeSetColumns.S12, s12);
			values.put(SaSizeSetColumns.S13, s13);
			values.put(SaSizeSetColumns.S14, s14);
			values.put(SaSizeSetColumns.S15, s15);
			values.put(SaSizeSetColumns.S16, s16);
			values.put(SaSizeSetColumns.S17, s17);
			values.put(SaSizeSetColumns.S18, s18);
			values.put(SaSizeSetColumns.S19, s19);
			values.put(SaSizeSetColumns.S20, s20);
			return values;
		}
		
		private static SaSizeSet restoreSaSizeSetWithCursor(Cursor cursor) {
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, SaSizeSet.class);
				} else {
					return null;
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
		}
		
		public static SaSizeSet restoreSaSizeSetWithSiezeGroup(Context context, String sizeGroup) {
			Cursor cursor = context.getContentResolver()
				.query(SaSizeSet.CONTENT_URI, CONTENT_PROJECTION, SaSizeSetColumns.SIZEGROUP + "=?", new String[]{sizeGroup}, null);
			return restoreSaSizeSetWithCursor(cursor);
		}
	}
	
	public interface ViewOrderListColumns {
		public String DEPARTCODE = "departcode";
		public String WARECODE = "warecode";
		public String COLORCODE = "colorcode";
		public String SIZECODE = "sizecode";
		public String MOUNT = "mount";
		public String MONEY = "money";
	}
	
	public static final class ViewOrderList extends AsContent implements ViewOrderListColumns {

		public static final String TABLE_NAME = "view_ord_list";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/viewordlist");
		
		public static final int CONTENT_DEPARTCODE_COLUMN = 0;
		public static final int CONTENT_WARECODE_COLUMN = 1;
		public static final int CONTENT_SIZECODE_COLUMN = 2;
		public static final int CONTENT_MOUNT_COLUMN = 3;
		public static final int CONTENT_MONEY_COLUMN = 4;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			ViewOrderListColumns.DEPARTCODE,
			ViewOrderListColumns.WARECODE,
			ViewOrderListColumns.COLORCODE,
			ViewOrderListColumns.MOUNT,
			ViewOrderListColumns.MONEY
		};
		
		public String departcode;
		public String warecode;
		public String colorcode;
		public int mount;
		public int money;
		
		public ViewOrderList() {
			mBaseUri = CONTENT_URI;
		}
		
		@Override
		public ViewOrderList restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
			try {
				if(cursor != null && cursor.moveToFirst()) {
					return getContent(cursor, ViewOrderList.class);
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
			}
			return null;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(ViewOrderListColumns.DEPARTCODE, departcode);
			values.put(ViewOrderListColumns.WARECODE, warecode);
			values.put(ViewOrderListColumns.COLORCODE, colorcode);
			values.put(ViewOrderListColumns.MOUNT, mount);
			values.put(ViewOrderListColumns.MONEY, money);
			return values;
		}
	}
	
	public interface UserColumns {
		public static final String ID = "_id";
		public static final String DEPTCODE = "deptcode";
		public static final String LOGPWD = "logpwd";
		public static final String DEPTNAME = "deptname";
		public static final String LOGINDATE = "logindate";
		public static final String STATE = "state";
		public static final String STOPSTATE = "stopstate";
		public static final String MACID = "macid";
		public static final String MACADDR = "macaddr";
		public static final String UPIP = "upip";
		public static final String UPTIME = "uptime";
		public static final String DOWNIP = "downip";
		public static final String DOWNTIME = "downtime";
		public static final String STARTDATE = "startdate";
		public static final String ENDDATE = "enddate";
		public static final String INTTIME = "inttiem";
		public static final String MAXORD = "maxord";
		public static final String FTPUSER = "ftpuser";
		public static final String FTPPWD = "ftppwd";
		public static final String INDENTNAME = "indentname";
	}
	
	public static final class User extends AsContent implements UserColumns {
		public static final String TABLE_NAME = "user";
		public static final Uri CONTENT_URI = Uri.parse(AsContent.CONTENT_URI + "/user");
		
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_DEPTCODE_COLUMN = 1;
		public static final int CONTENT_LOGPWD_COLUMN = 2;
		public static final int CONTENT_DEPTNAME_COLUMN = 3;
		public static final int CONTENT_LOGINDATE_COLUMN = 4;
		public static final int CONTENT_STATE_COLUMN = 5;
		public static final int CONTENT_STOPSTATE_COLUMN = 6;
		public static final int CONTENT_MACID_COLUMN = 7;
		public static final int CONTENT_MACADDR_COLUMN = 8;
		public static final int CONTENT_UPIP_COLUMN = 9;
		public static final int CONTENT_UPTIME_COLUMN = 10;
		public static final int CONTENT_DOWNIP_COLUMN = 11;
		public static final int CONTENT_DOWNTIME_COLUMN = 12;
		public static final int CONTENT_STARTDATE_COLUMN = 13;
		public static final int CONTENT_EDNDATE_COLUMN = 14;
		public static final int CONTENT_INITIME_COLUMN = 15;
		public static final int CONTENT_MAXORD_COLUMN = 16;
		public static final int CONTENT_FTPUSER_COLUMN = 17;
		public static final int CONTENT_FTPPWD_COLUMN = 18;
		public static final int CONTENT_INDENTNAME_COLUMN = 19;
		
		public static final String[] CONTENT_PROJECTION = new String[]{
			RECORD_ID,
			UserColumns.DEPTCODE,
			UserColumns.LOGPWD,
			UserColumns.DEPTNAME,
			UserColumns.LOGINDATE,
			UserColumns.STATE,
			UserColumns.STOPSTATE,
			UserColumns.MACID,
			UserColumns.MACADDR,
			UserColumns.UPIP,
			UserColumns.UPTIME,
			UserColumns.DOWNIP,
			UserColumns.DOWNTIME,
			UserColumns.STARTDATE,
			UserColumns.ENDDATE,
			UserColumns.INTTIME,
			UserColumns.MAXORD,
			UserColumns.FTPUSER,
			UserColumns.FTPPWD,
			UserColumns.INDENTNAME
		};
		
		public String deptcode;
		public String logpwd;
		public String deptname;
		public String logindate;
		public String state;
		public String stopstate;
		public String macid;
		public String macaddr;
		public String upip;
		public String uptime;
		public String downip;
		public String downtime;
		public String startdate;
		public String enddate;
		public int inttime;
		public int maxord;
		public String ftpuser;
		public String ftppwd;
		public String indentname;
		
		public User() {
			mBaseUri = CONTENT_URI;
		}

		@Override
		public User restore(Cursor cursor) {
			mBaseUri = CONTENT_URI;
				if(cursor != null && cursor.moveToFirst()) {
					deptcode = cursor.getString(User.CONTENT_DEPTCODE_COLUMN);
					logpwd = cursor.getString(User.CONTENT_LOGPWD_COLUMN);
					deptname = cursor.getString(User.CONTENT_DEPTNAME_COLUMN);
					logindate = cursor.getString(User.CONTENT_LOGINDATE_COLUMN);
					state = cursor.getString(User.CONTENT_STATE_COLUMN);
					stopstate = cursor.getString(User.CONTENT_STOPSTATE_COLUMN);
					macid = cursor.getString(User.CONTENT_MACID_COLUMN);
					macaddr = cursor.getString(User.CONTENT_MACADDR_COLUMN);
					upip = cursor.getString(User.CONTENT_UPIP_COLUMN);
					uptime = cursor.getString(User.CONTENT_UPTIME_COLUMN);
					downip = cursor.getString(User.CONTENT_DOWNIP_COLUMN);
					downtime = cursor.getString(User.CONTENT_DOWNTIME_COLUMN);
					startdate = cursor.getString(User.CONTENT_STARTDATE_COLUMN);
					enddate = cursor.getString(User.CONTENT_EDNDATE_COLUMN);
					inttime = cursor.getInt(User.CONTENT_INITIME_COLUMN);
					maxord = cursor.getInt(User.CONTENT_MAXORD_COLUMN);
					ftpuser = cursor.getString(User.CONTENT_FTPUSER_COLUMN);
					ftppwd = cursor.getString(User.CONTENT_FTPPWD_COLUMN);
					indentname = cursor.getString(User.CONTENT_INDENTNAME_COLUMN);
				}
			return this;
		}

		@Override
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(UserColumns.DEPTCODE, deptcode);
			values.put(UserColumns.LOGPWD, logpwd);
			values.put(UserColumns.DEPTNAME, deptname);
			values.put(UserColumns.LOGINDATE, logindate);
			values.put(UserColumns.STATE, state);
			values.put(UserColumns.STOPSTATE, stopstate);
			values.put(UserColumns.MACID, macid);
			values.put(UserColumns.MACADDR, macaddr);
			values.put(UserColumns.UPIP, upip);
			values.put(UserColumns.UPTIME, uptime);
			values.put(UserColumns.DOWNIP, downip);
			values.put(UserColumns.DOWNTIME, downtime);
			values.put(UserColumns.STARTDATE, startdate);
			values.put(UserColumns.ENDDATE, enddate);
			values.put(UserColumns.INTTIME, inttime);
			values.put(UserColumns.MAXORD, maxord);
			values.put(UserColumns.FTPUSER, ftpuser);
			values.put(UserColumns.FTPPWD, ftppwd);
			values.put(UserColumns.INDENTNAME, indentname);
			return values;
		}
		
		private static User restoreUserWithCursor(Cursor c) {
			return getContent(c, User.class);
		}
		
		public static User resotoreUserWithId(Context context, long id) {
			Uri u = ContentUris.withAppendedId(User.CONTENT_URI, id);
			Cursor c = context.getContentResolver().query(User.CONTENT_URI, User.CONTENT_PROJECTION, null, null, null);
			return restoreUserWithCursor(c);
		}
	}
}
