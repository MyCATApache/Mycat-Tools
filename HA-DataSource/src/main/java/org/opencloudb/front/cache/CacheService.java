package org.opencloudb.front.cache;

import org.apache.log4j.Logger;

public class CacheService {
	private static final Logger logger = Logger.getLogger(CacheService.class);

	private CachePool cachePool;

	public CacheService() {
		cachePool = CacheFactory.createCachePool();
	}

	/**
	 * get cache pool by name ,caller should cache result
	 * 
	 * @param poolName
	 * @return CachePool
	 */
	public CachePool getCachePool() {
		CachePool pool = this.cachePool;
		if (pool == null) {
			throw new IllegalArgumentException("can't find cache pool:");
		} else {
			return pool;
		}
	}

	public void clearCache() {
		logger.info("clear all cache pool ");
		cachePool.clearCache();
	}

}
