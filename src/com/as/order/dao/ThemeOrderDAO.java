package com.as.order.dao;

import android.text.TextUtils;

public class ThemeOrderDAO {
	public String style;
	public int styleCount;
	public int styleColorCount;
	public int wareNum;
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public int getStyleCount() {
		return styleCount;
	}
	public void setStyleCount(int styleCount) {
		this.styleCount = styleCount;
	}
	public int getStyleColorCount() {
		return styleColorCount;
	}
	public void setStyleColorCount(int styleColorCount) {
		this.styleColorCount = styleColorCount;
	}
	public int getWareNum() {
		return wareNum;
	}
	public void setWareNum(int wareNum) {
		this.wareNum = wareNum;
	}
	
	public String[] toArray() {
		String[] rowArr = new String[4];
		rowArr[0] = TextUtils.isEmpty(style) ? "" : style;
		rowArr[1] = styleCount + "";
		rowArr[2] = styleColorCount + "";
		rowArr[3] = wareNum + "";
		return rowArr;
	}
}
