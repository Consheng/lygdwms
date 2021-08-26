package ykk.xc.com.lygdwms.basics.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView

import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.ShippingListTitle
import ykk.xc.com.lygdwms.bean.Stock
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter

class ShippingListTitle_DialogAdapter(private val context: Activity, private val datas: List<ShippingListTitle>) : BaseArrayRecyclerAdapter<ShippingListTitle>(datas) {
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.shippinglist_title_dialog_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: ShippingListTitle, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_title = holder.obtainView<TextView>(R.id.tv_title)
        val tv_item1 = holder.obtainView<TextView>(R.id.tv_item1)
        val tv_item2 = holder.obtainView<TextView>(R.id.tv_item2)
        val tv_item3 = holder.obtainView<TextView>(R.id.tv_item3)
        val tv_item4 = holder.obtainView<TextView>(R.id.tv_item4)
        val tv_item5 = holder.obtainView<TextView>(R.id.tv_item5)
        val tv_item6 = holder.obtainView<TextView>(R.id.tv_item6)
        // 赋值
        tv_row!!.text = (pos + 1).toString()
        if(entity.item1 != null) {
            tv_title.visibility = View.GONE
            tv_item1.visibility = View.VISIBLE
            tv_item2.visibility = View.VISIBLE
            tv_item3.visibility = View.VISIBLE
            tv_item4.visibility = View.VISIBLE
            tv_item5.visibility = View.VISIBLE
            tv_item6.visibility = View.VISIBLE
            tv_item1.text = entity.item1
            tv_item2.text = Html.fromHtml("客户订单号:&nbsp;<font color='#6a5acd'>"+ entity.item2 +"</font>")
            tv_item3.text = Html.fromHtml("出货时间:&nbsp;<font color='#6a5acd'>"+ entity.item3 +"</font>")
            tv_item4.text = Html.fromHtml("出货方式:&nbsp;<font color='#6a5acd'>"+ entity.item4 +"</font>")
            tv_item5.text = Html.fromHtml("跟单:&nbsp;<font color='#6a5acd'>"+ entity.item5 +"</font>")
            tv_item6.text = Html.fromHtml("船务:&nbsp;<font color='#6a5acd'>"+ entity.item6 +"</font>")

        } else {
            tv_title.visibility = View.VISIBLE
            tv_item1.visibility = View.GONE
            tv_item2.visibility = View.GONE
            tv_item3.visibility = View.GONE
            tv_item4.visibility = View.GONE
            tv_item5.visibility = View.GONE
            tv_item6.visibility = View.GONE
            tv_title.text = entity.title
        }
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onClick(entity: Stock, position: Int)
    }

}
