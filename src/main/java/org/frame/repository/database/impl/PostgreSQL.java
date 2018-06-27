/**
 * PostgreSQL contains pagination method for postgreSQL
 */
package org.frame.repository.database.impl;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class PostgreSQL implements IDatabase {

	/**
	 * create pagination sql
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return sting of pagination sql
	 */
	@Override
	public String pagination(Page page) {
		StringBuffer sbufSql = new StringBuffer("");
		sbufSql.append(" select * \n");
		sbufSql.append(" from ( ");
		sbufSql.append(page.getSql());
		sbufSql.append(" ) page"); 
		sbufSql.append(" limit ");
		sbufSql.append(page.getPageSize());
		sbufSql.append(" offset ");
		sbufSql.append(page.getStartIndex());
		
		return sbufSql.toString();
	}
	
	@Override
	public String tables() {
		return "";
	}
	
	@Override
	public String fields() {
		return "";
	}
	
}
