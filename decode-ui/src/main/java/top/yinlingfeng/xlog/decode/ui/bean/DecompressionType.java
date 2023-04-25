package top.yinlingfeng.xlog.decode.ui.bean;

public enum DecompressionType {

    ZIP("zip格式(只做展示)"),
    ZSTD("zstd格式(只做展示)");

    private String decompressionName;

    DecompressionType(String decompressionName) {
        this.decompressionName = decompressionName;
    }

    public String getDecompressionName() {
        return decompressionName;
    }

    @Override
    public String toString() {
        return decompressionName;
    }
}
