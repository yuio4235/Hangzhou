package com.as.ui.utils;

public class SQLs {
	
	/**
	 * �������
	 */
	public static String sql1001 = 
		" SELECT "
		+" (select waretypename from sawaretype where rtrim(sawaretype.[waretypeid])  = rtrim(sawarecode.[waretypeid])) dalei, "
		+" sum(saindent.[warenum]) amount, "
		+" sum(saindent.[warenum]* retailprice) price,  "
		+" count(distinct saindent.[warecode]) ware_cnt,  "
		+" (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all ";
		
	/**
	 * С�����
	 */
	public static final String sql1002 = "";
	
	/**
	 * �������
	 */
	public static final String sql1003 = "";
	
	/**
	 * ���η���
	 */
	public static final String sql1004 = "";
	
	/**
	 * ��ɫ����
	 */
	public static final String sql1005 = "";
	
	/**
	 * �������
	 */
	public static final String sql1006 = "";
	
	/**
	 * �۸������ 
	 */
	public static final String sql1007 = "";
	
	/**
	 * ����װ����
	 */
	public static final String sql1008 = "";
}
