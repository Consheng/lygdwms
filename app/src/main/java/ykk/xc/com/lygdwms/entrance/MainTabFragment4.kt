package ykk.xc.com.lygdwms.entrance


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.comm.BaseFragment


/**
 * 仓库
 */
class MainTabFragment4 : BaseFragment() {

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.aa_main_item4, container, false)
    }

    @OnClick(R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6, R.id.relative7, R.id.relative8, R.id.relative9,
             R.id.relative10)
    fun onViewClicked(view: View) {
        val bundle: Bundle? = null
        when (view.id) {
            R.id.relative1 -> { // 盘点
            }
            R.id.relative2 -> { // 复盘
            }
            R.id.relative3 -> { // 工具移库

            }
            R.id.relative4 -> { // 待上传
                val bundle = Bundle()
                bundle.putInt("pageId", 0)
                bundle.putString("billType", "QTRK")
//                show(OutInStock_Search_MainActivity::class.java, bundle)
            }
            R.id.relative5 -> { // 库内装箱
            }
            R.id.relative6 -> { // 拣货位置
            }
            R.id.relative7 -> { // 物料位置
            }
            R.id.relative8 -> { // 自由调拨
            }
            R.id.relative9 -> { // 待确认
            }
            R.id.relative10 -> { // 组装拆卸
            }
        }
    }
}
