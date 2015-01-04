package org.opencloudb.config.model;

public class TableConfig {
	private final String name;
	private final String mycatHost;

	public TableConfig(String name, String mycatHost) {
		this.name = name;
		this.mycatHost = mycatHost;
	}

	public String getName() {
		return name;
	}

	public String getMycatHost() {
		return mycatHost;
	}

}
