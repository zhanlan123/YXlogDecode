package top.yinlingfeng.xlog.decode.dialog;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import top.yinlingfeng.xlog.decode.R;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/11 15:58
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class ManagerKeyDialog {

    public static BottomSheetDialog appCompatDialog;

    public static void show(Context context, View.OnClickListener importPrivateKeyListener,
                            View.OnClickListener addPrivateKeyListener,
                            View.OnClickListener managerPrivateKeyListener) {
        appCompatDialog = new BottomSheetDialog(context);
        appCompatDialog.setContentView(R.layout.dialog_manager_private_key_type);
        appCompatDialog.show();
        appCompatDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent));
        AppCompatButton importPrivateKey = appCompatDialog.findViewById(R.id.btn_import_private_key);
        AppCompatButton addPrivateKey = appCompatDialog.findViewById(R.id.btn_add_private_key);
        AppCompatButton managerPrivateKey = appCompatDialog.findViewById(R.id.btn_manager_private_key);
        importPrivateKey.setOnClickListener(importPrivateKeyListener);
        addPrivateKey.setOnClickListener(addPrivateKeyListener);
        managerPrivateKey.setOnClickListener(managerPrivateKeyListener);
    }

    public static void dismiss() {
        if (appCompatDialog != null) {
            appCompatDialog.dismiss();
        }
    }

}
