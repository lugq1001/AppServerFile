package com.appserver.file.handler;

import java.net.URI;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.appserver.common.network.SBMessage;
import com.appserver.common.util.StringBufferLine;
import com.appserver.file.config.LogicServer;
import com.appserver.file.config.ServerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 业务逻辑处理入口
 * @author Luguangqing
 *
 */
public abstract class ServerHandler {

	private static Logger logger = LogManager.getLogger(ServerHandler.class);
	
	protected ObjectMapper objMapper = new ObjectMapper();
	
	public abstract void logicProcess(SBMessage message);
	
	public void process(SBMessage message) {
		StringBufferLine logBuffer = new StringBufferLine();
		logBuffer.append("\n*************************** FileServerHandler process start *********************************************************");
		long time = System.currentTimeMillis();
		logBuffer.append("== reqid(" + message.getReq_id() + ") file process start ==");
		logicProcess(message);
		long interval = System.currentTimeMillis() - time;
		logBuffer.append("== reqid(" + message.getReq_id() + ") completed with file process in " + interval + " ms. ==");
		logBuffer.append("***************************** FileServerHandler process end *******************************************************");
		logger.info(logBuffer.toString());
	}
	

	protected String logicResult(SBMessage message) {
		String result = "";
		try {
			ObjectMapper objectMapper = new ObjectMapper();  
			String files = objectMapper.writeValueAsString(message.getFiles());
			LogicServer logic = ServerConfig.getInstance().getLogicServer();
			switch (message.getType()) {
			case Http:
				URIBuilder builder = new URIBuilder();
				builder.setParameter("rid", message.getReq_id() + "");
				builder.setParameter("data", message.getReq_data());
				builder.setParameter("uid", message.getReq_uid());
				builder.setParameter("files", files);
				builder.setParameter("tkey", logic.getTkey());
				builder.setScheme("http");
				builder.setHost(logic.getHost());
				builder.setPort(logic.getPort());
				builder.setPath(logic.getPath());
				URI uri = builder.build();
				HttpPost post = new HttpPost(uri);
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse resp = httpClient.execute(post);
				result = EntityUtils.toString(resp.getEntity());  
				resp.close();
				break;
			case WebSocket:
				break;
			}
			logger.debug("result from logic:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return result;
	}
	
}
