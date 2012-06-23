package com.as.order.pager;

import java.util.List;

public interface PageDao {
	public int getCount();
	public void nextPage();
	public void prevPage();
	public void headPage();
	public void lastPage();
	public void gotoPage(int pageIndex);
	public void setPerPage(int pageItemCount);
	public void setCurrentPage(int pageIndex);
	public int getPerPage();
	public boolean hasNextPage();
	public boolean hasPrevPage();
	public int getCurrentPage();
	public int getPageNum();
	public List<?> getCurrentList();
	public void initList(List<?> list, int allNum);
}
