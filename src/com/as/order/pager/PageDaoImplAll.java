package com.as.order.pager;

import java.util.List;

public class PageDaoImplAll implements PageDao {

	private int perPage = 15;
	private int pageNum = 1;
	private int currentPage = 1;
	private int allNum;
	private List<?> list;
	private List<?> cList;
	
	public PageDaoImplAll(){}
	
	public PageDaoImplAll(List<?> list, int perPage, int allNum) {
		this.list = list;
		this.allNum = allNum;
		
		if(perPage >= 1) {
			this.perPage = perPage;
		}
		
		if(this.allNum % this.perPage == 0) {
			this.pageNum = this.allNum / this.perPage;
		} else {
			this.pageNum = this.allNum/this.perPage + 1;
		}
	}
	
	@Override
	public int getCount() {
		return this.allNum;
	}

	@Override
	public List<?> getCurrentList() {
		if(this.list.size() == 0) {
			return this.list;
		}
		
		if(this.currentPage == this.pageNum) {
			this.cList = this.list.subList((this.currentPage-1)*this.perPage, this.allNum);
		} else {
			this.cList = this.list.subList((this.currentPage-1)*this.perPage, this.currentPage*this.perPage);
		}
		return this.cList;
	}

	@Override
	public int getCurrentPage() {
		return this.currentPage;
	}

	@Override
	public int getPageNum() {
		return this.pageNum;
	}

	@Override
	public int getPerPage() {
		return this.perPage;
	}

	@Override
	public void gotoPage(int pageIndex) {
		if(pageIndex > this.pageNum) {
			pageIndex = this.pageNum;
			this.currentPage = pageIndex;
		} else {
			this.currentPage = pageIndex;
		}
	}

	@Override
	public boolean hasNextPage() {
		this.currentPage ++;
		if(this.currentPage <= this.pageNum) {
			return true;
		} else {
			this.currentPage = this.pageNum;
		}
		return false;
	}

	@Override
	public boolean hasPrevPage() {
		this.currentPage --;
		if(this.currentPage > 0) {
			return true;
		} else {
			this.currentPage = 1;
		}
		return false;
	}

	@Override
	public void headPage() {
		this.currentPage = 1;
	}

	@Override
	public void initList(List<?> list, int allNum) {
		this.list = list;
		
		if(this.perPage < 0 ) {
			this.perPage = 15;
		}
		
		this.allNum = allNum;
		
		if(this.allNum%this.perPage==0) {
			this.pageNum = this.allNum/this.perPage;
		} else {
			this.pageNum = this.allNum/this.perPage + 1;
		}
	}

	@Override
	public void lastPage() {
		this.currentPage = pageNum;
	}

	@Override
	public void nextPage() {
		this.hasNextPage();
	}

	@Override
	public void prevPage() {
		this.prevPage();
	}

	@Override
	public void setCurrentPage(int pageIndex) {
		this.currentPage = pageIndex;
	}

	@Override
	public void setPerPage(int pageItemCount) {
		this.perPage = pageItemCount;
	}

}
