package ykk.xc.com.lygdwms.produce.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.BarcodeTable
import ykk.xc.com.lygdwms.bean.MaterialDeliveryEntry
import ykk.xc.com.lygdwms.bean.ShippingListEntry
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class ShippingList_Fragment2_Adapter(private val context: Activity, datas: List<ShippingListEntry>) : BaseArrayRecyclerAdapter<ShippingListEntry>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.shippinglist_fragment2_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: ShippingListEntry, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_mtlNumber = holder.obtainView<TextView>(R.id.tv_mtlNumber)
        val tv_mtlName = holder.obtainView<TextView>(R.id.tv_mtlName)
        val tv_fmodel = holder.obtainView<TextView>(R.id.tv_fmodel)
        val tv_orderNo = holder.obtainView<TextView>(R.id.tv_orderNo)
        val tv_sourceQty = holder.obtainView<TextView>(R.id.tv_sourceQty)
        val tv_fqty = holder.obtainView<TextView>(R.id.tv_fqty)

        // 赋值
        tv_row.text = (pos+1).toString()
        tv_mtlName.text = entity.productName
        tv_mtlNumber.text = Html.fromHtml("代码:&nbsp;<font color='#6a5acd'>"+entity.materialNumber+"</font>")
        tv_fmodel.text = Html.fromHtml("型号:&nbsp;<font color='#6a5acd'>"+ Comm.isNULLS(entity.model)+"</font>")
        tv_orderNo.text = Html.fromHtml("订单号:&nbsp;<font color='#6a5acd'>"+ Comm.isNULLS(entity.orderNo)+"</font>")
        tv_sourceQty.text = Html.fromHtml("发货数:&nbsp;<font color='#6a5acd'>"+ df.format(entity.usableQty) +"</font>&nbsp;<font color='#666666'>"+ entity.unitName +"</font>")
        tv_fqty.text = Html.fromHtml("扫码数:&nbsp;<font color='#FF0000'>"+ df.format(entity.outQty) +"</font>")

        val view = tv_row!!.getParent() as View
        if (entity.id == 0) {
            view.setBackgroundResource(R.drawable.back_style_check1_true)
        } else {
            view.setBackgroundResource(R.drawable.back_style_check1_false)
        }
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
    }

}
