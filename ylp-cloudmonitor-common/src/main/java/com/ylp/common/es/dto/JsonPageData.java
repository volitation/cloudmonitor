package com.ylp.common.es.dto;

import java.io.Serializable;

/**
 * 分页数据
 * @author fuj
 * @date 2016年5月12日
 */
public class JsonPageData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 数据总条数
	 */
	private Integer total;
	
	/**
	 * 当前页数
	 */
	private Integer page;
	
	/**
	 * 每页显示条数
	 */
	private Integer pageSize;
	
	/**
	 * 排序
	 */
	private String sort;
	
	/**
	 * 数据内容
	 */
	private Object pageData;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Object getPageData() {
		return pageData;
	}

	public void setPageData(Object pageData) {
		this.pageData = pageData;
	}

	public JsonPageData(Integer total, Integer page, Integer pageSize, String sort, Object pageData) {
		super();
		this.total = total;
		this.page = page;
		this.pageSize = pageSize;
		this.sort = sort;
		this.pageData = pageData;
	}
}
