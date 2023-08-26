package top.yinlingfeng.xlog.decode.setting;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import top.yinlingfeng.xlog.decode.R;
import top.yinlingfeng.xlog.decode.databinding.ActivitySettingBinding;
import top.yinlingfeng.xlog.decode.databinding.CommonToolbarBinding;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/4 9:54
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    private ActivitySettingBinding binding;

    private CommonToolbarBinding toolbarBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        toolbarBinding = CommonToolbarBinding.bind(binding.getRoot());
        View view = binding.getRoot();
        setContentView(view);
        initView();
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        settingPreference();
    }

    private void initView() {
        toolbarBinding.tvTitle.setText(R.string.add_private_key);
        toolbarBinding.commonToolbar.setNavigationIcon(R.drawable.back);
        toolbarBinding.commonToolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void settingPreference() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingFragment())
                .commit();
    }


}
