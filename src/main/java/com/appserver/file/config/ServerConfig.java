package com.appserver.file.config;

import java.io.File;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


/**
 * Server配置
 * @author Luguangqing
 *
 */
@JacksonXmlRootElement(localName = "ServerConfig")
public class ServerConfig {

	private static Logger logger = LogManager.getLogger(ServerConfig.class);
	
	/*@JacksonXmlProperty(localName = "name")
	private String name = "";*/
	
	/*@JacksonXmlProperty(localName = "versionName")
	private String verName = "1.0";*/
	
	/*@JacksonXmlProperty(localName = "versionCode")
	private int verCode = 1;*/
	
	/*@JacksonXmlProperty(localName = "host")
	private String host = "";*/
	
	/*@JacksonXmlProperty(localName = "port")
	private int port = 8080;*/
	
	/*@JacksonXmlProperty(localName = "url")
	private String url = "AppServer/";*/

	@JacksonXmlProperty(localName = "fileStorePath")
	private String fileStorePath = "";
	
	@JacksonXmlProperty(localName = "magicKey")
	private String magicKey = "";
	
	@JacksonXmlProperty(localName = "maxFileSize")
	private long maxFileSize = 0;
	
	
	private LogicServer logicServer;
	
	private static ServerConfig instance = null;

	private ServerConfig() {
		
	}

	public static ServerConfig getInstance() {
		if (instance == null) {
			instance = loadConfig();
		}
		return instance;
	}

	private static ServerConfig loadConfig() {
		ServerConfig config = new ServerConfig();
		URL u = ServerConfig.class.getResource("/ServerConfig.xml");
		File xml = new File(u.getFile());
		if (xml == null || !xml.exists()) {
			logger.error("读取ServerConfig.xml失败: ServerConfig.xml文件不存在。");
			return config;
		} 
		try {
			logger.info("读取ServerConfig.xml:");
			XmlMapper mapper = new XmlMapper();
			config = mapper.readValue(xml, ServerConfig.class);
			logger.info("===========ServerConfig================");
			//logger.info("name:" + config.getName());
			//logger.info("verName:" + config.getVerName());
			//logger.info("verCode:" + config.getVerCode());
			//logger.info("host:" + config.getHost());
			//logger.info("port:" + config.getPort());
			//logger.info("url:" + config.getUrl());
			logger.info("fileStorePath:" + config.getFileStorePath());
			logger.info("magicKey:" + config.getMagicKey());
			logger.info("maxFileSize:" + config.getMaxFileSize());
			LogicServer logic = config.getLogicServer();
			logger.info("logicHost:" + logic.getHost());
			logger.info("logicPort:" + logic.getPort());
			logger.info("logicTkey:" + logic.getTkey());
			logger.info("logicPath:" + logic.getPath());
			logger.info("=======================================");
			logger.info("ServerConfig.xml 读取成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取ServerConfig.xml失败:" + e.getLocalizedMessage());
		}
		return config;
	}

	public String getFileStorePath() {
		return fileStorePath;
	}

	public void setFileStorePath(String fileStorePath) {
		this.fileStorePath = fileStorePath;
	}

	public LogicServer getLogicServer() {
		return logicServer;
	}

	public void setLogicServer(LogicServer logicServer) {
		this.logicServer = logicServer;
	}

	public String getMagicKey() {
		return magicKey;
	}

	public void setMagicKey(String magicKey) {
		this.magicKey = magicKey;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	
}
