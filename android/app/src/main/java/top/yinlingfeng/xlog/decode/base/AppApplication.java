package top.yinlingfeng.xlog.decode.base;

import android.app.Application;

import com.hgsoft.log.LogUtil;
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
        // 私钥：05cb6f67b111a49660d706b15875b1ffc840db68e3545bd2786f02ac9a1233ef
        LogUtil.initXlog(getApplicationContext(), false, true,
                "",
                "94e62d97637f357fbd20f0c1f667a67c2f675e158e46015dd0cc54cb3995d0a5d468f7e98b20aec266effb61ec0a2321fb1f8c61af72bf76567921a0d8305005",
                "");
        MMKVOperating.Companion.initMMKV(getApplicationContext(), false, "top.yinlingfeng.xlog.decode");
    }

}
