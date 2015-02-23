package com.appserver.file.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.appserver.common.Constant;
import com.appserver.common.annotation.LogicHandler;
import com.appserver.common.network.MessageID;
import com.appserver.common.network.SBMessage;
import com.appserver.common.network.SBMessageFile;
import com.appserver.common.util.LangUtil;
import com.appserver.file.config.ServerConfig;
import com.appserver.file.helper.FileHelper;




@LogicHandler(id = MessageID.TEST, desc = "测试")
public class TestHandler extends ServerHandler {

	private static Logger logger = LogManager.getLogger(TestHandler.class);
	
	@Override
	public void logicProcess(SBMessage message) {
		try {
			logger.debug("-测试(file)-");
			logger.debug("-测试(file)-请求数据：" + message.getReq_data());
			String userSid = message.getReq_uid();
			if (LangUtil.isEmpty(userSid)) {
				message.send(Constant.BAD_ACCESS);
				return;
			}
			
			String uploadPath = ServerConfig.getInstance().getFileStorePath();
			List<SBMessageFile> files = message.getFiles();
			int size = files.size();
			logger.debug("-测试(file)- 开始处理文件");
	
			ArrayList<SBMessageFile> storedFiles = new ArrayList<SBMessageFile>();
			for (int i = 0; i < size; i++) {
				SBMessageFile f = files.get(i);
				String filePath = FileHelper.generateFilePath(MessageID.TEST, userSid, i + "");
				String storePath = uploadPath +  filePath;
				FileUtils.writeByteArrayToFile(new File(storePath), f.getBytes());
				logger.debug("-----文件" + (i + 1) + "-----");
				logger.debug("文件保存路径:" + storePath);
				logger.debug("文件类型:" + f.getFileType());
				logger.debug("文件名:" + f.getFileName());
				logger.debug("文件大小:" + f.getFileSize());
				logger.debug("写入成功");
				f.setFilePath(filePath);
				storedFiles.add(f);
			}
			message.setFiles(storedFiles);
			logger.debug("-测试(file)- 处理文件完成");
			logger.debug("-测试(file)- 转发至logic server");
			String result = logicResult(message);
			logger.debug("-测试(file)- logic响应:" + result);
			message.send(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("-测试(logic)-(文件上传) 失败:" + e.getLocalizedMessage());
		}
	}
	

}
