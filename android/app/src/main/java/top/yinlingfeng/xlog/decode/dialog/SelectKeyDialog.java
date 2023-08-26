package top.yinlingfeng.xlog.decode.dialog;

import android.content.Context;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import top.yinlingfeng.xlog.decode.R;
import top.yinlingfeng.xlog.decode.adapter.SelectPrivateKeyAdapter;
import top.yinlingfeng.xlog.decode.bean.PrivateKey;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/16 9:57
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class SelectKeyDialog {
    public static BottomSheetDialog appCompatDialog;

    public interface SelectPrivateKey {
        void onClick(PrivateKey keyData);
    }

    private static PrivateKey currentSelectPrivateKey;

    public static void show(Context context, @NonNull List<PrivateKey> allPrivateKeyData, @NonNull SelectPrivateKey confirmClick) {
        appCompatDialog = new BottomSheetDialog(context);
        appCompatDialog.setContentView(R.layout.dialog_private_key_select);
        appCompatDialog.show();
        appCompatDialog.setCancelable(false);
        appCompatDialog.setCanceledOnTouchOutside(false);
        Window window = appCompatDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent));
        }
        AppCompatTextView tvEmpty = appCompatDialog.findViewById(R.id.tv_empty);
        RecyclerView privateKeView = appCompatDialog.findViewById(R.id.rv_private_key);
        AppCompatTextView cancel = appCompatDialog.findViewById(R.id.tv_cancel);
        if (cancel != null) {
            cancel.setOnClickListener(v -> {
                dismiss();
            });
        }
        if (allPrivateKeyData.size() == 0) {
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (privateKeView != null) {
                privateKeView.setVisibility(View.GONE);
            }
            return;
        }
        currentSelectPrivateKey = allPrivateKeyData.get(0);
        if (privateKeView != null) {
            privateKeView.setLayoutManager(new LinearLayoutManager(context));
            SelectPrivateKeyAdapter privateKeyAdapter = new SelectPrivateKeyAdapter(allPrivateKeyData, privateKey -> {
                currentSelectPrivateKey = privateKey;
                dismiss();
                confirmClick.onClick(currentSelectPrivateKey);
            } );
            privateKeView.setAdapter(privateKeyAdapter);
        }

    }

    public static void dismiss() {
        if (appCompatDialog != null) {
            appCompatDialog.dismiss();
        }
    }
}
