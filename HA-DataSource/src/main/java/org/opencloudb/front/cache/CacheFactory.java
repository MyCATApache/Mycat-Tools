package org.opencloudb.front.cache;

import java.util.concurrent.TimeUnit;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.opencloudb.front.cache.impl.MapDBCachePool;

public class CacheFactory {
	private static DB db = DBMaker.newMemoryDirectDB().cacheLRUEnable().make();
	private static final String DEFALUT_CACHE_NAME = "RouteResult";

	public static CachePool createCachePool() {
		HTreeMap<Object, Object> cache = db.createHashMap(DEFALUT_CACHE_NAME)
				.expireMaxSize(100000)
				.expireAfterAccess(1000, TimeUnit.SECONDS).makeOrGet();
		return new MapDBCachePool(cache, 100000);
	}
}
