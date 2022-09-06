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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;

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
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param 参数说明
	 * @return boolean    返回类型 
	 * @throws
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
}
