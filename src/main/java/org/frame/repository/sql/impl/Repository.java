/**
 * Repository contains basic repository methods
 */
package org.frame.repository.sql.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.frame.common.lang.reflect.Reflect;
import org.frame.common.security.Certificate;
import org.frame.common.util.Properties;
import org.frame.repository.annotation.Column;
import org.frame.repository.annotation.Foreign;
import org.frame.repository.annotation.Mirror;
import org.frame.repository.annotation.Reference;
import org.frame.repository.annotation.resolver.Resolver;
import org.frame.repository.constant.IRepositoryConstant;
import org.frame.repository.database.IDatabase;
import org.frame.repository.database.IDatabase.DATABASE;
import org.frame.repository.database.impl.DB2;
import org.frame.repository.database.impl.Informix;
import org.frame.repository.database.impl.MySql;
import org.frame.repository.database.impl.Oracle;
import org.frame.repository.database.impl.PostgreSQL;
import org.frame.repository.database.impl.SqlServer;
import org.frame.repository.database.impl.Sybase;
import org.frame.repository.exception.IlleagalDataException;
import org.frame.repository.sql.IRepository;
import org.frame.repository.sql.model.Page;

public abstract class Repository implements IRepository {
	
	protected IDatabase database;
	protected DATABASE databaseType;
	
	protected String DELETE = "delete";
	protected String INSERT = "insert";
	protected String SELECT = "select";
	protected String UPDATE = "update";
	
	private List<Map<String, Object>> container = new ArrayList<Map<String, Object>>();
	
	public Repository() {
		//this.authorization();
	}
	
	/**
	 * delete operation
	 * 
	 * @param sql sql for delete operation
	 * 
	 * @return number of effect records
	 */
	@Override
	public int delete(String sql) {
		Object[] parameters = null;
		return this.delete(sql, parameters);
	}
	
	/**
	 * release resource after operation
	 * 
	 * @param conn instance of java.sql.Connection
	 * @param stmt instance of java.sql.Statement
	 * @param pstmt instance of java.sql.PreparedStatement
	 * @param rs instance of java.sql.ResultSet
	 * @param rsmd instance of java.sql.ResultSetMetaData
	 */
	public void destory(Connection conn, Statement stmt, PreparedStatement pstmt, ResultSet rs, ResultSetMetaData rsmd) {
		try {
			if (conn != null)
				conn.close();
			if (stmt != null)
				stmt.close();
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();
			
			conn = null;
			stmt = null;
			pstmt = null;
			rs = null;
			rsmd = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * 
	 * @return map of data
	 */
	@Override
	public Map<String, Object> find(String sql) {
		Object[] parameters = null;
		return this.find(sql, parameters);
	}
	
	/**
	 * find operation(select with id)
	 * 
	 * @param object instance of java bean to be operated
	 * 
	 * @return instance of java bean(fill with data)
	 */
	@Override
	public Object find(Object object) {
		try {
			Resolver annotation = new Resolver();
			object = this.map2Object(this.find(annotation.sql(object.getClass()).get("find"), annotation.parameters(this, object, this.SELECT).get("id")), object.getClass());
		} catch (IlleagalDataException e) {
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		}
	
		return object;
	}

	/**
	 * find operation(select with id)
	 * 
	 * @param sql sql for find operation
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of java bean
	 */
	@Override
	public Object find(String sql, Class<?> clazz) {
		Object[] parameters = null;
		return this.find(sql, parameters, clazz);
	}

	/**
	 * to get database
	 * 
	 * @return instance of IDatabase
	 */
	public IDatabase getDatabase() {
		return database;
	}
	
	/**
	 * to get database type
	 * 
	 * @return string of database type
	 */
	public DATABASE getDatabaseType() {
		return databaseType;
	}
	
	/**
	 * insert operation
	 * 
	 * @param sql sql for insert operation
	 * 
	 * @return number of effect records
	 */
	@Override
	public Object insert(String sql) {
		Object[] parameters = null;
		return this.insert(sql, parameters);
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Page page) {
		Object[] parameters = null;
		return this.pagination(sql, parameters, page);
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Class<?> clazz, Page page) {
		Object[] parameters = null;
		return this.pagination(sql, parameters, clazz, page);
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Map<String, Object> parameters, Page page) {
		return this.pagination(this.refreshPage(sql, parameters, page));
	}
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Object[] parameters, Page page) {
		return this.pagination(this.refreshPage(sql, parameters, page));
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Map<String, Object> parameters, Class<?> clazz, Page page) {
		return this.pagination(clazz, this.refreshPage(sql, parameters, page));
	}
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param clazz class of java bean to load data
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Object[] parameters, Class<?> clazz, Page page) {
		return this.pagination(clazz, this.refreshPage(sql, parameters, page));
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, int targetPage, Page page) {
		Object[] parameters = null;
		return this.pagination(targetPage, this.refreshPage(sql, parameters, page));
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Map<String, Object> parameters, int targetPage, Page page) {
		return this.pagination(targetPage, this.refreshPage(sql, parameters, page));
	}
	
	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Object[] parameters, int targetPage, Page page) {
		return this.pagination(targetPage, this.refreshPage(sql, parameters, page));
	}

	/**
	 * pagination operation
	 * 
	 * @param sql sql for pagination operation
	 * @param targetPage number of target page
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(String sql, Class<?> clazz, int targetPage, Page page) {
		Object[] parameters = null;
		return this.pagination(clazz, targetPage, this.refreshPage(sql, parameters, page));
	}

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
	@Override
	public Page pagination(String sql, Map<String, Object> parameters, Class<?> clazz, int targetPage, Page page) {
		return this.pagination(clazz, targetPage, this.refreshPage(sql, parameters, page));
	}
	
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
	@Override
	public Page pagination(String sql, Object[] parameters, Class<?> clazz, int targetPage, Page page) {
		return this.pagination(clazz, targetPage, this.refreshPage(sql, parameters, page));
	}

	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param targetPage number of target page
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(int targetPage, Page page) {
		if (page != null) {
			page.setTargetPage(targetPage);
			return this.pagination(page);
		} else {
			System.err.println("page is null");
			return new Page();
		}
	}

	/**
	 * pagination operation
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * @param targetPage number of target page
	 * @param clazz class of java bean to load data
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@Override
	public Page pagination(Class<?> clazz, int targetPage, Page page) {
		if (page != null) {
			page.setTargetPage(targetPage);
			return this.pagination(clazz, page);
		} else {
			System.err.println("page is null");
			return new Page();
		}
	}

	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * 
	 * @return list of data
	 */
	@Override
	public List<?> select(String sql) {
		Object[] parameters = null;
		return this.select(sql, parameters);
	}

	/**
	 * select operation
	 * 
	 * @param sql sql for select operation
	 * @param clazz class of java bean to load data
	 * 
	 * @return list of data
	 */
	@Override
	public List<?> select(String sql, Class<?> clazz) {
		Object[] parameters = null;
		return this.select(sql, parameters, clazz);
	}

	/**
	 * set database type
	 * 
	 * @param database string of database type
	 */
	public void setDatabaseType(String url) {
		if (url == null)
			this.databaseType = DATABASE.valueOf(String.valueOf(new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES).read(IRepositoryConstant.SYSTEM_DATABASE)).toUpperCase());
		else {
			if (url.toLowerCase().contains(DATABASE.DB2.name().toLowerCase())) {
				this.databaseType = DATABASE.DB2;
			} else if (url.toLowerCase().contains(DATABASE.INFORMIX.name().toLowerCase())) {
				this.databaseType = DATABASE.INFORMIX;
			} else if (url.toLowerCase().contains(DATABASE.MYSQL.name().toLowerCase())) {
				this.databaseType = DATABASE.MYSQL;
			} else if (url.toLowerCase().contains(DATABASE.ORACLE.name().toLowerCase())) {
				this.databaseType = DATABASE.ORACLE;
			} else if (url.toLowerCase().contains(DATABASE.POSTGRESQL.name().toLowerCase())) {
				this.databaseType = DATABASE.POSTGRESQL;
			} else if (url.toLowerCase().contains(DATABASE.SQLSERVER.name().toLowerCase())) {
				this.databaseType = DATABASE.SQLSERVER;
			} else if (url.toLowerCase().contains(DATABASE.SYBASE.name().toLowerCase())) {
				this.databaseType = DATABASE.SYBASE;
			} else this.databaseType = null;
		}
		
		if (this.databaseType !=null) {
			switch (this.databaseType) {
			case DB2:
				this.database = new DB2();
				break;
			case INFORMIX:
				this.database = new Informix();
				break;
			case MYSQL:
				this.database = new MySql();
				break;
			case ORACLE:
				this.database = new Oracle();
				break;
			case POSTGRESQL:
				this.database = new PostgreSQL();
				break;
			case SQLSERVER:
				this.database = new SqlServer();
				break;
			case SYBASE:
				this.database = new Sybase();
				break;
			default:
				this.database = null;
				break;
			}
		}
	}
	
	/**
	 * update operation
	 * 
	 * @param sql sql for update operation
	 * 
	 * @return number of effect data
	 */
	@Override
	public int update(String sql) {
		Object[] parameters = null;
		return this.update(sql, parameters);
	}

	protected Map<String, Object> batchInfoHandler(String sql, List<Map<String, Object>> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Object[]> param = new ArrayList<Object[]>();
		List<Object> data = new ArrayList<Object>();
		
		int count = 0;
		String key = null;
		boolean exists = false;
		int index = -1;
		String strSql = "";
		
		for (int i = 0; i < parameters.size(); i++) {
			strSql = sql;
			Map<String, Object> map = (Map<String, Object>) parameters.get(i);
			
			try {
				count = 0;
				while (strSql.indexOf(":") != -1) {
					strSql = strSql.replaceFirst(":", "?" + count++ + "?");
				}

				key = null;
				exists = false;
				index = -1;
				data.clear();
				for (int j = 0; j <= count; j++) {
					exists = false;
					for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
						key = (String) iterator.next();
						index = strSql.indexOf("?" + j + "?" + key);
						if (index != -1) {
							exists = true;
							break;
						}
					}
					
					if (exists) {
						strSql = strSql.replace("?" + j + "?" + key, "?");
						data.add(map.get(key));
					} else {
						strSql = strSql.replace("?" + j + "?", ":");
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("sql execute error.");
			}
			
			param.add(data.toArray());
		}
		
		result.put("sql", strSql);
		result.put("parameters", param);
		
		return result;
	}
	
	/**
	 * to get sql for count total records
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return string of sql for count total records
	 */
	protected String count(Page page) {
		StringBuffer sbufSql = new StringBuffer("");
		sbufSql.append(" select count(*) \n");
		sbufSql.append(" from ( ");
		sbufSql.append(page.getSql());
		sbufSql.append(" ) total");
		
		return sbufSql.toString();
	}
	
	/**
	 * delete operation helper
	 * 
	 * @param repository instance of org.frame.repository.IRepository to provide database operate
	 * @param object instance of java bean to be operated
	 * 
	 * @return List<Map<String, Object>> sqls and parameters to be executed
	 */
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> delete(IRepository repository, Object object) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> record;
		if (object != null) {
			try {
				Class<?> clazz = object.getClass();
				
				Resolver annotation = new Resolver();
				Reflect reflect = new Reflect();
				
				boolean isSimple = new Resolver().simple(clazz);
				
				if (isSimple) {
					record = new HashMap<String, Object>();
					record.put("sql", annotation.sql(clazz).get("delete"));
					record.put("args", annotation.parameters(this, object, this.DELETE).get("data"));
					
					result.add(record);
				} else {
					Object data;
					
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						if (field.isAnnotationPresent(Reference.class)) {// refer self
							Reference reference = field.getAnnotation(Reference.class);

							for (String sync : reference.sync()) {
								if (Reference.DELETE.equals(sync)) {
									/*parameters = new HashMap();
									sbufSql = new StringBuffer("");

									sbufSql.append("delete form ").append(annotation.sql(Class.forName(reference.wrapper())).get("table")).append(" where 1 = 1");
									for (String feature : reference.features()) {
										sbufSql.append(" and ").append(feature);
									}

									for (int i = 0; i < reference.reference().length; i++) {
										sbufSql.append(" and (:").append(reference.field()[i]).append(" is null or ").append(reference.field()[i]).append(" = :").append(reference.field()[i]).append(")");
										parameters.put(reference.field()[i], data.get(reference.reference()[i].toLowerCase()));
									}

									record = new HashMap<String, Object>();
									record.put("sql", sbufSql.toString());
									record.put("args", parameters);
									
									result.add(record);
									
									break;*/
									
									data = reflect.get(object, field.getName().toLowerCase());
									if (data instanceof java.util.List) {
										if (data != null) {
											for (Object ref : (List<Object>) data) {
												if (ref != null) {
													record = new HashMap<String, Object>();
													record.put("sql", annotation.sql(ref.getClass()).get("delete"));
													record.put("args", annotation.parameters(this, ref, this.DELETE).get("data"));

													result.add(record);
												}
											}
										}
									} else if (data instanceof java.lang.Object[]) {
										if (data != null) {
											for (Object ref : (Object[]) data) {
												if (ref != null) {
													record = new HashMap<String, Object>();
													record.put("sql", annotation.sql(ref.getClass()).get("delete"));
													record.put("args", annotation.parameters(this, ref, this.DELETE).get("data"));

													result.add(record);
												}
											}
										}
									} else ;
									
									break;
								}
							}
						}
					}
					
					record = new HashMap<String, Object>();
					record.put("sql", annotation.sql(clazz).get("delete"));
					record.put("args", annotation.parameters(this, object, this.DELETE).get("data"));
					
					result.add(record);
					
					for (Field field : fields) {
						if (field.isAnnotationPresent(Foreign.class)) {
							Foreign foreign = field.getAnnotation(Foreign.class);

							for (String sync : foreign.sync()) {
								if (Foreign.DELETE.equals(sync)) {// self foreign
									data = reflect.get(object, field.getName().toLowerCase());
									if (data != null) {
										record = new HashMap<String, Object>();
										record.put("sql", annotation.sql(data.getClass()).get("delete"));
										record.put("args", annotation.parameters(this, data, this.DELETE).get("data"));

										result.add(record);
									}
									
									break;
								}
							}
						}
					}

				}
			} catch (IlleagalDataException e) {
				throw new RuntimeException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("sql execute error.");
			}
		}
		
		return result;
	}
	
	/**
	 * initiate instance of org.frame.model.system.page.Page
	 * 
	 * @param sql sql for pagination
	 * @param parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page
	 */
	@SuppressWarnings("unchecked")
	protected Page initPage(String sql, Object parameters) {
		if (parameters instanceof Map<?, ?>) {
			return new Page(sql, 1, (Map<String, Object>) parameters);
		} else {
			return new Page(sql, 1, (Object[]) parameters);
		}
		
	}
	
	/**
	 * insert operation helper
	 * 
	 * @param repository instance of org.frame.repository.IRepository to provide database operate
	 * @param object instance of java bean to be operated
	 * 
	 * @return List<Map<String, Object>> sqls and parameters to be executed
	 */
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> insert(IRepository repository, Object object) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> record;
		if (object != null) {
			try {
				Class<?> clazz = object.getClass();

				Resolver annotation = new Resolver();
				Reflect reflect = new Reflect();

				boolean isSimple = annotation.simple(clazz);
				if (isSimple) {
					record = new HashMap<String, Object>();
					record.put("sql", annotation.sql(clazz).get("insert"));
					record.put("args", annotation.parameters(this, object, this.INSERT).get("data"));
					
					result.add(record);
				} else {
					Object data;
					Field[] fields = clazz.getDeclaredFields(); 
					Map<String, Object> foreignData, referenceData;
					for (Field field : fields) {
						if (field.isAnnotationPresent(Foreign.class)) {//foreign slef
							Foreign foreign = field.getAnnotation(Foreign.class);

							for (String sync : foreign.sync()) {
								if (Foreign.INSERT.equals(sync)) {
									data = reflect.get(object, field.getName().toLowerCase());
									if (data != null) {
										//this.handleInsert(data);
										
										if (reflect.get(object, field.getName().toLowerCase()) != null) {
											foreignData = repository.find(annotation.sql(field.getType()).get("find"), annotation.parameters(repository, reflect.get(object, field.getName().toLowerCase()), Resolver.SELECT).get("id"));
										} else {
											foreignData = null;
										}
										
										if (foreignData == null) {
											record = new HashMap<String, Object>();
											record.put("sql", annotation.sql(data.getClass()).get("insert"));
											record.put("args", annotation.parameters(this, data, this.INSERT).get("data"));
											
											result.add(record);
										} else {
											record = new HashMap<String, Object>();
											record.put("sql", annotation.sql(data.getClass()).get("update"));
											record.put("args", annotation.parameters(this, data, this.UPDATE).get("data"));
											
											result.add(record);
										}
										
									}
								}
							}
						}
					}

					//result = this.insert(annotation.sql(clazz).get("insert"), annotation.parameters(this, object, this.INSERT).get("data"));
					record = new HashMap<String, Object>();
					record.put("sql", annotation.sql(clazz).get("insert"));
					record.put("args", annotation.parameters(this, object, this.INSERT).get("data"));
					
					result.add(record);

					for (Field field : fields) {	
						if (field.isAnnotationPresent(Reference.class)) {//self reference
							Reference reference = field.getAnnotation(Reference.class);

							for (String sync : reference.sync()) {
								if (Reference.INSERT.equals(sync)) {
									data = reflect.get(object, field.getName().toLowerCase());
									if (data instanceof java.util.List) {
										if (data != null) {
											for (Object ref : (List<Object>) data) {
												if (ref != null) {
													//this.insert(annotation.sql(ref.getClass()).get("insert"), annotation.parameters(this, ref, this.INSERT).get("data"));
													
													referenceData = repository.find(annotation.sql(Class.forName(reference.wrapper())).get("find"), annotation.parameters(repository, ref, Resolver.SELECT).get("id"));
													if (referenceData == null) {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("insert"));
														record.put("args", annotation.parameters(this, ref, this.INSERT).get("data"));
														
														result.add(record);
													} else {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("update"));
														record.put("args", annotation.parameters(this, ref, this.UPDATE).get("data"));
														
														result.add(record);
													}
												}
											}
										}
									} else if (data instanceof java.lang.Object[]) {
										if (data != null) {
											for (Object ref : (Object[]) data) {
												if (ref != null) {
													//this.insert(annotation.sql(ref.getClass()).get("insert"), annotation.parameters(this, ref, this.INSERT).get("data"));
													
													referenceData = repository.find(annotation.sql(Class.forName(reference.wrapper())).get("find"), annotation.parameters(repository, ref, Resolver.SELECT).get("id"));
													if (referenceData == null) {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("insert"));
														record.put("args", annotation.parameters(this, ref, this.INSERT).get("data"));
														
														result.add(record);
													} else {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("update"));
														record.put("args", annotation.parameters(this, ref, this.UPDATE).get("data"));
														
														result.add(record);
													}
												}
											}
										}
									} else ;
								}
							}
						}
					}
				}
			} catch (IlleagalDataException e) {
				throw new RuntimeException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("sql execute error.");
			}
		}

		return result;
	}
	
	/**
	 * convert map of query result record to bean 
	 * 
	 * @param map query result
	 * @param clazz class of java bean
	 * 
	 * @return instance of java bean
	 */
	@SuppressWarnings("unchecked")
	protected Object map2Object(Map<String, Object> map, Class<?> clazz) {
		Object result = null;
		Map<String, Object> handling;
		try {
			if (map != null && map.size() > 0) {
				StringBuffer sbufSql;
				Map<String, Object> parameters;
				Object data = null;

				String[] ids;
				boolean needQuery = false;
				boolean same = true;
				
				result = clazz.newInstance();
				handling = new HashMap<String, Object>();
				handling.put("'" + clazz.getName(), result);
				container.add(handling);
				
				Resolver annotation = new Resolver();
				Reflect reflect = new Reflect();
				
				boolean isSimple = annotation.simple(clazz);

				String key;
				if (isSimple) {
					for (Iterator<?> iterator = map.keySet().iterator(); iterator.hasNext();) {
						key = (String) iterator.next();
						reflect.set(result, key.toLowerCase(), map.get(key.toLowerCase()));
						if ("id".equals(key))
							handling.put(key.toLowerCase(), map.get(key));
					}
				} else {
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						if (field.isAnnotationPresent(Column.class)) {
							Column column = field.getAnnotation(Column.class);

							data = map.get(column.name().toLowerCase());
							reflect.set(result, field.getName().toLowerCase(), data);
							if (column.primary())
								handling.put(column.name().toLowerCase(), data);
						}
						
						if (field.isAnnotationPresent(Mirror.class)) {
							Mirror mirror = field.getAnnotation(Mirror.class);

							data = map.get(mirror.name().toLowerCase());
							reflect.set(result, field.getName().toLowerCase(), data);
							if (mirror.primary())
								handling.put(mirror.name().toLowerCase(), data);
						}
					}
					
					for (Field field : fields) {
						if (field.isAnnotationPresent(Foreign.class)) {
							Foreign foreign = field.getAnnotation(Foreign.class);
							
							if (!foreign.lazy()) {
								needQuery = false;
								parameters = new HashMap<String, Object>();
								for (int i = 0; i < foreign.id().length; i++) {
									if (map.get(foreign.reference()[i].toLowerCase()) != null) needQuery = true;
									parameters.put(foreign.id()[i], map.get(foreign.reference()[i].toLowerCase()));
								}

								if (needQuery) {
									data = this.find(annotation.sql(field.getType()).get("find"), parameters);
								} else {
									data = null;
								}
								
								if (data != null) {
									for (Map<String, Object> ready : container) {
										if (ready.get("'" + field.getType().getName()) == null) {
											same = false;
											continue;
										} else {
											same = true;
											if (annotation.sql(field.getType()).get("id") != null && !"".equals(annotation.sql(field.getType()).get("id"))) {
												ids = annotation.sql(field.getType()).get("id").split(",");
											} else {
												ids = new String[]{};
											}
											for (String id : ids) {
												if (ready.get(id).equals(((Map<String, Object>) data).get(id))) {
													continue;
												} else {
													same = false;
													break;
												}
											}
											
											if (same) {
												data = ready.get("'" + field.getType().getName());
												break;
											}
										}
									}
									
									if (same) ;
									else {
										//data = this.map2Object(this.find(annotation.sql(field.getType()).get("find"), parameters), field.getType());
										data = this.map2Object((Map<String, Object>) data, field.getType());
									}
								}
								
								reflect.set(result, field.getName().toLowerCase(), data);
							}
						}

						if (field.isAnnotationPresent(Reference.class)) {
							Reference reference = field.getAnnotation(Reference.class);

							if (!reference.lazy()) {
								parameters = new HashMap<String, Object>();
								
								if ("java.util.Map".equals(reference.wrapper())) {
									sbufSql = new StringBuffer();
									sbufSql.append("select * from ").append(reference.name()).append(" where 1 = 1");
								} else {
									sbufSql = new StringBuffer(annotation.sql(Class.forName(reference.wrapper())).get("select"));
								}
								
								for (String features : reference.features()) {
									if (!"".equals(features)) {
										sbufSql.append(" and ").append(features);
									}
								}

								for (int i = 0; i < reference.reference().length; i++) {
									sbufSql.append(" and (:").append(reference.field()[i].toLowerCase()).append(" is null or ").append(reference.field()[i].toLowerCase()).append(" = :").append(reference.field()[i].toLowerCase()).append(")");
									parameters.put(reference.field()[i].toLowerCase(), map.get(reference.reference()[i]));
								}
								
								if ("java.util.Map".equals(reference.wrapper())) {
									data = this.select(sbufSql.toString(), parameters);
								} else {
									data = this.select(sbufSql.toString(), parameters, Class.forName(reference.wrapper()));
								}
								
								reflect.set(result, field.getName().toLowerCase(), data);
							}
						}
					}
				}
			} else {
				result = clazz.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("load to object error.");
		}

		return result;
	}
	
	/**
	 * to get pagination sql
	 * 
	 * @param page instance of org.frame.model.system.page.Page
	 * 
	 * @return sql for pagination
	 */
	protected String page(Page page) {
		String result = null;
		
		this.setDatabaseType(null);
		if (database != null) {
			result = database.pagination(page);
		}
		
		return result;
	}
	
	/**
	 * set sql and parameters to instance of java.sql.PreparedStatement
	 * 
	 * @param conn instance of java.sql.Connection
	 * @param sql sql for operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java.sql.PreparedStatement
	 */
	@SuppressWarnings("unchecked")
	protected PreparedStatement preparedStatement(Connection conn, String sql, Object parameters) {
		if (parameters != null && parameters instanceof Map<?, ?>) {
			return this.preparedStatement(conn, sql, (Map<String, Object>) parameters);
		} else if (parameters instanceof Object[]) {
			return this.preparedStatement(conn, sql, (Object[]) parameters);
		} else return this.preparedStatement(conn, sql, new Object[]{});
	}
	
	@SuppressWarnings("unchecked")
	protected PreparedStatement preparedStatement(PreparedStatement pstmt, String sql, Object parameters) {
		if (parameters != null && parameters instanceof Map<?, ?>) {
			return this.preparedStatement(pstmt, sql, (Map<String, Object>) parameters);
		} else if (parameters instanceof Object[]) {
			return this.preparedStatement(pstmt, sql, (Object[]) parameters);
		} else return this.preparedStatement(pstmt, sql, new Object[]{});
	}
	
	/**
	 * refresh instance of org.frame.model.system.page.Page
	 * 
	 * @param page instance of org.frame.model.system.page.Page to be refreshed
	 * @param sql sql for operation
	 * @param parameters parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page be refreshed
	 */
	protected Page refreshPage(String sql, Object parameters, Page page) {
		if (page == null) {
			page = initPage(sql, parameters);
		} else {
			page.setSql(sql);
			page.setParameters(parameters);
		}
		
		return page;
	}
	
	/**
	 * reset instance of org.frame.model.system.page.Page
	 * 
	 * @param page instance of org.frame.model.system.page.Page to be reseted
	 * @param sql sql for operation
	 * @param parameters parameters parameters for sql
	 * 
	 * @return instance of org.frame.model.system.page.Page be reseted
	 */
	protected Page resetPage(Page page) {
		page.reset();
		return page;
	}
	
	/**
	 * set parameters to instance of java.sql.PreparedStatement
	 * 
	 * @param pstmt instance of java.sql.PreparedStatement to be set
	 * @param parameters parameters to set
	 * 
	 * @return instance of java.sql.PreparedStatement
	 */
	protected PreparedStatement setParameters(PreparedStatement pstmt, Object[] parameters) {
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				try {
					pstmt.setObject(i + 1, parameters[i]);
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
		}
		
		return pstmt;
	}
	
	/**
	 * update operation helper
	 * 
	 * @param repository instance of org.frame.repository.IRepository to provide database operate
	 * @param object instance of java bean to be operated
	 * 
	 * @return List<Map<String, Object>> sqls and parameters to be executed
	 */
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> update(IRepository repository, Object object) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> record;
		if (object != null) {
			try {
				Class<?> clazz = object.getClass();

				Resolver annotation = new Resolver();
				Reflect reflect = new Reflect();

				boolean isSimple = annotation.simple(clazz);
				if (isSimple) {
					record = new HashMap<String, Object>();
					record.put("sql", annotation.sql(clazz).get("update"));
					record.put("args", annotation.parameters(this, object, this.UPDATE).get("data"));
					
					result.add(record);
				} else {
					Object data;
					Field[] fields = clazz.getDeclaredFields(); 
					Map<String, Object> foreignData, referenceData;
					for (Field field : fields) {
						if (field.isAnnotationPresent(Foreign.class)) {
							Foreign foreign = field.getAnnotation(Foreign.class);

							for (String sync : foreign.sync()) {
								if (Foreign.UPDATE.equals(sync)) {
									data = reflect.get(object, field.getName().toLowerCase());
									if (data != null) {
										if (reflect.get(object, field.getName().toLowerCase()) != null) {
											foreignData = repository.find(annotation.sql(field.getType()).get("find"), annotation.parameters(repository, reflect.get(object, field.getName().toLowerCase()), Resolver.SELECT).get("id"));
										} else {
											foreignData = null;
										}
										
										if (foreignData == null) {
											record = new HashMap<String, Object>();
											record.put("sql", annotation.sql(data.getClass()).get("insert"));
											record.put("args", annotation.parameters(this, data, this.INSERT).get("data"));
											
											result.add(record);
										} else {
											record = new HashMap<String, Object>();
											record.put("sql", annotation.sql(data.getClass()).get("update"));
											record.put("args", annotation.parameters(this, data, this.UPDATE).get("data"));
											
											result.add(record);
										}
										
									}
								}
							}
						}
					}

					record = new HashMap<String, Object>();
					record.put("sql", annotation.sql(clazz).get("update"));
					record.put("args", annotation.parameters(this, object, this.UPDATE).get("data"));
					
					result.add(record);

					for (Field field : fields) {	
						if (field.isAnnotationPresent(Reference.class)) {
							Reference reference = field.getAnnotation(Reference.class);

							for (String sync : reference.sync()) {
								if (Reference.UPDATE.equals(sync)) {
									data = reflect.get(object, field.getName().toLowerCase());
									if (data instanceof java.util.List) {
										if (data != null) {
											for (Object ref : (List<Object>) data) {
												if (ref != null) {
													referenceData = repository.find(annotation.sql(Class.forName(reference.wrapper())).get("find"), annotation.parameters(repository, ref, Resolver.SELECT).get("id"));
													if (referenceData == null) {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("insert"));
														record.put("args", annotation.parameters(this, ref, this.INSERT).get("data"));
														
														result.add(record);
													} else {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("update"));
														record.put("args", annotation.parameters(this, ref, this.UPDATE).get("data"));
														
														result.add(record);
													}
												}
											}
										}
									} else if (data instanceof java.lang.Object[]) {
										if (data != null) {
											for (Object ref : (Object[]) data) {
												if (ref != null) {
													referenceData = repository.find(annotation.sql(Class.forName(reference.wrapper())).get("find"), annotation.parameters(repository, ref, Resolver.SELECT).get("id"));
													if (referenceData == null) {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("insert"));
														record.put("args", annotation.parameters(this, ref, this.INSERT).get("data"));
														
														result.add(record);
													} else {
														record = new HashMap<String, Object>();
														record.put("sql", annotation.sql(ref.getClass()).get("update"));
														record.put("args", annotation.parameters(this, ref, this.UPDATE).get("data"));
														
														result.add(record);
													}
												}
											}
										}
									} else ;
								}
							}
						}
					}
				}
			} catch (IlleagalDataException e) {
				throw new RuntimeException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("sql execute error.");
			}
		}

		return result;
	}
	
	//@PostConstruct
	@SuppressWarnings("unused")
	private void authorization() {
		if (!new Certificate().authorization()) System.exit(0);
	}
	
	/**
	 * convert sql with :parameters to sql with ?
	 * 
	 * @param conn instance of java.sql.Connection
	 * @param sql sql for operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java.sql.PreparedStatement
	 */
	private PreparedStatement preparedStatement(Connection conn, String sql, Map<String, Object> parameters) {
		PreparedStatement pstmt = null;
		try {
			List<Object> data = new ArrayList<Object>();
			
			int count = 0;
			while (sql.indexOf(":") != -1) {
				sql = sql.replaceFirst(":", "?" + count++ + "?");
			}

			String key = null;
			boolean exists = false;
			int index = -1;
			for (int i = 0; i <= count; i++) {
				exists = false;
				for (Iterator<String> iterator = parameters.keySet().iterator(); iterator.hasNext();) {
					key = (String) iterator.next();
					index = sql.indexOf("?" + i + "?" + key);
					if (index != -1) {
						exists = true;
						break;
					}
				}
				
				if (exists) {
					sql = sql.replace("?" + i + "?" + key, "?");
					data.add(parameters.get(key));
				} else {
					sql = sql.replace("?" + i + "?", ":");
				}
			}

			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt = this.setParameters(pstmt, data.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		}
		
		return pstmt;
	}
	
	/**
	 * set sql and parameters to instance of java.sql.PreparedStatement
	 * 
	 * @param conn instance of java.sql.Connection
	 * @param sql sql for operation
	 * @param parameters parameters for sql
	 * 
	 * @return instance of java.sql.PreparedStatement
	 */
	private PreparedStatement preparedStatement(Connection conn, String sql, Object[] parameters) {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt = this.setParameters(pstmt, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		}
		
		return pstmt;
	}
	
	/**
	 * set sql to instance of java.sql.Statement
	 * 
	 * @param conn instance of java.sql.Connection
	 * @param sql sql for operation
	 * 
	 * @return instance of java.sql.PreparedStatement
	 */
	protected Statement statement(Connection conn, String sql) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sql execute error.");
		}
		
		return stmt;
	}
	
	/*public static void main(String[] args) {
		JDBCDao repostitory = new JDBCDao();
		String sql = "select * from tablea where 1=1 and titlea = :aaa and titleb = :bbb and titlec = ':cc' and titled = :bbb and titlee='?'";
		Map<String, Object> parameters = new HashMap();
		parameters.put("aaa", "aaa");
		parameters.put("bbb", "bbb");
		parameters.put("ccc", "ccc");
		repostitory.preparedStatement(null, sql, parameters);
	}*/
	
}
