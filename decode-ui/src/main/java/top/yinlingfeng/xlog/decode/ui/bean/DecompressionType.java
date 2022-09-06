package top.yinlingfeng.xlog.decode.ui.bean;

public enum DecompressionType {

    ZIP("zip流式解压"),
    ZSTD("zstd流式解压");

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
