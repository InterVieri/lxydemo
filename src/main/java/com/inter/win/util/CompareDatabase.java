package com.inter.win.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CompareDatabase {
    public static void main(String[] args) throws Exception {
        String[] targetConn = {"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@172.16.60.22:1521:orcl", "dataman_sc", "dataman_sc"};
        String[] sourceConn1 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata", "root", "root"};
        String[] sourceConn2 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_standard", "root", "root"};
        String[] sourceConn3 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_qa_new", "root", "root"};
        String[] sourceConn4 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata_common_admin", "root", "root"};
        Connection conn = DBUtil.conn(targetConn[0], targetConn[1], targetConn[2], targetConn[3]);
        String sql = "begin insert into m_col_log (id) values (sys_guid());" +
                "insert into m_col_log (id) values (sys_guid()); end;";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.execute();
    }
}
