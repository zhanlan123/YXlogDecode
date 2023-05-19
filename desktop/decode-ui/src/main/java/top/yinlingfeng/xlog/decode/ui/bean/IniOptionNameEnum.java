package top.yinlingfeng.xlog.decode.ui.bean;

public enum IniOptionNameEnum {
    LOG_FILE_PATH("logFilePath", "日志文件地址"),
    SAVE_TO_PATH("saveLogPath", "保存的目录"),
    PRIVATE_KEY_SELECT_MODE("privateKeySelectMode", "密钥选择模式"),

    SELECT_PRIVATE_KEY_SELECT_INDEX("selectPrivateKeySelectIndex", "选择密钥,密钥列表序号"),
    INPUT_PRIVATE_KEY_INFO("inputPrivateKeyInfo", "输入的密钥信息");

    private String name;
    private String hint;

    IniOptionNameEnum(String name, String hint) {
        this.name = name;
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String toString() {
        return "IniOptionNameEnum{" +
                "name='" + name + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }
}
