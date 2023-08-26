package top.yinlingfeng.xlog.decode.bean;

import java.io.Serializable;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/18 11:02
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class HistoryRecord implements Serializable {
    private static final long serialVersionUID = 4412678428919690554L;
    private PrivateKey privateKey;

    private String logFilePath;

    public HistoryRecord() {
    }

    public HistoryRecord(PrivateKey privateKey, String logFilePath) {
        this.privateKey = privateKey;
        this.logFilePath = logFilePath;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryRecord)) return false;

        HistoryRecord that = (HistoryRecord) o;

        if (!privateKey.equals(that.privateKey)) return false;
        return logFilePath.equals(that.logFilePath);
    }

    @Override
    public int hashCode() {
        int result = privateKey.hashCode();
        result = 31 * result + logFilePath.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "privateKey=" + privateKey +
                ", logFilePath='" + logFilePath + '\'' +
                '}';
    }
}
