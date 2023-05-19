package top.yinlingfeng.xlog.createxlogfile;

import android.content.Context;
import androidx.annotation.NonNull;
import com.tencent.mars.xlog.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * 打印日志封装
 *
 * @author  yinxueqin
 */
public final class LogUtil {

    /**
     * 可以全局控制android.util.Log是否打印log日志
     */
    private static boolean isPrintLog = true;
    /**
     * 在某些机器上小于4000，小于2000
     */
    private static int logMaxLength = 1800;
    /**
     * 长度最好不超过 23 个字符
     */
    public static final String TAG = "-hgits-log";

    public static boolean xlogInitComplete = false;

    /**
     * 向外提供日志输出
     */
    private static LogInfo mLogInfo;

    /**
     * 初始化Xlog（默认方式（文件默认保存10天，不设置文件最大字节）
     * @param context 上下文
     * @param isDebugStatus 是否debug false：不是，true:是
     * @param consoleLogOpen 控制台是否打印
     * @param logFileNamePrefix 日志文件名前缀
     * @param releasePubKey release版本时需要密钥
     * @param needPrintSystemInfo 需要打印的系统信息
     */
    public static void initXlog(Context context, boolean isDebugStatus, boolean consoleLogOpen, @NonNull String logFileNamePrefix,
                         @NonNull String releasePubKey, @NonNull String needPrintSystemInfo) {
        LogUtil.appenderFlush(false);
        LogUtil.appenderClose();
        MarsXLogInit.getInstance().setXlogOpenStatus(false);
        MarsXLogInit.getInstance().setDebugStatus(isDebugStatus);
        MarsXLogInit.getInstance().setConsoleLogOpen(consoleLogOpen);
        if (isDebugStatus) {
            MarsXLogInit.getInstance().setPUBKEY("");
            MarsXLogInit.getInstance().setLogFileNamePrefix("Debug_" + logFileNamePrefix);
        } else {
            MarsXLogInit.getInstance().setPUBKEY(releasePubKey);
            MarsXLogInit.getInstance().setLogFileNamePrefix("Release_" + logFileNamePrefix);
        }
        MarsXLogInit.getInstance().openXlog(context);
        LogUtil.ei(TAG, needPrintSystemInfo);
        LogUtil.appenderFlush(false);
    }

    /**
     * 初始化Xlog（默认方式（文件默认保存10天，不设置文件最大字节）
     * @param context 上下文
     * @param isDebugStatus 是否debug false：不是，true:是
     * @param consoleLogOpen 控制台是否打印
     * @param logFileNamePrefix 日志文件名前缀
     * @param releasePubKey release版本时需要密钥
     * @param needPrintSystemInfo 需要打印的系统信息
     * @param logFileSaveDays  文件保存天数，这个保存天数根据文件创建属性决定。最小1天，默认10天
     * @param logFileMaxSize 单个文件最大字节 0：表示不分割 单位字节
     */
    public static void initXlog(Context context, boolean isDebugStatus, boolean consoleLogOpen, @NonNull String logFileNamePrefix,
                                @NonNull String releasePubKey, @NonNull String needPrintSystemInfo, int logFileSaveDays, long logFileMaxSize) {
        LogUtil.appenderFlush(false);
        LogUtil.appenderClose();
        MarsXLogInit.getInstance().setXlogOpenStatus(false);
        MarsXLogInit.getInstance().setDebugStatus(isDebugStatus);
        MarsXLogInit.getInstance().setConsoleLogOpen(consoleLogOpen);
        MarsXLogInit.getInstance().setLogFileSaveDays(logFileSaveDays);
        MarsXLogInit.getInstance().setLogFileMaxSize(logFileMaxSize);
        if (isDebugStatus) {
            MarsXLogInit.getInstance().setPUBKEY("");
            MarsXLogInit.getInstance().setLogFileNamePrefix("Debug_" + logFileNamePrefix);
        } else {
            MarsXLogInit.getInstance().setPUBKEY(releasePubKey);
            MarsXLogInit.getInstance().setLogFileNamePrefix("Release_" + logFileNamePrefix);
        }
        MarsXLogInit.getInstance().openXlog(context);
        LogUtil.ei(TAG, needPrintSystemInfo);
        LogUtil.appenderFlush(false);
    }

    public static void initXlogState(boolean state) {
        xlogInitComplete = state;
    }

    /**
     * 设置android原生日志打印是否可以打印
     *
     * @param isPrint true:可以，false:不可以
     */
    public static void setAndroidLogPrintConsoleLog(final boolean isPrint) {
        isPrintLog = isPrint;
    }

    /**
     * 设置android logcat最大打印长度，超出将自动分割
     * @param logMaxLength 一条日志最大长度
     */
    public static void setLogPrintMaxLength(final int logMaxLength) {
        LogUtil.logMaxLength = logMaxLength;
    }

    /**
     * 打印日志，级别为：LEVEL_VERBOSE
     *
     * @param msg 日志信息
     */
    public static void v(final String msg) {
        v(TAG, msg);
    }

    /**
     * 打印日志，级别为：LEVEL_VERBOSE
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void v(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogV(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.v(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_VERBOSE, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_DEBUG
     *
     * @param msg 日志信息
     */
    public static void d(final String msg) {
        d(TAG, msg);
    }

    /**
     * 打印日志，级别为：LEVEL_DEBUG
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void d(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogD(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.d(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_DEBUG, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_INFO
     *
     * @param msg 日志信息
     */
    public static void i(final String msg) {
        i(TAG, msg);
    }

    /**
     * 打印日志，级别为：LEVEL_INFO
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void i(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogI(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.i(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_INFO, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_INFO，有额外返回
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void ei(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogI(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.i(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_INFO, tagName + TAG, log);
                mLogInfo.log(LogLevel.LEVEL_EXTRA, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_WARNING
     *
     * @param msg 日志信息
     */
    public static void w(final String msg) {
        w(TAG, msg);
    }

    /**
     * 打印日志，级别为：LEVEL_WARNING
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void w(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogW(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.w(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_WARNING, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_WARNING，有额外返回
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void ew(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogW(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.w(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_WARNING, tagName + TAG, log);
                mLogInfo.log(LogLevel.LEVEL_EXTRA, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_ERROR
     *
     * @param msg 日志信息
     */
    public static void e(final String msg) {
        e(TAG, msg);
    }

    /**
     * 打印日志，级别为：LEVEL_ERROR
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void e(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogE(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.e(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_ERROR, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_ERROR，有额外返回
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void ee(final String tagName, final String msg) {
        if (!xlogInitComplete) {
            androidLogE(tagName, msg);
            return;
        }
        List<String> logMsg = splitString(msg, logMaxLength);
        for (String log: logMsg) {
            Log.e(tagName + TAG, log);
            if (mLogInfo != null) {
                mLogInfo.log(LogLevel.LEVEL_ERROR, tagName + TAG, log);
                mLogInfo.log(LogLevel.LEVEL_EXTRA, tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_VERBOSE
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void androidLogV(final String tagName, final String msg) {
        if (isPrintLog) {
            List<String> logMsg = splitString(msg, logMaxLength);
            for (String log: logMsg) {
                android.util.Log.v(tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_DEBUG
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void androidLogD(final String tagName, final String msg) {
        if (isPrintLog) {
            List<String> logMsg = splitString(msg, logMaxLength);
            for (String log: logMsg) {
                android.util.Log.d(tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_INFO
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void androidLogI(final String tagName, final String msg) {
        if (isPrintLog) {
            List<String> logMsg = splitString(msg, logMaxLength);
            for (String log: logMsg) {
                android.util.Log.i(tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_WARNING
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void androidLogW(final String tagName, final String msg) {
        if (isPrintLog) {
            List<String> logMsg = splitString(msg, logMaxLength);
            for (String log: logMsg) {
                android.util.Log.w(tagName + TAG, log);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_ERROR
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void androidLogE(final String tagName, final String msg) {
        if (isPrintLog) {
            final List<String> logMsg = splitString(msg, logMaxLength);
            for (String log: logMsg) {
                android.util.Log.e(tagName + TAG, log);
            }
        }
    }




    /**
     * 关闭日志，在程序退出时调用。
     */
    public static void appenderClose() {
        MarsXLogInit.getInstance().setXlogOpenStatus(false);
    }

    /**
     * 当日志写入模式为异步时，调用该接口会把内存中的日志写入到文件。
     *
     * @param isSync isSync : true 为同步 flush，flush 结束后才会返回。 false 为异步 flush，不等待 flush 结束就返回。
     */
    public static void appenderFlush(final boolean isSync) {
        LogUtil.i(TAG, "appenderFlush:" + isSync);
        Log.appenderFlush();
    }

    /**
     * 设置日志回调
     * @param logInfo 回调对象
     */
    public static void logPrintInfo(final LogInfo logInfo) {
        mLogInfo = logInfo;
    }

    private static List<String> splitString(final String text, final int splitLength) {
        List<String> temp = new ArrayList<>();
        if (text != null && text.length() > 0) {
            if (text.length() <= splitLength) {
                temp.add(text);
            } else {
                int num = text.length() / splitLength;
                if (num * splitLength < text.length()) {
                    num = num + 1;
                }
                for (int i = 0; i < num - 1; i++) {
                    temp.add(text.substring(i * splitLength, (i + 1) * splitLength));
                }
                temp.add(text.substring((num -1) * splitLength));
            }
        }
        return temp;
    }
}