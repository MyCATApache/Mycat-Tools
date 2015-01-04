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
package org.opencloudb.config.loader.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencloudb.config.loader.ConfigLoader;
import org.opencloudb.config.model.MycatHostConfig;
import org.opencloudb.config.model.TableConfig;
import org.opencloudb.config.util.ConfigException;
import org.opencloudb.config.util.ConfigUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author mycat
 */
public class XMLConfigLoader implements ConfigLoader {
	private final static String DEFAULT_DTD = "/schema.dtd";
	private final static String DEFAULT_XML = "/schema.xml";

	private final Map<String, MycatHostConfig> mycatHosts;
	private final List<TableConfig> tables;
	private final List<TableConfig> regularTables;

	public XMLConfigLoader(String schemaFile, String ruleFile) {

		this.tables = new LinkedList<>();
		this.regularTables = new LinkedList<>();
		this.mycatHosts = new HashMap<>();

		this.load(DEFAULT_DTD, schemaFile == null ? DEFAULT_XML : schemaFile);
	}

	public XMLConfigLoader() {
		this(null, null);
	}

	@Override
	public Map<String, MycatHostConfig> getMycatHosts() {
		return mycatHosts.isEmpty() ? Collections.emptyMap() : mycatHosts;
	}

	@Override
	public List<TableConfig> getTables() {
		return tables.isEmpty() ? Collections.emptyList() : tables;
	}

	private void load(String dtdFile, String xmlFile) {
		InputStream dtd = null;
		InputStream xml = null;
		try {
			dtd = XMLConfigLoader.class.getResourceAsStream(dtdFile);
			xml = XMLConfigLoader.class.getResourceAsStream(xmlFile);
			Element root = ConfigUtil.getDocument(dtd, xml)
					.getDocumentElement();

			loadHosts(root);
			loadTables(root);
		} catch (ConfigException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ConfigException(e);
		} finally {
			if (dtd != null) {
				try {
					dtd.close();
				} catch (IOException e) {
				}
			}
			if (xml != null) {
				try {
					xml.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void loadHosts(Element root) {
		NodeList list = root.getElementsByTagName("mycatHost");
		for (int i = 0, n = list.getLength(); i < n; i++) {
			Element schemaElement = (Element) list.item(i);

			String name = schemaElement.getAttribute("name");

			String url = schemaElement.getElementsByTagName("url").item(0)
					.getTextContent();

			String username = schemaElement.getElementsByTagName("username")
					.item(0).getTextContent();

			String password = schemaElement.getElementsByTagName("password")
					.item(0).getTextContent();

			String heartbeatSQL = schemaElement
					.getElementsByTagName("heartbeat").item(0).getTextContent();

			int minCon = Integer.valueOf(schemaElement
					.getElementsByTagName("minCon").item(0).getTextContent());

			int maxCon = Integer.valueOf(schemaElement
					.getElementsByTagName("maxCon").item(0).getTextContent());

			int timeout = Integer.valueOf(schemaElement
					.getElementsByTagName("timeout").item(0).getTextContent());

			MycatHostConfig mycatConf = new MycatHostConfig(name);
			mycatConf.setMaxCon(maxCon);
			mycatConf.setMinCon(minCon);
			mycatConf.setUsername(username);
			mycatConf.setPassword(password);
			mycatConf.setHeartbeatSQL(heartbeatSQL);
			mycatConf.setUrl(url);
			mycatConf.setTimeout(timeout);

			mycatHosts.put(mycatConf.getName(), mycatConf);
		}
	}

	private void loadTables(Element root) {
		Node tablesNode = root.getElementsByTagName("tables").item(0);
		NodeList nodeList = tablesNode.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node theNode = nodeList.item(i);

			if (theNode.getNodeName().equals("table")) {

				Element tableElement = (Element) theNode;
				String name = tableElement.getAttribute("name");
				String mycatHost = tableElement.getAttribute("mycatHost");
				TableConfig config = new TableConfig(name, mycatHost);

				tables.add(config);
				continue;
			} else if (theNode.getNodeName().equals("rugularTable")) {
				processrgularTables(theNode);
			} else {
				continue;
			}
		}

	}

	private void processrgularTables(Node theNode) {
		NodeList regularTag = theNode.getChildNodes();

		for (int i = 0; i < regularTag.getLength(); i++) {
			Node regularTable = regularTag.item(i);

			if (regularTable.getNodeName().equals("table")) {
				Element tableElement = (Element) regularTable;
				String name = tableElement.getAttribute("name");
				String mycatHost = tableElement.getAttribute("mycatHost");
				TableConfig config = new TableConfig(name, mycatHost);

				regularTables.add(config);
			} else {
				continue;
			}
		}
	}

	public List<TableConfig> getRegularTables() {
		return regularTables.isEmpty() ? Collections.emptyList()
				: regularTables;
	}

}