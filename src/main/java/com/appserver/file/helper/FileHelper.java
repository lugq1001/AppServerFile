package com.appserver.file.helper;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class FileHelper {

	public static String generateFilePath(int messageID, String userSid, String tag) {
		String sep = File.separator;

		String path = userSid + sep + messageID;
		
		//文件名生成规则: 时间秒数十六进制 + 零补齐10位用户id + 时间毫秒数十六进制 + tag
		long time = System.currentTimeMillis();
		// 取时间秒数十六进制
		String sHex = Long.toHexString(time);
		// 零补齐10位用户id
		String sid = StringUtils.leftPad(userSid, 10, "0");
		// 时间毫秒数十六进制
		String suffix = StringUtils.right(time + "", 3);
		String msHex = Integer.toHexString(Integer.parseInt(suffix));
		
		String fileName = sHex + sid + msHex + tag;
		return path + sep + fileName;
	}
	
}
