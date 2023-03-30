package top.yinxueqin.createtestlog;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import com.tencent.mars.BuildConfig;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    AppCompatButton btnInitLog;
    AppCompatButton btnCloseLog;
    AppCompatButton btnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInitLog = findViewById(R.id.btn_init_log);
        btnCloseLog = findViewById(R.id.btn_close_log);
        btnPrint = findViewById(R.id.btn_print_log);

        btnInitLog.setOnClickListener(onClickListener);
        btnCloseLog.setOnClickListener(onClickListener);
        btnPrint.setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = v -> {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_init_log:
                initLog();
                LogUtil.i(TAG, systemInfo(MainActivity.this));
                LogUtil.appenderFlush(false);
                break;
            case R.id.btn_close_log:
                LogUtil.appenderClose();
                break;
            case R.id.btn_print_log:
                LogUtil.v(TAG, "测试日志-v");
                LogUtil.d(TAG, "测试日志-d");
                LogUtil.i(TAG, "测试日志-i");
                LogUtil.w(TAG, "测试日志-w");
                LogUtil.e(TAG, "测试日志-e");
                LogUtil.appenderFlush(true);
                break;
            default:
                break;
        }
    };


    private void initLog() {
        //private key
        //05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef
        MarsXLogInit.getInstance().setDebugStatus(BuildConfig.DEBUG);
        MarsXLogInit.getInstance().setPUBKEY("94e62d97637f357fbd20f0c1f667a67c2f675e158e46015dd0cc54cb3995d0a5d468f7e98b20aec266effb61ec0a2321fb1f8c61af72bf76567921a0d8305005");
        MarsXLogInit.getInstance().setConsoleLogOpen(true);
        MarsXLogInit.getInstance().setLogFileNamePrefix("Release_" + System.currentTimeMillis());
        MarsXLogInit.getInstance().openXlog(this);
        LogUtil.logPrintInfo((priority, tag, message) -> {
            switch (priority) {
                case LEVEL_DEBUG:
                    Log.d(TAG, message);
                    break;
                case LEVEL_ERROR:
                    Log.e(TAG, message);
                    break;
                case LEVEL_INFO:
                    Log.i(TAG, message);
                    break;
                case LEVEL_WARNING:
                    Log.w(TAG, message);
                    break;
                case LEVEL_VERBOSE:
                default:
                    Log.v(TAG, message);
                    break;
            }
        });
    }


    private String systemInfo(Context context) {
        final StringBuilder sb = new StringBuilder();
        try {
            String appVersionName = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionName;
            long appVersionCode = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionCode;
            sb.append("APP.VESIONNAME:[").append(appVersionName);
            sb.append("] APP.VERSIONCODE:[").append(appVersionCode);
            sb.append("] VERSION.RELEASE:[").append(Build.VERSION.RELEASE);
            sb.append("] VERSION.CODENAME:[").append(Build.VERSION.CODENAME);
            sb.append("] VERSION.INCREMENTAL:[").append(Build.VERSION.INCREMENTAL);
            sb.append("] BOARD:[").append(Build.BOARD);
            sb.append("] DEVICE:[").append(Build.DEVICE);
            sb.append("] DISPLAY:[").append(Build.DISPLAY);
            sb.append("] FINGERPRINT:[").append(Build.FINGERPRINT);
            sb.append("] HOST:[").append(Build.HOST);
            sb.append("] MANUFACTURER:[").append(Build.MANUFACTURER);
            sb.append("] MODEL:[").append(Build.MODEL);
            sb.append("] PRODUCT:[").append(Build.PRODUCT);
            sb.append("] TAGS:[").append(Build.TAGS);
            sb.append("] TYPE:[").append(Build.TYPE);
            sb.append("] USER:[").append(Build.USER).append("]");
        } catch (Throwable e) {
            LogUtil.e(TAG, "异常信息:" + e.getMessage());
        }
        return sb.toString();
    }
}