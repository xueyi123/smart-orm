package com.iih5.smartorm.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * Page is the result of Model.paginate(......) or Db.paginate(......)
 */
public class Page<T> implements Serializable {
	
	private static final long serialVersionUID = -5395997221963176643L;

	private List<T> rows;				// 查询结果列表
	private Long pageNumber;		// 第几页
	private Integer pageSize;			// 每一页的大小
	private Long totalPage;		// 总共有几页
	private Long total;				// 总共有几行

	/**
	 * Constructor.
	 * @param list the list of paginate result
	 * @param pageNumber the page number
	 * @param pageSize the page size
	 * @param totalPage the total page of paginate
	 * @param totalRow the total row of paginate
	 */
	public Page(List<T> list, Long pageNumber, Integer pageSize, Long totalPage, Long totalRow) {
		this.rows = list;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalPage = totalPage;
		this.total = totalRow;
	}

	public List<T> getRows() {
		return rows;
	}
	/**
	 * Return page number.
	 */
	public Long getPageNumber() {
		return pageNumber;
	}

	/**
	 * Return page size.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Return total page.
	 */
	public long getTotalPage() {
		return totalPage;
	}

	public long getTotal() {
		return total;
	}

	public boolean isFirstPage() {
		return pageNumber == 1;
	}

	public boolean isLastPage() {
		return pageNumber == totalPage;
	}

	/**
	 * 转换为json字符串
	 * @return json str
	 */
	public String toString() {
		return JSON.toJSONString(this);
	}
}

