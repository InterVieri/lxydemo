package com.inter.win.util;

import com.inter.win.collecter.ColumnCollector;
import com.inter.win.comparator.Comparator;


import java.util.List;
import java.util.Map;

public class CompareDatabase {
    public static void main(String[] args) throws Exception {
        String[] targetConn = {"oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@172.16.60.22:1521:orcl", "dataman_sc", "dataman_sc"};
        String[] sourceConn1 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata", "root", "root"};
        String[] sourceConn2 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_standard", "root", "root"};
        String[] sourceConn3 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/data_qa_new", "root", "root"};
        String[] sourceConn4 = {"com.mysql.jdbc.Driver", "jdbc:mysql://172.16.60.51:3306/metadata_common_admin", "root", "root"};
        ColumnCollector columnCollector = new ColumnCollector();
        Map<String, List<Map<String, Map<String, String>>>>[] map = columnCollector.getColumns(targetConn, sourceConn1);
        Comparator comparator = new Comparator();
        List<List<String>> lists = comparator.compareColumns(map[0], map[1], targetConn[0]);
        System.out.println(lists);
    }
}
