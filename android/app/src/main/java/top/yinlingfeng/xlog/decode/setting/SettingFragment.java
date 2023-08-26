package top.yinlingfeng.xlog.decode.setting;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.hgsoft.log.LogUtil;
import top.yinlingfeng.xlog.decode.R;
import top.yinlingfeng.xlog.decode.constants.MMKVConstants;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/4 11:14
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class SettingFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setClickListener();
    }

    private void setClickListener() {
        Preference selectPrivateKey = getPreferenceManager().findPreference("select_private_key");
        if (selectPrivateKey != null) {
            selectPrivateKey.setOnPreferenceChangeListener((preference, newValue) -> {
                LogUtil.i(TAG, "Preference:" + preference + ",newValue:" + newValue);
                MMKVConstants.INSTANCE.setSelectDefaultPrivateKey((boolean) newValue);
                return true;
            });
        }
    }


}
