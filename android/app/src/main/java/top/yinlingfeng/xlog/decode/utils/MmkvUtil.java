package top.yinlingfeng.xlog.decode.utils;

import android.content.Context;
import com.tencent.mmkv.MMKV;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.tencent.mmkv.MMKVLogLevel;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/7 11:36
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class MmkvUtil {
    private volatile static MmkvUtil instance;
    private WeakReference<Context> reference;
    /**
     * 是否加密
     */
    private boolean encrypt;
    /**
     * 秘钥
     */
    private String cryptKey;
    /**
     * 是否迁移SharedPreferences旧数据
     */
    private boolean migrate;

    private MmkvUtil() {
    }

    public static MmkvUtil get() {
        if (instance == null) {
            synchronized (MmkvUtil.class) {
                if (instance == null) {
                    instance = new MmkvUtil();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        reference = new WeakReference<>(context);
        String root = context.getFilesDir().getAbsolutePath() + "/mmkv";
        MMKV.initialize(context);
    }

    /**
     * 日志等级
     */
    public void setLogLevel(MMKVLogLevel level) {
        MMKV.setLogLevel(level);
    }

    /**
     * 是否开启加密解密
     *
     * @param encrypt  是否开启
     * @param cryptKey 秘钥
     */
    public void setEncrypt(boolean encrypt, String cryptKey) {
        this.encrypt = encrypt;
        this.cryptKey = cryptKey;
    }

    /**
     * 是否迁移旧数据
     */
    public void setMigrate(boolean migrate) {
        this.migrate = migrate;
    }

    public MMKV getMMKV() {
        return getMMKV(null);
    }

    public MMKV getMMKV(String name) {
        MMKV mmkv;
        if (TextUtils.isEmpty(name)) {
            mmkv = encrypt ? MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, cryptKey) : MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null);
        } else {
            mmkv = encrypt ? MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE, cryptKey) : MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE);
        }
        if (migrate) {
            //迁移SharedPreferences旧数据
            SharedPreferences sharedPreferences;
            if (TextUtils.isEmpty(name)) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(reference.get());
            } else {
                sharedPreferences = reference.get().getSharedPreferences(name, Context.MODE_PRIVATE);
            }
            mmkv.importFromSharedPreferences(sharedPreferences);
            sharedPreferences.edit().clear().apply();
        }
        return mmkv;
    }

    //String Set类型---------------------------------------------------------------------------------
    public Set<String> getStringSet(String key) {
        return getMMKV().getStringSet(key, new HashSet<>());
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return getMMKV().getStringSet(key, defValues);
    }

    public Set<String> getStringSet(String name, String key, Set<String> defValues) {
        return getMMKV(name).getStringSet(key, defValues);
    }

    public void putStringSet(String key, Set<String> values) {
        getMMKV().putStringSet(key, values);
    }

    public void putStringSet(String name, String key, Set<String> values) {
        getMMKV(name).putStringSet(key, values);
    }

    //Double类型---------------------------------------------------------------------------------
    public void putDouble(String key, double value) {
        getMMKV().encode(key, value);
    }

    public double getDouble(String key) {
        return getMMKV().decodeDouble(key);
    }

    public double getDouble(String key, double defValue) {
        return getMMKV().decodeDouble(key, defValue);
    }

    public double getDouble(String name, String key, double defValue) {
        return getMMKV(name).decodeDouble(key, defValue);
    }
    //byte[]类型---------------------------------------------------------------------------------

    public void putByte(String key, byte[] value) {
        getMMKV().encode(key, value);
    }

    public byte[] getBytes(String key) {
        return getMMKV().decodeBytes(key);
    }

    public byte[] getBytes(String key, byte[] defValue) {
        return getMMKV().decodeBytes(key, defValue);
    }

    public byte[] getBytes(String name, String key, byte[] defValue) {
        return getMMKV(name).decodeBytes(key, defValue);
    }
    //String类型---------------------------------------------------------------------------------

    public String getString(String key) {
        return getMMKV().getString(key, "");
    }

    public String getString(String key, String defValue) {
        return getMMKV().getString(key, defValue);
    }

    public String getString(String name, String key, String defValue) {
        return getMMKV(name).getString(key, defValue);
    }

    public void putString(String key, String value) {
        getMMKV().putString(key, value);
    }

    public void putString(String name, String key, String value) {
        getMMKV(name).putString(key, value);
    }

    //Boolean类型-----------------------------------------------------------------------------------------------
    public boolean getBoolean(String key) {
        return getMMKV().getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return getMMKV().getBoolean(key, defValue);
    }

    public boolean getBoolean(String name, String key, boolean defValue) {
        return getMMKV(name).getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        getMMKV().putBoolean(key, value);
    }

    public void putBoolean(String name, String key, boolean value) {
        getMMKV(name).putBoolean(key, value);
    }

    //Int类型-----------------------------------------------------------------------------------------------
    public void putInt(String key, int value) {
        getMMKV().putInt(key, value);
    }

    public void putInt(String name, String key, int value) {
        getMMKV(name).putInt(key, value);
    }

    public int getInt(String key) {
        return getMMKV().getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return getMMKV().getInt(key, defValue);
    }

    public int getInt(String name, String key, int defValue) {
        return getMMKV(name).getInt(key, defValue);
    }

    //Float类型-----------------------------------------------------------------------------------------------
    public void putFloat(String key, float value) {
        getMMKV().putFloat(key, value);
    }

    public void putFloat(String name, String key, float value) {
        getMMKV(name).putFloat(key, value);
    }

    public float getFloat(String key) {
        return getMMKV().getFloat(key, 0f);
    }

    public float getFloat(String key, float defValue) {
        return getMMKV().getFloat(key, defValue);
    }

    public float getFloat(String name, String key, float defValue) {
        return getMMKV(name).getFloat(key, defValue);
    }

    //Long类型-----------------------------------------------------------------------------------------------
    public void putLong(String key, long value) {
        getMMKV().putLong(key, value);
    }

    public void putLong(String name, String key, long value) {
        getMMKV(name).putLong(key, value);
    }

    public long getLong(String key) {
        return getMMKV().getLong(key, 0L);
    }

    public long getLong(String key, long defValue) {
        return getMMKV().getLong(key, defValue);
    }

    public long getLong(String name, String key, long defValue) {
        return getMMKV(name).getLong(key, defValue);
    }

    //其他方法-----------------------------------------------------------------------------------------------
    public void remove(String key) {
        getMMKV().remove(key);
    }

    public void remove(String name, String key) {
        getMMKV(name).remove(key);
    }

    public void clear() {
        getMMKV().clear();
    }

    public void clear(String name) {
        getMMKV(name).clear();
    }

    public boolean contains(String key) {
        return getMMKV().contains(key);
    }

    public boolean contains(String name, String key) {
        return getMMKV(name).contains(key);
    }
}
