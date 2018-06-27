/**
 * Sybase contains pagination method for sybase
 */
package org.frame.repository.database.impl;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class Sybase implements IDatabase {

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
		sbufSql.append(" ) page \n");
		
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
