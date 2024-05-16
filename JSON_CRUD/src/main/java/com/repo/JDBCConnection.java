package com.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
	String url = "jdbc:mysql://localhost:3306/Student_Details";
	String userName = "root";
	String password = "root";

	public Connection establishConnection() {

		try {
			return DriverManager.getConnection(url, userName, password);
		} catch (SQLException e) {
			System.out.println("Connection Failed");
			e.printStackTrace();
			return null;
		}

	}
}
