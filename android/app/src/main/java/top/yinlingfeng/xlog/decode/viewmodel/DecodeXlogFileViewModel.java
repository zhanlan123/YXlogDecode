package top.yinlingfeng.xlog.decode.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import top.yinlingfeng.xlog.decode.core.XLogFileDecode;
import top.yinlingfeng.xlog.decode.utils.Event;
import top.yinlingfeng.xlog.decode.utils.ThreadPoolUtils;
import com.hgsoft.log.LogUtil;



/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/18 15:27
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class DecodeXlogFileViewModel extends AndroidViewModel {

    private static final String TAG = "DecodeXlogFileViewModel";

    /**
     * 解密失败
     */
    private final MutableLiveData<Event<String>> _decodeXLogFail = new MutableLiveData<>();
    public final LiveData<Event<String>> decodeXLogFail = _decodeXLogFail;

    /**
     * 解密成功
     */
    private final MutableLiveData<Event<String>> _decodeXLogSuccess = new MutableLiveData<>();
    public final LiveData<Event<String>> decodeXLogSuccess = _decodeXLogSuccess;

    /**
     * 删除历史记录
     */
    private final MutableLiveData<Event<Boolean>> _delHistoryRecord = new MutableLiveData<>();
    public final LiveData<Event<Boolean>> delHistoryRecord = _delHistoryRecord;


    private final List<String> allLogFilesPath = new ArrayList<>();

    /**
     * 当前选择的密钥
     */
    private String privateKey = "";


    /**
     * 保存解密文件的路径
     */
    private String saveLogFilePath = "";

    public DecodeXlogFileViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 解密压缩的日志文件
     */
    public void decodeZipXlogFile(InputStream inputStream, String fileName, String privateKey) {
        this.privateKey = privateKey;
        ThreadPoolUtils.execute(() -> {
            try {
                allLogFilesPath.clear();
                LogUtil.i(TAG, "fileName：" + fileName);
                File externalFileDir = getApplication().getExternalFilesDir("decodeLog");
                if (externalFileDir != null) {
                    String decodeLogDirPath = externalFileDir.getAbsolutePath();
                    FileOutputStream fileOutputStream = new FileOutputStream(decodeLogDirPath + "/" + fileName);
                    byte[] buffer = new byte[2048];
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    LogUtil.i(TAG, "复制文件完成-开始解压");
                    //开始解压
                    String zipLogFilePath = decodeLogDirPath + "/" + fileName;
                    saveLogFilePath = decodeLogDirPath + File.separator + fileName.substring(0, fileName.lastIndexOf("."));
                    // 防止因为解压出错，但是实际解压成功，导致不能解密。
                    ZipFile zipFile = new ZipFile(zipLogFilePath);
                    zipFile.extractAll(saveLogFilePath);
                    LogUtil.i(TAG, "解压文件完成-开始获取所有需要解密的文件");
                    File saveFileDir = new File(saveLogFilePath);
                    if (saveFileDir.isDirectory()) {
                        getAllFilePath(saveFileDir);
                    } else {
                        _decodeXLogFail.postValue(new Event<>("解密失败,不存在解密文件夹！"));
                    }
                    if (allLogFilesPath.size() > 0) {
                        buildDataLogFileQueue(allLogFilesPath);
                    } else {
                        _decodeXLogFail.postValue(new Event<>("解密失败，没有需要解密的文件！"));
                    }
                }
            } catch (IOException e) {
                LogUtil.e("ZIP文件存在异常！");
                LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
                _decodeXLogFail.postValue(new Event<>("解密失败：ZIP文件存在异常！"));
            }

        });
    }

    /**
     * 直接解密日志文件
     */
    public void decodeXlogFile(InputStream inputStream, String fileName, String privateKey) {
        this.privateKey = privateKey;
        ThreadPoolUtils.execute(() -> {
            try {
                LogUtil.i(TAG, "fileName：" + fileName);
                File externalFileDir = getApplication().getExternalFilesDir("decodeLog");
                if (externalFileDir != null) {
                    String logPath = externalFileDir.getAbsolutePath();
                    FileOutputStream fileOutputStream = new FileOutputStream(logPath + "/" + fileName);
                    byte[] buffer = new byte[2048];
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    LogUtil.i(TAG, "复制文件完成");
                    String logFilePath = logPath + "/" + fileName;
                    saveLogFilePath = logPath + "/" + fileName + ".log";
                    LogUtil.i(TAG, "logFilePath:" + logFilePath);
                    LogUtil.i(TAG, "saveLogFilePath:" + saveLogFilePath);
                    XLogFileDecode.parseFile(logFilePath, saveLogFilePath, privateKey);
                    LogUtil.i(TAG, "解密文件完成");
                    _decodeXLogSuccess.postValue(new Event<>(saveLogFilePath));
                }
            } catch (Exception e) {
                LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
                _decodeXLogFail.postValue(new Event<>("解密失败！"));
            }
        });
    }

    public void copyFile(InputStream inputStream, String fileName) {
        ThreadPoolUtils.execute(() -> {
            try {
                LogUtil.i(TAG, "fileName：" + fileName);
                File externalFileDir = getApplication().getExternalFilesDir("decodeLog");
                if (externalFileDir != null) {
                    String logPath = externalFileDir.getAbsolutePath();
                    FileOutputStream fileOutputStream = new FileOutputStream(logPath + "/" + fileName);
                    byte[] buffer = new byte[2048];
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    LogUtil.i(TAG, "复制文件完成");
                    String logFilePath = logPath + "/" + fileName;
                    _decodeXLogSuccess.postValue(new Event<>(logFilePath));
                }
            } catch (Exception e) {
                LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
                _decodeXLogFail.postValue(new Event<>("复制失败！"));
            }
        });
    }

    private void getAllFilePath(File saveFileDir) {
        File[] fileArray = saveFileDir.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.isDirectory()) {
                    getAllFilePath(file);
                } else {
                    if (file.getAbsolutePath().endsWith("xlog")) {
                        LogUtil.i("增加需要解密的日志文件：" + file.getName());
                        allLogFilesPath.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private LinkedBlockingQueue<String> logFileQueue;

    private void buildDataLogFileQueue(List<String> logFiles) throws IOException {
        logFileQueue  = new LinkedBlockingQueue<>(logFiles.size());
        for (String logFilePath : logFiles) {
            logFileQueue.offer(logFilePath);
        }
        pollingDecode();
    }

    private void pollingDecode() throws IOException {
        if (logFileQueue != null) {
            String logFileData = logFileQueue.poll();
            if (logFileData != null) {
                startDecodeLogFile(logFileData);
            } else {
                LogUtil.i(TAG, "解密文件完成");
                _decodeXLogSuccess.postValue(new Event<>(saveLogFilePath));
            }
        } else {
            LogUtil.i(TAG, "解密文件完成");
            _decodeXLogSuccess.postValue(new Event<>(saveLogFilePath));
        }
    }

    private void startDecodeLogFile(String logFilePath) throws IOException {
        LogUtil.i(TAG, "开始解密：" + logFilePath);
        String outFilePath = logFilePath + ".log";
        LogUtil.i(TAG, "logFilePath:" + logFilePath);
        LogUtil.i(TAG, "outFilePath:" + outFilePath);
        LogUtil.i(TAG, "privateKey:" + privateKey);
        XLogFileDecode.parseFile(logFilePath, outFilePath, privateKey);
        pollingDecode();
    }

    /**
     * 删除历史记录
     */
    public void delHistoryRecord() {
        ThreadPoolUtils.execute(() -> {
            try {
                File externalFileDir = getApplication().getExternalFilesDir("decodeLog");
                if (externalFileDir == null) {
                    _delHistoryRecord.postValue(new Event<>(false));
                    return;
                }
                if (!externalFileDir.exists()) {
                    _delHistoryRecord.postValue(new Event<>(false));
                    return;
                }
                deleteAllFile(externalFileDir);
                _delHistoryRecord.postValue(new Event<>(true));
            } catch (Exception e) {
                LogUtil.e("异常信息：" + ExceptionUtils.getStackTrace(e));
                _decodeXLogFail.postValue(new Event<>("复制失败！"));
            }
        });
    }

    private void deleteAllFile(File file) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteAllFile(temp);
            }
        }
        boolean result = file.delete();
        LogUtil.i(TAG, file.getName() + "->删除结果:" + result);
    }

}
