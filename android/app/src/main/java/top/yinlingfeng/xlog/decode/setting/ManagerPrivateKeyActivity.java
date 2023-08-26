package top.yinlingfeng.xlog.decode.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.blankj.utilcode.util.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import top.yinlingfeng.xlog.decode.R;
import top.yinlingfeng.xlog.decode.bean.PrivateKey;
import top.yinlingfeng.xlog.decode.constants.MMKVConstants;
import top.yinlingfeng.xlog.decode.databinding.ActivityManagerPrivateKeyBinding;
import top.yinlingfeng.xlog.decode.databinding.CommonToolbarBinding;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/11 11:27
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class ManagerPrivateKeyActivity extends AppCompatActivity {

    private ActivityManagerPrivateKeyBinding binding;

    private CommonToolbarBinding toolbarBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerPrivateKeyBinding.inflate(getLayoutInflater());
        toolbarBinding = CommonToolbarBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());
        initView();
        setListener();
    }

    private PrivateKey selectPrivateKey = null;

    private void initView() {
        List<PrivateKey> allPrivateKey = MMKVConstants.INSTANCE.getAllPrivateKey();
        if (allPrivateKey.size() > 0) {
            selectPrivateKey = allPrivateKey.get(0);
            binding.edSelectPrivateKeyName.setText(selectPrivateKey.getName());
            binding.edSelectPrivateKeyValue.setText(selectPrivateKey.getKey());
            SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(ManagerPrivateKeyActivity.this,
                    R.layout.spinner_item, allPrivateKey);
            binding.spPrivateKey.setAdapter(spinnerAdapter);
            binding.spPrivateKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectPrivateKey = allPrivateKey.get(position);
                    binding.edSelectPrivateKeyName.setText(selectPrivateKey.getName());
                    binding.edSelectPrivateKeyValue.setText(selectPrivateKey.getKey());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        } else {
            List<String> empty = new ArrayList<>();
            empty.add("无密钥");
            SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(ManagerPrivateKeyActivity.this,
                    R.layout.spinner_item, empty);
            binding.spPrivateKey.setAdapter(spinnerAdapter);
            binding.edSelectPrivateKeyName.setText("");
            binding.edSelectPrivateKeyValue.setText("");
        }

    }

    private void setListener() {
        toolbarBinding.tvTitle.setText(R.string.manager_private_key);
        toolbarBinding.commonToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
        toolbarBinding.commonToolbar.setNavigationIcon(R.drawable.back);
        toolbarBinding.commonToolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnSave.setOnClickListener(v -> {
            String keyName = Objects.requireNonNull(binding.edSelectPrivateKeyName.getText()).toString().trim();
            String keyValue = Objects.requireNonNull(binding.edSelectPrivateKeyValue.getText()).toString().trim();
            ArrayList<PrivateKey> allPrivateKey = MMKVConstants.INSTANCE.getAllPrivateKey();
            allPrivateKey.remove(selectPrivateKey);
            allPrivateKey.add(new PrivateKey(keyName, keyValue));
            MMKVConstants.INSTANCE.setAllPrivateKey(allPrivateKey);
            PrivateKey currentSaveSelectPrivateKey = MMKVConstants.INSTANCE.getCurrentSelectPrivateKey();
            if (currentSaveSelectPrivateKey.getName().equals(keyName)) {
                MMKVConstants.INSTANCE.setCurrentSelectPrivateKey(selectPrivateKey);
            }
            ToastUtils.showShort(getString(R.string.private_key_change_success));
            initView();
        });
        binding.btnDelete.setOnClickListener(v -> {
            ArrayList<PrivateKey> allPrivateKey = MMKVConstants.INSTANCE.getAllPrivateKey();
            allPrivateKey.remove(selectPrivateKey);
            MMKVConstants.INSTANCE.setAllPrivateKey(allPrivateKey);
            PrivateKey currentSaveSelectPrivateKey = MMKVConstants.INSTANCE.getCurrentSelectPrivateKey();
            if (currentSaveSelectPrivateKey.getName().equals(selectPrivateKey.getName())) {
                MMKVConstants.INSTANCE.setCurrentSelectPrivateKey(new PrivateKey());
            }
            ToastUtils.showShort(getString(R.string.private_key_delete_success));
            initView();
        });
    }
}
