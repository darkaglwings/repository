/**
 * ISpringDao contains method for spring-jdbc operation
 */
package org.frame.repository.sql.spring;

import java.util.List;
import java.util.Map;

import org.frame.repository.sql.IRepository;
import org.frame.repository.sql.model.Page;
import org.springframework.jdbc.core.RowMapper;


public interface ISpring extends IRepository{
	
	public List<?> batchExecute(String sql, final List<?> parameters);
	
	@Deprecated
	public int[] batchUpdate(String sql, final List<?> parameters);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of java bean
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object find(String sql, RowMapper rowMapper);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java bean
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object find(String sql, Object[] parameters, RowMapper rowMapper);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param parameters parameters for sql
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of java bean
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object find(String sql, Map<String, Object> parameters, RowMapper rowMapper);
	
	/**
	 * execute sql
	 * 
	 * @param sql sql for find operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	public String execute(String sql, Object[] parameters);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, RowMapper rowMapper, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, Object[] parameters, RowMapper rowMapper, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param parameters parameters for sql
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, Map<String, Object> parameters, RowMapper rowMapper, Page page);

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, RowMapper rowMapper, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, Object[] parameters, RowMapper rowMapper, int targetPage, Page page);

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param parameters parameters for sql
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, Map<String, Object> parameters, RowMapper rowMapper, int targetPage, Page page);

	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(RowMapper rowMapper, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param targetPage number of target page
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(RowMapper rowMapper, int targetPage, Page page);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return list of data
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<?> select(String sql, RowMapper rowMapper);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<?> select(String sql, Object[] parameters, RowMapper rowMapper);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param parameters parameters for sql
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return list of data
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<?> select(String sql, Map<String, Object> parameters, RowMapper rowMapper);
	
}
