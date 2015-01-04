package org.opencloudb.front.route;

import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opencloudb.BaseService;
import org.opencloudb.config.model.TableConfig;
import org.opencloudb.front.cache.CachePool;
import org.opencloudb.front.cache.CacheService;
import org.opencloudb.front.route.impl.DruidRouteStrategy;

public class RouteService {

	private CachePool cachePool;
	private RouteStrategy routeStrategy;

	public RouteService(CacheService cacheService) {
		this.cachePool = cacheService.getCachePool();
		this.routeStrategy = new DruidRouteStrategy();
	}

	public RouteResult route(String sqlStmt) throws SQLSyntaxErrorException {

		RouteResult rr = (RouteResult) cachePool.get(sqlStmt);

		if (rr != null) {
			return rr;
		}

		RouteResult newRr = new RouteResult();

		newRr.setStmt(sqlStmt);

		String tableName = removeBackquote(routeStrategy.route(sqlStmt));
		tableName = removeTableDot(tableName);

		newRr.setTartgetHost(getTartgetMycat(tableName));

		cachePool.putIfAbsent(sqlStmt, newRr);

		return newRr;
	}

	private String getTartgetMycat(String tableName)
			throws SQLSyntaxErrorException {
		String tartgetMycat = BaseService.getInstance().getTables()
				.get(tableName);

		if (tartgetMycat != null && !("".equals(tartgetMycat))) {
			return tartgetMycat;
		} else {
			// 表不存在。尝试正则。
			for (TableConfig config : BaseService.getInstance()
					.getRegularTables()) {
				Pattern pattern = Pattern.compile(config.getName());
				Matcher matcher = pattern.matcher(tableName);
				if (matcher.matches()) {
					return config.getMycatHost();
				}
			}
			throw new SQLSyntaxErrorException("can't find route for table: "
					+ tableName);
		}
	}

	/**
	 * 移除`符号
	 * 
	 * @param str
	 * @return
	 */
	public String removeBackquote(String str) {
		// 删除名字中的`tablename`和'value'
		if (str.length() > 0) {
			StringBuilder sb = new StringBuilder(str);
			if (sb.charAt(0) == '`' || sb.charAt(0) == '\'') {
				sb.deleteCharAt(0);
			}
			if (sb.charAt(sb.length() - 1) == '`'
					|| sb.charAt(sb.length() - 1) == '\'') {
				sb.deleteCharAt(sb.length() - 1);
			}

			return sb.toString();
		}
		return "";

	}

	private String removeTableDot(String tableName) {
		int index = tableName.indexOf(".");
		if (tableName.indexOf(".") != -1) {
			return tableName.substring(index + 1);
		}
		return tableName;
	}

}
