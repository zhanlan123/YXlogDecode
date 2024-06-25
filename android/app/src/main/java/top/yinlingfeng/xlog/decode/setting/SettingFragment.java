package top.yinlingfeng.xlog.decode.setting;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.blankj.utilcode.util.ToastUtils;
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
                LogUtil.i(TAG, "selectPrivateKey-Preference:" + preference + ",newValue:" + newValue);
                MMKVConstants.INSTANCE.setSelectDefaultPrivateKey((boolean) newValue);
                return true;
            });
        }
        Preference splitFileStartSizeKey = getPreferenceManager().findPreference("split_file_start_size_key");
        if (splitFileStartSizeKey != null) {
            splitFileStartSizeKey.setOnPreferenceChangeListener((preference, newValue) -> {
                LogUtil.i(TAG, "splitFileStartSizeKey-Preference:" + preference + ",newValue:" + newValue);
                int maxFileSize = 100;
                String splitFileSizeTxt = (String) newValue;
                if (splitFileSizeTxt.length() > 3) {
                    ToastUtils.showLong("不能超过3位数");
                    return false;
                }
                int splitFileSize = Integer.parseInt(splitFileSizeTxt);
                if (splitFileSize < 1) {
                    ToastUtils.showLong("不能小于1");
                    return false;
                }
                if (splitFileSize > maxFileSize) {
                    ToastUtils.showLong("不能超过100");
                    return false;
                }
                MMKVConstants.INSTANCE.setSplitFileStartSizeKey(splitFileSize);
                return true;
            });
        }
        Preference splitFileKey = getPreferenceManager().findPreference("split_file_key");
        if (splitFileKey != null) {
            splitFileKey.setOnPreferenceChangeListener((preference, newValue) -> {
                LogUtil.i(TAG, "splitFileKey-Preference:" + preference + ",newValue:" + newValue);
                int maxFileSize = 100;
                String splitFileSizeTxt = (String) newValue;
                if (splitFileSizeTxt.length() > 3) {
                    ToastUtils.showLong("不能超过3位数");
                    return false;
                }
                int splitFileSize = Integer.parseInt(splitFileSizeTxt);
                if (splitFileSize < 1) {
                    ToastUtils.showLong("不能小于1");
                    return false;
                }
                if (splitFileSize > maxFileSize) {
                    ToastUtils.showLong("不能超过100");
                    return false;
                }
                MMKVConstants.INSTANCE.setSplitFileSizeKey(splitFileSize);
                return true;
            });
        }
    }


}
