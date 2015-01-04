package org.opencloudb.front.sql.error;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class SQLError {
	public static SQLException createSQLException(String message) {
		return new SQLException(message);
	}

	public static SQLException notImplemented() {
		return new SQLFeatureNotSupportedException();
	}
}
