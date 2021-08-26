package ykk.xc.com.lygdwms.produce.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.BarcodeTable
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class OtherOutStock_Adapter(private val context: Activity, datas: List<BarcodeTable>) : BaseArrayRecyclerAdapter<BarcodeTable>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.ware_other_out_stock_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: BarcodeTable, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_mtlNumber = holder.obtainView<TextView>(R.id.tv_mtlNumber)
        val tv_mtlName = holder.obtainView<TextView>(R.id.tv_mtlName)
        val tv_fmodel = holder.obtainView<TextView>(R.id.tv_fmodel)
        val tv_sourceQty = holder.obtainView<TextView>(R.id.tv_sourceQty)
        val tv_fqty = holder.obtainView<TextView>(R.id.tv_fqty)
        val tv_stockName = holder.obtainView<TextView>(R.id.tv_stockName)
        val tv_stockAreaName = holder.obtainView<TextView>(R.id.tv_stockAreaName)
        val tv_storageRackName = holder.obtainView<TextView>(R.id.tv_storageRackName)
        val tv_stockPosName = holder.obtainView<TextView>(R.id.tv_stockPosName)
        val view_del = holder.obtainView<View>(R.id.view_del)

        // 赋值
        tv_row.text = (pos+1).toString()
        tv_mtlName.text = entity.materialName
        tv_mtlNumber.text = Html.fromHtml("代码:&nbsp;<font color='#6a5acd'>"+entity.materialNumber+"</font>")
        tv_fmodel.text = Html.fromHtml("规格型号:&nbsp;<font color='#6a5acd'>"+ Comm.isNULLS(entity.materialSize)+"</font>")
        tv_sourceQty.text = Html.fromHtml("结存数:&nbsp;<font color='#6a5acd'>"+ df.format(entity.remainQty) +"</font>&nbsp;<font color='#666666'>"+ entity.unitName +"</font>")
        tv_fqty.text = Html.fromHtml("数量:&nbsp;<font color='#FF0000'>"+ df.format(entity.smFqty) +"</font>")

        // 仓库组信息
        if(entity.stock != null ) {
            tv_stockName.visibility = View.VISIBLE
            tv_stockName.text = Html.fromHtml("仓库:&nbsp;<font color='#000000'>"+entity.stock!!.fname+"</font>")
        } else {
            tv_stockName.visibility = View.INVISIBLE
        }
        if(entity.stockArea != null ) {
            tv_stockAreaName.visibility = View.VISIBLE
            tv_stockAreaName.text = Html.fromHtml("库区:&nbsp;<font color='#000000'>"+entity.stockArea!!.fname+"</font>")
        } else {
            tv_stockAreaName.visibility = View.INVISIBLE
        }
        if(entity.storageRack != null ) {
            tv_storageRackName.visibility = View.VISIBLE
            tv_storageRackName.text = Html.fromHtml("货架:&nbsp;<font color='#000000'>"+entity.storageRack!!.fnumber+"</font>")
        } else {
            tv_storageRackName.visibility = View.INVISIBLE
        }
        if(entity.stockPosition != null ) {
            tv_stockPosName.visibility = View.VISIBLE
            tv_stockPosName.text = Html.fromHtml("库位:&nbsp;<font color='#000000'>"+entity.stockPosition!!.fname+"</font>")
        } else {
            tv_stockPosName.visibility = View.INVISIBLE
        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.view_del -> {  // 选中行
                    if (callBack != null) {
                        callBack!!.onDelete(entity, pos)
                    }
                }
            }
        }
        view_del.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onDelete(entity: BarcodeTable, position: Int)
    }
}
