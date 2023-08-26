package top.yinlingfeng.xlog.decode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import top.yinlingfeng.xlog.decode.utils.Event

/**
 *
 * <p>用途：</p>
 *
 * @author  yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/18 17:40
 * @author  更新者：
 * 更新时间:
 * 更新说明：
 * @since   1.0
 *
 */
class DisplayLogFileViewModel: ViewModel() {

    private val TAG = "DisplayLogFileViewModel"

    private val _openFilePathState = MutableSharedFlow<String>()
    val openFilePathState: SharedFlow<String> = _openFilePathState

    fun openFile(filePath: String) {
        viewModelScope.launch {
            _openFilePathState.emit(filePath)
        }
    }

}