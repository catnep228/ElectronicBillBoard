package ru.vlsu.ispi.daoimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBImpl{
	private static String url = "jdbc:mysql://localhost:3306/boardNEPASDF";
	private static String username = "root";
	private static String password = "root";
	public static Connection getConnection() throws SQLException{
		Connection connection = null;
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
			throw new SQLException("Mysql driver not found");
		}
	
		return connection;
	}
	
}