package dao;

import java.sql.Connection;

public class AnnotationDAO {

	private Connection connection;
	
	public AnnotationDAO(Connection connection) {
		this.connection = connection;
	}

}
