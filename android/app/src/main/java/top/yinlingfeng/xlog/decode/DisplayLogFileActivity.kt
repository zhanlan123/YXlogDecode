package top.yinlingfeng.xlog.decode

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.ToastUtils
import com.hgsoft.log.LogUtil
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import io.github.rosemoe.sora.event.PublishSearchResultEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.SideIconClickEvent
import io.github.rosemoe.sora.text.ContentIO
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorSearcher
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.Magnifier
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.style.LineInfoPanelPosition
import io.github.rosemoe.sora.widget.style.LineInfoPanelPositionMode
import io.github.rosemoe.sora.widget.subscribeEvent
import kotlinx.coroutines.launch
import top.yinlingfeng.xlog.decode.databinding.ActivityDisplayLogFileBinding
import top.yinlingfeng.xlog.decode.databinding.CommonToolbarBinding
import top.yinlingfeng.xlog.decode.filetree.FileTreeFragment
import top.yinlingfeng.xlog.decode.utils.EventObserver
import top.yinlingfeng.xlog.decode.viewmodel.DisplayLogFileViewModel
import java.io.FileInputStream
import java.io.IOException
import java.util.regex.PatternSyntaxException

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
class DisplayLogFileActivity: AppCompatActivity() {

    private val viewModel by viewModels<DisplayLogFileViewModel>()

    private val TAG = "DisplayLogFileActivity"

    private lateinit var binding: ActivityDisplayLogFileBinding
    private lateinit var toolbarBinding: CommonToolbarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayLogFileBinding.inflate(layoutInflater)
        toolbarBinding = CommonToolbarBinding.bind(binding.root)
        setContentView(binding.root)
        LogUtil.i(TAG, "viewModel：${viewModel}")
        initView()
        initEventObserver()
    }

    private lateinit var searchMenu: PopupMenu
    private var searchOptions = EditorSearcher.SearchOptions(false, false)
    private var findInFile: MenuItem? = null
    private var editSetting: MenuItem? = null
    private var openFileTree: MenuItem? = null

    private fun initView() {
        initSearchView()
        initCodeEditor()
        if (!binding.root.isDrawerOpen(GravityCompat.END)) {
            binding.root.openDrawer(GravityCompat.END)
        }
        setListener()
    }

    private fun setListener() {
        toolbarBinding.tvLeftTitle.setText(R.string.display_log)
        toolbarBinding.commonToolbar.setNavigationIcon(R.drawable.back)
        toolbarBinding.commonToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        toolbarBinding.commonToolbar.inflateMenu(R.menu.menu_main)
        findInFile = toolbarBinding.commonToolbar.menu.findItem(R.id.text_found_in_file)
        editSetting = toolbarBinding.commonToolbar.menu.findItem(R.id.edit_setting)
        openFileTree = toolbarBinding.commonToolbar.menu.findItem(R.id.open_file_tree)
        toolbarBinding.commonToolbar.setOnMenuItemClickListener(menuItemOnClick)
    }

    private fun initEventObserver() {
        LogUtil.i(TAG, "initEventObserver")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openFilePathState.collect { result ->
                    LogUtil.i(TAG, "打开文件:${result}")
                    openAssetsFile(result)
                    updatePositionText()
                    updateBtnState()
                }
            }
        }
    }

    private fun initSearchView() {
        binding.searchEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                tryCommitSearch()
            }
        })
        searchMenu = PopupMenu(this, binding.searchOptions)
        searchMenu.inflate(R.menu.menu_search_options)
        searchMenu.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked
            val ignoreCase = !searchMenu.menu.findItem(R.id.search_option_match_case)!!.isChecked
            if (it.isChecked) {
                when (it.itemId) {
                    R.id.search_option_regex -> {
                        searchMenu.menu.findItem(R.id.search_option_whole_word)!!.isChecked = false
                    }

                    R.id.search_option_whole_word -> {
                        searchMenu.menu.findItem(R.id.search_option_regex)!!.isChecked = false
                    }
                }
            }
            var type = EditorSearcher.SearchOptions.TYPE_NORMAL
            val regex = searchMenu.menu.findItem(R.id.search_option_regex)!!.isChecked
            if (regex) {
                type = EditorSearcher.SearchOptions.TYPE_REGULAR_EXPRESSION
            }
            val wholeWord = searchMenu.menu.findItem(R.id.search_option_whole_word)!!.isChecked
            if (wholeWord) {
                type = EditorSearcher.SearchOptions.TYPE_WHOLE_WORD
            }
            searchOptions = EditorSearcher.SearchOptions(type, ignoreCase)
            tryCommitSearch()
            true
        }
    }

    private fun initCodeEditor() {
        val typeface = Typeface.createFromAsset(assets, "JetBrainsMono-Regular.ttf")
        binding.codeEditor.apply {
            typefaceText = typeface
            props.stickyScroll = true
            setLineSpacing(2f, 1.1f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ -> updatePositionText() }
            subscribeEvent<PublishSearchResultEvent> { _, _ -> updatePositionText() }
            subscribeEvent<ContentChangeEvent> { _, _ ->
                postDelayedInLifecycle(
                    ::updateBtnState,
                    50
                )
            }
            subscribeEvent<SideIconClickEvent> { _, _ ->
                Toast.makeText(this@DisplayLogFileActivity, "Side icon clicked", Toast.LENGTH_SHORT).show()
            }

            subscribeEvent<KeyBindingEvent> { event, _ ->
                if (event.eventType != EditorKeyEvent.Type.DOWN) {
                    return@subscribeEvent
                }

                Toast.makeText(
                    context,
                    "Keybinding event: " + generateKeybindingString(event),
                    Toast.LENGTH_LONG
                ).show()
            }

            getComponent<EditorAutoCompletion>()
                .setEnabledAnimation(true)
        }
        binding.codeEditor.editable = false
    }

    private fun openAssetsFile(name: String) {
        Thread {
            try {
                val inputStream = FileInputStream(name)
                val text = ContentIO.createFrom(inputStream)
                runOnUiThread {
                    binding.codeEditor.setText(text, null)
                    //setupDiagnostics()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
        updatePositionText()
        updateBtnState()
    }

    private fun tryCommitSearch() {
        val editable = binding.searchEditor.editableText
        if (editable.isNotEmpty()) {
            try {
                binding.codeEditor.searcher.search(
                    editable.toString(),
                    searchOptions
                )
            } catch (e: PatternSyntaxException) {
                // Regex error
            }
        } else {
            binding.codeEditor.searcher.stopSearch()
        }
    }



    private fun updatePositionText() {
        val cursor = binding.codeEditor.cursor
        var text =
            (1 + cursor.leftLine).toString() + ":" + cursor.leftColumn + ";" + cursor.left + " "
        text += if (cursor.isSelected) {
            "(" + (cursor.right - cursor.left) + " chars)"
        } else {
            val content = binding.codeEditor.text
            if (content.getColumnCount(cursor.leftLine) == cursor.leftColumn) {
                "(<" + content.getLine(cursor.leftLine).lineSeparator.let {
                    if (it == LineSeparator.NONE) {
                        "EOF"
                    } else {
                        it.name
                    }
                } + ">)"
            } else {
                val char = binding.codeEditor.text.charAt(
                    cursor.leftLine,
                    cursor.leftColumn
                )
                if (char.isLowSurrogate() && cursor.leftColumn > 0) {
                    "(" + String(
                        charArrayOf(
                            binding.codeEditor.text.charAt(
                                cursor.leftLine,
                                cursor.leftColumn - 1
                            ), char
                        )
                    ) + ")"
                } else if (char.isHighSurrogate() && cursor.leftColumn + 1 < binding.codeEditor.text.getColumnCount(
                        cursor.leftLine
                    )
                ) {
                    "(" + String(
                        charArrayOf(
                            char, binding.codeEditor.text.charAt(
                                cursor.leftLine,
                                cursor.leftColumn + 1
                            )
                        )
                    ) + ")"
                } else {
                    "(" + escapeIfNecessary(
                        binding.codeEditor.text.charAt(
                            cursor.leftLine,
                            cursor.leftColumn
                        )
                    ) + ")"
                }
            }
        }
        val searcher = binding.codeEditor.searcher
        if (searcher.hasQuery()) {
            val idx = searcher.currentMatchedPositionIndex
            val count = searcher.matchedPositionCount
            val matchText = if (count == 0) {
                "no match"
            } else if (count == 1) {
                "1 match"
            } else {
                "$count matches"
            }
            if (idx == -1) {
                text += "($matchText)"
            } else {
                text += "(${idx+1} of $matchText)"
            }
        }
        binding.positionDisplay.text = text
    }

    private var undoEnable = false
    private var redoEnable = false

    private fun updateBtnState() {
        undoEnable = binding.codeEditor.canUndo()
        redoEnable = binding.codeEditor.canRedo()
    }

    private fun escapeIfNecessary(c: Char): String {
        return when (c) {
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\r' -> "\\r"
            ' ' -> "<ws>"
            else -> c.toString()
        }
    }

    private fun generateKeybindingString(event: KeyBindingEvent): String {
        val sb = StringBuilder()
        if (event.isCtrlPressed) {
            sb.append("Ctrl + ")
        }

        if (event.isAltPressed) {
            sb.append("Alt + ")
        }

        if (event.isShiftPressed) {
            sb.append("Shift + ")
        }

        sb.append(KeyEvent.keyCodeToString(event.keyCode))
        return sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.codeEditor.release()
    }

    private var menuItemOnClick = object : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            val id = item.itemId
            val editor = binding.codeEditor
            when (id) {
                R.id.open_file_tree -> {
                    if (!binding.root.isDrawerOpen(GravityCompat.END)) {
                        binding.root.openDrawer(GravityCompat.END)
                    }
                }
                R.id.text_undo -> {
                    if (undoEnable) {
                        editor.undo()
                    }
                }
                R.id.text_redo -> {
                    if (redoEnable) {
                        editor.redo()
                    }
                }
                R.id.goto_end -> editor.setSelection(
                    editor.text.lineCount - 1,
                    editor.text.getColumnCount(editor.text.lineCount - 1)
                )

                R.id.move_up -> editor.moveSelectionUp()
                R.id.move_down -> editor.moveSelectionDown()
                R.id.home -> editor.moveSelectionHome()
                R.id.end -> editor.moveSelectionEnd()
                R.id.move_left -> editor.moveSelectionLeft()
                R.id.move_right -> editor.moveSelectionRight()
                R.id.magnifier -> {
                    item.isChecked = !item.isChecked
                    editor.getComponent(Magnifier::class.java).isEnabled = item.isChecked
                }

                R.id.useIcu -> {
                    item.isChecked = !item.isChecked
                    editor.props.useICULibToSelectWords = item.isChecked
                }

                R.id.ln_panel_fixed -> {
                    val themes = arrayOf(
                        getString(R.string.top),
                        getString(R.string.bottom),
                        getString(R.string.left),
                        getString(R.string.right),
                        getString(R.string.center),
                        getString(R.string.top_left),
                        getString(R.string.top_right),
                        getString(R.string.bottom_left),
                        getString(R.string.bottom_right)
                    )
                    AlertDialog.Builder(this@DisplayLogFileActivity)
                        .setTitle(R.string.fixed)
                        .setSingleChoiceItems(themes, -1) { dialog: DialogInterface, which: Int ->
                            editor.lnPanelPositionMode = LineInfoPanelPositionMode.FIXED
                            when (which) {
                                0 -> editor.lnPanelPosition = LineInfoPanelPosition.TOP
                                1 -> editor.lnPanelPosition = LineInfoPanelPosition.BOTTOM
                                2 -> editor.lnPanelPosition = LineInfoPanelPosition.LEFT
                                3 -> editor.lnPanelPosition = LineInfoPanelPosition.RIGHT
                                4 -> editor.lnPanelPosition = LineInfoPanelPosition.CENTER
                                5 -> editor.lnPanelPosition =
                                    LineInfoPanelPosition.TOP or LineInfoPanelPosition.LEFT

                                6 -> editor.lnPanelPosition =
                                    LineInfoPanelPosition.TOP or LineInfoPanelPosition.RIGHT

                                7 -> editor.lnPanelPosition =
                                    LineInfoPanelPosition.BOTTOM or LineInfoPanelPosition.LEFT

                                8 -> editor.lnPanelPosition =
                                    LineInfoPanelPosition.BOTTOM or LineInfoPanelPosition.RIGHT
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }

                R.id.ln_panel_follow -> {
                    val themes = arrayOf(
                        getString(R.string.top),
                        getString(R.string.center),
                        getString(R.string.bottom)
                    )
                    AlertDialog.Builder(this@DisplayLogFileActivity)
                        .setTitle(R.string.fixed)
                        .setSingleChoiceItems(themes, -1) { dialog: DialogInterface, which: Int ->
                            editor.lnPanelPositionMode = LineInfoPanelPositionMode.FOLLOW
                            when (which) {
                                0 -> editor.lnPanelPosition = LineInfoPanelPosition.TOP
                                1 -> editor.lnPanelPosition = LineInfoPanelPosition.CENTER
                                2 -> editor.lnPanelPosition = LineInfoPanelPosition.BOTTOM
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
                R.id.text_found_in_file,
                R.id.search_panel_st -> {
                    if (binding.searchPanel.visibility == View.GONE) {
                        binding.apply {
                            replaceEditor.setText("")
                            searchEditor.setText("")
                            editor.searcher.stopSearch()
                            searchPanel.visibility = View.VISIBLE
                            item.isChecked = true
                        }
                    } else {
                        binding.searchPanel.visibility = View.GONE
                        editor.searcher.stopSearch()
                        item.isChecked = false
                    }
                }

                R.id.edit_setting -> {
                    if (binding.codeEditor.editable) {
                        editSetting?.icon = ContextCompat.getDrawable(this@DisplayLogFileActivity, R.drawable.dis_edit)
                        editSetting?.title = getString(R.string.dis_enable_edit)
                        binding.codeEditor.editable = false
                    } else {
                        editSetting?.icon = ContextCompat.getDrawable(this@DisplayLogFileActivity, R.drawable.edit)
                        editSetting?.title = getString(R.string.enable_edit)
                        binding.codeEditor.editable = true
                    }
                }

                R.id.search_am -> {
                    binding.replaceEditor.setText("")
                    binding.searchEditor.setText("")
                    editor.searcher.stopSearch()
                    editor.beginSearchMode()
                }
                R.id.text_wordwrap -> {
                    item.isChecked = !item.isChecked
                    editor.isWordwrap = item.isChecked
                }

                R.id.completionAnim -> {
                    item.isChecked = !item.isChecked
                    editor.getComponent<EditorAutoCompletion>()
                        .setEnabledAnimation(item.isChecked)
                }
                R.id.editor_line_number -> {
                    editor.isLineNumberEnabled = !editor.isLineNumberEnabled
                    item.isChecked = editor.isLineNumberEnabled
                }

                R.id.pin_line_number -> {
                    editor.setPinLineNumber(!editor.isLineNumberPinned)
                    item.isChecked = editor.isLineNumberPinned
                }
            }
            return true
        }
    }



    fun gotoNext(view: View?) {
        try {
            binding.codeEditor.searcher.gotoNext()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun gotoLast(view: View?) {
        try {
            binding.codeEditor.searcher.gotoPrevious()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun replace(view: View?) {
        try {
            binding.codeEditor.searcher.replaceThis(binding.replaceEditor.text.toString())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun replaceAll(view: View?) {
        try {
            binding.codeEditor.searcher.replaceAll(binding.replaceEditor.text.toString())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun showSearchOptions(view: View?) {
        searchMenu.show()
    }
}