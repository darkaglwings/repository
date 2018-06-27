/**
 * Informix contains pagination method for informix
 */
package org.frame.repository.database.impl;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public class Informix implements IDatabase {

	/**
	 * create pagination sql
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return sting of pagination sql
	 */
	@Override
	public String pagination(Page page) {
		return page.getSql().toLowerCase().replaceFirst("select", "select skip " + page.getStartIndex() + " first " + page.getPageSize() + " ");
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
