package ykk.xc.com.lygdwms.produce


import android.content.Context
import android.text.Html
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.shippinglist_scan_confim_dialog.*
import org.greenrobot.eventbus.EventBus
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.EventBusEntity
import ykk.xc.com.lygdwms.comm.BaseDialog
import ykk.xc.com.lygdwms.comm.Comm
import java.text.DecimalFormat

/**
 * 装车发货扫码确认框
 */
class ShippingScanConfirmDialog(context: Context, map: Map<String, String>) : BaseDialog(
    context,
    View.inflate(context, R.layout.shippinglist_scan_confim_dialog, null),
    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
) {
    private var mContext :Context? = null
    private var mMap: Map<String, String>
    private var df = DecimalFormat("#.######")

    init {
        ButterKnife.bind(this)
        mContext = context
        mMap = map
        init()
    }


    private fun init() {
        tv_item1.text = Html.fromHtml("命中装车清单第（<font color='#6a5acd'>"+ Comm.isNULLS(mMap.get("item1")) +"</font>）项：")
        tv_item2.text = Html.fromHtml("品名：<font color='#6a5acd'>"+ Comm.isNULLS(mMap.get("item2")) +"</font>")
        tv_item3.text = Html.fromHtml("订单号：<font color='#6a5acd'>"+ Comm.isNULLS(mMap.get("item3")) +"</font>")
        tv_item4.text = Html.fromHtml("共需数量：<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item4"))) +"</font>")
        tv_item5.text = Html.fromHtml("已扫码数量：<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item5"))) +"</font>")
        tv_item6.text = Html.fromHtml("条码数量：<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item6"))) +"</font>")
        tv_item7.text = Html.fromHtml("本次录入数量：<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item7"))) +"</font>")
        tv_item8.text = Html.fromHtml("计入本次后，共计扫码数量（：<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item8"))) +"</font>）")
        tv_item9.text = Html.fromHtml("还欠数量（<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item9"))) +"</font>）")
        tv_item10.text = Html.fromHtml("超装数量（<font color='#6a5acd'>"+ df.format(Comm.parseDouble(mMap.get("item10"))) +"</font>）")
    }


    @OnClick(R.id.btn_cancel, R.id.btn_confirm)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_cancel -> {
                EventBus.getDefault().post(EventBusEntity(100));
                dismiss()
            }
            R.id.btn_confirm -> {
                EventBus.getDefault().post(EventBusEntity(200));
                dismiss()
            }
        }
    }

}
