package org.opencloudb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencloudb.backend.BackendPool;
import org.opencloudb.backend.DBHeartBeat;
import org.opencloudb.backend.core.PhysicalDatasource;
import org.opencloudb.config.ConfigInitializer;
import org.opencloudb.config.model.TableConfig;
import org.opencloudb.front.cache.CacheService;
import org.opencloudb.front.route.RouteService;

public class BaseService {

	private static ConfigInitializer config = new ConfigInitializer();
	private static CacheService cacheService = new CacheService();
	private static RouteService routeService = new RouteService(cacheService);
	private static BaseService instance = new BaseService();

	private ScheduledExecutorService schedule;

	private BaseService() {

		this.schedule = Executors.newScheduledThreadPool(10);

		for (Entry<String, PhysicalDatasource> entry : BackendPool
				.getInstance().getDbMap().entrySet()) {

			// init dataSouce
			try {
				Connection conn = entry.getValue().getConnection();
				conn.close();
			} catch (SQLException e) {
				BackendPool.getInstance().removeDataSouce(
						entry.getValue().getName());
			}

			this.schedule.scheduleAtFixedRate(new DBHeartBeat(entry.getValue(),
					entry.getValue().getName(), entry.getValue().getTimeout()),
					0, entry.getValue().getTimeout(), TimeUnit.SECONDS);
		}
	}

	public static BaseService getInstance() {
		return instance;
	}

	public RouteService getRouteService() {
		return routeService;
	}

	public Map<String, String> getTables() {
		return config.getTables();
	}

	public List<TableConfig> getRegularTables() {
		return config.getRegularTables();
	}

}
