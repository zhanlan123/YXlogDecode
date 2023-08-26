package top.yinlingfeng.xlog.decode.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/18 10:19
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class PrivateKey implements Serializable {
    private static final long serialVersionUID = 4135908063456933665L;
    private String name;

    private String key;

    public PrivateKey() {
    }

    public PrivateKey(String name, String key) {
        this.name = name;
        this.key = key;
    }

    /**
     * 是否有效
     * @return 是否有效
     */
    public boolean isEffective() {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(key)) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrivateKey)) {
            return false;
        }

        PrivateKey that = (PrivateKey) o;

        if (!name.equals(that.name)) {
            return false;
        }
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + key.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
