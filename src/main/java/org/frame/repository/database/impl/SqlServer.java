/**
 * SqlServer contains pagination method for sqlServer
 */
package org.frame.repository.database.impl;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class SqlServer implements IDatabase {

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
		sbufSql.append(" select top ").append(page.getPageSize()).append(" \n");
		sbufSql.append(" from ( ");
		sbufSql.append(page.getSql());
		sbufSql.append(" ) page \n");
		sbufSql.append(" where id not in ");
		sbufSql.append(" (select top ").append(page.getStartIndex()).append(" id from page order by id) ");
		sbufSql.append(" order by id ");
		
		return sbufSql.toString();
	}
	
	@Override
	public String tables() {
		return "select name from sysobjects where xtype='U'";
	}
	
	@Override
	public String fields() {
		return "";
	}
	
}
