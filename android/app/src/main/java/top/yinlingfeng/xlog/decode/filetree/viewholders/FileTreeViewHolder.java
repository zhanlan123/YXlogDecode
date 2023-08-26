/************************************************************************************
 * This file is part of AndroidIDE.
 *
 *
 *
 * AndroidIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AndroidIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 *
 **************************************************************************************/

package top.yinlingfeng.xlog.decode.filetree.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.transition.ChangeImageTransform;
import androidx.transition.TransitionManager;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import top.yinlingfeng.xlog.decode.R;
import java.io.File;

import top.yinlingfeng.xlog.decode.databinding.LayoutFiletreeItemBinding;
import top.yinlingfeng.xlog.decode.treeview.model.TreeNode;
import top.yinlingfeng.xlog.decode.utils.FileExtension;

public class FileTreeViewHolder extends TreeNode.BaseNodeViewHolder<File> {

  private LayoutFiletreeItemBinding binding;

  public FileTreeViewHolder(Context context) {
    super(context);
  }

  @Override
  public View createNodeView(TreeNode node, File file) {
    this.binding = LayoutFiletreeItemBinding.inflate(LayoutInflater.from(context));

    final int dp15 = SizeUtils.dp2px(15);
    final int icon = getIconForFile(file);
    final ImageView chevron = binding.filetreeChevron;
    binding.filetreeName.setText(file.getName());
    binding.filetreeIcon.setImageResource(icon);

    final LinearLayout root = applyPadding(node, binding, dp15);

    if (file.isDirectory()) {
      chevron.setVisibility(View.VISIBLE);
      updateChevronIcon(node.isExpanded());
    } else {
      chevron.setVisibility(View.INVISIBLE);
    }

    return root;
  }

  private void updateChevronIcon(boolean expanded) {
    final int chevronIcon;
    if (expanded) {
      chevronIcon = R.drawable.ic_chevron_down;
    } else {
      chevronIcon = R.drawable.ic_chevron_right;
    }

    TransitionManager.beginDelayedTransition(binding.getRoot(), new ChangeImageTransform());
    binding.filetreeChevron.setImageResource(chevronIcon);
  }

  protected LinearLayout applyPadding(
      final TreeNode node, final LayoutFiletreeItemBinding binding, final int padding) {
    final LinearLayout root = binding.getRoot();
    root.setPaddingRelative(
        root.getPaddingLeft() + (padding * (node.getLevel() - 1)),
        root.getPaddingTop(),
        root.getPaddingRight(),
        root.getPaddingBottom());
    return root;
  }

  protected int getIconForFile(final File file) {

    if (file.isDirectory()) {
      return R.drawable.ic_folder;
    }

    if (ImageUtils.isImage(file)) {
      return R.drawable.ic_file_image;
    }

    if (file.getName().endsWith(".log")) {
      return R.drawable.ic_file_log;
    }

    if (file.getName().endsWith(".xlog")) {
      return R.drawable.ic_file_xlog;
    }

    return FileExtension.Factory.forFile(file).getIcon();
  }

  public void updateChevron(boolean expanded) {
    setLoading(false);
    updateChevronIcon(expanded);
  }

  public void setLoading(boolean loading) {
    final int viewIndex;
    if (loading) {
      viewIndex = 1;
    } else {
      viewIndex = 0;
    }

    binding.chevronLoadingSwitcher.setDisplayedChild(viewIndex);
  }
}
