package top.yinlingfeng.xlog.decode.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.hgsoft.log.LogUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ini4j.Wini;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import top.yinlingfeng.xlog.decode.bean.IniSelectionEnum;
import top.yinlingfeng.xlog.decode.bean.PrivateKey;
import top.yinlingfeng.xlog.decode.constants.MMKVConstants;
import top.yinlingfeng.xlog.decode.utils.Event;
import top.yinlingfeng.xlog.decode.utils.ThreadPoolUtils;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/18 16:07
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class ImportPcConfigIniFileViewModel extends AndroidViewModel {

    public static final String TAG = "ImportPCConfigIniFile";

    /**
     * 导入密钥结果
     */
    private final MutableLiveData<Event<String>> _importPrivateKeyResult = new MutableLiveData<>();
    public final LiveData<Event<String>> importPrivateKeyResult = _importPrivateKeyResult;

    public ImportPcConfigIniFileViewModel(@NonNull Application application) {
        super(application);
    }

    public static final String EMPTY = "empty";

    public static final String FAIL = "fail";

    public static final String SUCCESS = "success";


    public void importConfigIni(InputStream inputStream, String fileName) {
        LogUtil.i(TAG, "开始导入密钥");
        ThreadPoolUtils.execute(() -> {
            try {
                LogUtil.i(TAG, "fileName：" + fileName);
                File externalFileDir = getApplication().getExternalFilesDir("config");
                if (externalFileDir != null) {
                    String configPath = externalFileDir.getAbsolutePath();
                    FileOutputStream fileOutputStream = new FileOutputStream(configPath + "/" + fileName);
                    byte[] buffer = new byte[2048];
                    while (inputStream.read(buffer) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    LogUtil.i(TAG, "复制文件完成");
                    String configFilePath = configPath + "/" + fileName;
                    importPrivateKey(configFilePath);
                }
            } catch (IOException e) {
                LogUtil.i("异常信息：" + ExceptionUtils.getStackTrace(e));
                _importPrivateKeyResult.postValue(new Event<>(FAIL));
            }
        });
    }

    private Wini getWini(String configFilePath) throws URISyntaxException, IOException {
        File tempIniFile = new File(configFilePath);
        return new Wini(tempIniFile);
    }

    private void importPrivateKey(String configFilePath) {
        try {
            ArrayList<PrivateKey> savePrivateKey = MMKVConstants.INSTANCE.getAllPrivateKey();
            Map<String, PrivateKey> savePrivateNameKey = new HashMap<>(savePrivateKey.size());
            for (PrivateKey data : savePrivateKey) {
                savePrivateNameKey.put(data.getName(), data);
            }
            Wini ini = getWini(configFilePath);
            if (ini.get(IniSelectionEnum.APP.getName()) == null) {
                _importPrivateKeyResult.postValue(new Event<>(EMPTY));
                return;
            }
            Set<String> tempSet = ini.get(IniSelectionEnum.APP.getName()).keySet();
            for (String name : tempSet) {
                if (!savePrivateNameKey.containsKey(name)) {
                    String privateKey = ini.get(IniSelectionEnum.PRIVATE_KEY.getName(), name, String.class);
                    savePrivateKey.add(new PrivateKey(name, privateKey));
                }
            }
            MMKVConstants.INSTANCE.setAllPrivateKey(savePrivateKey);
            _importPrivateKeyResult.postValue(new Event<>(SUCCESS));
        } catch (IOException | URISyntaxException e) {
            LogUtil.i("异常信息：" + ExceptionUtils.getStackTrace(e));
            _importPrivateKeyResult.postValue(new Event<>(FAIL));
        }
    }


}
