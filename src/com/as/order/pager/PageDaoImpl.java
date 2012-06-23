package com.as.order.pager;

import java.util.List;

public class PageDaoImpl implements PageDao {

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public List<?> getCurrentList() {
		return null;
	}

	@Override
	public int getCurrentPage() {
		return 0;
	}

	@Override
	public int getPageNum() {
		return 0;
	}

	@Override
	public int getPerPage() {
		return 0;
	}

	@Override
	public void gotoPage(int pageIndex) {

	}

	@Override
	public boolean hasNextPage() {
		return false;
	}

	@Override
	public boolean hasPrevPage() {
		return false;
	}

	@Override
	public void headPage() {

	}

	@Override
	public void initList(List<?> list, int allNum) {

	}

	@Override
	public void lastPage() {

	}

	@Override
	public void nextPage() {

	}

	@Override
	public void prevPage() {

	}

	@Override
	public void setCurrentPage(int pageIndex) {

	}

	@Override
	public void setPerPage(int pageItemCount) {
	}

}
