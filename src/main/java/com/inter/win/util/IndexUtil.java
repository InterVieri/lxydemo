package com.inter.win.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexUtil {

    public static List<Map<String, String>> getIndex(String[]... sourceConns) throws Exception {
        System.out.println("开始获取索引===>");
        List<Map<String, String>> result = new ArrayList<>();
        for (String[] sourceConn : sourceConns) {
            List<String> tables = TableUtil.getTables(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
            for (String tableName : tables) {
                Connection conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
                int databaseType = TypeUtil.getDatabaseType(sourceConn[1]);
                ResultSet rs = null;
                List<Map<String, String>> maps = ConnUtil.putIndexMap(conn, rs, tableName, databaseType);
                result.addAll(maps);
            }
        }
        return result;
    }
}
