/**
 * Annotation contains methods to analyze repository annotation
 */
package org.frame.repository.annotation.resolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.frame.common.lang.reflect.Reflect;
import org.frame.repository.annotation.Column;
import org.frame.repository.annotation.Foreign;
import org.frame.repository.annotation.Reference;
import org.frame.repository.annotation.Table;
import org.frame.repository.exception.IlleagalDataException;
import org.frame.repository.sql.IRepository;

public class Resolver {

	public static String DELETE = "delete";
	
	public static String INSERT = "insert";
	
	public static String SELECT = "select";
	
	public static String UPDATE = "update";
	
	/**
	 * check number if legal
	 * 
	 * @param object instance of java bean to be operated
	 * @param scale length of integer part of number
	 * @param precision length of fractional part of number
	 * 
	 * @return  true number is legal
	 *         false number is illegal
	 */
	public boolean checkNumber(Object object, int scale, int precision) {
		boolean result = false;
		
		try {
			String string = String.valueOf(object);
			Double.parseDouble(string);
			
			if (scale == 0 && precision == 0) {
				result = true;
			} else {
				int index = string.indexOf(".");
				if (index == -1) {
					if (string.length() <= scale) {
						result = true;
					}
				} else {
					String prefix = string.substring(0, index);
					String suffix = string.substring(index, string.length());
					
					if (prefix.length() <= scale && suffix.length() <= precision) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * check string if legal
	 * 
	 * @param object instance of java bean to be operated
	 * @param length length of string
	 * 
	 * @return  true string is legal
	 *         false string is illegal
	 */
	public boolean checkString(Object object, int length) {
		boolean result = false;
		
		try {
			String string = String .valueOf(object);
			
			if (length == 0)
				result = true;
			else
				if (string.length() <= length)
					result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * create sql to be operated
	 * 
	 * @param clazz class of java bean to be operated
	 * 
	 * @return map of sql <br>
	 *         option keys:   find sql for find
	 *                      delete sql for delete
	 *                      insert sql for insert
	 *                      select sql for select
	 *                      update sql for update
	 *                       field field information for class
	 *                       table table name information for class
	 *                      unique where part in sql
	 *                          id id information for class
	 */
	public Map<String, String> sql(Class<?> clazz) {
		Map<String, String> result = new HashMap<String, String>();
		
		String find = "";
		String delete = "";
		String insert = "";
		String select = "";
		String update = "";
		
		String tabName = this.table(clazz);
		
		List<String> lstField = new ArrayList<String>();
		List<String> lstWhere = new ArrayList<String>();
		List<String> lstValues = new ArrayList<String>();
		List<String> lstUpdate = new ArrayList<String>();
		List<String> lstId = new ArrayList<String>();
		
		StringBuffer sbufField = new StringBuffer("");
		StringBuffer sbufWhere = new StringBuffer("");
		StringBuffer sbufValues = new StringBuffer("");
		StringBuffer sbufUpdate = new StringBuffer("");
		StringBuffer sbufId = new StringBuffer("");
		
		try {
			boolean isSimple = this.simple(clazz);
			
			Field[] fields = clazz.getDeclaredFields();
			
			if (isSimple) {
				for (Field field : fields) {
					if (!lstField.contains(field.getName().toLowerCase())) {
						lstField.add(field.getName().toLowerCase());
					}
					
					if (!lstValues.contains(":" + field.getName().toLowerCase())) {
						lstValues.add(":" + field.getName().toLowerCase());
					}
					
					if (!lstUpdate.contains(field.getName().toLowerCase() + " = :" + field.getName().toLowerCase())) {
						lstUpdate.add(field.getName().toLowerCase() + " = :" + field.getName().toLowerCase());
					}
					
					if ("id".equals(field.getName().toLowerCase())) {
						if (!lstWhere.contains(" and (:" + field.getName().toLowerCase() + " is null or " + field.getName().toLowerCase() + " = :" + field.getName().toLowerCase() + ")")) {
							lstWhere.add(" and (:" + field.getName().toLowerCase() + " is null or " + field.getName().toLowerCase() + " = :" + field.getName().toLowerCase() + ")");
						}
						
						lstId.add(field.getName().toLowerCase());
					}
				}
			} else {
				for (Field field : fields) {
					if (field.isAnnotationPresent(Column.class)) {
						Column column = field.getAnnotation(Column.class);

						if (!lstField.contains(column.name().toLowerCase())) {
							lstField.add(column.name().toLowerCase());
						}
						
						if (!lstValues.contains(":" + column.name().toLowerCase())) {
							lstValues.add(":" + column.name().toLowerCase());
						}
						
						if (!lstUpdate.contains(column.name().toLowerCase() + " = :" + column.name().toLowerCase())) {
							lstUpdate.add(column.name().toLowerCase() + " = :" + column.name().toLowerCase());
						}

						if (column.primary()) {
							if (!lstWhere.contains(" and (:" + column.name().toLowerCase() + " is null or " + column.name().toLowerCase() + " = :" + column.name().toLowerCase() + ")")) {
								lstWhere.add(" and (:" + column.name().toLowerCase() + " is null or " + column.name().toLowerCase() + " = :" + column.name().toLowerCase() + ")");
							}
							
							lstId.add(column.name().toLowerCase());
						}
					}

					if (field.isAnnotationPresent(Foreign.class)) {
						Foreign foreign = field.getAnnotation(Foreign.class);

						for (String reference : foreign.reference()) {
							if (!lstField.contains(reference.toLowerCase())) {
								lstField.add(reference.toLowerCase());
							}
							
							if (!lstValues.contains(":" + reference.toLowerCase())) {
								lstValues.add(":" + reference.toLowerCase());
							}
							
							if (!lstUpdate.contains(reference.toLowerCase() + " = :" + reference.toLowerCase())) {
								lstUpdate.add(reference.toLowerCase() + " = :" + reference.toLowerCase());
							}
						}
					}
					
					if (field.isAnnotationPresent(Reference.class)) {
						Reference reference = field.getAnnotation(Reference.class);

						for (String references : reference.reference()) {
							if (!lstField.contains(references.toLowerCase())) {
								lstField.add(references.toLowerCase());
							}
							
							if (!lstValues.contains(":" + references.toLowerCase())) {
								lstValues.add(":" + references.toLowerCase());
							}
							
							if (!lstUpdate.contains(references.toLowerCase() + " = :" + references.toLowerCase())) {
								lstUpdate.add(references.toLowerCase() + " = :" + references.toLowerCase());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String data : lstField) {
			if ("".equals(sbufField.toString())) {
				sbufField.append(data);
			} else {
				sbufField.append(", ").append(data);
			}
		}
		
		for (String data : lstValues) {
			if ("".equals(sbufValues.toString())) {
				sbufValues.append(data);
			} else {
				sbufValues.append(", ").append(data);
			}
		}
		
		for (String data : lstUpdate) {
			if ("".equals(sbufUpdate.toString())) {
				sbufUpdate.append(data);
			} else {
				sbufUpdate.append(", ").append(data);
			}
		}
		
		for (String data : lstWhere) {
			sbufWhere.append(data);
		}
		
		for (String data : lstId) {
			if ("".equals(sbufId.toString())) {
				sbufId.append(data);
			} else {
				sbufId.append(",").append(data);
			}
		}
		
		if ("".equals(sbufField.toString())) sbufField.append("*");
		
		find = "select " + sbufField.toString() + " from " + tabName + " where 1 = 1" + sbufWhere.toString();
		delete = "delete from " + tabName + " where 1 = 1" + sbufWhere.toString();
		insert = "insert into " + tabName + "(" +sbufField.toString() + ") values(" + sbufValues.toString() + ")";
		select = "select " + sbufField.toString() + " from " + tabName + " where 1 = 1";
		update = "update " + tabName + " set " + sbufUpdate.toString() + " where 1 = 1" + sbufWhere.toString();
		
		result.put("find", find);
		result.put("delete",delete);
		result.put("insert", insert);
		result.put("select", select);
		result.put("update", update);
		result.put("field", sbufField.toString());
		result.put("table", tabName);
		result.put("unique", sbufWhere.toString());
		result.put("id", sbufId.toString());
		
		return result;
	}
	
	/**
	 * analyze parameters for operation in object
	 * 
	 * @param repository instance of org.frame.repository.IRepository
	 * @param object instance of java bean to be operated
	 * @param type parameter analysis mode(delete, insert, select, update)
	 * 
	 * @return map of parameters
	 *         option keys:   id id parameters in object
	 *                      data parameters in object
	 * 
	 * @throws IlleagalDataException if data in object is illegal
	 */
	public Map<String, Map<String, Object>> parameters(IRepository repository, Object object, String type)  throws IlleagalDataException {
		Map<String, Map<String, Object>> result = null;
		
		if (object != null) {
			result = new HashMap<String, Map<String, Object>>();
			
			Map<String, Object> id = new HashMap<String, Object>();
			Map<String, Object> data = new HashMap<String, Object>();
			
			try {
				Class<?> clazz = object.getClass();
				
				Reflect reflect = new Reflect();
				
				boolean isSimple = this.simple(clazz);
				
				Field[] fields = clazz.getDeclaredFields();
				if (isSimple) {
					for (Field field : fields) {
						if ("id".equals(field.getName().toLowerCase()))
							id.put(field.getName().toLowerCase(), reflect.get(object, field.getName().toLowerCase()));

						data.put(field.getName().toLowerCase(), reflect.get(object, field.getName().toLowerCase()));
					}
				} else {
					Object value = null;
					//Map<String, Object> foreignData;
					Object foreignRow, referenceRow;
					List<?> referenceData;
					for (Field field : fields) {
						if (field.isAnnotationPresent(Column.class)) {
							Column column = field.getAnnotation(Column.class);

							if (Resolver.DELETE.equals(type)) {
								if (column.primary()) {
									if (reflect.get(object, field.getName().toLowerCase()) == null) {
										System.err.println("warning: id is null for delete.");
										//id.put(field.getName().toLowerCase(), this.get(object, field.getName().toLowerCase()));
									}
								}
							} else if (Resolver.INSERT.equals(type)) {
								if (reflect.get(object, field.getName().toLowerCase()) == null) {//data is null
									if (!"".equals(value)) {
										value = column.value();
									}
								} else {
									value = reflect.get(object, field.getName().toLowerCase());
								}
								
								if (value == null) {
									if (column.primary()) ;
									else {
										if (column.nullable()) ;
										else {
											throw new IlleagalDataException(field.getName() + " data can not be null.");
										}
									}
								} else {
									if (0 != column.length()) {
										if (this.checkString(value, column.length())) ;
										else
											throw new IlleagalDataException(field.getName() + " is too long, max length is " + column.length());
									}

									if (0 != column.scale()) {
										if (this.checkNumber(value, column.scale(), column.precision())) ;
										else
											throw new IlleagalDataException(field.getName() + " default value must be a (" + column.scale() + ", " + column.precision() + ")");
									}
								}
							} else if (Resolver.SELECT.equals(type)) {
								
							} else if (Resolver.UPDATE.equals(type)) {
								if (reflect.get(object, field.getName().toLowerCase()) == null) {//data is null
									if (!"".equals(value)) {
										value = column.value();
									}
								} else {
									value = reflect.get(object, field.getName().toLowerCase());
								}
								
								if (value == null) {
									if (column.primary()) {
										throw new IlleagalDataException("unique: " + field.getName() + ", data can not be null.");
									} else {
										if (column.nullable()) ;
										else {
											throw new IlleagalDataException(field.getName() + " data can not be null.");
										}
									}
								} else {
									if (0 != column.length()) {
										if (this.checkString(value, column.length())) ;
										else
											throw new IlleagalDataException(field.getName() + " is too long, max length is " + column.length());
									}

									if (0 != column.scale()) {
										if (this.checkNumber(value, column.scale(), column.precision())) ;
										else
											throw new IlleagalDataException(field.getName() + " default value must be a (" + column.scale() + ", " + column.precision() + ")");
									}
								}
							} else
								throw new IlleagalDataException("unsupported data check type: " + type);
							
							if (column.primary())
								id.put(column.name().toLowerCase(), reflect.get(object, field.getName().toLowerCase()));

							data.put(column.name().toLowerCase(), reflect.get(object, field.getName().toLowerCase()));
						}

						if (field.isAnnotationPresent(Foreign.class)) {
							Foreign foreign = field.getAnnotation(Foreign.class);

							/*if (reflect.get(object, field.getName().toLowerCase()) != null) {
								foreignData = repository.find(this.sql(field.getType()).get("find"), this.parameters(repository, reflect.get(object, field.getName().toLowerCase()), Annotation.SELECT).get("id"));
							} else {
								foreignData = null;
							}
							
							for (int i = 0; i < foreign.reference().length; i++) {
								if (foreignData == null)
									data.put(foreign.reference()[i], null);
								else
									data.put(foreign.reference()[i], foreignData.get(foreign.id()[i]));
							}*/
							
							foreignRow = reflect.get(object, field.getName().toLowerCase());
							for (int i = 0; i < foreign.reference().length; i++) {
								if (foreignRow == null)
									data.put(foreign.reference()[i], null);
								else
									data.put(foreign.reference()[i], reflect.get(foreignRow, foreign.id()[i]));
							}
							
						}
						
						if (field.isAnnotationPresent(Reference.class)) {
							Reference reference = field.getAnnotation(Reference.class);

							referenceData = (List<?>) reflect.get(object, field.getName().toLowerCase());
							
							if (referenceData != null && referenceData.size() > 0) {
								referenceRow = Class.forName(reference.wrapper()).cast(referenceData.get(0));
								for (int i = 0; i < reference.reference().length; i++) {
									if (!data.containsKey(reference.reference()[i])) {
										data.put(reference.reference()[i], reflect.get(referenceRow, reference.field()[i]));
									}
								}
							} else {
								for (int i = 0; i < reference.reference().length; i++) {
									if (!data.containsKey(reference.reference()[i])) {
										if (!"".equals(reference.value())) {
											data.put(reference.reference()[i], reference.value());
										} else {
											data.put(reference.reference()[i], null);
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			result.put("id", id);
			result.put("data", data);
		}
		
		return result;
	}
	
	public Map<String, Object> pk(Object object) {
		Map<String, Object> result = null;
		
		if (object != null) {
			result = new HashMap<String, Object>();
			
			try {
				Class<?> clazz = object.getClass();
				
				Reflect reflect = new Reflect();
				
				boolean isSimple = this.simple(clazz);
				
				Field[] fields = clazz.getDeclaredFields();
				if (isSimple) {
					for (Field field : fields) {
						if ("id".equals(field.getName().toLowerCase()))
							result.put(field.getName().toLowerCase(), reflect.get(object, field.getName().toLowerCase()));
					}
				} else {
					for (Field field : fields) {
						if (field.isAnnotationPresent(Column.class)) {
							Column column = field.getAnnotation(Column.class);

							if (column.primary())
								result.put(column.name().toLowerCase(), reflect.get(object, field.getName().toLowerCase()));

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * to get analyze mode
	 * 
	 * @param clazz class of java bean to be operated
	 * 
	 * @return  true use simple mode
	 *         false not use simple mode
	 */
	public boolean simple(Class<?> clazz) {
		boolean result = true;
		
		if (clazz.isAnnotationPresent(Table.class)) {
			result = clazz.getAnnotation(Table.class).simple();
		}
		
		return result;
	}
	
	/**
	 * to get table name
	 * 
	 * @param clazz class of java bean to be operated
	 * 
	 * @return string of table name
	 */
	public String table(Class<?> clazz) {
		String result = clazz.getSimpleName().toLowerCase();

		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = clazz.getAnnotation(Table.class);

			if (!"".equals(table.name())) {
				if (!"".equals(table.scheme())) {
					result = table.scheme().toLowerCase() + "." + table.name().toLowerCase();
				} else {
					result = table.name().toLowerCase();
				}
			}
		}

		return result;
	}
	
}
