package ykk.xc.com.lygdwms.basics.adapter

import android.app.Activity
import android.widget.TextView

import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.StockPosition
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter

class StockPos_DialogAdapter(private val context: Activity, private val datas: List<StockPosition>) : BaseArrayRecyclerAdapter<StockPosition>(datas) {
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.ab_stock_pos_dialog_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: StockPosition, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_fnumber = holder.obtainView<TextView>(R.id.tv_fnumber)
        val tv_fname = holder.obtainView<TextView>(R.id.tv_fname)
        // 赋值
        tv_row!!.text = (pos + 1).toString()
        tv_fnumber!!.text = entity.fnumber
        tv_fname!!.text = entity.fname
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onClick(entity: StockPosition, position: Int)
    }

}
