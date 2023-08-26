package top.yinlingfeng.xlog.decode.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import top.yinlingfeng.xlog.decode.R;
import top.yinlingfeng.xlog.decode.bean.PrivateKey;

/**
 * <p>用途：</p>
 *
 * @author yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/18 11:35
 * @author 更新者：
 * 更新时间:
 * 更新说明：
 * @since 1.0
 */
public class SelectPrivateKeyAdapter extends RecyclerView.Adapter<SelectPrivateKeyAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(PrivateKey privateKey);
    }

    List<PrivateKey> data;

    OnItemClickListener itemClickListener;

    public SelectPrivateKeyAdapter(List<PrivateKey> keyData, OnItemClickListener itemClickListener) {
        this.data = keyData;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View privateKeyView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.select_private_key_item, parent, false
        );
        return new ViewHolder(privateKeyView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.btnPrivateKey.setText(data.get(position).getName());
        holder.btnPrivateKey.setOnClickListener(v -> {
            itemClickListener.onClick(data.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatButton btnPrivateKey;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPrivateKey = itemView.findViewById(R.id.btn_private_key);
        }


    }

}
