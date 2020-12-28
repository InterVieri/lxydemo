package com.inter.win.collecter;

import com.inter.win.resolver.Resolver;
import com.inter.win.util.DBUtil;
import com.inter.win.util.TypeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class ColumnCollector {
    @Resource
    private TableCollecter tableCollecter;
    @Resource
    private Resolver resolver;
    public Map<String, List<Map<String, Map<String, String>>>>[] getColumns(String[] targetConn, String[]... sourceConns) throws Exception {
        System.out.println("开始获取字段===>");
        Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap = new ConcurrentHashMap<>();
        Map<String, List<Map<String, Map<String, String>>>> sourceConnColumenMap = new ConcurrentHashMap<>();
        Map<String, Map<String, String>> tMap = null;
        Map<String, Map<String, String>> sMap = null;
        Connection conn = null;
        ResultSet rs = null;
        int databaseType = 0;
        try {
            databaseType = TypeUtil.getDatabaseType(targetConn[1]);
            conn = DBUtil.conn(targetConn[0], targetConn[1], targetConn[2], targetConn[3]);
            List<String> targetTableNames = tableCollecter.getTables(conn, targetConn[1]);
            resolver.putMap(conn, rs, tMap, targetTableNames, targetConnColumenMap, databaseType);
            for (String[] sourceConn : sourceConns) {
                databaseType = TypeUtil.getDatabaseType(sourceConn[1]);
                conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
                List<String> sourceTableNames = tableCollecter.getTables(conn, sourceConn[1]);
                resolver.putMap(conn, rs, sMap, sourceTableNames, sourceConnColumenMap, databaseType);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return new Map[]{targetConnColumenMap, sourceConnColumenMap};
    }
}
