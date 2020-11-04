package com.inter.win.util;


import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class CompareDatabase {
    public static void main(String[] args) {
        String[] targetConn = {"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@172.16.60.22:1521:orcl", "dataman_sc", "dataman_sc"};
        String[] sourceConn1 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_datasource", "root", "root"};
        //String[] sourceConn = {"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@172.16.60.22:1521:orcl", "dataman_zz", "dataman_zz"};
        //String[] sourceConn2 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_standard", "root", "root"};
        //String[] sourceConn3 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_qa_new", "root", "root"};
        //String[] sourceConn4 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata_common_admin", "root", "root"};
        try {
            Map<String, List<Map<String, Map<String, String>>>>[] maps = ColumnUtil.getColumns(targetConn, sourceConn1);
            List<List<String>> lists = ColumnUtil.compareColumns(maps[0], maps[1], targetConn[0]);
            System.out.println(lists.toString());
//            SQLUtil instance = SQLUtilFactory.getInstance("oracle.jdbc.driver.OracleDriver");
//            List<Map<String, String>> index = IndexUtil.getIndex(sourceConn1, sourceConn2, sourceConn3);
//            for (Map<String, String> stringStringMap : index) {
//                System.out.println(instance.getCreateIndexSql(stringStringMap));
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }












}
