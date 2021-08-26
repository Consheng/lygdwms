package ykk.xc.com.lygdwms.entrance


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.produce.*

/**
 * 生产
 */
class MainTabFragment2 : BaseFragment() {

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.aa_main_item2, container, false)
    }

    @OnClick(R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.relative1 -> {  // 物料生码
                show(BarcodeGenerateActivity::class.java, null)
            }
            R.id.relative2 -> { // 组码拆码
                show(Prod_Barcode_Change_MainActivity::class.java, null)
            }
            R.id.relative3  -> { // 位置移动
                show(MaterialPositionMoveActivity::class.java, null)
            }
            R.id.relative4  -> { // 装车发货
//                show(MaterialDeliveryActivity::class.java, null)
                show(ShippingList_MainActivity::class.java, null)
            }
            R.id.relative5  -> { // 其他出库
                show(OtherOutStockActivity::class.java, null)
            }
        }
    }
}
