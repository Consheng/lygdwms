package ykk.xc.com.lygdwms.basics.adapter

import android.app.Activity
import android.text.Html
import android.widget.TextView
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter

class Mtl_DialogAdapter(private val context: Activity, private val datas: List<Material_K3>) : BaseArrayRecyclerAdapter<Material_K3>(datas) {
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.ab_mtl_list_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: Material_K3, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_fnumber = holder.obtainView<TextView>(R.id.tv_fnumber)
        val tv_fname = holder.obtainView<TextView>(R.id.tv_fname)
        val tv_fmodel = holder.obtainView<TextView>(R.id.tv_fmodel)
        // 赋值
        tv_row!!.setText((pos + 1).toString())
        tv_fname!!.setText(entity.fname)
        tv_fnumber.text = Html.fromHtml("代码:&nbsp;<font color='#6a5acd'>"+entity.fnumber+"</font>")
        tv_fmodel.text = Html.fromHtml("规格:&nbsp;<font color='#6a5acd'>"+entity.materialSize+"</font>")
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onClick(entity: Material_K3, position: Int)
    }

}
