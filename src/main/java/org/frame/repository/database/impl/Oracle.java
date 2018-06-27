/**
 * Oracle contains pagination method for oracle
 */
package org.frame.repository.database.impl;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class Oracle implements IDatabase {

	public enum Field {
		BINARY_DOUBLE("String"),
		BINARY_FLOAT("String"),
		BLOB("Byte[]"),
		CLOB("String"),
		CHAR("String"),
		DATE("Date"),
		INTERVAL_DAY_TO_SECOND("Double"),
		INTERVAL_YEAR_TO_MONTH("Long"),
		LONG("Long"),
		LONG_RAW("Long"),
		NCLOB("String"),
		NUMBER("Double"),
		NVARCHAR2("String"),
		RAW("String"),
		TIMESTAMP("Date"),
		TIMESTAMP_WITH_LOCAL_TIME_ZONE("Date"),
		TIMESTAMP_WITH_TIME_ZONE("Date"),
		VARCHAR2("String");
		
		private Field(String field){

		}
	}
	
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
		sbufSql.append(" from ( \n");
		sbufSql.append(" select \n");
		sbufSql.append(" page.*, rownum rn \n");
		sbufSql.append(" from( ");
		sbufSql.append(page.getSql());
		sbufSql.append(" ) page \n");
		sbufSql.append(" where \n");
		sbufSql.append(" rownum <= (");
		sbufSql.append(page.getStartIndex() + page.getPageSize());
		sbufSql.append(")) \n");
		sbufSql.append("where \n");
		sbufSql.append("rn > ");
		sbufSql.append(page.getStartIndex());
		
		return sbufSql.toString();
	}

	
	@Override
	public String tables() {
		return "select table_name from user_tables";
	}
	
	@Override
	public String fields() {
		return "select * from user_tab_columns a left join (select cu.owner as owner, cu.table_name as table_name, cu.column_name as column_name, 'pri' as column_key from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'p' and au.owner = ?) b on a.column_name = b.column_name and a.table_name = b.table_name where a.table_name = ? order by a.column_id";
	}
	
}
