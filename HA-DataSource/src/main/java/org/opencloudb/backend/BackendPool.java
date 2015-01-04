package org.opencloudb.backend;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.opencloudb.backend.core.PhysicalDatasource;

public class BackendPool {

	private ThreadLocal<Random> random;

	private BackendPool() {
		super();
		dbMap = new ConcurrentHashMap<String, PhysicalDatasource>();
		this.random = new ThreadLocal<Random>() {
			@Override
			protected Random initialValue() {
				return new Random();
			}
		};
	}

	private ConcurrentHashMap<String, PhysicalDatasource> dbMap;

	private static BackendPool instance = new BackendPool();

	public static BackendPool getInstance() {
		return instance;
	}

	public PhysicalDatasource getDataSouce(String dsName) {
		return dbMap.get(dsName);
	}

	public void putDataSouce(String dsName, PhysicalDatasource ds) {
		dbMap.putIfAbsent(dsName, ds);
	}

	public void removeDataSouce(String dsName) {
		dbMap.remove(dsName);
	}

	public boolean isAlive(String dsName) {
		return dbMap.containsKey(dsName);
	}

	public ConcurrentHashMap<String, PhysicalDatasource> getDbMap() {
		return dbMap;
	}

	public PhysicalDatasource getAlivePhysicalDatasource() {
		Collection<PhysicalDatasource> collection = BackendPool.getInstance()
				.getDbMap().values();
		PhysicalDatasource[] newArray = new PhysicalDatasource[collection
				.size()];
		collection.toArray(newArray);
		int randomNum = random.get().nextInt(collection.size());

		return newArray[randomNum];
	}

}
