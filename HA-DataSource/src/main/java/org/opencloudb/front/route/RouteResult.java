package org.opencloudb.front.route;

import java.io.Serializable;

public class RouteResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String stmt;
	private String tartgetHost;

	public RouteResult() {
		super();
	}

	public String getStmt() {
		return stmt;
	}

	public void setStmt(String stmt) {
		this.stmt = stmt;
	}

	public String getTartgetHost() {
		return tartgetHost;
	}

	public void setTartgetHost(String tartgetHost) {
		this.tartgetHost = tartgetHost;
	}

}
