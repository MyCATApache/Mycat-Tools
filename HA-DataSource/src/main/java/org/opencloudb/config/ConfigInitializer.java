/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opencloudb.backend.BackendPool;
import org.opencloudb.backend.core.PhysicalDatasource;
import org.opencloudb.backend.impl.DBCPDataSource;
import org.opencloudb.config.loader.ConfigLoader;
import org.opencloudb.config.loader.xml.XMLConfigLoader;
import org.opencloudb.config.model.MycatHostConfig;
import org.opencloudb.config.model.TableConfig;
import org.opencloudb.config.util.ConfigException;

/**
 * @author mycat
 */
public class ConfigInitializer {

	private Map<String, String> tables;

	private List<TableConfig> regularTables;

	public ConfigInitializer() {
		ConfigLoader configLoader = new XMLConfigLoader();

		tables = new HashMap<String, String>();

		regularTables = configLoader.getRegularTables();

		initTables(configLoader.getTables());

		checkConfig(configLoader);
		initMyCatHosts(configLoader.getMycatHosts());

		configLoader = null;
	}

	private void checkConfig(ConfigLoader configLoader) throws ConfigException {
		for (TableConfig table : configLoader.getTables()) {
			if (table == null) {
				continue;
			}

			MycatHostConfig hostConfig = configLoader.getMycatHosts().get(
					table.getMycatHost());

			if (hostConfig == null) {
				String errMsg = "table" + table.getName()
						+ "can not reference to a nonexistent mycatHost "
						+ table.getMycatHost();
				throw new ConfigException(errMsg);
			}
		}
	}

	private void initMyCatHosts(Map<String, MycatHostConfig> hostConfig) {
		for (Entry<String, MycatHostConfig> entry : hostConfig.entrySet()) {
			PhysicalDatasource physicalDatasource = new DBCPDataSource(
					entry.getValue());
			BackendPool.getInstance().putDataSouce(entry.getKey(),
					physicalDatasource);
		}
	}

	private void initTables(List<TableConfig> tableConfig) {
		for (TableConfig config : tableConfig) {
			this.tables.put(config.getName(), config.getMycatHost());
		}
	}

	public Map<String, String> getTables() {
		return tables;
	}

	public List<TableConfig> getRegularTables() {
		return regularTables;
	}

}