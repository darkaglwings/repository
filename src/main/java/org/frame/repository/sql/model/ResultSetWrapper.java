package org.frame.repository.sql.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetWrapper {
	
	public Object wrapper(ResultSet rs) throws SQLException;

}
