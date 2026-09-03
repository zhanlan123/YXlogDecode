package top.yinlingfeng.xlog.decode.core;

import java.util.HashMap;
import java.util.Map;

import top.yinlingfeng.xlog.decode.core.log.LogUtil;

public class ECDHUtils {

    private static final String TAG = "ECDHUtils";

    // Used to load the 'core' library on application startup.
    static {
        System.loadLibrary("core");
    }

    private static native byte[] getEcdhKeyFromJNI(String publicKey, String privateKey);

    // 考虑一个文件多个加密
    private static final Map<String, byte[]> keyMap = new HashMap<>();

    /**
     * 生成 ECDH 共享密钥。
     *
     * @param publicKey  客户端的十六进制公钥字符串
     * @param privateKey 服务器的十六进制私钥字符串
     * @return 计算得到的 ECDH 共享密钥字节数组
     * @throws IllegalArgumentException 当传入的 publicKey 或 privateKey 为空时
     * @throws RuntimeException 当 JNI 底层生成密钥失败或发生内存不足等错误时
     */
    public static byte[] getECDHKey(String publicKey, String privateKey) {
        if (publicKey == null || publicKey.isEmpty()) {
            throw new IllegalArgumentException("Public key must not be null or empty.");
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("Private key must not be null or empty.");
        }
        if (keyMap.containsKey(publicKey + privateKey)) {
            return keyMap.get(publicKey + privateKey);
        }
        byte[] result = getEcdhKeyFromJNI(publicKey, privateKey);
        if (result == null) {
            throw new RuntimeException("Failed to generate ECDH shared secret from JNI layer.");
        }
        keyMap.put(publicKey + privateKey, result);
        LogUtil.i(TAG, "publicKey:" + publicKey);
        LogUtil.i(TAG, "privateKey:" + privateKey);
        LogUtil.i(TAG, "result:" + CommonUtils.bytesToHexString(result));
        return result;
    }
}