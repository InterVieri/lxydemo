package com.inter.win.collecter;

import com.inter.win.resolver.Resolver;
import com.inter.win.util.DBUtil;
import com.inter.win.util.TypeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class IndexCollecter {
    @Resource
    private TableCollecter tableCollecter;
    @Resource
    private Resolver resolver;
    public List<Map<String, String>> getIndex(String[]... sourceConns) throws Exception {
        System.out.println("开始获取索引===>");
        List<Map<String, String>> result = new ArrayList<>();
        for (String[] sourceConn : sourceConns) {
            Connection conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
            List<String> tables = tableCollecter.getTables(conn, sourceConn[1]);
            for (String tableName : tables) {
                int databaseType = TypeUtil.getDatabaseType(sourceConn[1]);
                ResultSet rs = null;
                List<Map<String, String>> maps = resolver.putIndexMap(conn, rs, tableName, databaseType);
                result.addAll(maps);
            }
        }
        return result;
    }
}
