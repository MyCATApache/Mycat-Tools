package org.opencloudb.backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.opencloudb.backend.core.PhysicalDatasource;

public class DBHeartBeat implements Runnable {

	private static Logger LOGGER = Logger.getLogger(DBHeartBeat.class);
	private static final int MAX_RETRY_COUNT = 5;

	private volatile boolean closed;
	private volatile Connection conn;

	private final int timeout;
	private final PhysicalDatasource ds;
	private final String dataSourceName;
	private final AtomicBoolean fetching;
	private int errorCount;

	public DBHeartBeat(PhysicalDatasource ds, String name, int timeout) {
		this.ds = ds;
		this.closed = true;
		this.timeout = timeout;
		this.dataSourceName = name;
		this.fetching = new AtomicBoolean(false);
	}

	@Override
	public void run() {
		Connection newConn = null;
		if (closed) {
			try {
				newConn = ds.getConnection();
				trySendHeartBeat(newConn);

				conn = newConn;
				errorCount = 0;
				return;
			} catch (SQLException e) {
				closed = true;
				LOGGER.error("DataSource " + dataSourceName
						+ " create socket error - heart beat fail");

				BackendPool.getInstance().removeDataSouce(dataSourceName);
				return;
			}
		}

		trySendHeartBeat(conn);
	}

	private void trySendHeartBeat(Connection beatConn) {
		if (fetching.compareAndSet(false, true)) {
			try {
				Statement st = beatConn.createStatement();
				st.setQueryTimeout(timeout);

				if (!st.execute("select user()")) {
					LOGGER.error("DataSource " + dataSourceName
							+ " heart beat fail - timeout");
					++errorCount;
				}
				st.close();

				closed = false;
				BackendPool.getInstance().putDataSouce(dataSourceName, ds);
			} catch (SQLTimeoutException timee) {
				if (++errorCount < MAX_RETRY_COUNT) {
					LOGGER.error("DataSource "
							+ dataSourceName
							+ " send heartbeat error but enough, heart beat again");
				} else {
					BackendPool.getInstance().removeDataSouce(dataSourceName);
				}
			} catch (SQLException e) {
				LOGGER.error("DataSource " + dataSourceName
						+ " heart beat fail - connction error");
				closed = true;
				BackendPool.getInstance().removeDataSouce(dataSourceName);
			} finally {
				fetching.set(false);
			}
		}
	}
}
