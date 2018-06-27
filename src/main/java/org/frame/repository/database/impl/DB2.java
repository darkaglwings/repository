/**
 * DB2 contains pagination method for DB2
 */
package org.frame.repository.database.impl;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class DB2 implements IDatabase{

	/**
	 * create pagination sql
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return sting of pagination sql
	 */
	@Override
	public String pagination(Page page) {
		String sql = page.getSql();
		sql = sql.toLowerCase().replaceFirst("select", "select rownumber()over() as tempid,");
		
		StringBuffer sbufSql = new StringBuffer("");
		sbufSql.append(" select * \n");
		sbufSql.append(" from ( ");
		sbufSql.append(sql);
		sbufSql.append(" ) as page \n");
		sbufSql.append(" where page.tempid >= ");
		sbufSql.append(page.getStartIndex());
		sbufSql.append(" and page.tempid < ");
		sbufSql.append(page.getEndIndex());
		
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
