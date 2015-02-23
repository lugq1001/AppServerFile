package com.appserver.file.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


/**
 * Server配置
 * @author Luguangqing
 *
 */
@JacksonXmlRootElement(localName = "logicServer")
public class LogicServer {
	
	@JacksonXmlProperty(localName = "host")
	private String host = "";
	
	@JacksonXmlProperty(localName = "port")
	private int port = 8080;
	
	@JacksonXmlProperty(localName = "path")
	private String path = "AppServer/";
	
	@JacksonXmlProperty(localName = "tkey")
	private String tkey = "";

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getTkey() {
		return tkey;
	}

	public void setTkey(String tkey) {
		this.tkey = tkey;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	

	
}
