package com.inter.win.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Java中使用JDBC连接数据库 
 *  1） 加载驱动 2） 创建数据库连接
 *  3） 创建执行sql的语句 4） 执行语句 5） 处理执行结果 6） 释放资源 
 * @author liu.hb
 *
 */
public class DBUtil {
	/**
	 * Statement 和 PreparedStatement之间的关系和区别.
	    关系：PreparedStatement继承自Statement,都是接口
	    区别：PreparedStatement可以使用占位符，是预编译的，批处理比Statement效率高  
	 */
		public static Connection conn(String driver, String url, String user, String password) {
		Connection conn = null;
		// 1.加载驱动程序
		try {
			Class.forName(driver);
			// 2.获得数据库链接
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static String getSchema(Connection conn) throws Exception {
		String schema;
		schema = conn.getMetaData().getUserName();
		if ((schema == null) || (schema.length() == 0)) {
			throw new Exception("ORACLE数据库模式不允许为空");
		}
		return schema.toUpperCase().toString();

	}


}