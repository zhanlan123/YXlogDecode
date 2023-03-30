package top.yinxueqin.createtestlog;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import java.io.File;

/**
 * 打印日志初始化设置
 *
 * @author  yinxueqin
 */
public final class MarsXLogInit {

    private static volatile MarsXLogInit INSTANCE;
    //加密公钥
    private String PUB_KEY = "";
    //设置日志打开状态，如果日志需要写入不同的文件需要先设置关闭。
    private boolean xlogOpenStatus = false;
    //设置日志文件的前缀
    private String prefix = "log";
    //日志是否控制台打印
    private boolean consoleLogOpen = false;
    // 设置是否是调试模式，调试模式会把所有日志写入文件，不是调试模式，只会写入大于INFO级别的日志
    private boolean debugStatus = false;
    //文件保存天数，这个保存天数根据文件创建属性决定。最小1天，默认10天
    private int logFileSaveDays = 0;
    //单个文件最大字节 0：表示不分割 单位字节
    private long logFileMaxSize = 0;

    private MarsXLogInit(){
    }

    public static MarsXLogInit getInstance() {
        if (INSTANCE == null) {
            synchronized (MarsXLogInit.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MarsXLogInit();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 设置日志打开状态，如果日志需要写入不同的文件需要先设置关闭。
     *
     * @param xlogOpenStatus true:已经打开，false:关闭
     */
    public void setXlogOpenStatus(final boolean xlogOpenStatus) {
        this.xlogOpenStatus = xlogOpenStatus;
        Log.appenderFlush();
        Log.appenderClose();
    }

    /**
     * 设置日志文件加密的公钥
     *
     * @param pubkey 公钥字符串
     */
    public void setPUBKEY(final String pubkey) {
        PUB_KEY = pubkey;
    }

    /**
     * 设置日志文件的前缀
     *
     * @param prefix 日志文件前缀
     */
    public void setLogFileNamePrefix(final String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取日志文件前缀
     *
     * @return 日志文件前缀
     */
    public String getLogFileNamePrefix() {
        return prefix;
    }

    /**
     * 设置控制台是否打印
     *
     * @param consoleLogOpen true:打印，false:不打印
     */
    public void setConsoleLogOpen(final boolean consoleLogOpen) {
        this.consoleLogOpen = consoleLogOpen;
    }

    /**
     * 是否是调试模式
     * @return 是否是调试模式
     */
    public boolean isDebugStatus() {
        return debugStatus;
    }

    /**
     * 设置是否是调试模式，调试模式会把所有日志写入文件，不是调试模式，只会写入大于INFO级别的日志
     *
     * @param debugStatus true:是，false:否
     */
    public void setDebugStatus(final boolean debugStatus) {
        this.debugStatus = debugStatus;
    }

    /**
     * 设置文件保存天数
     * @param logFileSaveDays 0：表示默认10天，其它表示其它天数，最小不能小于1
     */
    public void setLogFileSaveDays(final int logFileSaveDays) {
        this.logFileSaveDays = logFileSaveDays;
    }

    /**
     * 获取文件缓存天数
     * @return 缓存天数
     */
    public int getLogFileSaveDays() {
        return logFileSaveDays;
    }

    /**
     * 获取单个文件最大字节，0：表示不分割
     * @return 文件最大字节
     */
    public long getLogFileMaxSize() {
        return logFileMaxSize;
    }

    /**
     * 设置单个文件最大字节，设置后文件会分包。（log_20200710.xlog，log_20200710_1.xlog，log_20200710_2.xlog)
     * @param logFileMaxSize 最大字节数，单位是字节
     */
    public void setLogFileMaxSize(long logFileMaxSize) {
        this.logFileMaxSize = logFileMaxSize;
    }

    /**
     * 打开日志使日志数据能够写入文件和在控制台打印（内部根据 {@link #xlogOpenStatus} 的状态来判断调用是否有效）,
     * 文件默认保存在/sdcard/android/{packageName}/XLog如果前者不可以就保存在context.getFilesDir() + "/XLog"中
     *
     * @param context 上下文对象
     */
    public void openXlog(final Context context) {
        if (context != null) {
            if (!xlogOpenStatus) {
                LogUtil.initXlogState(false);
                File externalFileDir = context.getExternalFilesDir("XLog");
                String logPath = null;
                if (externalFileDir != null) {
                    logPath = externalFileDir.getAbsolutePath();
                } else {
                    android.util.Log.i("LOG", "创建文件夹失败，可能需要重启设备。");
                }
                final String cachePath = context.getFilesDir() + "/XLog";
                android.util.Log.i("LOG", "logPath:" + logPath);
                //init xlog
                if (debugStatus) {
                    Xlog.open(true, Xlog.LEVEL_ALL, Xlog.AppednerModeAsync, cachePath, logPath, prefix, PUB_KEY);
                } else {
                    Xlog.open(true,  Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, prefix, PUB_KEY);
                }
                //下面需要放到appenderOpen方法后面
                Xlog xlog = new Xlog();
                xlog.setConsoleLogOpen(0, consoleLogOpen);
                Log.setLogImp(xlog);
                Log.i("SystemInfo", Log.getSysInfo());
                LogUtil.initXlogState(true);
                File logPathDir;
                if (TextUtils.isEmpty(logPath)) {
                    logPathDir = new File(cachePath);
                } else {
                    logPathDir = new File(logPath);
                }
                if (!logPathDir.exists()) {
                    xlogOpenStatus = false;
                } else {
                    xlogOpenStatus = true;
                    LogUtil.i("初始化日志成功");
                }
            }
        }
    }
}
