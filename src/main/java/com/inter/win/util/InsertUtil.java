package com.inter.win.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InsertUtil {
    public static void main(String[] args) throws SQLException {
        String[] sourceConn = {"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@172.16.60.22:1521:orcl", "dataman_test", "dataman_test"};
        List<String> sqls = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String sql = "CREATE TABLE DATAMAN_TEST_" + i + "(ID VARCHAR2(32 BYTE) NOT NULL ," +
                    "TEST_COLUMN1 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN2 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN3 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN4 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN5 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN6 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN7 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN8 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN9 VARCHAR2(32 BYTE) NULL , " +
                    "TEST_COLUMN10 VARCHAR2(32 BYTE) NULL )";
            String commonSql1 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN1 IS '测试1'";

            String commonSql2 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN2 IS '测试2'";
            String commonSql3 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN3 IS '测试3'";
            String commonSql4 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN4 IS '测试4'";
            String commonSql5 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN5 IS '测试5'";
            String commonSql6 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN6 IS '测试6'";
            String commonSql7 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN7 IS '测试7'";
            String commonSql8 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN8 IS '测试8'";
            String commonSql9 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN9 IS '测试9'";
            String commonSql10 = "COMMENT ON COLUMN DATAMAN_TEST_" + i + ".TEST_COLUMN10 IS '测试10'";
//            sqls.add(commonSql1);
//            sqls.add(commonSql2);
//            sqls.add(commonSql3);
//            sqls.add(commonSql4);
//            sqls.add(commonSql5);
//            sqls.add(commonSql6);
//            sqls.add(commonSql7);
//            sqls.add(commonSql8);
//            sqls.add(commonSql9);
//            sqls.add(commonSql10);
            String idSql = "ALTER TABLE DATAMAN_TEST_" + i + " ADD PRIMARY KEY (ID)";
            sqls.add(idSql);

        }
        try {
            createTable(sqls, sourceConn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void createTable(List<String> sqls, String[] sourceConn) throws SQLException {
        Connection conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
        Statement statement = conn.createStatement();
        for (String sql : sqls) {
            statement.addBatch(sql);
        }
        statement.executeBatch();
    }
}
