/**   
 * @Title: Utils.java 
 * @Package top.yinxueqin.adbinstall.ui 
 * @Description: TODO 
 * @author yin
 * @Email 19930108yin@gmail.com   
 * @date 2018年1月20日 下午8:36:46 
 * @version V1.0   
 */
package top.yinlingfeng.xlog.decode.ui.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import top.yinlingfeng.xlog.decode.core.log.LogUtil;

/** 
 * @ClassName: Utils 
 * @Description: TODO 
 * @author yin
 * @Email 19930108yin@gmail.com
 * @date 2018年1月20日 下午8:36:46 
 *  
 */
public class Utils {
	
	/**
	 * 判决是否存在文件
	 * @Title: judeFileExists 
	 * @Description: 判决是否存在文件
	 * @param filePath 文件路径
	 * @return boolean 是否存在
	 */
	public static boolean judeFileExists(String filePath) {
		if (filePath != null && filePath.length() > 0) {
			File adbFile = new File(filePath);
			if (adbFile.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/** 
     * 将字符串复制到剪切板。 
     */  
    public static void setSysClipboardText(String writeMe) {  
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
        Transferable tText = new StringSelection(writeMe);  
        clip.setContents(tText, null);  
    }

	public static ZipFile getZipFile(File unZipFile) throws ZipException {
		ZipFile zipFile = new ZipFile(unZipFile);
		zipFile.setCharset(StandardCharsets.UTF_8);
		List<FileHeader> headers = zipFile.getFileHeaders();
		if (Utils.isRandomCode(headers)) {
			try {
				zipFile.close();
			} catch (IOException e) {
				LogUtil.ei("ZIP文件存在异常！");
				LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
			}
			zipFile = new ZipFile(unZipFile);
			zipFile.setCharset(Charset.forName("GBK"));
		}
		return zipFile;
	}

	public static boolean isRandomCode(List<FileHeader> fileHeaders) {
		for (int i = 0; i < fileHeaders.size(); i++) {
			FileHeader fileHeader = fileHeaders.get(i);
			boolean canEnCode = Charset.forName("GBK").newEncoder().canEncode(fileHeader.getFileName());
			//canEnCode为true，表示不是乱码。false.表示乱码。是乱码则需要重新设置编码格式
			if (!canEnCode) {
				return true;
			}
		}
		return false;
	}
}
