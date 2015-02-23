package com.appserver.file.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.appserver.common.Constant;
import com.appserver.common.network.SBMessage;
import com.appserver.common.network.SBMessageFile;
import com.appserver.common.util.LangUtil;
import com.appserver.common.util.MD5Util;
import com.appserver.file.config.ServerConfig;
import com.appserver.file.handler.ServerHandler;
import com.appserver.file.helper.AnnotationManager;


@WebServlet(name = "FileServlet", urlPatterns = "/main")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 30) 
public class FileServlet extends HttpServlet {

	private static Logger logger = LogManager.getLogger(FileServlet.class);
	private static final long serialVersionUID = 1L;
	
	// 限制访问频率 500次每分钟
	private static Map<String, Integer> reqFrequencyMap = new HashMap<String, Integer>();
	private static final int MAX_ACCESS = 500;
	private static Timer clearTimer = new Timer();
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		SBMessage message = new SBMessage(resp);
		String tkey = req.getParameter("tkey");
		try {
			if (LangUtil.isEmpty(tkey)) {
				// 客户端请求，频率拦截
				if (!accessFrequencyFilter(req.getRemoteAddr())) {
					resp.getWriter().write(Constant.BAD_ACCESS);
					return;
				}
			} 
			String reqidStr = null;
			String data = null;
			String uid = null;
			String v = null;
			Collection<Part> parts = req.getParts();
			ArrayList<SBMessageFile> files = new ArrayList<SBMessageFile>();
			StringBuffer verifyStr = new StringBuffer();
			for (Part p : parts) {
				String name = p.getName();
				if (name.equals("rid")) {
					reqidStr = IOUtils.toString(p.getInputStream());
				} else if (name.equals("data")) {
					data = IOUtils.toString(p.getInputStream());
				} else if (name.equals("uid")) {
					uid = IOUtils.toString(p.getInputStream());
				} else if (name.equals("v")) {
					v = IOUtils.toString(p.getInputStream());
				} else {
					long maxSize = ServerConfig.getInstance().getMaxFileSize();
					if (p.getSize() > maxSize) {
						// 无效请求
						message.send(Constant.BAD_ACCESS);
						return;
					}
					String type = p.getContentType();
					long size = p.getSize();
					String fileName = p.getSubmittedFileName();
					SBMessageFile file = new SBMessageFile();
					file.setBytes(IOUtils.toByteArray(p.getInputStream()));
					file.setFileName(name);
					file.setFileSize(size);
					file.setFileType(type);
					files.add(file);
					verifyStr.append(name);
					logger.debug("接收文件：" + fileName + " size:" + size + " type:" + type);
				}
			}
			
			if (LangUtil.isEmpty(data) || LangUtil.isEmpty(reqidStr) || LangUtil.isEmpty(files) || LangUtil.isEmpty(uid)) {
				logger.debug("请求失败:参数错误");
				message.send(Constant.BAD_ACCESS);
				return;
			}
			String magicKey = ServerConfig.getInstance().getMagicKey();
			String verify = MD5Util.md5(verifyStr + magicKey);
			if (!verify.equals(v)) {
				logger.debug("请求失败:参数错误");
				message.send(Constant.BAD_ACCESS);
				return;
			}
			
			int reqid = Integer.parseInt(reqidStr);
			message.setFiles(files);
			message.setReq_data(data);
			message.setReq_id(reqid);
			message.setReq_uid(uid);
			// 传递给handler处理业务逻辑
			ServerHandler handler = AnnotationManager.createLogicHandlerInstance(reqid);
			if (handler != null) {
				handler.process(message);
			} else {
				// 无效请求
				logger.debug("请求失败:参数错误");
				message.send(Constant.BAD_ACCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求失败:" + e.getMessage());
			message.send(Constant.BAD_ACCESS);
		}
	}
	
	private boolean accessFrequencyFilter(String ip) {
		if (LangUtil.isEmpty(ip))
			return true;
		Integer count = reqFrequencyMap.get(ip);
		if (count == null) {
			reqFrequencyMap.put(ip, 1);
		} else {
			if (count >= MAX_ACCESS) {
				return false;
			} else {
				reqFrequencyMap.put(ip, ++count);
			}
		}
		return true;
	}
	
	static {
		clearTimer.schedule(new ClearTask(), 0, 1000 * 60);
	}
	
	static class ClearTask extends TimerTask {

		public void run() {
			reqFrequencyMap.clear();
		}
	}

}
