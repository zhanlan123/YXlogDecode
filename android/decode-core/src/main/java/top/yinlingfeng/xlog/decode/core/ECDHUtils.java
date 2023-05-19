package top.yinlingfeng.xlog.decode.core;

import top.yinlingfeng.xlog.decode.core.log.LogUtil;

public class ECDHUtils {

    private static final String TAG = "ECDHUtils";

    // Used to load the 'core' library on application startup.
    static {
        System.loadLibrary("core");
    }

    private static native byte[] getEcdhKeyFromJNI(String publicKey, String privateKey);

    public static byte[] getECDHKey(String publicKey, String privateKey) {
        byte[] result = getEcdhKeyFromJNI(publicKey, privateKey);
        LogUtil.i(TAG, "result:" + CommonUtils.bytesToHexString(result));
        return result;
    }
}