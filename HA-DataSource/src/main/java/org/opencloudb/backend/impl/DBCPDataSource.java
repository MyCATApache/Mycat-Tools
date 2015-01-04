package org.opencloudb.backend.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.opencloudb.backend.core.PhysicalDatasource;
import org.opencloudb.config.model.MycatHostConfig;

public class DBCPDataSource extends PhysicalDatasource {

	DataSource dataSource;

	public DBCPDataSource(MycatHostConfig config) {
		super(config);
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(classNmae);

		basicDataSource.setUrl(config.getUrl());
		basicDataSource.setUsername(config.getUsername());
		basicDataSource.setPassword(config.getPassword());

		basicDataSource.setInitialSize(config.getMinCon());
		basicDataSource.setMaxTotal(config.getMaxCon());
		this.dataSource = basicDataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}
