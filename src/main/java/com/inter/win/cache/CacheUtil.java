package com.inter.win.cache;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheUtil {
    private static CacheManager cacheManager;

    private static void close(Cache cache) {
        // cache.flush();
        cacheManager.shutdown();
    }

    public static void put(String key, Object value) {
        Cache cache = getCache();
        cache.put(new Element(key, value));
        // close(cache);
    }

    public static Object get(String key) {
        Cache cache = getCache();
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue();

    }

    public static void remove(String key) {
        Cache cache = getCache();
        cache.remove(key);
    }

    private static Cache getCache() {
        cacheManager = CacheManager.create("./src/main/resources/ehcache.xml");
        return cacheManager.getCache("socketCache");
    }

}
