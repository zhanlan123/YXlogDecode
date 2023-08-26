package top.yinlingfeng.xlog.decode.dialog;

import android.content.Context;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import top.yinlingfeng.xlog.decode.R;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/7 15:58
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class LoadingDialog {

    public static AppCompatDialog appCompatDialog;

    public static void show(Context context) {
        appCompatDialog = new AppCompatDialog(context);
        appCompatDialog.setContentView(R.layout.dialog_loading);
        appCompatDialog.show();
        appCompatDialog.setCancelable(false);
        appCompatDialog.setCanceledOnTouchOutside(false);
        appCompatDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent));
    }

    public static void dismiss() {
        if (appCompatDialog != null) {
            appCompatDialog.dismiss();
        }
    }

}
