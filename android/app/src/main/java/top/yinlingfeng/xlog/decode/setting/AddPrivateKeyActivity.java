package top.yinlingfeng.xlog.decode.setting;

import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import top.yinlingfeng.xlog.decode.R;
import top.yinlingfeng.xlog.decode.bean.PrivateKey;
import top.yinlingfeng.xlog.decode.constants.MMKVConstants;
import top.yinlingfeng.xlog.decode.databinding.ActivityAddPrivateKeyBinding;
import top.yinlingfeng.xlog.decode.databinding.CommonToolbarBinding;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/11 11:26
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class AddPrivateKeyActivity extends AppCompatActivity {

    private ActivityAddPrivateKeyBinding binding;

    private CommonToolbarBinding toolbarBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPrivateKeyBinding.inflate(getLayoutInflater());
        toolbarBinding = CommonToolbarBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {
        toolbarBinding.tvTitle.setText(R.string.add_private_key);
        toolbarBinding.commonToolbar.setNavigationIcon(R.drawable.back);
        toolbarBinding.commonToolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnSave.setOnClickListener(v -> {
            savePrivateKey();
        });
    }

    private void savePrivateKey() {
        String keyName = Objects.requireNonNull(binding.edSelectPrivateKeyName.getText()).toString().trim();
        String keyValue = Objects.requireNonNull(binding.edSelectPrivateKeyValue.getText()).toString().trim();
        if (TextUtils.isEmpty(keyName)) {
            ToastUtils.showShort(getString(R.string.private_key_name_hint));
            return;
        }
        if (TextUtils.isEmpty(keyValue)) {
            ToastUtils.showShort(getString(R.string.private_key_value_hint));
            return;
        }
        ArrayList<PrivateKey> allPrivateKey = MMKVConstants.INSTANCE.getAllPrivateKey();
        allPrivateKey.add(new PrivateKey(keyName, keyValue));
        MMKVConstants.INSTANCE.setAllPrivateKey(allPrivateKey);
        ToastUtils.showShort(getString(R.string.private_key_save_success));
    }
}
