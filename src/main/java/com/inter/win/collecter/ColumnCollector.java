package com.inter.win.collecter;

import com.inter.win.config.Config;
import com.inter.win.netty.ChannelSupervise;
import com.inter.win.resolver.Resolver;
import com.inter.win.util.DBUtil;
import com.inter.win.util.TypeUtil;
import io.netty.channel.Channel;
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
    private Config config;
    @Resource
    private TableCollecter tableCollecter;
    @Resource
    private Resolver resolver;
    public Map<String, List<Map<String, Map<String, String>>>>[] getColumns(Channel channel, String[] targetConn, String[]... sourceConns) throws Exception {
        ChannelSupervise.send2Channel(channel, "开始获取字段");
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
            ChannelSupervise.send2Channel(channel, "目标库建立连接成功" + targetConn[1]);
            ChannelSupervise.send2Channel(channel, "开始获取目标库所有表");
            List<String> targetTableNames = tableCollecter.getTables(conn, targetConn[1]);
            ChannelSupervise.send2Channel(channel, "获取目标库下所有表共" + targetTableNames.size() + "张");
            ChannelSupervise.send2Channel(channel, "开始加载目标库字段数据，请耐心等待");
            if (targetTableNames.size() > 2000) {
                ChannelSupervise.send2Channel(channel, "妈的这么多表，你想累死老子啊！不干了！！！");
                return null;
            }
            if (targetTableNames.size() > config.getBatchOperNum()) {
                int size = targetTableNames.size();
                int num = size / config.getBatchOperNum();
                for (int i = 0; i < num; i++) {
                    long start = System.currentTimeMillis();
                    resolver.putMap(conn, rs, tMap, targetTableNames.subList(i*config.getBatchOperNum(), (i+1)*config.getBatchOperNum()), targetConnColumenMap, databaseType);
                    long end = System.currentTimeMillis();
                    ChannelSupervise.send2Channel(channel, "第" + i + "次获取字段时长为" + (end-start)/1000 + "秒");
                }
                long start = System.currentTimeMillis();
                resolver.putMap(conn, rs, tMap, targetTableNames.subList(num*config.getBatchOperNum(), targetTableNames.size()), targetConnColumenMap, databaseType);
                long end = System.currentTimeMillis();
                ChannelSupervise.send2Channel(channel, "第" + num + "次获取字段时长为" + (end-start)/1000 + "秒");
            } else {
                long start = System.currentTimeMillis();
                resolver.putMap(conn, rs, tMap, targetTableNames, targetConnColumenMap, databaseType);
                long end = System.currentTimeMillis();
                ChannelSupervise.send2Channel(channel, "获取字段时长为" + (end-start)/1000 + "秒");
            }
            ChannelSupervise.send2Channel(channel, "加载目标库字段数据完毕");
            for (String[] sourceConn : sourceConns) {
                databaseType = TypeUtil.getDatabaseType(sourceConn[1]);
                conn = DBUtil.conn(sourceConn[0], sourceConn[1], sourceConn[2], sourceConn[3]);
                ChannelSupervise.send2Channel(channel, "源库建立连接成功" + sourceConn[1]);
                ChannelSupervise.send2Channel(channel, "开始获取源库所有表");
                List<String> sourceTableNames = tableCollecter.getTables(conn, sourceConn[1]);
                ChannelSupervise.send2Channel(channel, "获取源库下所有表共" + sourceTableNames.size() + "张");
                ChannelSupervise.send2Channel(channel, "开始加载源库字段数据");
                if (sourceTableNames.size() > 2000) {
                    ChannelSupervise.send2Channel(channel, "妈的这么多表，你想累死老子啊！不干了！！！");
                    return null;
                }
                if (sourceTableNames.size() > config.getBatchOperNum()) {
                    int size = sourceTableNames.size();
                    int num = size / config.getBatchOperNum();
                    for (int i = 0; i < num; i++) {
                        long start = System.currentTimeMillis();
                        resolver.putMap(conn, rs, sMap, sourceTableNames.subList(i*config.getBatchOperNum(), (i+1)*config.getBatchOperNum()), sourceConnColumenMap, databaseType);
                        long end = System.currentTimeMillis();
                        ChannelSupervise.send2Channel(channel, "第" + i + "次获取字段时长为" + (end-start)/1000 + "秒");
                    }
                    long start = System.currentTimeMillis();
                    resolver.putMap(conn, rs, sMap, sourceTableNames.subList(num*config.getBatchOperNum(), targetTableNames.size()), sourceConnColumenMap, databaseType);
                    long end = System.currentTimeMillis();
                    ChannelSupervise.send2Channel(channel, "次获取字段时长为" + (end-start)/1000 + "秒");
                } else {
                    long start = System.currentTimeMillis();
                    resolver.putMap(conn, rs, sMap, sourceTableNames, sourceConnColumenMap, databaseType);
                    long end = System.currentTimeMillis();
                    ChannelSupervise.send2Channel(channel, "获取字段时长为" + (end-start)/1000 + "秒");
                }
                ChannelSupervise.send2Channel(channel, "加载源库字段数据完毕");
            }
            ChannelSupervise.send2Channel(channel, "数据加载完毕");
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
