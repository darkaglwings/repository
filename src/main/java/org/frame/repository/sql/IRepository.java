/**
 * IRepository contains basic repository methods
 */
package org.frame.repository.sql;

import java.util.List;
import java.util.Map;

import org.frame.repository.database.IDatabase;
import org.frame.repository.sql.model.Page;

public interface IRepository {

	/**
	 * delete operation
	 * 
	 * @param sql sql for delete operation
	 * 
	 * @return number of effect records
	 */
	public int delete(String sql);
	
	/**
	 * delete operation
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	public int delete(String sql, Map<String, Object> parameters);
	
	/**
	 * delete operation
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	public int delete(String sql, Object[] parameters);
	
	/**
	 * delete operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	public Object delete(Object object);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * 
	 * @return map of data
	 */
	public Map<String, Object> find(String sql);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of java bean
	 */
	public Object find(String sql, Class<?> clazz);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return map of data
	 */
	public Map<String, Object> find(String sql, Map<String, Object> parameters);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return map of data
	 */
	public Map<String, Object> find(String sql, Object[] parameters);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return instance of java bean(fill with data)
	 */
	public Object find(Object object);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java bean
	 */
	public Object find(String sql, Map<String, Object> parameters, Class<?> clazz);
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java bean
	 */
	public Object find(String sql, Object[] parameters, Class<?> clazz);
	
	/**
	 * get database information
	 * 
	 * @return instance of IDatabase
	 */
	public IDatabase getDatabase();
	
	/**
	 * insert operation
	 * 
	 * @param sql sql for insert operation
	 * 
	 * @return number of effect records
	 */
	public Object insert(String sql);
	
	/**
	 * insert operation
	 * 
	 * @param sql sql for insert operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	public Object insert(String sql, Map<String, Object> parameters);
	
	/**
	 * insert operation
	 * 
	 * @param sql sql for insert operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	public Object insert(String sql, Object[] parameters);
	
	/**
	 * insert operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	public Object insert(Object object);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Class<?> clazz, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Map<String, Object> parameters, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Object[] parameters, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Map<String, Object> parameters, Class<?> clazz, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Object[] parameters, Class<?> clazz, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Map<String, Object> parameters, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Object[] parameters, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Class<?> clazz, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Map<String, Object> parameters, Class<?> clazz, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(String sql, Object[] parameters, Class<?> clazz, int targetPage, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(Class<?> clazz, Page page);
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param targetPage number of target page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(int targetPage, Page page);

	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param targetPage number of target page
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	public Page pagination(Class<?> clazz, int targetPage, Page page);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * 
	 * @return list of data
	 */
	public List<?> select(String sql);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param clazz class of java bean to load data
	 * 
	 * @return list of data
	 */
	public List<?> select(String sql, Class<?> clazz);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	public List<?> select(String sql, Map<String, Object> parameters);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	public List<?> select(String sql, Object[] parameters);

	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	public List<?> select(String sql, Map<String, Object> parameters, Class<?> clazz);
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	public List<?> select(String sql, Object[] parameters, Class<?> clazz);
	
	/**
	 * update operation
	 * 
	 * @param sql sql for update operation
	 * 
	 * @return number of effect data
	 */
	public int update(String sql);
	
	/**
	 * update operation
	 * 
	 * @param sql sql for update operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect data
	 */
	public int update(String sql, Map<String, Object> parameters);
	
	/**
	 * update operation
	 * 
	 * @param sql sql for update operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect data
	 */
	public int update(String sql, Object[] parameters);
	
	/**
	 * update operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect data
	 */
	public Object update(Object object);

}
