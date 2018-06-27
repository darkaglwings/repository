/**
 * SpringDao contains method for spring-jdbc operation
 */
package org.frame.repository.sql.spring.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.frame.repository.annotation.resolver.Resolver;
import org.frame.repository.sql.jdbc.impl.JDBC;
import org.frame.repository.sql.model.Page;
import org.frame.repository.sql.spring.ISpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("springDao")
@Scope("singleton")
public class Spring extends org.frame.repository.sql.impl.Repository implements ISpring{
	
	@Autowired
	@Resource
	DefaultLobHandler defaultLobHandler;
	
	@Autowired
	@Resource
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Resource
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<?> batchExecute(String sql, final List<?> parameters) {
		List<?> result = new ArrayList<Object>();
		
		try {
			result = new JDBC(jdbcTemplate.getDataSource().getConnection()).batchExecute(sql, parameters);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public int[] batchUpdate(String sql, final List<?> parameters) {
		if (parameters != null && parameters.size() > 0) {
			if (parameters.get(0) instanceof Map<?, ?>) {
				Map<String, Object> map = this.batchInfoHandler(sql, (List<Map<String, Object>>) parameters);
				final String strSql = String.valueOf(map.get("sql"));
				final List<Object[]> data = (List<Object[]>) map.get("parameters");
				
				int[] result = jdbcTemplate.batchUpdate(strSql, new BatchPreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement pstmt, int i) throws SQLException {
						for (int j = 0; j < data.get(i).length; j++) {
							pstmt.setObject(j + 1, ((Object[]) data.get(i))[j]);
						}
					}
					
					@Override
					public int getBatchSize() {
						return parameters.size();
					}
				});
				
				return result;
			} else if (parameters.get(0) instanceof Object[]) {
				int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement pstmt, int i) throws SQLException {
						Object[] param = (Object[]) parameters.get(i);
						for (int j = 0; j < param.length; j++) {
							pstmt.setObject(j + 1, param[j]);
						}
					}

					@Override
					public int getBatchSize() {
						return parameters.size();
					}
					
				});

				return result;
			} else return new int[]{ jdbcTemplate.update(sql) };
		} else {
			return new int[]{ jdbcTemplate.update(sql) };
		}
	}
	
	/**
	 * delete operation
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	@Override
	public int delete(String sql, Object[] parameters) {
		return jdbcTemplate.update(sql, parameters);
	}
	
	/**
	 * delete operation
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	@Override
	public int delete(String sql, Map<String, Object> parameters) {
		return namedParameterJdbcTemplate.update(sql, parameters);
	}

	/**
	 * delete operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor = {Exception.class})
	public Object delete(Object object) {
		Object result = null;
		
		List<Map<String, Object>> handler = this.delete(this, object);
		for (Map<String, Object> record : handler) {
			namedParameterJdbcTemplate.update((String) record.get("sql"), (Map<String, Object>) record.get("args"));
		}

		Map<String, Object> pk = new Resolver().pk(object);
		if (pk != null && !pk.keySet().isEmpty()) {
			if (pk.size() == 1) {
				for (Iterator<String> iterator = pk.keySet().iterator(); iterator.hasNext();) {
					result = pk.get((String) iterator.next());
				}
			} else {
				result = pk;
			}
		}
		
		return result;
	}
	
	/**
	 * execute sql
	 * 
	 * @param sql sql for find operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	@Override
	public String execute(String sql, final Object[] parameters) {
		Integer result = jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback(defaultLobHandler) {
			protected void setValues(PreparedStatement ps,LobCreator lobCreator) throws SQLException {
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] instanceof Boolean) {
						ps.setBoolean(i + 1, (Boolean) parameters[i]);
					} else if (parameters[i] instanceof Byte) {
						ps.setByte(i + 1, (Byte) parameters[i]);
					} else if (parameters[i] instanceof byte[]) {
						lobCreator.setBlobAsBytes(ps, i + 1, (byte[]) parameters[i]);
					} else if (parameters[i] instanceof Character) {
						//TODO change parameters[i] to Character
					} else if (parameters[i] instanceof StringBuffer) {
						if (parameters[i] == null)
							lobCreator.setClobAsString(ps, i + 1, null);
						else
							lobCreator.setClobAsString(ps, i + 1, parameters[i].toString());
					} else if (parameters[i] instanceof java.util.Date) {
						ps.setString(i + 1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) parameters[i]));
					} else if (parameters[i] instanceof java.sql.Date) {
						ps.setDate(i + 1, (java.sql.Date) parameters[i]);
					} else if (parameters[i] instanceof Double) {
						ps.setDouble(i + 1, (Double) parameters[i]);
					} else if (parameters[i] instanceof Float) {
						ps.setFloat(i + 1, (Float) parameters[i]);
					} else if (parameters[i] instanceof Integer) {
						ps.setInt(i + 1, (Integer) parameters[i]);
					} else if (parameters[i] instanceof Long) {
						ps.setLong(i + 1, (Long) parameters[i]);
					} else if (parameters[i] instanceof Short) {
						ps.setShort(i + 1, (Short) parameters[i]);
					} else if (parameters[i] instanceof String) {
						ps.setString(i + 1, (String) parameters[i]);
					} else {
						ps.setObject(i + 1, parameters[i]);
					}
				}
			}
		});

		return result.toString();
	}

	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return map of data
	 */
	@Override
	public Map<String, Object> find(String sql, Object[] parameters) {
		try {
			return jdbcTemplate.queryForMap(sql, parameters);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of java bean
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object find(String sql, RowMapper rowMapper) {
		return this.find(sql, new Object[]{}, rowMapper);
	}
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java bean
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object find(String sql, Object[] parameters, RowMapper rowMapper) {
		try {
			return jdbcTemplate.queryForObject(sql, rowMapper, parameters);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param parameters parameters for sql
	 * 
	 * @return map of data
	 */
	@Override
	public Map<String, Object> find(String sql, Map<String, Object> parameters) {
		try {
			return namedParameterJdbcTemplate.queryForMap(sql, parameters);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param parameters parameters for sql
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of java bean
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object find(String sql, Map<String, Object> parameters, RowMapper rowMapper) {
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java bean
	 */
	@Override
	public Object find(String sql, Map<String, Object> parameters, Class<?> clazz) {
		return this.map2Object(this.find(sql, parameters), clazz);
	}

	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for delete operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java bean
	 */
	@Override
	public Object find(String sql, Object[] parameters, Class<?> clazz) {
		return this.map2Object(this.find(sql, parameters), clazz);
	}
	
	/**
	 * insert operation
	 * 
	 * @param sql sql for insert operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	@Override
	public Object insert(String sql, Object[] parameters) {
		Object result = null;
		
		final String strSql = sql;
		final Object[] params = parameters;
		
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement pstmt = conn.prepareStatement(strSql, Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < params.length; i++) {
					try {
						pstmt.setObject(i + 1, params[i]);
					} catch (Exception e) {
						try {
							pstmt.setObject(i + 1, null);
						} catch (SQLException exception) {
							exception.printStackTrace();
						}
						e.printStackTrace();
						continue;
					}
				}
				
				return pstmt;
			}
			
		}, generatedKeyHolder);
		
		if (generatedKeyHolder.getKeys() != null && !generatedKeyHolder.getKeys().keySet().isEmpty()) {
			if (generatedKeyHolder.getKeys().size() == 1) {
				for (Iterator<String> iterator = generatedKeyHolder.getKeys().keySet().iterator(); iterator.hasNext();) {
					result = generatedKeyHolder.getKeys().get((String) iterator.next());
				}
			} else {
				result = generatedKeyHolder.getKeys();
			}
		}
		
		return result;
	}
	
	/**
	 * insert operation
	 * 
	 * @param sql sql for insert operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect records
	 */
	@Override
	public Object insert(String sql, Map<String, Object> parameters) {
		Object result = null;
		
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();  
		SqlParameterSource paramSource = new MapSqlParameterSource(parameters);
		namedParameterJdbcTemplate.update(sql, paramSource, generatedKeyHolder);
		
		if (generatedKeyHolder.getKeys() != null && !generatedKeyHolder.getKeys().keySet().isEmpty()) {
			if (generatedKeyHolder.getKeys().size() == 1) {
				for (Iterator<String> iterator = generatedKeyHolder.getKeys().keySet().iterator(); iterator.hasNext();) {
					result = generatedKeyHolder.getKeys().get((String) iterator.next());
				}
			} else {
				result = generatedKeyHolder.getKeys();
			}
		}
		
		return result;
	}
	
	/**
	 * insert operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor = {Exception.class})
	public Object insert(Object object) {
		Object result = null;
		
		GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();  
		SqlParameterSource paramSource;
		
		String tableName = new Resolver().table(object.getClass());
		List<Map<String, Object>> handler = this.insert(this, object);
		for (Map<String, Object> record : handler) {
			paramSource = new MapSqlParameterSource((Map<String, Object>) record.get("args"));
			namedParameterJdbcTemplate.update((String) record.get("sql"), paramSource, generatedKeyHolder);
			
			if (((String) record.get("sql")).indexOf(tableName.toLowerCase()) != -1) {
				if (generatedKeyHolder.getKeys() != null && !generatedKeyHolder.getKeys().keySet().isEmpty()) {
					if (generatedKeyHolder.getKeys().size() == 1) {
						for (Iterator<String> iterator = generatedKeyHolder.getKeys().keySet().iterator(); iterator.hasNext();) {
							result = generatedKeyHolder.getKeys().get((String) iterator.next());
						}
					} else {
						result = generatedKeyHolder.getKeys();
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes" })
	public Page pagination(String sql, RowMapper rowMapper, Page page) {
		return this.pagination(sql, new Object[]{}, rowMapper, page);
	}
	
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
	public Page pagination(String sql, Object[] parameters, RowMapper rowMapper, Page page) {
		return this.pagination(rowMapper, refreshPage(sql, parameters, page));
	}
	
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
	public Page pagination(String sql, Map<String, Object> parameters, RowMapper rowMapper, Page page) {
		return this.pagination(rowMapper, refreshPage(sql, parameters, page));
	}
	
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
	public Page pagination(String sql, RowMapper rowMapper, int targetPage, Page page) {
		return this.pagination(rowMapper, targetPage, refreshPage(sql, new Object[]{}, page));
	}
	
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
	public Page pagination(String sql, Object[] parameters, RowMapper rowMapper, int targetPage, Page page) {
		return this.pagination(rowMapper, targetPage, refreshPage(sql, parameters, page));
	}

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
	public Page pagination(String sql, Map<String, Object> parameters, RowMapper rowMapper, int targetPage, Page page) {
		return this.pagination(rowMapper, targetPage, refreshPage(sql, parameters, page));
	}
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Page pagination(Page page) {
		if (page != null) {
			if (this.databaseType == null) this.setDatabaseType(null);
			if (databaseType == null) {
				List<Map<String, Object>> lstResult = null;
				List<Map<String, Object>> lstData = null;
				
				if (page.getParameters() instanceof Map<?, ?>) {
					lstData = namedParameterJdbcTemplate.queryForList(page.getSql(), (Map<String, Object>) page.getParameters());
				} else {
					lstData = jdbcTemplate.queryForList(page.getSql(), (Object[]) page.getParameters());
				}
				
				if (lstData != null) {
					page.setTotalCount(lstData.size());
					page.init();
					lstResult = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < page.getPageSize(); i++) {
						if (page.getStartIndex() + i < lstData.size()) {
							lstResult.add(lstData.get(page.getStartIndex() + i));
						}
					}
					page.setData(lstResult);
					page.setCurrPage(page.getTargetPage());
				} else {
					page.setTotalCount(0);
					page.init();
					page.setData(null);
					page.setCurrPage(page.getTargetPage());
				}
			} else {
				if (page.getParameters() instanceof Map<?, ?>) {
					page.setTotalCount(namedParameterJdbcTemplate.queryForObject(this.count(page), (Map<String, Object>) page.getParameters(), Integer.class));
				} else {
					page.setTotalCount(jdbcTemplate.queryForObject(this.count(page), (Object[]) page.getParameters(), Integer.class));
				}

				page.init();
				
				if (page.getParameters() instanceof Map<?, ?>) {
					page.setData(namedParameterJdbcTemplate.queryForList(super.page(page), (Map<String, Object>) page.getParameters()));
					page.setCurrPage(page.getTargetPage());
				} else {
					page.setData(jdbcTemplate.queryForList(super.page(page), (Object[]) page.getParameters()));
					page.setCurrPage(page.getTargetPage());
				}
			}
		} else {
			System.err.println("page is null");
			page = new Page();
		}
		
		return page;
	}
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Page pagination(Class<?> clazz, Page page) {
		List<Object> result = new ArrayList<Object>();
		
		try {
			page = this.pagination(page);
			
			for (Map<String, Object> map : (List<Map<String, Object>>) page.getData()) {
				result.add(this.map2Object(map, clazz));
			}
			
			page.setData(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return page;
	}
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page pagination(RowMapper rowMapper, Page page) {
		if (page != null) {
			if (this.databaseType == null) this.setDatabaseType(null);
			if (databaseType == null) {
				List<Object> lstResult = null;
				List<Object> lstData = null;
				
				if (page.getParameters() instanceof Map<?, ?>) {
					lstData = namedParameterJdbcTemplate.query(page.getSql(), (Map<String, Object>) page.getParameters(), rowMapper);
				} else {
					lstData = jdbcTemplate.query(page.getSql(), (Object[]) page.getParameters(), rowMapper);
				}
				
				if (lstData != null) {
					page.setTotalCount(lstData.size());
					page.init();
					lstResult = new ArrayList<Object>();
					for(int i = 0; i < page.getPageSize(); i++) {
						if (page.getStartIndex() + i < lstData.size()) {
							lstResult.add(lstData.get(page.getStartIndex() + i));
						}
					}
					page.setData(lstResult);
					page.setCurrPage(page.getTargetPage());
				} else {
					page.setTotalCount(0);
					page.init();
					page.setData(null);
					page.setCurrPage(page.getTargetPage());
				}
			} else {
				if (page.getParameters() instanceof Map<?, ?>) {
					page.setTotalCount(namedParameterJdbcTemplate.queryForObject(this.count(page), (Map<String, Object>) page.getParameters(), Integer.class));
				} else {
					page.setTotalCount(jdbcTemplate.queryForObject(this.count(page), (Object[]) page.getParameters(), Integer.class));
				}

				page.init();
				
				if (page.getParameters() instanceof Map<?, ?>) {
					page.setData(namedParameterJdbcTemplate.query(super.page(page), (Map<String, Object>) page.getParameters(), rowMapper));
					page.setCurrPage(page.getTargetPage());
				} else {
					page.setData(jdbcTemplate.query(super.page(page), (Object[]) page.getParameters(), rowMapper));
					page.setCurrPage(page.getTargetPage());
				}

				return page;
			}
		} else {
			System.err.println("page is null");
			return new Page();
		}
		
		return page;
	}
	
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
	public Page pagination(RowMapper rowMapper, int targetPage, Page page) {
		if (page != null) {
			page.setTargetPage(targetPage);
			return this.pagination(rowMapper, page);
		} else {
			System.err.println("page is null");
			return new Page();
		}
	}
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return list of data
	 */
	@SuppressWarnings({ "rawtypes" })
	public List<?> select(String sql, RowMapper rowMapper) {
		Object[] parameters = null;
		return this.select(sql, parameters, rowMapper);
	}
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<?> select(String sql, Object[] parameters, RowMapper rowMapper) {
		return jdbcTemplate.query(sql, parameters, rowMapper);
	}
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param parameters parameters for sql
	 * @param rowMapper instance of org.springframework.jdbc.core.RowMapper
	 * 
	 * @return list of data
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<?> select(String sql, Map<String, Object> parameters, RowMapper rowMapper) {
		return namedParameterJdbcTemplate.query(sql, parameters, rowMapper);
	}
	
	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	@Override
	public List<?> select(String sql, Map<String, Object> parameters) {
		return namedParameterJdbcTemplate.queryForList(sql, parameters);
	}

	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	@Override
	public List<?> select(String sql, Object[] parameters) {
		return jdbcTemplate.queryForList(sql, parameters);
	}

	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<?> select(String sql, Map<String, Object> parameters, Class<?> clazz) {
		List<Object> result = new ArrayList<Object>();
		
		try {
			List<?> data = this.select(sql, parameters);
			for (Map<String, Object> map : (List<Map<String, Object>>) data) {
				result.add(this.map2Object(map, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return list of data
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<?> select(String sql, Object[] parameters, Class<?> clazz) {
		List<Object> result = new ArrayList<Object>();
		
		try {
			List<?> data = this.select(sql, parameters);
			for (Map<String, Object> map : (List<Map<String, Object>>) data) {
				result.add(this.map2Object(map, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * update operation
	 * 
	 * @param sql sql for update operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect data
	 */
	@Override
	public int update(String sql, Object[] parameters) {
		return jdbcTemplate.update(sql, parameters);
	}
	
	/**
	 * update operation
	 * 
	 * @param sql sql for update operation
	 * @param parameters parameters for sql
	 * 
	 * @return number of effect data
	 */
	@Override
	public int update(String sql, Map<String, Object> parameters) {
		return namedParameterJdbcTemplate.update(sql, parameters);
		
	}
	
	/**
	 * update operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor = {Exception.class})
	public Object update(Object object) {
		Object result = null;
		
		List<Map<String, Object>> handler = this.update(this, object);
		for (Map<String, Object> record : handler) {
			namedParameterJdbcTemplate.update((String) record.get("sql"), (Map<String, Object>) record.get("args"));
		}

		Map<String, Object> pk = new Resolver().pk(object);
		if (pk != null && !pk.keySet().isEmpty()) {
			if (pk.size() == 1) {
				for (Iterator<String> iterator = pk.keySet().iterator(); iterator.hasNext();) {
					result = pk.get((String) iterator.next());
				}
			} else {
				result = pk;
			}
		}
		
		return result;
	}
	
}