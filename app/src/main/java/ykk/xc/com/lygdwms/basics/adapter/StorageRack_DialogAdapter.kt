package ykk.xc.com.lygdwms.basics.adapter

import android.app.Activity
import android.widget.TextView
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.StorageRack
import ykk.xc.com.lygdwms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.lygdwms.util.basehelper.BaseRecyclerAdapter

class StorageRack_DialogAdapter(private val context: Activity, private val datas: List<StorageRack>) : BaseArrayRecyclerAdapter<StorageRack>(datas) {
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.ab_storage_rack_dialog_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: StorageRack, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_fnumber = holder.obtainView<TextView>(R.id.tv_fnumber)
        // 赋值
        tv_row!!.text = (pos + 1).toString()
        tv_fnumber!!.text = entity.fnumber
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onClick(entity: StorageRack, position: Int)
    }

}
