package top.yinlingfeng.xlog.decode.ui.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/** 
 * @ClassName: ApkFileFilter 
 * @Description: TODO 
 * @author yin
 * @Email 19930108yin@gmail.com
 * @date 2018年5月9日 下午4:41:48 
 *  
 */
public class LogFileFilter extends FileFilter {

	
	@Override
	public boolean accept(File pathname) {
		String filename = pathname.getName().toLowerCase();
		File file = new File(filename);
		if (file.isDirectory()) {
			return true;
		} else {
			return filename.endsWith(".xlog") || filename.endsWith(".zip");
		}
	}

	@Override
	public String getDescription() {
		return "日志文件或日志文件压缩包";
	}


}
