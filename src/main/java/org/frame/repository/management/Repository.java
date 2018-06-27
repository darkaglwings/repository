package org.frame.repository.management;

import java.util.HashMap;
import java.util.Map;

import org.frame.common.management.Common;
import org.frame.common.management.server.Register;
import org.frame.common.util.Properties;
import org.frame.repository.constant.IRepositoryConstant;

public class Repository extends Common implements RepositoryMBean {
	
	private String system_database;
	
	private String system_name;
	
	private String system_os;
	
	private String system_server;
	
	private String system_state;
	
	private String system_struct;

	private String page_dispread;
	
	private String page_size;

	private String datasource_jndi;

	private String datasource_classname;
	
	private String datasource_url;
	
	private String datasource_username;
	
	private String datasource_password;

	public String getSystem_database() {
		return system_database;
	}

	public void setSystem_database(String system_database) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.SYSTEM_DATABASE, system_database);
		
		this.system_database = system_database;
	}

	public String getSystem_name() {
		return system_name;
	}

	public void setSystem_name(String system_name) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.SYSTEM_NAME, system_name);
		
		this.system_name = system_name;
	}

	public String getSystem_os() {
		return system_os;
	}

	public void setSystem_os(String system_os) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.SYSTEM_OS, system_os);
		
		this.system_os = system_os;
	}

	public String getSystem_server() {
		return system_server;
	}

	public void setSystem_server(String system_server) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.SYSTEM_SERVER, system_server);
		
		this.system_server = system_server;
	}

	public String getSystem_state() {
		return system_state;
	}

	public void setSystem_state(String system_state) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.SYSTEM_STATE, system_state);
		
		this.system_state = system_state;
	}

	public String getSystem_struct() {
		return system_struct;
	}

	public void setSystem_struct(String system_struct) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.SYSTEM_STRUCT, system_struct);
		
		this.system_struct = system_struct;
	}

	public String getPage_dispread() {
		return page_dispread;
	}

	public void setPage_dispread(String page_dispread) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.PAGE_DISPREAD, page_dispread);
		
		this.page_dispread = page_dispread;
	}

	public String getPage_size() {
		return page_size;
	}

	public void setPage_size(String page_size) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.PAGE_SIZE, page_size);
		
		this.page_size = page_size;
	}

	public String getDatasource_jndi() {
		return datasource_jndi;
	}

	public void setDatasource_jndi(String datasource_jndi) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.DATASOURCE_JNDI, datasource_jndi);
		
		this.datasource_jndi = datasource_jndi;
	}

	public String getDatasource_classname() {
		return datasource_classname;
	}

	public void setDatasource_classname(String datasource_classname) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.DATASOURCE_CLASSNAME, datasource_classname);
		
		this.datasource_classname = datasource_classname;
	}

	public String getDatasource_url() {
		return datasource_url;
	}

	public void setDatasource_url(String datasource_url) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.DATASOURCE_URL, datasource_url);
		
		this.datasource_url = datasource_url;
	}

	public String getDatasource_username() {
		return datasource_username;
	}

	public void setDatasource_username(String datasource_username) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.DATASOURCE_USERNAME, datasource_username);
		
		this.datasource_username = datasource_username;
	}

	public String getDatasource_password() {
		return datasource_password;
	}

	public void setDatasource_password(String datasource_password) {
		Properties properties = new Properties(IRepositoryConstant.DEFAULT_CONFIG_PROPERTIES);
		properties.write(IRepositoryConstant.DATASOURCE_PASSWORD, datasource_password);
		
		this.datasource_password = datasource_password;
	}
	
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("org.frame.management.common:name=common", new Common());
		map.put("org.frame.management.repository:name=repository", new Repository());
		
		new Register(map);
	}

}
