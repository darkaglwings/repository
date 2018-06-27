package org.frame.repository.sql.model;

import java.util.List;
import java.util.Map;

import org.frame.common.util.Properties;
import org.frame.repository.constant.IRepositoryConstant;

public class Page {
	
	private int totalCount = 0;
	
	private int pageSize = 15;
	
	private int totalPage = 0;
	
	private int currPage = 1;
	
	private int startIndex = 0;
	
	private int endIndex = 0;
	
	private List<?> data;
	
	private String sql = "";
	
	private Object parameters;
	
	private Map<String, Object> mapParams;
	
	private Object[] objectParams;
	
	private int targetPage = 1;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	public void setTotalPage() {
		if (totalCount % pageSize == 0) {
			this.totalPage = totalCount / pageSize;
		} else {
			this.totalPage = (totalCount / pageSize) + 1;
		}
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	public void setStartIndex() {
		if (this.targetPage - 1 < 0) {
			this.startIndex = 0;
		} else {
			this.startIndex = (this.targetPage - 1) * this.pageSize;
		}
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = this.totalCount - 1;
	}
	
	public void setEndIndex() {
		if ( totalCount < pageSize) {
			this.endIndex = totalCount;
		} else if ((totalCount % pageSize == 0) || (totalCount % pageSize != 0 && targetPage < totalPage)) {
			this.endIndex = targetPage * pageSize - 1;
		} else if (totalCount % pageSize != 0 && targetPage == totalPage) {
			this.endIndex = totalCount - 1;
		}
		
		if (endIndex < 0) {
			this.endIndex = 0;
		}
		
		if (this.endIndex > this.totalCount - 1) {
			this.endIndex = this.totalCount - 1;
		}
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public int getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(int targetPage) {
		this.targetPage = targetPage;
	}
	
	public Object getParameters() {
		if (objectParams == null && mapParams == null)
			return null;
		else if (objectParams != null && mapParams == null)
			return objectParams;
		else if (objectParams == null && mapParams != null)
			return mapParams;
		else if (objectParams != null && mapParams != null)
			return objectParams;
		else
			return parameters;
	}

	@SuppressWarnings("unchecked")
	public void setParameters(Object parameters) {
		if (parameters instanceof Object[]) {
			this.setObjectParams((Object[]) parameters);
			this.setMapParams(null);
		} else if (parameters instanceof Map<?, ?>) {
			this.setObjectParams(null);
			this.setMapParams((Map<String, Object>) parameters);
		} else {
			this.setObjectParams(null);
			this.setMapParams(null);
			parameters = null;
		}
		
		this.parameters = parameters;
	}

	public Object[] getObjectParams() {
		return objectParams;
	}

	public void setObjectParams(Object[] objectParams) {
		this.objectParams = objectParams;
		this.parameters = objectParams;
	}
	
	public Map<String, Object> getMapParams() {
		return mapParams;
	}

	public void setMapParams(Map<String, Object> mapParams) {
		this.mapParams = mapParams;
		this.parameters = mapParams;
	}

	public Page() {
		this.pageSize = this.initPageSize();
	}
	
	public Page(int pageSize) {
		this.pageSize = pageSize;
	}

	public Page(String sql, Object... parameters) {
		this.pageSize = this.initPageSize();
		this.setSql(sql);
		this.setMapParams(null);
		this.setObjectParams(parameters);
	}
	
	public Page(String sql, Map<String, Object> parameters) {
		this.pageSize = this.initPageSize();
		this.setSql(sql);
		this.setObjectParams(null);
		this.setMapParams(parameters);
		
	}
	
	public Page(String sql, int targetPage, Object... parameters) {
		this.pageSize = this.initPageSize();
		this.setSql(sql);
		this.setCurrPage(targetPage);
		this.setTargetPage(targetPage);
		this.setMapParams(null);
		this.setObjectParams(parameters);
	}
	
	public Page(String sql, int targetPage, Map<String, Object> parameters) {
		this.pageSize = this.initPageSize();
		this.setSql(sql);
		this.setCurrPage(targetPage);
		this.setTargetPage(targetPage);
		this.setObjectParams(null);
		this.setMapParams(parameters);
	}
	
	public Page(String sql, int targetPage, int pageSize, Object... parameters) {
		this.setSql(sql);
		this.setCurrPage(targetPage);
		this.setTargetPage(targetPage);
		this.setPageSize(pageSize);
		this.setMapParams(null);
		this.setObjectParams(parameters);
	}
	
	public Page(String sql, int targetPage, int pageSize, Map<String, Object> parameters) {
		this.setSql(sql);
		this.setCurrPage(targetPage);
		this.setTargetPage(targetPage);
		this.setPageSize(pageSize);
		this.setObjectParams(null);
		this.setMapParams(parameters);
	}
	
	public void init() {
		if (this.totalCount == 0) {
			this.currPage = 0;
			this.targetPage = 0;
		}
		
		this.setTotalPage();
		this.setStartIndex();
		this.setEndIndex();
	}
	
	public void refresh(String sql, Object... objects) {
		this.sql = sql;
		this.parameters = objects;
	}
	
	public void reset() {
		this.totalCount = 0;
		this.totalPage = 0;
		this.currPage = 0;
		this.startIndex = 0;
		this.endIndex = 0;
		data = null;
		this.sql = "";
		this.parameters = null;
		this.targetPage = 0;
	}
	
	public boolean hasData() {
		return (this.data != null && this.data.size() > 0);
	}
	
	public boolean hasNextPage() {
		return (this.currPage < this.totalPage);
	}

	public boolean hasPreviousPage() {
		return (this.currPage > 1);
	}
	
	private int initPageSize() {
		try {
			String size = String.valueOf(new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES).read(IRepositoryConstant.PAGE_SIZE)).toLowerCase();
			if (size != null && !"".equals(size)) {
				this.pageSize = Integer.parseInt(size);
			} else {
				this.pageSize = 15;
			}
		} catch (Exception e) {
			this.pageSize = 15;
			e.printStackTrace();
		}
		
		return pageSize;
	}
	
}
