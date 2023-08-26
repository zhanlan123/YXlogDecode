package top.yinlingfeng.xlog.decode.base;

import android.app.Application;
import com.hgsoft.mmkv.MMKVOperating;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/7 11:33
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class AppApplication extends Application {

    public void onCreate() {
        super.onCreate();
        MMKVOperating.Companion.initMMKV(getApplicationContext(), false, "top.yinlingfeng.xlog.decode");
    }

}
