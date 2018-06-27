/**
 * MySql contains pagination method for mysql
 */
package org.frame.repository.database.impl;

import org.frame.repository.constant.IRepositoryConstant;
import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class MySql implements IDatabase{

	public MySql() {
		info.put(IRepositoryConstant.DATABASE_INFO_COLUMN_NAME, "COLUMN_NAME");
		info.put(IRepositoryConstant.DATABASE_INFO_COLUMN_TYPE, "DATA_TYPE");
		info.put(IRepositoryConstant.DATABASE_INFO_COLUMN_KEY, "COLUMN_KEY");
		info.put(IRepositoryConstant.DATABASE_INFO_COLUMN_COMMENT, "COLUMN_COMMENT");
		info.put(IRepositoryConstant.DATABASE_INFO_KEY, "PRI");
		
		reference.clear();
		
		reference.put("int", "Integer");
		reference.put("tinyint", "Integer");
		reference.put("smallint", "Integer");
		reference.put("mediumint", "Integer");
		reference.put("integer", "Integer");
		reference.put("bigint", "Long");
		reference.put("float", "Float");
		reference.put("double", "Double");
		reference.put("decimal", "Double");
		reference.put("date", "Date");
		reference.put("datetime", "Date");
		reference.put("timestamp", "Date");
		reference.put("time", "String");
		reference.put("year", "String");
		reference.put("char", "String");
		reference.put("varchar", "String");
		reference.put("tinyblob", "Byte[]");
		reference.put("blob", "Byte[]");
		reference.put("mediumblob", "Byte[]");
		reference.put("longblob", "Byte[]");
		reference.put("tintext", "String");
		reference.put("text", "String");
		reference.put("mediumtext", "String");
		reference.put("longtext", "String");
		reference.put("enum", "String");
		reference.put("set", "String");
		reference.put("binary", "Byte");
		reference.put("varbinary", "Byte");
		reference.put("bit", "Byte");
		reference.put("boolean", "Boolean");
		reference.put("geometry", "String");
		reference.put("point", "String");
		reference.put("linestring", "String");
		reference.put("polygon", "String");
		reference.put("multipoint", "String");
		reference.put("multilinestring", "String");
		reference.put("multipolygon", "String");
		reference.put("geometrycollection", "String");
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
		sbufSql.append(" from ( ");
		sbufSql.append(page.getSql());
		sbufSql.append(" ) page \n");
		sbufSql.append(" limit ");
		sbufSql.append(page.getStartIndex());
		sbufSql.append(", ");
		sbufSql.append(page.getPageSize());
		
		return sbufSql.toString();
	}
	
	@Override
	public String tables() {
		return "show tables";
	}
	
	@Override
	public String fields() {
		return "select * from information_schema.columns where table_schema = ? and table_name = ?";
	}
	
}
