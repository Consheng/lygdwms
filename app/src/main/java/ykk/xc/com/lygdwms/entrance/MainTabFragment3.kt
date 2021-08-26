package ykk.xc.com.lygdwms.entrance


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.comm.BaseFragment

/**
 * 销售
 */
class MainTabFragment3 : BaseFragment() {

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.aa_main_item3, container, false)
    }

    @OnClick(R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.relative1 -> {// 销售出库

            }
            R.id.relative2 -> {// 业助回签
            }
            R.id.relative3 -> { // 财务回签
            }
            R.id.relative4 -> {// （内销）销售退货
            }
            R.id.relative5 -> {// 电商退生产
            }
            R.id.relative6 -> { // 销售装箱

            }
        }
    }
}
