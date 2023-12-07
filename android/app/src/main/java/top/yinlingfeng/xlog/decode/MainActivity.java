package top.yinlingfeng.xlog.decode;

import static top.yinlingfeng.xlog.decode.viewmodel.ImportPcConfigIniFileViewModel.FAIL;
import static top.yinlingfeng.xlog.decode.viewmodel.ImportPcConfigIniFileViewModel.SUCCESS;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.hgsoft.log.LogUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import top.yinlingfeng.xlog.decode.bean.PrivateKey;
import top.yinlingfeng.xlog.decode.constants.MMKVConstants;
import top.yinlingfeng.xlog.decode.core.log.LogInfo;
import top.yinlingfeng.xlog.decode.core.log.LogLevel;
import top.yinlingfeng.xlog.decode.databinding.ActivityMainBinding;
import top.yinlingfeng.xlog.decode.dialog.LoadingDialog;
import top.yinlingfeng.xlog.decode.dialog.ManagerKeyDialog;
import top.yinlingfeng.xlog.decode.dialog.SelectKeyDialog;
import top.yinlingfeng.xlog.decode.filetree.FileTreeFragment;
import top.yinlingfeng.xlog.decode.setting.AddPrivateKeyActivity;
import top.yinlingfeng.xlog.decode.setting.ManagerPrivateKeyActivity;
import top.yinlingfeng.xlog.decode.setting.SettingActivity;
import top.yinlingfeng.xlog.decode.utils.MainThreadExecutor;
import top.yinlingfeng.xlog.decode.viewmodel.DecodeXlogFileViewModel;
import top.yinlingfeng.xlog.decode.viewmodel.ImportPcConfigIniFileViewModel;


/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/3 17:47
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String STREAM = "application/octet-stream";

    private static final String ZIP = "application/zip";

    ActivityResultLauncher<Intent> openFileStateResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Uri uri = intent.getData();
                        if (uri != null) {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            );
                            startOperationFile(uri);
                        }
                    }
                }
            });

    private ActivityMainBinding binding;

    private DecodeXlogFileViewModel decodeXlogFileViewModel;

    private ImportPcConfigIniFileViewModel importPcConfigIniFileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setXDecodeCoreLog();
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        decodeXlogFileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DecodeXlogFileViewModel.class);
        importPcConfigIniFileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImportPcConfigIniFileViewModel.class);
        initView();
        setClickListener();
        dealWithActionSend(getIntent());
    }

    private void setXDecodeCoreLog() {
        top.yinlingfeng.xlog.decode.core.log.LogUtil.setmLogInfo(new LogInfo() {
            @Override
            public void log(LogLevel priority, String tag, String message) {
                switch (priority) {
                    case LEVEL_DEBUG:
                        LogUtil.d(tag, message);
                        break;
                    case LEVEL_INFO:
                        LogUtil.i(tag, message);
                        break;
                    case LEVEL_WARNING:
                        LogUtil.w(tag, message);
                        break;
                    case LEVEL_ERROR:
                    case LEVEL_EXTRA:
                        LogUtil.e(tag, message);
                        break;
                    case LEVEL_VERBOSE:
                        LogUtil.v(tag, message);
                        break;
                }
            }
        });
    }

    private void initView() {
        PrivateKey currentPrivateKey = MMKVConstants.INSTANCE.getCurrentSelectPrivateKey();
        if (currentPrivateKey.isEffective()) {
            String currentSelectPrivateKeyHint = getString(R.string.current_private_key_hint);
            currentSelectPrivateKeyHint = String.format(currentSelectPrivateKeyHint, currentPrivateKey);
            binding.tvCurrentPrivateKey.setText(currentSelectPrivateKeyHint);
        }
    }

    private void setClickListener() {
        binding.clManagerPrivateKey.setOnClickListener(view -> {
            ManagerKeyDialog.show(MainActivity.this, v -> {
                LogUtil.i(TAG, "导入密钥");
                ManagerKeyDialog.dismiss();
                openFileStateResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*"));
            }, v -> {
                ManagerKeyDialog.dismiss();
                startActivity(new Intent(MainActivity.this, AddPrivateKeyActivity.class));
            }, v -> {
                ManagerKeyDialog.dismiss();
                startActivity(new Intent(MainActivity.this, ManagerPrivateKeyActivity.class));
            });
        });
        binding.clSelectPrivateKey.setOnClickListener(view -> {
            SelectKeyDialog.show(MainActivity.this, MMKVConstants.INSTANCE.getAllPrivateKey(),
                    keyData -> {
                        LogUtil.i(TAG, "选中密钥:" + keyData);
                        MMKVConstants.INSTANCE.setCurrentSelectPrivateKey(keyData);
                        initView();
                    });
        });
        binding.clOpenFile.setOnClickListener(view -> {
            openFileStateResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*"));
        });
        binding.clOpenLogDir.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, DisplayLogFileActivity.class));
        });
        binding.clCleanHistory.setOnClickListener(view -> {
            delHistoryRecord();
        });
        binding.clSetting.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i(TAG, "onNewIntent：" + intent.toString());
        decodeXlogFileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DecodeXlogFileViewModel.class);
        importPcConfigIniFileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImportPcConfigIniFileViewModel.class);
        dealWithActionSend(intent);
    }

    /**
     * 处理分享文件
     * @param intent 传入的分享文件Intent
     */
    private void dealWithActionSend(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction()) && !TextUtils.isEmpty(intent.getType())) {
            ShareCompat.IntentReader intentReader = new ShareCompat.IntentReader(this, intent);
            Uri xlogFileUri = intentReader.getStream();
            startOperationFile(xlogFileUri);
            return;
        }
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && !TextUtils.isEmpty(intent.getType())) {
            Uri xlogFileUri = intent.getData();
            startOperationFileTwo(xlogFileUri);
        }
    }

    public static final String X_LOG_FILE_SUFFIX = ".xlog";

    public static final String INI_FILE_SUFFIX = ".ini";

    public static final String ZIP_FILE_SUFFIX = ".zip";

    /**
     * 开发处理文件
     */
    private void startOperationFile(Uri xlogFileUri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(xlogFileUri);
            File file = UriUtils.uri2File(xlogFileUri);
            String fileName = file.getName();
            LogUtil.i(TAG, "startOperationFile：" + fileName);
            if (fileName.endsWith(X_LOG_FILE_SUFFIX)) {
                startOperationLogFile(inputStream, fileName);
            } else if (fileName.endsWith(INI_FILE_SUFFIX)) {
                importConfigIni(inputStream, fileName);
            } else if (fileName.endsWith(ZIP_FILE_SUFFIX)) {
                startOperationLogFile(inputStream, fileName);
            } else {
                copyFile(inputStream, fileName);
            }
        } catch (FileNotFoundException e) {
            LogUtil.i("异常信息：" + ExceptionUtils.getStackTrace(e));
            ToastUtils.showLong(getString(R.string.analysis_file_fail));
        }
    }

    private void startOperationFileTwo(Uri xlogFileUri) {
        InputStream inputStream = null;
        try {
            Cursor cursor = getContentResolver().query(xlogFileUri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = cursor.getString(nameIndex);
                LogUtil.i(TAG, "startOperationFileTwo：" + fileName);
                inputStream = getContentResolver().openInputStream(xlogFileUri);
                if (fileName.endsWith(X_LOG_FILE_SUFFIX)) {
                    startOperationLogFile(inputStream, fileName);
                } else if (fileName.endsWith(INI_FILE_SUFFIX)) {
                    importConfigIni(inputStream, fileName);
                } else if (fileName.endsWith(ZIP_FILE_SUFFIX)) {
                    startOperationLogFile(inputStream, fileName);
                } else {
                    copyFile(inputStream, fileName);
                }
            }
        } catch (FileNotFoundException e) {
            LogUtil.i("异常信息：" + ExceptionUtils.getStackTrace(e));
            ToastUtils.showLong(getString(R.string.analysis_file_fail));
        }
    }

    /**
     * 处理日志文件
     */
    private void startOperationLogFile(InputStream inputStream, String fileName) {
        PrivateKey currentPrivateKey = MMKVConstants.INSTANCE.getCurrentSelectPrivateKey();
        if (currentPrivateKey.isEffective() && MMKVConstants.INSTANCE.getSelectDefaultPrivateKey()) {
            if (fileName.endsWith(".zip")) {
                startUnZipDecodeFile(inputStream, fileName);
            } else {
               startDecodeFile(inputStream, fileName);
            }
        } else {
            SelectKeyDialog.show(MainActivity.this, MMKVConstants.INSTANCE.getAllPrivateKey(),
                    keyData -> {
                        LogUtil.i(TAG, "选中密钥:" + keyData);
                        MMKVConstants.INSTANCE.setCurrentSelectPrivateKey(keyData);
                        initView();
                        if (fileName.endsWith(".zip")) {
                            startUnZipDecodeFile(inputStream, fileName);
                        } else {
                            startDecodeFile(inputStream, fileName);
                        }
                    }
            );
        }
    }

    private void startUnZipDecodeFile(InputStream inputStream, String fileName) {
        decodeXlogFileViewModel.decodeXLogFail.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.decodeXLogFail.observe(MainActivity.this, stringEvent -> {
            LogUtil.i(TAG, "解密失败");
            LoadingDialog.dismiss();
            ToastUtils.showLong(stringEvent.peekContent());
        });
        decodeXlogFileViewModel.decodeXLogSuccess.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.decodeXLogSuccess.observe(MainActivity.this, stringEvent -> {
            String result = stringEvent.peekContent();
            LogUtil.i(TAG, "解密成功：" + result);
            LoadingDialog.dismiss();
            ToastUtils.showLong(result);
            LogUtil.i(TAG, "开始查看");
            Intent showLogIntent = new Intent(MainActivity.this, DisplayLogFileActivity.class);
            showLogIntent.putExtra(FileTreeFragment.FILE_PATH, result);
            startActivity(showLogIntent);
        });
        MainThreadExecutor.getInstance().execute(() -> LoadingDialog.show(MainActivity.this));
        decodeXlogFileViewModel.decodeZipXlogFile(inputStream, fileName, MMKVConstants.INSTANCE.getCurrentSelectPrivateKey().getKey());
    }

    private void startDecodeFile(InputStream inputStream, String fileName) {
        decodeXlogFileViewModel.decodeXLogFail.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.decodeXLogFail.observe(MainActivity.this, stringEvent -> {
            LogUtil.i(TAG, "解密失败");
            LoadingDialog.dismiss();
            ToastUtils.showLong(stringEvent.peekContent());
        });
        decodeXlogFileViewModel.decodeXLogSuccess.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.decodeXLogSuccess.observe(MainActivity.this, stringEvent -> {
            String result = stringEvent.peekContent();
            LogUtil.i(TAG, "解密成功：" + result);
            LoadingDialog.dismiss();
            ToastUtils.showLong(result);
            LogUtil.i(TAG, "开始查看");
            Intent showLogIntent = new Intent(MainActivity.this, DisplayLogFileActivity.class);
            showLogIntent.putExtra(FileTreeFragment.FILE_PATH, result);
            startActivity(showLogIntent);
        });
        MainThreadExecutor.getInstance().execute(() -> LoadingDialog.show(MainActivity.this));
        decodeXlogFileViewModel.decodeXlogFile(inputStream, fileName, MMKVConstants.INSTANCE.getCurrentSelectPrivateKey().getKey());
    }

    private void importConfigIni(InputStream inputStream, String fileName) {
        importPcConfigIniFileViewModel.importPrivateKeyResult.removeObservers(MainActivity.this);
        importPcConfigIniFileViewModel.importPrivateKeyResult.observe(MainActivity.this, stringEvent -> {
            String result = stringEvent.peekContent();
            LoadingDialog.dismiss();
            switch (result) {
                case FAIL:
                    ToastUtils.showLong(getString(R.string.import_private_key_fail));
                    break;
                case SUCCESS:
                    ToastUtils.showLong(getString(R.string.import_private_key_success));
                    break;
                default:
                    ToastUtils.showLong(getString(R.string.import_private_key_empty));
                    break;
            }
        });
        MainThreadExecutor.getInstance().execute(() -> LoadingDialog.show(MainActivity.this));
        importPcConfigIniFileViewModel.importConfigIni(inputStream, fileName);
    }

    private void copyFile(InputStream inputStream, String fileName) {
        decodeXlogFileViewModel.decodeXLogFail.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.decodeXLogFail.observe(MainActivity.this, stringEvent -> {
            LogUtil.i(TAG, "复制失败");
            LoadingDialog.dismiss();
            ToastUtils.showLong(stringEvent.peekContent());
        });
        decodeXlogFileViewModel.decodeXLogSuccess.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.decodeXLogSuccess.observe(MainActivity.this, stringEvent -> {
            String result = stringEvent.peekContent();
            LogUtil.i(TAG, "复制成功：" + result);
            LoadingDialog.dismiss();
            ToastUtils.showLong(result);
            LogUtil.i(TAG, "开始查看");
            Intent showLogIntent = new Intent(MainActivity.this, DisplayLogFileActivity.class);
            showLogIntent.putExtra(FileTreeFragment.FILE_PATH, result);
            startActivity(showLogIntent);
        });
        MainThreadExecutor.getInstance().execute(() -> LoadingDialog.show(MainActivity.this));
        decodeXlogFileViewModel.copyFile(inputStream, fileName);
    }

    private void delHistoryRecord() {
        decodeXlogFileViewModel.delHistoryRecord.removeObservers(MainActivity.this);
        decodeXlogFileViewModel.delHistoryRecord.observe(MainActivity.this, booleanEvent -> {
            LoadingDialog.dismiss();
            boolean result = booleanEvent.peekContent();
            LogUtil.i(TAG, "删除结果:" + result);
            ToastUtils.showLong(getString(R.string.del_history_record) + ":" + result);
        });
        MainThreadExecutor.getInstance().execute(() -> LoadingDialog.show(MainActivity.this));
        decodeXlogFileViewModel.delHistoryRecord();
    }
}
