/**
 * IJDBCDao contains method for jdbc operation
 */
package org.frame.repository.sql.jdbc;

import java.util.List;

import org.frame.repository.sql.IRepository;

public interface IJDBC extends IRepository {
	
	public List<?> batchExecute(String sql, final List<?> parameters);
	
}
