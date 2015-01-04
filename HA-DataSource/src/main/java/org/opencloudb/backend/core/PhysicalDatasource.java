package org.opencloudb.backend.core;

import java.sql.Connection;
import java.sql.SQLException;

import org.opencloudb.config.model.MycatHostConfig;

public abstract class PhysicalDatasource {

	protected static final String classNmae = "com.mysql.jdbc.Driver";

	private final String name;
	private final String heartbeatSQL;
	private final int timeout;

	public PhysicalDatasource(MycatHostConfig config) {
		this.name = config.getName();
		this.timeout = config.getTimeout();
		this.heartbeatSQL = config.getHeartbeatSQL();
	}

	public String getName() {
		return name;
	}

	public static String getClassnmae() {
		return classNmae;
	}

	public String getHeartbeatSQL() {
		return heartbeatSQL;
	}

	public int getTimeout() {
		return timeout;
	}

	abstract public Connection getConnection() throws SQLException;

}
