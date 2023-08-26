package top.yinlingfeng.xlog.decode.filetree

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hgsoft.log.LogUtil
import top.yinlingfeng.xlog.decode.R
import top.yinlingfeng.xlog.decode.databinding.FragmentFileTreeBinding
import top.yinlingfeng.xlog.decode.filetree.viewholders.FileTreeViewHolder
import top.yinlingfeng.xlog.decode.treeview.model.TreeNode
import top.yinlingfeng.xlog.decode.treeview.view.AndroidTreeView
import top.yinlingfeng.xlog.decode.utils.FileTreeCallable
import top.yinlingfeng.xlog.decode.utils.TaskExecutor.executeAsync
import top.yinlingfeng.xlog.decode.viewmodel.DisplayLogFileViewModel
import java.io.File
import java.util.Arrays

/**
 *
 * <p>用途：</p>
 *
 * @author  yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/10 11:34
 * @author  更新者：
 * 更新时间:
 * 更新说明：
 * @since   1.0
 *
 */
class FileTreeFragment :
    BottomSheetDialogFragment(), TreeNode.TreeNodeClickListener,
    TreeNode.TreeNodeLongClickListener {

    private var binding: FragmentFileTreeBinding? = null
    private var mFileTreeView: AndroidTreeView? = null
    private var mRoot: TreeNode? = null
    private var mTreeState: String? = null

    private val viewModel by activityViewModels<DisplayLogFileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileTreeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_STORED_TREE_STATE)) {
            mTreeState = savedInstanceState.getString(KEY_STORED_TREE_STATE, null)
        }
        val intent = requireActivity().intent
        val path = intent.getStringExtra(FILE_PATH)
        path?.let {
            val logFile = File(path)
            if (logFile.isDirectory) {
                listProjectFiles(path)
            } else {
                listProjectFiles(logFile.parent!!)
            }
        } ?: run {
            val filePath = requireContext().getExternalFilesDir("decodeLog")!!.path
            listProjectFiles(filePath)
        }
        LogUtil.i(TAG, "viewmodel：${viewModel}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveTreeState()
        outState.putString(KEY_STORED_TREE_STATE, mTreeState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mFileTreeView = null
    }

    fun saveTreeState() {
        mTreeState =
            if (mFileTreeView != null) {
                mFileTreeView!!.saveState
            } else {
                LogUtil.e(TAG, "Unable to save tree state. TreeView is null.")
                null
            }
    }

    override fun onClick(node: TreeNode, p2: Any) {
        val file = p2 as File
        if (!file.exists()) {
            return
        }
        if (file.isDirectory) {
            if (node.isExpanded) {
                collapseNode(node)
            } else {
                setLoading(node)
                listNode(node) { expandNode(node) }
            }
        }
        if (file.isFile) {
            LogUtil.i(TAG, "点击文件：${file.absolutePath}")
            viewModel.openFile(file.absolutePath)
        }
    }

    private fun collapseNode(node: TreeNode) {
        if (mFileTreeView == null) {
            return
        }
        TransitionManager.beginDelayedTransition(binding!!.root, ChangeBounds())
        mFileTreeView!!.collapseNode(node)
        updateChevron(node)
    }

    private fun updateChevron(node: TreeNode) {
        if (node.viewHolder is FileTreeViewHolder) {
            (node.viewHolder as FileTreeViewHolder).updateChevron(node.isExpanded)
        }
    }

    private fun expandNode(node: TreeNode) {
        if (mFileTreeView == null) {
            return
        }
        TransitionManager.beginDelayedTransition(binding!!.root, ChangeBounds())
        mFileTreeView!!.expandNode(node)
        updateChevron(node)
    }

    private fun setLoading(node: TreeNode) {
        if (node.viewHolder is FileTreeViewHolder) {
            (node.viewHolder as FileTreeViewHolder).setLoading(true)
        }
    }

    private fun listNode(node: TreeNode, whenDone: Runnable) {
        node.children.clear()
        node.isExpanded = false
        executeAsync({
            listFilesForNode(node.value.listFiles() ?: return@executeAsync null, node)
            var temp = node
            while (temp.size() == 1) {
                temp = temp.childAt(0)
                if (!temp.value.isDirectory) {
                    break
                }
                listFilesForNode(temp.value.listFiles() ?: continue, temp)
                temp.isExpanded = true
            }
            null
        }) {
            whenDone.run()
        }
    }

    private fun listFilesForNode(files: Array<File>, parent: TreeNode) {
        Arrays.sort(files) { f1, f2 -> f1.name.compareTo(f2.name) }
        Arrays.sort(files) { f1, f2 ->
            if (f1.isDirectory == f2.isDirectory) {
                0
            } else if (f1.isDirectory && !f2.isDirectory) {
                -1
            } else {
                1
            }
        }
        for (file in files) {
            val node = TreeNode(file)
            node.viewHolder = FileTreeViewHolder(context)
            parent.addChild(node)
        }
    }

    override fun onLongClick(node: TreeNode?, value: Any?): Boolean {
        return true
    }

    fun listProjectFiles(projectDirPath: String) {
        if (binding == null) {
            // Fragment has been destroyed
            return
        }
        val projectDir = File(projectDirPath)
        mRoot = TreeNode(File(""))
        mRoot!!.viewHolder = FileTreeViewHolder(requireContext())

        val projectRoot = TreeNode.root(projectDir)
        projectRoot.viewHolder = FileTreeViewHolder(context)
        mRoot!!.addChild(projectRoot)

        binding!!.horizontalCroll.visibility = View.GONE
        binding!!.horizontalCroll.visibility = View.VISIBLE
        executeAsync(FileTreeCallable(context, projectRoot, projectDir)) {
            if (binding == null) {
                // Fragment has been destroyed
                return@executeAsync
            }
            binding!!.horizontalCroll.visibility = View.VISIBLE
            binding!!.loading.visibility = View.GONE
            val tree = createTreeView(mRoot)
            if (tree != null) {
                tree.setUseAutoToggle(false)
                tree.setDefaultNodeClickListener(this@FileTreeFragment)
                tree.setDefaultNodeLongClickListener(this@FileTreeFragment)
                binding!!.horizontalCroll.removeAllViews()
                val view = tree.view
                binding!!.horizontalCroll.addView(view)
                view.post { tryRestoreState() }
            }
        }
    }

    private fun createTreeView(node: TreeNode?): AndroidTreeView? {
        return if (context == null) {
            null
        } else AndroidTreeView(context, node, R.drawable.bg_ripple).also { mFileTreeView = it }
    }

    private fun tryRestoreState(state: String? = mTreeState) {
        if (!TextUtils.isEmpty(state) && mFileTreeView != null) {
            mFileTreeView!!.collapseAll()
            val openNodes =
                state!!.split(AndroidTreeView.NODES_PATH_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
            restoreNodeState(mRoot!!, HashSet(openNodes))
        }

        mRoot?.let { rootNode ->
            if (rootNode.children.isNotEmpty()) {
                rootNode.childAt(0)?.let { projectRoot -> expandNode(projectRoot) }
            }
        }
    }

    private fun restoreNodeState(root: TreeNode, openNodes: Set<String>) {
        val children = root.children
        var i = 0
        val childrenSize = children.size
        while (i < childrenSize) {
            val node = children[i]
            if (openNodes.contains(node.path)) {
                listNode(node) {
                    expandNode(node)
                    restoreNodeState(node, openNodes)
                }
            }
            i++
        }
    }

    companion object {

        // Should be same as defined in layout/activity_editor.xml
        const val TAG = "editor.fileTree"
        private const val KEY_STORED_TREE_STATE = "fileTree_state"
        const val FILE_PATH = "file_path"
        @JvmStatic
        fun newInstance(): FileTreeFragment {
            return FileTreeFragment()
        }
    }
}