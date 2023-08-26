package top.yinlingfeng.xlog.decode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import com.blankj.utilcode.util.ToastUtils;
import com.hgsoft.log.LogUtil;
import com.permissionx.guolindev.PermissionX;
import java.util.ArrayList;
import java.util.List;

/**
  *
  * 用途：
  * 创建时间: 2023/7/21 16:59
  * @author  yinxueqin@rd.hgits.cn
  * @author  更新者：
  * 更新时间:
  * 更新说明：
  * @since   1.0
  *
  */
public class LogoActivity extends AppCompatActivity {

    private static final String TAG = "LogoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_logo);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        startPermission();
    }

    private void startPermission() {
        List<String> permissions = new ArrayList<>();
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        PermissionX.init(this)
                .permissions(permissions)
                .onForwardToSettings((scope, deniedList) -> scope.showForwardToSettingsDialog(
                        deniedList,
                        "您需要手动在设置中允许必要的权限\n" + deniedList,
                        "确定",
                        "关闭"
                ))
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        LogUtil.i(TAG, getString(R.string.permission_success));
                        startActivity(new Intent(LogoActivity.this, MainActivity.class));
                        LogoActivity.this.finish();
                    } else {
                        LogUtil.i(TAG, getString(R.string.permission_fail));
                        ToastUtils.showLong(getString(R.string.permission_fail));
                        LogoActivity.this.finish();
                    }
                });
    }
}