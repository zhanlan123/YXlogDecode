package top.yinlingfeng.xlog.decode.bean;

public enum IniSelectionEnum {
    APP("app", "密钥名称"),
    PRIVATE_KEY("privateKey", "密钥私钥"),
    OPERATION_SAVE("operationSave", "操作保存");

    private String name;
    private String hint;

    IniSelectionEnum(String name, String hint) {
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
        return "IniSelectionEnum{" +
                "name='" + name + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }
}
