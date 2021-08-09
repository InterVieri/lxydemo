package com.inter.win.collecter;


import com.inter.win.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TableCollecter {

    public List<String> getTables(Connection conn, String url) throws Exception {
        List<String> tableNameList = new ArrayList<>();
        ResultSet tables = null;
        /* ------------------------------------------*/
        long tableStart = System.currentTimeMillis();
        String sql = "";
        int databaseType = TypeUtil.getDatabaseType(url);
        if (databaseType == 1) {
            String schema = conn.getCatalog();
            sql = "SELECT table_name FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '" + schema + "'";
        } else {
            sql = "select table_name from user_tables where TABLESPACE_NAME is not null";
        }
        PreparedStatement stmt = conn.prepareStatement(sql);
        tables = stmt.executeQuery(sql);
        long tableEnd = System.currentTimeMillis();
        long tableTime = (tableEnd - tableStart) / 1000;
        log.info("获取表时长为：" + tableTime + "s");
        /* ------------------------------------------*/

        /* ------------------------------------------*/
        long getTablesStart = System.currentTimeMillis();
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            tableNameList.add(tableName.toUpperCase());
        }
        long getTablesEnd = System.currentTimeMillis();
        long getTablesTime = (getTablesEnd - getTablesStart) / 1000;
        log.info("装载所有表时长为：" + getTablesTime + "s");
        /* ------------------------------------------*/

        return tableNameList;
    }

}
