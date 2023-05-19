package top.yinlingfeng.xlog.decode.ui.bean;

public class PrivateKeyInfo {
    // 密钥名称
    private String privateKeyName;
    // 密钥
    private String privateKeyValue;

    public PrivateKeyInfo() {
    }

    public PrivateKeyInfo(String privateKeyName, String privateKeyValue) {
        this.privateKeyName = privateKeyName;
        this.privateKeyValue = privateKeyValue;
    }

    public String getPrivateKeyName() {
        return privateKeyName;
    }

    public void setPrivateKeyName(String privateKeyName) {
        this.privateKeyName = privateKeyName;
    }

    public String getPrivateKeyValue() {
        return privateKeyValue;
    }

    public void setPrivateKeyValue(String privateKeyValue) {
        this.privateKeyValue = privateKeyValue;
    }

    @Override
    public String toString() {
        return privateKeyName;
    }

    public String getAllData() {
        return "PrivateKeyInfo{" +
                "privateKeyName='" + privateKeyName + '\'' +
                ", privateKeyValue='" + privateKeyValue + '\'' +
                '}';
    }
}
