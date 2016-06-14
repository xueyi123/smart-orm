package com.iih5.smartorm.model;
/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The SmartORM Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * Page is the result of Model.paginate(......) or Db.paginate(......)
 */
public class Page<T> implements Serializable {
	
	private static final long serialVersionUID = -5395997221963176643L;

	private List<T> rows;				// 查询结果列表
	private int pageNumber;			// 第几页
	private int pageSize;			    // 每一页的大小
	private long totalPage;			// 总共有几页
	private long total;				// 总共有几行

	/**
	 * Constructor.
	 * @param list the list of paginate result
	 * @param pageNumber the page number
	 * @param pageSize the page size
	 * @param totalPage the total page of paginate
	 * @param totalRow the total row of paginate
	 */
	public Page(List<T> list, int pageNumber, int pageSize, long totalPage, long totalRow) {
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
	public int getPageNumber() {
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

