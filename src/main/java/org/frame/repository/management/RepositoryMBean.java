package org.frame.repository.management;


public interface RepositoryMBean {
	
	public String getSystem_database();

	public void setSystem_database(String system_database);

	public String getSystem_name();

	public void setSystem_name(String system_name);

	public String getSystem_os();

	public void setSystem_os(String system_os);

	public String getSystem_server();

	public void setSystem_server(String system_server);

	public String getSystem_state();

	public void setSystem_state(String system_state);

	public String getSystem_struct();

	public void setSystem_struct(String system_struct);

	public String getPage_dispread();

	public void setPage_dispread(String page_dispread);

	public String getPage_size();

	public void setPage_size(String page_size);

	public String getDatasource_jndi();

	public void setDatasource_jndi(String datasource_jndi);

	public String getDatasource_classname();

	public void setDatasource_classname(String datasource_classname);

	public String getDatasource_url();

	public void setDatasource_url(String datasource_url);

	public String getDatasource_username();

	public void setDatasource_username(String datasource_username);

	public String getDatasource_password();

	public void setDatasource_password(String datasource_password);

}
