package com.as.ui.utils;

public class SQLs {
	
	/**
	 * 大类分析
	 */
	public static String sql1001 = 
		" SELECT "
		+" (select waretypename from sawaretype where rtrim(sawaretype.[waretypeid])  = rtrim(sawarecode.[waretypeid])) dalei, "
		+" sum(saindent.[warenum]) amount, "
		+" sum(saindent.[warenum]* retailprice) price,  "
		+" count(distinct saindent.[warecode]) ware_cnt,  "
		+" (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all ";
		
	/**
	 * 小类分析
	 */
	public static final String sql1002 = "";
	
	/**
	 * 主题分析
	 */
	public static final String sql1003 = "";
	
	/**
	 * 波段分析
	 */
	public static final String sql1004 = "";
	
	/**
	 * 颜色分析
	 */
	public static final String sql1005 = "";
	
	/**
	 * 尺码分析
	 */
	public static final String sql1006 = "";
	
	/**
	 * 价格带分析 
	 */
	public static final String sql1007 = "";
	
	/**
	 * 上下装分析
	 */
	public static final String sql1008 = "";
}
