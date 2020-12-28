package com.inter.win.comparator;

import com.inter.win.collecter.TableCollecter;
import com.inter.win.handler.SQLHandler;
import com.inter.win.handler.SQLHandlerFactory;
import com.inter.win.util.DBUtil;
import com.inter.win.util.TypeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Component
public class Comparator {
    @Resource
    private TableCollecter tableCollecter;
    public List<String> compareTables(List<String> targetConn, List<List<String>> sourceConns) throws Exception {
        List<String> sourceTables = new ArrayList<>();
        String targetConnAdapter = TypeUtil.getAdapter(targetConn.get(3));
        Connection conn = DBUtil.conn(targetConnAdapter, targetConn.get(0), targetConn.get(1), targetConn.get(2));
        List<String> targetTables = tableCollecter.getTables(conn, targetConn.get(0));
        for (List<String> sourceConn : sourceConns) {
            String sourceConnAdapter = TypeUtil.getAdapter(sourceConn.get(3));
            conn = DBUtil.conn(targetConnAdapter, targetConn.get(0), targetConn.get(1), targetConn.get(2));
            List<String> tables = tableCollecter.getTables(conn, sourceConn.get(0));
            sourceTables.addAll(tables);
        }
        sourceTables.removeAll(targetTables);
        return sourceTables;
    }
    public List<String> compareTables(String[] targetConn, String[]... sourceConns) throws Exception {
        List<String> sourceTables = new ArrayList<>();
        Connection conn = DBUtil.conn(targetConn[0], targetConn[1], targetConn[2], targetConn[3]);
        List<String> targetTables = tableCollecter.getTables(conn, targetConn[1]);
        for (String[] sourceConn : sourceConns) {
            conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
            List<String> tables = tableCollecter.getTables(conn, sourceConn[1]);
            sourceTables.addAll(tables);
        }
        sourceTables.removeAll(targetTables);
        return sourceTables;
    }
    public List<List<String>> compareColumns(Map<String, List<Map<String, Map<String, String>>>> targetConnColumenMap, Map<String, List<Map<String, Map<String, String>>>> sourceConnColumenMap, String targetDatabaseDriver) {
        SQLHandler sqlUtil = SQLHandlerFactory.getInstance(targetDatabaseDriver);
        List<List<String>> allList = new ArrayList<>();
        List<String> tableList = new ArrayList<>();
        List<String> columnList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> tableSqlList = new ArrayList<>();
        List<String> columnSqlList = new ArrayList<>();
        Set<Map.Entry<String, List<Map<String, Map<String, String>>>>> entries = sourceConnColumenMap.entrySet();
        for (Map.Entry<String, List<Map<String, Map<String, String>>>> entry : entries) {
            if (targetConnColumenMap.containsKey(entry.getKey())) {
                List<Map<String, Map<String, String>>> sValue = entry.getValue();
                List<Map<String, Map<String, String>>> tValue = targetConnColumenMap.get(entry.getKey());
                for (Map<String, Map<String, String>> sColValueMap : sValue) {
                    Set<Map.Entry<String, Map<String, String>>> sColValueMapEntries = sColValueMap.entrySet();
                    for (Map.Entry<String, Map<String, String>> sColValueMapEntry : sColValueMapEntries) {
                        List<Map<String, Map<String, String>>> collect = tValue.stream().filter(u -> u.containsKey(sColValueMapEntry.getKey())).collect(Collectors.toList());
                        if (collect.isEmpty()) {
                            columnList.add("目标库表" + entry.getKey() + "缺少字段" + sColValueMapEntry.getKey());
                            String alertSql = sqlUtil.getAlertColumnSql(entry.getKey(), sColValueMap);
                            columnSqlList.add(alertSql);
                        } else {
                            Map<String, String> value1 = sColValueMapEntry.getValue();
                            Map<String, String> value2 = collect.get(0).get(sColValueMapEntry.getKey());
                            String dbType1 = value1.get("dbType");
                            String dbType2 = value2.get("dbType");
                            String valueType1 = value1.get("valueType");
                            String valueType2 = value2.get("valueType");
                            if (!valueType1.toUpperCase().equals(valueType2.toUpperCase())) {
                                typeList.add("目标库表" + entry.getKey() + "字段" + dbType1 + sColValueMapEntry.getKey() + "数据格式不一致！" + dbType2);
                                String alertColumnSql = sqlUtil.getChangeTypeSql(entry.getKey(), sColValueMap);
                                columnSqlList.add(alertColumnSql);
                            }
                        }
                    }
                }
            } else {
                tableList.add("目标库缺少表" + entry.getKey());
                List<Map<String, Map<String, String>>> value = entry.getValue();
                String createSql = sqlUtil.getCreateSql(entry.getKey(), value);
                tableSqlList.add(createSql);
            }
        }
        allList.add(tableList);
        allList.add(columnList);
        allList.add(typeList);
        allList.add(tableSqlList);
        allList.add(columnSqlList);
        return allList;
    }
}
