package top.yinlingfeng.xlog.decode.constants

import com.hgsoft.mmkv.PerpetualMMKVOperatingDelegate
import top.yinlingfeng.xlog.decode.bean.HistoryRecord
import top.yinlingfeng.xlog.decode.bean.PrivateKey
import java.util.ArrayList

/**
 *
 * <p>用途：</p>
 *
 * @author  yinxueqin@rd.hgits.cn
 * 创建时间: 2023/8/17 16:11
 * @author  更新者：
 * 更新时间:
 * 更新说明：
 * @since   1.0
 *
 */
object MMKVConstants {

    var allPrivateKey by PerpetualMMKVOperatingDelegate(MMKVName.ALL_PRIVATE_KEY, ArrayList<PrivateKey>())

    var currentSelectPrivateKey: PrivateKey by PerpetualMMKVOperatingDelegate(MMKVName.CURRENT_SELECT_PRIVATE_KEY, PrivateKey())

    var selectDefaultPrivateKey by PerpetualMMKVOperatingDelegate(MMKVName.SELECT_DEFAULT_PRIVATE_KEY, true)

    var allHistoryRecord by PerpetualMMKVOperatingDelegate(MMKVName.ALL_HISTORY_RECORD, ArrayList<HistoryRecord>())
}