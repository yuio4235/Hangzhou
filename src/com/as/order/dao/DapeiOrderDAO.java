package com.as.order.dao;

import java.io.Serializable;
import java.util.List;

import com.as.db.provider.AsContent.SaWareCode;

public class DapeiOrderDAO implements Serializable{
	
	private static final long serialVersionUID = 8203325984931015654L;
	private String itemCode;
	private String groupName;
	private List<SaWareCode> wareCodes;
	
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public List<SaWareCode> getWareCodes() {
		return wareCodes;
	}
	public void setWareCodes(List<SaWareCode> wareCodes) {
		this.wareCodes = wareCodes;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
