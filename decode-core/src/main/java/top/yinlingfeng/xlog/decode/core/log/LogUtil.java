package top.yinlingfeng.xlog.decode.core.log;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 打印日志封装
 *
 * @author  yinxueqin
 */
public final class LogUtil {

    public static final Logger LOGGER = Logger.getLogger("XLogDecode");

    /**
     * 向外提供日志输出
     */
    private static LogInfo mLogInfo;

    /**
     * 长度最好不超过 23 个字符
     */
    public static final String TAG = "Decode-Core";



    public static LogInfo getmLogInfo() {
        return mLogInfo;
    }

    public static void setmLogInfo(LogInfo mLogInfo) {
        LogUtil.mLogInfo = mLogInfo;
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
        if (mLogInfo != null) {
            if (tagName == null || tagName.length() == 0) {
                mLogInfo.log(LogLevel.LEVEL_VERBOSE, TAG, msg);
            } else {
                mLogInfo.log(LogLevel.LEVEL_VERBOSE, tagName, msg);
            }
        } else {
            if (tagName == null || tagName.length() == 0) {
                LOGGER.log(Level.ALL, TAG + ": " + msg);
            } else {
                LOGGER.log(Level.ALL,tagName + ": " + msg);
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
        if (mLogInfo != null) {
            if (tagName == null || tagName.length() == 0) {
                mLogInfo.log(LogLevel.LEVEL_DEBUG, TAG, msg);
            } else {
                mLogInfo.log(LogLevel.LEVEL_DEBUG, tagName, msg);
            }
        } else {
            if (tagName == null || tagName.length() == 0) {
                LOGGER.log(Level.WARNING, TAG + ": " + msg);
            } else {
                LOGGER.log(Level.WARNING, tagName + ": " + msg);
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
        if (mLogInfo != null) {
            if (tagName == null || tagName.length() == 0) {
                mLogInfo.log(LogLevel.LEVEL_INFO, TAG, msg);
            } else {
                mLogInfo.log(LogLevel.LEVEL_INFO, tagName, msg);
            }
        } else {
            if (tagName == null || tagName.length() == 0) {
                LOGGER.log(Level.INFO, TAG + ": " + msg);
            } else {
                LOGGER.log(Level.INFO, tagName + ": " + msg);
            }
        }
    }

    /**
     * 打印日志，级别为：LEVEL_INFO
     *
     * @param msg 日志信息
     */
    public static void ei(final String msg) {
        ei(TAG, msg);
    }

    /**
     * 打印日志，级别为：LEVEL_INFO
     *
     * @param tagName 日志tag
     * @param msg 日志信息
     */
    public static void ei(final String tagName, final String msg) {
        if (mLogInfo != null) {
            if (tagName == null || tagName.length() == 0) {
                mLogInfo.log(LogLevel.LEVEL_EXTRA, TAG, msg);
            } else {
                mLogInfo.log(LogLevel.LEVEL_EXTRA, tagName, msg);
            }
        } else {
            if (tagName == null || tagName.length() == 0) {
                LOGGER.log(Level.INFO, TAG + ": " + msg);
            } else {
                LOGGER.log(Level.INFO, tagName + ": " + msg);
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
        if (mLogInfo != null) {
            if (tagName == null || tagName.length() == 0) {
                mLogInfo.log(LogLevel.LEVEL_WARNING, TAG, msg);
            } else {
                mLogInfo.log(LogLevel.LEVEL_WARNING, tagName, msg);
            }
        } else {
            if (tagName == null || tagName.length() == 0) {
                LOGGER.log(Level.WARNING, TAG + ": " + msg);
            } else {
                LOGGER.log(Level.WARNING, tagName + ": " + msg);
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
        if (mLogInfo != null) {
            if (tagName == null || tagName.length() == 0) {
                mLogInfo.log(LogLevel.LEVEL_ERROR, TAG, msg);
                LOGGER.log(Level.WARNING, TAG + ": " + msg);
            } else {
                mLogInfo.log(LogLevel.LEVEL_ERROR, tagName, msg);
                LOGGER.log(Level.WARNING, tagName + ": " + msg);
            }
        } else {
            if (tagName == null || tagName.length() == 0) {
                LOGGER.log(Level.WARNING, TAG + ": " + msg);
            } else {
                LOGGER.log(Level.WARNING, tagName + ": " + msg);
            }
        }
    }
}