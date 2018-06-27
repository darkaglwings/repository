/**
 * IDatabase contains pagination method for kinds of databases
 */
package org.frame.repository.database;

import java.util.HashMap;
import java.util.Map;

import org.frame.repository.sql.model.Page;

public interface IDatabase {
	
	public enum DATABASE {
		DB2("db2"),
		INFORMIX("informix"),
		MYSQL("mysql"),
		ORACLE("oracle"),
		POSTGRESQL("postgresql"),
		SQLSERVER("sqlserver"),
		SYBASE("sybase"),
		NULL(null);

		private DATABASE(String database){

		}
	}
	
	public Map<String, String> info = new HashMap<String, String>();
	
	public Map<String, String> reference = new HashMap<String, String>();
	
	/**
	 * create pagination sql
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return sting of pagination sql
	 */
	public String pagination(Page page);
	
	public String tables();
	
	public String fields();

}
