package org.opencloudb.front.route;

import java.sql.SQLSyntaxErrorException;

import org.opencloudb.front.cache.CachePool;

public interface RouteStrategy {

	String route(String sqlStmt) throws SQLSyntaxErrorException;
}
