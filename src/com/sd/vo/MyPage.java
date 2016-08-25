package com.sd.vo;

import java.util.List;

public class MyPage {
	private int page;
	private List<Object> rows;
	
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<Object> getRows() {
		return rows;
	}
	public void setRows(List<Object> rows) {
		this.rows = rows;
	}
	
	
}
