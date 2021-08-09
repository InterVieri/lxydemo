package com.inter.win.resources;

import com.inter.win.cache.CacheUtil;
import com.inter.win.collecter.ColumnCollector;
import com.inter.win.comparator.Comparator;
import com.inter.win.dto.ConvertDto;
import com.inter.win.netty.ChannelSupervise;
import com.inter.win.util.IOUtil;
import com.inter.win.util.TypeUtil;
import io.netty.channel.Channel;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/convert")
public class ConvertResource {
    @Resource
    private Comparator comparator;
    @Resource
    private ColumnCollector columnCollector;

    @PostMapping
    public Object convert(@RequestBody ConvertDto params) throws Exception {
        CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            String frontId = params.getFrontId();
            Channel channel = (Channel) CacheUtil.get(frontId);

            ChannelSupervise.send2Channel(channel, "数据转换开始");

            List<List<String>> sources = params.getSources();

            List<String> target = params.getTarget();
            Iterator<List<String>> iterator = sources.iterator();
            while (iterator.hasNext()) {
                List<String> next = iterator.next();
                List<String> collect = next.stream().filter(StringUtils::isEmpty).collect(Collectors.toList());
                if (collect.size() > 0) {
                    iterator.remove();
                }
            }
            String targetAdapter = TypeUtil.getAdapter(target.get(3));
            String[] targetArr = new String[target.size()];
            targetArr[0] = targetAdapter;
            targetArr[1] = target.get(0);
            targetArr[2] = target.get(1);
            targetArr[3] = target.get(2);
            ChannelSupervise.send2Channel(channel, "目标库：" + targetArr[1]);
            String[][] sourcesArr = new String[sources.size()][4];
            for (int i = 0; i < sources.size(); i++) {
                List<String> source = sources.get(i);
                String[] sourceArr = new String[source.size()];
                String sourceAdapter = TypeUtil.getAdapter(source.get(3));
                sourceArr[0] = sourceAdapter;
                sourceArr[1] = source.get(0);
                sourceArr[2] = source.get(1);
                sourceArr[3] = source.get(2);
                sourcesArr[i] = sourceArr;
                ChannelSupervise.send2Channel(channel, "源库" + i + "：" + sourceArr[1]);
            }
            Map<String, List<Map<String, Map<String, String>>>>[] columns = null;
            try {
                columns = columnCollector.getColumns(channel, targetArr, sourcesArr);
                List<List<String>> lists = new ArrayList<>();
                if (columns != null) {
                    lists = comparator.compareColumns(channel, columns[0], columns[1], targetAdapter);
                }
                ChannelSupervise.send2Channel(channel, "比对结束");
                String fileName = IOUtil.upload(lists);
                String a = "<a href=/convert/file?filename=" + fileName + ">下载报告文件</a>";
                ChannelSupervise.send2Channel(channel, a);
                return fileName;
            } catch (Exception e) {
                ChannelSupervise.send2Channel(channel, "比对失败！" + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
        return null;
    }
    @GetMapping("/file")
    public void convert(String filename, HttpServletResponse response) throws Exception {
        IOUtil.download(filename, response);
    }
}
