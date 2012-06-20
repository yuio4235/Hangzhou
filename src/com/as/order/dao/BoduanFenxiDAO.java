package com.as.order.dao;

public class BoduanFenxiDAO {

	private String boduan;
	private int wareCnt;
	private int orderedWareCnt;
	private int warenum;
	private int orderedPrice;
	public String getBoduan() {
		return boduan;
	}
	public void setBoduan(String boduan) {
		this.boduan = boduan;
	}
	public int getWareCnt() {
		return wareCnt;
	}
	public void setWareCnt(int wareCnt) {
		this.wareCnt = wareCnt;
	}
	public int getOrderedWareCnt() {
		return orderedWareCnt;
	}
	public void setOrderedWareCnt(int orderedWareCnt) {
		this.orderedWareCnt = orderedWareCnt;
	}
	public int getWarenum() {
		return warenum;
	}
	public void setWarenum(int warenum) {
		this.warenum = warenum;
	}
	public int getOrderedPrice() {
		return orderedPrice;
	}
	public void setOrderedPrice(int orderedPrice) {
		this.orderedPrice = orderedPrice;
	}
}
