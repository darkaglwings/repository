/**
 * JDBCDao contains method for jdbc operation
 */
package org.frame.repository.sql.jdbc.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.frame.common.lang.reflect.Reflect;
import org.frame.common.path.Path;
import org.frame.repository.annotation.resolver.Resolver;
import org.frame.repository.constant.IRepositoryConstant;
import org.frame.repository.sql.impl.Repository;
import org.frame.repository.sql.jdbc.IJDBC;
import org.frame.repository.sql.model.Page;

public class JDBC extends Repository implements IJDBC {
	
	private Connection conn = null;
	
	private String jndi = null;
	
	private String className = null;
	private String url = null;
	private String userName = null;
	private String password = null;
	
	/**
	 * constructor with default configuration
	 */
	public JDBC() {
		String path = new Path().resource(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		this.initDataSource(new File(path));
	}
	
	/**
	 * constructor with specific configuration
	 * 
	 * @param config file path of configuration file
	 */
	public JDBC(String config) {
		if (config.endsWith(".properties"))
			this.initDataSource(new File(config));
		else {
			this.initDataSource(config);
		}
	}
	
	public JDBC(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * constructor with specific configuration
	 * 
	 * @param file configuration file
	 */
	public JDBC(File file) {
		this.initDataSource(file);
	}
	
	public JDBC(String className, String url, String username, String password) {
		this.className = className;
		this.url = url;
		this.userName = username;
		this.password = password;
		
		this.setDatabaseType(url);
	}
	
	@SuppressWarnings("unchecked")
	public List<?> batchExecute(String sql, final List<?> parameters) {
		List<Object> result = new ArrayList<Object>();
		
		Map<String, Object> generatedKeyHolder;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		try {
			if (parameters != null && parameters.size() > 0) {
				Map<String, Object> map;
				List<Object[]> param;
				if (parameters.get(0) instanceof Map<?, ?>) {
					map = super.batchInfoHandler(sql, (List<Map<String, Object>>) parameters);
					
					pstmt = conn.prepareStatement(String.valueOf(map.get("sql")));
					param = (List<Object[]>) map.get("parameters");
					
					for (int i = 0; i < param.size(); i++) {
						Object[] object = (Object[]) param.get(i);
						
						for (int j = 0; j < object.length; j++) {
							pstmt.setObject(j + 1, object[j]);
						}
						 
						pstmt.addBatch();
					}
				} else if (parameters.get(0) instanceof Object[]) {
					pstmt = conn.prepareStatement(sql);
					
					for (int i = 0; i < parameters.size(); i++) {
						Object[] object = (Object[]) parameters.get(i);
						
						for (int j = 0; j < object.length; j++) {
							pstmt.setObject(j + 1, object[j]);
						}
						 
						pstmt.addBatch();
					}
				} else {
					pstmt = conn.prepareStatement(sql);
					pstmt.addBatch();
				}
			} else {
				pstmt = conn.prepareStatement(sql);
				pstmt.addBatch();
			}
			
			int[] effect = pstmt.executeBatch();
			
			if (sql.toLowerCase().indexOf("insert") != -1) {
				rs = pstmt.getGeneratedKeys();
				rsmd = rs.getMetaData();
				while(rs.next()) {
					generatedKeyHolder = new HashMap<String, Object>();
					
					for(int i = 1; i <= rsmd.getColumnCount(); i++) {
						generatedKeyHolder.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					
					if (generatedKeyHolder != null && !generatedKeyHolder.keySet().isEmpty()) {
						if (generatedKeyHolder.size() == 1) {
							for (Iterator<String> iterator = generatedKeyHolder.keySet().iterator(); iterator.hasNext();) {
								result.add(generatedKeyHolder.get((String) iterator.next()));
							}
						} else {
							result.add(generatedKeyHolder);
						}
					}
				}
			} else {
				for (int i : effect) {
					result.add(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, rsmd);
		}
		
		return result;
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
		return (Integer) this.execute(sql, parameters);
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
		return (Integer) this.execute(sql, parameters);
	}
	
	/**
	 * delete operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object delete(Object object) {
		Object result = 0;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		
		try {
			conn.setAutoCommit(false);
			List<Map<String, Object>> handler = this.delete(this, object);
			for (Map<String, Object> record : handler) {
				pstmt = this.preparedStatement(conn, (String) record.get("sql"), (Map<String, Object>) record.get("args"));
				pstmt.executeUpdate();
			}

			conn.commit();
			
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
		} catch (Exception e) {
			try {
				if (conn != null && !conn.isClosed()) conn.rollback();
			} catch (Exception exception) {
				System.err.println("JDBCDao delete(Object object) can not rollback.");
			}
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception exception) {
				System.err.println("JDBCDao delete(Object object) can not set auto commit to true.");
			}
			
			this.destory(conn, null, pstmt, null, null);
		}

		return result;
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
		Map<String, Object> result = null;
		
		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2Map(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
		}

		return result;
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
		Map<String, Object> result = null;
		
		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2Map(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
		}

		return result;
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
		Object result = null;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2Object(rs, clazz);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
		}

		return result;
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
		Object result = null;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2Object(rs, clazz);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
		}

		return result;
	}
	
	public Connection getConn() {
		return conn;
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
		return this.execute(sql, parameters);
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
		return this.execute(sql, parameters);
	}

	/**
	 * insert operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object insert(Object object) {
		Object result = null;
		Map<String, Object> generatedKeyHolder = new HashMap<String, Object>();

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		try {
			conn.setAutoCommit(false);
			String tableName = new Resolver().table(object.getClass());
			List<Map<String, Object>> handler = this.insert(this, object);
			for (Map<String, Object> record : handler) {
				pstmt = this.preparedStatement(conn, (String) record.get("sql"), (Map<String, Object>) record.get("args"));
				pstmt.executeUpdate();

				if (((String) record.get("sql")).indexOf(tableName.toLowerCase()) != -1) {
					rs = pstmt.getGeneratedKeys();
					rsmd = rs.getMetaData();
					while(rs.next()) {
						for(int i = 1; i <= rsmd.getColumnCount(); i++) {
							generatedKeyHolder.put(rsmd.getColumnName(i), rs.getObject(i));
						}
					}

					if (generatedKeyHolder != null && !generatedKeyHolder.keySet().isEmpty()) {
						if (generatedKeyHolder.size() == 1) {
							for (Iterator<String> iterator = generatedKeyHolder.keySet().iterator(); iterator.hasNext();) {
								result = generatedKeyHolder.get((String) iterator.next());
							}
						} else {
							result = generatedKeyHolder;
						}
					}
				}
			}
				
			conn.commit();
		} catch (Exception e) {
			try {
				if (conn != null && !conn.isClosed()) conn.rollback();
			} catch (Exception exception) {
				System.err.println("JDBCDao update(Object object) can not rollback.");
			}
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception exception) {
				System.err.println("JDBCDao update(Object object) can not set auto commit to true.");
			}
			
			this.destory(conn, null, pstmt, rs, rsmd);
		}

		return result;
	}
	
	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	@SuppressWarnings("resource")
	public Page pagination(Page page) {
		if (page != null) {
			List<Map<String, Object>> lstData = null;

			Connection conn = this.connect();
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				String sql = super.page(page);
				
				if (sql == null) {
					pstmt = this.preparedStatement(conn, page.getSql(), page.getParameters());
					rs = pstmt.executeQuery();
					int i = 1;
					if (rs != null) {
						page.setStartIndex();

						lstData = new ArrayList<Map<String, Object>>();
						rs.relative(page.getStartIndex());
						while (rs.next()) {
							if (i > page.getPageSize())
								break;
							else {
								lstData.add(this.rs2Map(rs));
							}
							i++;
						}

						if (lstData != null) {
							page.setTotalCount(rs.getRow());
							page.init();
							page.setData(lstData);
							page.setCurrPage(page.getTargetPage());
						}
					}
				} else {
					List<Map<String, Object>> data = null;
					try {
						pstmt = this.preparedStatement(conn, count(page), page.getParameters());
						rs = pstmt.executeQuery();
						while (rs.next()) {
							page.setTotalCount(rs.getInt(1));
						}
						page.init();
						
						pstmt = this.preparedStatement(conn, sql, page.getParameters());
						rs = pstmt.executeQuery();
						data = this.rs2MapList(rs);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						this.destory(conn, null, pstmt, rs, null);
					}

					if (data != null) {
						page.setData(data);
						page.setCurrPage(page.getTargetPage());
					} else {
						page.setData(null);
						page.setCurrPage(page.getTargetPage());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				this.destory(conn, null, pstmt, rs, null);
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
	@SuppressWarnings("resource")
	public Page pagination(Class<?> clazz, Page page) {
		if (page != null) {
			List<Object> lstData = null;

			Connection conn = this.connect();
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				String sql = this.page(page);
				
				if (sql == null) {
					pstmt = this.preparedStatement(conn, page.getSql(), page.getParameters());
					rs = pstmt.executeQuery();
					int i = 1;
					if (rs != null) {
						page.setStartIndex();

						lstData = new ArrayList<Object>();
						rs.relative(page.getStartIndex());
						while (rs.next()) {
							if (i > page.getPageSize())
								break;
							else {
								lstData.add(this.rs2Object(rs, clazz));
							}
							i++;
						}

						rs.last();

						if (lstData != null) {
							page.setTotalCount(rs.getRow());
							page.init();
							page.setData(lstData);
							page.setCurrPage(page.getTargetPage());
						}
					}
				} else {
					List<Object> data = null;
					try {
						pstmt = this.preparedStatement(conn, count(page), page.getParameters());
						rs = pstmt.executeQuery();
						while (rs.next()) {
							page.setTotalCount(rs.getInt(1));
						}
						page.init();
						
						pstmt = this.preparedStatement(conn, sql, page.getParameters());
						rs = pstmt.executeQuery();
						data = this.rs2ObjectList(rs, clazz);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						this.destory(conn, null, pstmt, rs, null);
					}

					if (data != null) {
						page.setData(data);
						page.setCurrPage(page.getTargetPage());
					} else {
						page.setData(null);
						page.setCurrPage(page.getTargetPage());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				this.destory(conn, null, pstmt, rs, null);
			}
		} else {
			System.err.println("page is null");
			page = new Page();
		}
		
		return page;
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
		List<Map<String, Object>> result = null;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2MapList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
		}

		return result;
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
		List<Map<String, Object>> result = null;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2MapList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
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
	public List<?> select(String sql, Map<String, Object> parameters, Class<?> clazz) {
		List<Object> result = null;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2ObjectList(rs, clazz);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.destory(conn, null, pstmt, rs, null);
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
	public List<?> select(String sql, Object[] parameters, Class<?> clazz) {
		List<Object> result = null;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			rs = pstmt.executeQuery();
			result = this.rs2ObjectList(rs, clazz);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, null);
		}

		return result;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
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
	public int update(String sql, Map<String, Object> parameters)  {
		return (Integer) this.execute(sql, parameters);
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
		return (Integer) this.execute(sql, parameters);
	}

	/**
	 * update operation
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return number of effect records
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object update(Object object) {
		Object result = 0;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		
		try {
			conn.setAutoCommit(false);
			List<Map<String, Object>> handler = this.update(this, object);
			for (Map<String, Object> record : handler) {
				pstmt = this.preparedStatement(conn, (String) record.get("sql"), (Map<String, Object>) record.get("args"));
				pstmt.executeUpdate();
			}

			conn.commit();
			
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
		} catch (Exception e) {
			try {
				if (conn != null && !conn.isClosed()) conn.rollback();
			} catch (Exception exception) {
				System.err.println("JDBCDao update(Object object) can not rollback.");
			}
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception exception) {
				System.err.println("JDBCDao update(Object object) can not set auto commit to true.");
			}
			
			this.destory(conn, null, pstmt, null, null);
		}

		return result;
	}
	
	/**
	 * connect to database
	 * 
	 * @return instance of java.sql.Connection
	 */
	public Connection connect() {
		try {
			if (this.conn == null || this.conn.isClosed()) {
				if (conn != null) conn = null;
				
				if (className != null && url != null && userName != null && password != null) {
					Class.forName(className).newInstance();
					conn = DriverManager.getConnection(url, userName, password);
				} else if (this.jndi != null) {
					InitialContext ctx = new InitialContext();
					DataSource dataSource = (DataSource) ctx.lookup(this.jndi);
					conn = dataSource.getConnection();
				} else
					throw new RuntimeException("insufficient information for database connection.");
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return conn;
	}
	
	/**
	 * get configuration from spring jdbc configuration file
	 */
	protected void initSpringDataSource() {
		try {
			//File file = new File(Thread.currentThread().getContextClassLoader().getResource("./").getPath());
			File file = new File(java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("/").getPath(), "UTF-8"));
			file = new File((file.getParent() + "/applicationContext-jdbc.xml").replace("\\", "/"));
			if (file.exists())
				this.initSpringDataSource(file.getAbsolutePath());
			else
				System.err.println("default spring jdbc config file not found.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get configuration from spring jdbc configuration file
	 * 
	 * @param path file path of spring jdbc configuration file
	 */
	@SuppressWarnings("unchecked")
	protected void initSpringDataSource(String path) {
		try {
			File file = new File(path);

			if (file.exists()) {
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(file);
				List<Element> lstBean = (List<Element>) document.selectNodes("/*[name()='beans']/*[name()='bean']");
				if (lstBean != null && lstBean.size() > 0) {
					for (Element bean : lstBean) {
						if ("dataSource".equals(bean.attributeValue("id"))) {
							List<Element> lstProperty = (List<Element>) bean.selectNodes("/*[name()='beans']/*[name()='bean']/*[name()='property']");
							for (Element property :  lstProperty) {
								if ("driverClassName".equals(property.attributeValue("name"))) {
									className = property.attributeValue("value");
								}

								if ("url".equals(property.attributeValue("name"))) {
									url = property.attributeValue("value");
									this.setDatabaseType(url);
								}

								if ("username".equals(property.attributeValue("name"))) {
									userName =  property.attributeValue("value");
								}

								if ("password".equals(property.attributeValue("name"))) {
									password =  property.attributeValue("value");
								}
							}
						}
					}
				}
			} else {
				//System.err.println("default spring jdbc config file not found.");
			}
		} catch (Exception e) {
			System.err.println("anayly configuration occurred an error.");
			e.printStackTrace();
		}
	}

	/**
	 * initiate jdbc information from jndi
	 * 
	 * @param jndi string of jndi
	 */
	protected void initDataSource(String jndi) {
		this.jndi = jndi;
	}
	
	/**
	 * initiate jdbc information from specific configuration file
	 * 
	 * @param file configuration file
	 */
	protected void initDataSource(File file) {
		if (file.exists()) {
			if (file.getAbsolutePath().endsWith(".properties")) {
				Properties properties = new Properties();
				try {
					InputStream inputStream = new FileInputStream(file);
					properties.load(inputStream);
					className = properties.getProperty(IRepositoryConstant.DATASOURCE_CLASSNAME);
					url = properties.getProperty(IRepositoryConstant.DATASOURCE_URL);
					userName = properties.getProperty(IRepositoryConstant.DATASOURCE_USERNAME);
					password = properties.getProperty(IRepositoryConstant.DATASOURCE_PASSWORD);
					
					this.setDatabaseType(url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else
			System.err.println("unsupported file type. file name: " + file.getName());
	}
	
	/**
	 * convert query record in result set to map
	 * 
	 * @param rs instance of java.sql.ResultSet
	 * 
	 * @return map of data
	 */
	protected Map<String, Object> rs2Map(ResultSet rs) {
		Map<String, Object> result = null;

		List<Map<String, Object>> data = this.rs2MapList(rs);
		if (data != null && data.size() > 0) {
			result = data.get(0);
		}

		return result;
	}
	
	/**
	 * convert query records in result set to list
	 * 
	 * @param rs instance of java.sql.ResultSet
	 * 
	 * @return list of map for data
	 */
	protected List<Map<String, Object>> rs2MapList(ResultSet rs) {
		List<Map<String, Object>> lstResult = null;
		
		Map<String, Object> row = null;

		if (rs != null) {
			try {
				lstResult = new ArrayList<Map<String, Object>>();
				ResultSetMetaData rsmd = rs.getMetaData();
				while(rs.next()) {
					row = new HashMap<String, Object>();
					for(int i = 1; i <= rsmd.getColumnCount(); i++) {
						row.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					
					lstResult.add(row);
				}
			} catch (SQLException e) {
				lstResult = null;
				e.printStackTrace();
			}
		}
		
		return lstResult;
	}
	
	/**
	 * convert query record in result set to instance of java bean
	 * 
	 * @param rs instance of java.sql.ResultSet
	 * @param clazz class of java bean to be loaded
	 * 
	 * @return instance of java bean
	 */
	protected Object rs2Object(ResultSet rs, Class<?> clazz) {
		Object result = null;

		List<Object> data = this.rs2ObjectList(rs, clazz);
		if (data != null && data.size() > 0) {
			result = data.get(0);
		}

		return result;
	}
	
	/**
	 * convert query record in result set to list
	 * 
	 * @param rs instance of java.sql.ResultSet
	 * @param clazz class of java bean to be loaded
	 * 
	 * @return list for instance of java bean
	 */
	protected List<Object> rs2ObjectList(ResultSet rs, Class<?> clazz) {
		List<Object> lstResult = null;

		if (rs != null) {
			lstResult = new ArrayList<Object>();
			
			try {
				boolean flag = false;
				Class<?>[] interfaces = clazz.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					if ("org.frame.sql.model.ResultSetWrapper".equals(interfaces[i].getName())) {
						flag = true;
						break;
					}
				}

				if (flag) {
					while (rs.next()) {
						Object object = clazz.newInstance();
						try {
							object = clazz.getMethod("wrapper", ResultSet.class).invoke(object, rs);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
						
						lstResult.add(object);
					}
				} else {
					ResultSetMetaData rsmd = rs.getMetaData();
					
					Resolver annotation = new Resolver();
					Reflect reflect = new Reflect();

					boolean isSimple = annotation.simple(clazz);

					Object object = clazz.newInstance();

					if (isSimple) {
						while (rs.next()) {
							for(int i = 1; i <= rsmd.getColumnCount(); i++) {
								reflect.set(object, rsmd.getColumnName(i).toLowerCase(), rs.getObject(i));
							}

							lstResult.add(object);
						}
					} else {
						List<Map<String, Object>> data = this.rs2MapList(rs);
						for (Map<String, Object> map : data) {
							object = this.map2Object(map, clazz);
							lstResult.add(object);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return lstResult;
	}

	private Object execute(String sql, Object parameters) {
		Object result;

		Map<String, Object> generatedKeyHolder;

		Connection conn = this.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		
		try {
			pstmt = this.preparedStatement(conn, sql, parameters);
			result = pstmt.executeUpdate();
			
			if (sql.toLowerCase().indexOf("insert") != -1) {
				rs = pstmt.getGeneratedKeys();
				rsmd = rs.getMetaData();
				while(rs.next()) {
					generatedKeyHolder = new HashMap<String, Object>();
					
					for(int i = 1; i <= rsmd.getColumnCount(); i++) {
						generatedKeyHolder.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					
					if (generatedKeyHolder != null && !generatedKeyHolder.keySet().isEmpty()) {
						if (generatedKeyHolder.size() == 1) {
							for (Iterator<String> iterator = generatedKeyHolder.keySet().iterator(); iterator.hasNext();) {
								result = generatedKeyHolder.get((String) iterator.next());
							}
						} else {
							result = generatedKeyHolder;
						}
					}
				}
			}
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
			throw new RuntimeException("sql execute error: " + sql);
		} finally {
			this.destory(conn, null, pstmt, rs, rsmd);
		}

		return result;
	}
	
	/*public static void main(String[] args) {
		
		String url = "";
		String className = "";
		String username = "";
		String password = "";
		
		JDBC jdbc = new JDBC(className, url, username, password);
		String insert = "insert into sys_rolemenu(id, role, menu) values(?, ?, ?)";
		String update = "update sys_rolemenu set role=2, menu=3 where id = ?";
		String select = "select id, role, menu from sys_rolemenu where 1 = 1 and (? is null or id = ?)";
		String delete = "delete from sys_rolemenu where id = ?";
		
		String sql = "select max(id) as id from sys_rolemenu where 1=1";
		
		Object[] insert_parameters = {null, 1, 1};
		Object[] update_parameters = {2};
		Object[] delete_parameters = {1};
		Object[] select_parameters = {null, null};
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 1);
		map.put("role", 2);
		map.put("menu", 3);
		
		RoleMenu roleMenu;
		RoleMenuTest roleMenuTest;
		roleMenuTest = new RoleMenuTest();
		//roleMenuTest.setId(2l);
		
		//roleMenu = (RoleMenu) jdbcDao.find(select, RoleMenu.class, new Object[]{1, 1});
		//roleMenuTest = (RoleMenuTest) jdbcDao.find(roleMenuTest);

		//List<RoleMenu> data = (List<RoleMenu>) jdbcDao.select(select, RoleMenu.class, select_parameters);
		//List<RoleMenuTest> data = (List<RoleMenuTest>) jdbcDao.select(select, RoleMenuTest.class, select_parameters);
		
		String result = null;
		
		roleMenuTest.setId(100l);
		
		MenuTest menuTest = new MenuTest();
		menuTest.setId(100l);
		
		RoleTest roleTest = new RoleTest();
		roleTest.setId(100l);
		
		DictionaryTest dict = new DictionaryTest();
		dict.setId(100l);
		
		List<DictionaryTest> lst = new ArrayList<DictionaryTest>();
		lst.add(dict);
		
		//roleMenuTest.setMenu(menuTest);
		roleMenuTest.setRole(roleTest);
		roleMenuTest.setDictionary(lst);
		
		//roleMenuTest = (RoleMenuTest) jdbcDao.find(roleMenuTest);
		
		//result = jdbcDao.insert(roleMenuTest);
		//menuTest.setId(2l);
		//menuTest = (MenuTest) jdbcDao.find(menuTest);
		
		//roleMenuTest.setMenu(menuTest);
		
		roleTest.setTitle("aaaa");
		
		//result = jdbcDao.update(roleMenuTest);
		
		//result = jdbcDao.delete(roleMenuTest);
		
		System.out.println(roleMenuTest);
	}*/
	
}
