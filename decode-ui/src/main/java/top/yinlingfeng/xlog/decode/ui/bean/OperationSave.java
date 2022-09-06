package top.yinlingfeng.xlog.decode.ui.bean;

public class OperationSave {

    private String logFilePath = "";

    private String saveLogPath = "";

    private int privateKeySelectMode = 1;

    private int selectPrivateKeySelectIndex = 0;
    private String inputPrivateKeyInfo = "";

    public OperationSave() {
    }

    public OperationSave(String logFilePath, String saveLogPath, int privateKeySelectMode, int selectPrivateKeySelectIndex,
                         String inputPrivateKeyInfo) {
        this.logFilePath = logFilePath;
        this.saveLogPath = saveLogPath;
        this.privateKeySelectMode = privateKeySelectMode;
        this.selectPrivateKeySelectIndex = selectPrivateKeySelectIndex;
        this.inputPrivateKeyInfo = inputPrivateKeyInfo;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getSaveLogPath() {
        return saveLogPath;
    }

    public void setSaveLogPath(String saveLogPath) {
        this.saveLogPath = saveLogPath;
    }

    public int getPrivateKeySelectMode() {
        return privateKeySelectMode;
    }

    public void setPrivateKeySelectMode(int privateKeySelectMode) {
        this.privateKeySelectMode = privateKeySelectMode;
    }

    public int getSelectPrivateKeySelectIndex() {
        return selectPrivateKeySelectIndex;
    }

    public void setSelectPrivateKeySelectIndex(int selectPrivateKeySelectIndex) {
        this.selectPrivateKeySelectIndex = selectPrivateKeySelectIndex;
    }

    public String getInputPrivateKeyInfo() {
        return inputPrivateKeyInfo;
    }

    public void setInputPrivateKeyInfo(String inputPrivateKeyInfo) {
        this.inputPrivateKeyInfo = inputPrivateKeyInfo;
    }

    @Override
    public String toString() {
        return "OperationSave{" +
                "logFilePath='" + logFilePath + '\'' +
                ", saveLogPath='" + saveLogPath + '\'' +
                ", privateKeySelectMode=" + privateKeySelectMode +
                ", selectPrivateKeySelectIndex=" + selectPrivateKeySelectIndex +
                ", inputPrivateKeyInfo='" + inputPrivateKeyInfo + '\'' +
                '}';
    }
}
