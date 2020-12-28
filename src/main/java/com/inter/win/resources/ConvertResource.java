package com.inter.win.resources;

import com.inter.win.cache.CacheUtil;
import com.inter.win.collecter.ColumnCollector;
import com.inter.win.comparator.Comparator;
import com.inter.win.dto.ConvertDto;
import com.inter.win.netty.ChannelSupervise;
import com.inter.win.util.TypeUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/convert")
public class ConvertResource {
    @Resource
    private Comparator comparator;
    @Resource
    private ColumnCollector columnCollector;
    @PostMapping("/push")
    public Object push(@RequestBody ConvertDto params) throws InterruptedException {
        String frontId = params.getFrontId();
        Object o = CacheUtil.get(frontId);

        for(int i = 0; i < 100; i ++) {
            TextWebSocketFrame tws = new TextWebSocketFrame(o.toString());
            Thread.sleep(1000);
            ChannelSupervise.send2All(tws);
        }
        return null;
    }
    @PostMapping
    public Object convert(@RequestBody ConvertDto params) throws Exception {
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
        }

        Map<String, List<Map<String, Map<String, String>>>>[] columns = columnCollector.getColumns(targetArr, sourcesArr);
        List<List<String>> lists = comparator.compareColumns(columns[0], columns[1], targetAdapter);
        System.out.println(lists);
        return lists;
    }
}
