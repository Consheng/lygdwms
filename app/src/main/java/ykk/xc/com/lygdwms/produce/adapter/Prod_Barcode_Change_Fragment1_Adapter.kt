package ykk.xc.com.lygdwms.produce.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.BarcodeTableChange
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class Prod_Barcode_Change_Fragment1_Adapter(private val context: Activity, datas: List<BarcodeTableChange>) : BaseArrayRecyclerAdapter<BarcodeTableChange>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.prod_barcode_change_fragment1_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: BarcodeTableChange, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_mtlNumber = holder.obtainView<TextView>(R.id.tv_mtlNumber)
        val tv_mtlName = holder.obtainView<TextView>(R.id.tv_mtlName)
        val tv_fmodel = holder.obtainView<TextView>(R.id.tv_fmodel)
        val tv_scanBarcode = holder.obtainView<TextView>(R.id.tv_scanBarcode)
        val tv_scanQty = holder.obtainView<TextView>(R.id.tv_scanQty)
        val tv_newBarcode = holder.obtainView<TextView>(R.id.tv_newBarcode)
        val tv_newQty = holder.obtainView<TextView>(R.id.tv_newQty)
        val view_del = holder.obtainView<View>(R.id.view_del)

        // 赋值
        tv_row.text = (pos+1).toString()
        tv_mtlName.text = entity.material.fname
        tv_mtlNumber.text = Html.fromHtml("代码:&nbsp;<font color='#6a5acd'>"+entity.material.fnumber+"</font>")
        tv_fmodel.text = Html.fromHtml("规格型号:&nbsp;<font color='#6a5acd'>"+ Comm.isNULLS(entity.material.materialSize)+"</font>")
        tv_scanBarcode.text = Html.fromHtml("扫码:&nbsp;<font color='#6a5acd'>"+ entity.scanBarcode +"</font>")
        tv_scanQty.text = Html.fromHtml("数量:&nbsp;<font color='#6a5acd'>"+ df.format(entity.scanQty) +"</font>&nbsp;<font color='#666666'>"+ entity.unitName +"</font>")
        tv_newBarcode.text = Html.fromHtml("组码:&nbsp;<font color='#009900'>"+ entity.newBarcode +"</font>")
        tv_newQty.text = Html.fromHtml("数量:&nbsp;<font color='#FF0000'>"+ df.format(entity.newQty) +"</font>")

        /*if (entity.newBarcode.length > 0) {
            tv_newBarcode.visibility = View.VISIBLE
        } else {
            tv_newBarcode.visibility = View.GONE
        }*/
        if(entity.id > 0) {
            view_del.visibility = View.INVISIBLE
        } else {
            view_del.visibility = View.VISIBLE
        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.view_del -> {// 删除行
                    if (callBack != null) {
                        callBack!!.onDelete(entity, pos)
                    }
                }
            }
        }
        view_del!!.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onDelete(entity: BarcodeTableChange, position: Int)
    }

}
