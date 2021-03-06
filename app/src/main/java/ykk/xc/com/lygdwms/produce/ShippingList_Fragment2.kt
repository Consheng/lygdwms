package ykk.xc.com.lygdwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.shippinglist_fragment2.*
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.*
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.produce.adapter.ShippingList_Fragment2_Adapter
import ykk.xc.com.lygdwms.util.BigdecimalUtil
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.LogUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * 分录
 */
class ShippingList_Fragment2 : BaseFragment() {

    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SUCC2 = 201
        private val UNSUCC2 = 501

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
    }
    private val context = this
    private var okHttpClient: OkHttpClient? = null
    private var user: User? = null
    private var mContext: Activity? = null
    private var parent: ShippingList_MainActivity? = null
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    var listDatas = ArrayList<ShippingListEntry>()
    private var mAdapter: ShippingList_Fragment2_Adapter? = null
//    private val df = DecimalFormat("#.####")
    private val mapBarcodeQty = HashMap<Int, Double>()
    private var curPos = -1
    private var bt :BarcodeTable? = null

    // 消息处理
    private val mHandler = MyHandler(this)
    private class MyHandler(activity: ShippingList_Fragment2) : Handler() {
        private val mActivity: WeakReference<ShippingList_Fragment2>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()

                var errMsg: String? = null
                var msgObj: String? = null
                if (msg.obj is String) {
                    msgObj = msg.obj as String
                }
                when (msg.what) {
                    SUCC1 -> { // 扫码成功 进入
                        m.bt = JsonUtil.strToObject(msgObj, BarcodeTable::class.java)
                        m.scanAConfirmDialog()
//                        m.setRowData(bt)
                    }
                    UNSUCC1 -> { // 扫码失败 进入
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有查询到数据！"
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                    SUCC2 -> { // 删除成功 进入
                        m.deleteRow(m.curPos)
                    }
                    UNSUCC2 -> { // 删除失败 进入
//                        Comm.showWarnDialog(m.mContext, "删除失败！")
                        m.deleteRow(m.curPos)
                    }
                    SETFOCUS -> { // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.et_getFocus)
                        m.setFocusable(m.et_code)
                    }
                    SAOMA -> { // 扫码之后
                        // 执行查询方法
                        m.run_smDatas()
                    }
                }
            }
        }
    }

    @Subscribe
    fun onEventBus(entity: EventBusEntity) {
        when (entity.caseId) {
            1 -> { // 接收第一个页面发来的指令
                if(parent!!.fragment1.listEntry != null) {
                    setEntryList()
                }
            }
            100 -> { // 扫码确认--取消
                et_code.setText("")
                mHandler.sendEmptyMessage(SETFOCUS)
            }
            200 -> { // 扫码确认--确认
                setRowData(bt!!)

                et_code.setText("")
                mHandler.sendEmptyMessage(SETFOCUS)
            }
        }
    }

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.shippinglist_fragment2, container, false)
    }

    override fun initView() {
        mContext = getActivity()
        parent = mContext as ShippingList_MainActivity

        recyclerView.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = ShippingList_Fragment2_Adapter(mContext!!, listDatas)
        recyclerView.adapter = mAdapter
        // 设值listview空间失去焦点
        recyclerView.isFocusable = false

        // 长按清空数量
        mAdapter!!.setOnItemLongClickListener { adapter, holder, view, position ->
            val build = AlertDialog.Builder(mContext)
            build.setIcon(R.drawable.caution)
            build.setTitle("系统提示")
            build.setMessage("您确认要清空选中行的扫码信息吗？")
            build.setPositiveButton("是") { dialog, which ->
                curPos = position
                run_removeByParentId(listDatas[position].id.toString())
            }
            build.setNegativeButton("否", null)
            build.setCancelable(false)
            build.show()
        }

        /*// 行事件
        mAdapter!!.setCallBack(object : Prod_Barcode_Change_Fragment2_Adapter.MyCallBack {
            override fun onDelete(entity: BarcodeTableChange, position: Int) {
            }
        })*/

        EventBus.getDefault().register(this) // 注册EventBus
    }

    override fun initData() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                    //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(120, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(120, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
        }

        getUserInfo()
        hideSoftInputMode(mContext, et_code)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            /*if(parent!!.fragment1.listEntry != null) {
                setEntryList()
            }*/
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
        }
    }

    @OnClick(R.id.btn_scan)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_scan -> { // 调用摄像头扫描（物料）
                ScanUtil.startScan(mContext, BaseFragment.CAMERA_SCAN, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
            }
        }
    }

    override fun setListener() {
        val click = View.OnClickListener { v ->
            setFocusable(et_getFocus)
            when (v.id) {
                R.id.et_code -> setFocusable(et_code)
            }
        }
        et_code.setOnClickListener(click)

        // 物料---数据变化
        et_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) return
                if (!isTextChange) {
                    isTextChange = true
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
        // 物料---长按输入条码
        et_code.setOnLongClickListener {
            showInputDialog("输入条码号", getValues(et_code).trim(), "none", WRITE_CODE)
            true
        }
        // 物料---焦点改变
        et_code.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                lin_focusMtl.setBackgroundResource(R.drawable.back_style_red_focus)
            } else {
                if (lin_focusMtl != null) {
                    lin_focusMtl.setBackgroundResource(R.drawable.back_style_gray4)
                }
            }
        }

    }

    /**
     * 删除行扫码信息
     */
    private fun deleteRow(position :Int) {
        listDatas[position].shippingListEntryBarcodes.forEach{
            if(mapBarcodeQty.containsKey(it.barcodeId)) {
                mapBarcodeQty.remove(it.barcodeId)
            }
        }
        listDatas[position].shippingListEntryBarcodes.clear()
        listDatas[position].outQty = 0.0
    }

    private fun setEntryList() {
        listDatas.addAll(parent!!.fragment1.listEntry!!)
        val listBarcodes = ArrayList<ShippingListEntryBarcode>()
        listDatas.forEach{
//            it.outQty = 0.0
            listBarcodes.addAll(it.shippingListEntryBarcodes)
        }
        mapBarcodeQty.clear()
        // 先把每个条码的可用数量记录起来，第二次循环就减去历史扫码数量，得到可用扫码数
        listBarcodes.forEach {
            mapBarcodeQty.put(it.barcodeId, it.barcodeQty)
        }
        // 计算条码目前可用的扫码数
        listBarcodes.forEach {
            if(mapBarcodeQty.containsKey(it.barcodeId)) {
                val barcodeQty = mapBarcodeQty.get(it.barcodeId)
                val subVal = BigdecimalUtil.sub(barcodeQty!!, it.fqty)
                mapBarcodeQty.put(it.barcodeId, subVal)
            }
        }

        mAdapter!!.notifyDataSetChanged()
        parent!!.fragment1.listEntry = null
    }

    /**
     * 重置
     */
    fun reset() {
        et_code.setText("")
        mapBarcodeQty.clear()

        listDatas.clear()
        mAdapter!!.notifyDataSetChanged()
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 扫码之后弹框确认
     */
    private fun scanAConfirmDialog() {
        var position = -1
        listDatas.forEachIndexed { index, it ->
            if(bt!!.materialId == it.materialId && isNULLS(bt!!.forderBillNo).equals(it.orderNo)) {
                position = index
            }
        }
        val map = HashMap<String, String>()
        map.put("item1", if(position == -1) "0" else (position+1).toString())
        map.put("item2", bt!!.materialName)
        map.put("item3", bt!!.forderBillNo)
        if(position == -1) {
            if(bt!!.caseId == 31 || bt!!.caseId == 32 || bt!!.caseId == 33) {
                Comm.showWarnDialog(mContext,"当前条码（"+bt!!.barcode+"）不能匹配到行列表！")
                return
            }
            map.put("item4", bt!!.remainQty.toString())
            map.put("item5", "0")
            map.put("item6", bt!!.remainQty.toString())
            map.put("item7", bt!!.remainQty.toString())
            map.put("item8", bt!!.remainQty.toString())
            map.put("item9", "0" )
            map.put("item10", "0" )

        } else {

            if(!mapBarcodeQty.containsKey(bt!!.id)) {
                mapBarcodeQty.put(bt!!.id, bt!!.remainQty)
            }
            // 如果用完了提示
            if(mapBarcodeQty[bt!!.id]!! <= 0) {
                Comm.showWarnDialog(mContext,"当前条码（"+bt!!.barcode+"）数量已经用完！")
                return
            }
            val subVal = BigdecimalUtil.sub(listDatas[position].usableQty, listDatas[position].outQty)
            val usableQty = mapBarcodeQty.get(bt!!.id)!!
            var realQty = 0.0

            if (subVal > usableQty || listDatas[position].id == 0) {
                realQty = usableQty

            } else {
                realQty = subVal
            }

            map.put("item4", listDatas[position].usableQty.toString())
            map.put("item5", listDatas[position].outQty.toString())
            map.put("item6", usableQty.toString())
            map.put("item7", realQty.toString())
            val sumSmQty = BigdecimalUtil.add(listDatas[position].outQty, realQty)
            val remainSmQty = BigdecimalUtil.sub(listDatas[position].usableQty, sumSmQty)
            map.put("item8", sumSmQty.toString() )
            map.put("item9", if(remainSmQty <= 0) "0" else remainSmQty.toString() )
            map.put("item10", "0" )
        }

        val dialog = ShippingScanConfirmDialog(mContext!!, map)
        dialog.show()
    }

    /**
     * 设置行数据
     */
    private fun setRowData(bt :BarcodeTable) {
        var isMatch = false
        var position = -1
        listDatas.forEachIndexed { index, it ->
            if(bt.materialId == it.materialId && isNULLS(bt.forderBillNo).equals(it.orderNo)) {
                isMatch = true
                if(it.outQty < it.usableQty || it.id == 0) {
                    position = index
                }
            }
        }
        if(isMatch) {
            if(position > -1) {
                /*if(!mapBarcodeQty.containsKey(bt.id)) {
                    mapBarcodeQty.put(bt.id, bt.remainQty)
                }
                // 如果用完了提示
                if(mapBarcodeQty[bt.id]!! <= 0) {
                    Comm.showWarnDialog(mContext,"当前条码（"+bt.barcode+"）数量已经用完！")
                    return
                }
                */
                val subVal = BigdecimalUtil.sub(listDatas[position].usableQty, listDatas[position].outQty)
                val usableQty = mapBarcodeQty.get(bt.id)!!
                var updateBarcodeQty = 0.0
                var realQty = 0.0
                if(subVal > usableQty || listDatas[position].id == 0) {
                    realQty = usableQty
                    updateBarcodeQty = BigdecimalUtil.sub(usableQty, usableQty)

                } else {
                    realQty = subVal
                    updateBarcodeQty = BigdecimalUtil.sub(usableQty, subVal)
                }
                mapBarcodeQty[bt.id] = updateBarcodeQty

                var isExist = false
                listDatas[position].shippingListEntryBarcodes.forEach{
                    if(it.barcodeId == bt.id) {
                        isExist = true
                    }
                }
                if(!isExist) {
                    val entryBarcode = ShippingListEntryBarcode()
                    entryBarcode.barcodeId = bt.id
                    entryBarcode.barcodeQty = usableQty
                    entryBarcode.fqty = realQty
                    entryBarcode.createUserId = user!!.id
                    entryBarcode.createUserName = user!!.username

                    listDatas[position].shippingListEntryBarcodes.add(entryBarcode)
                }
                listDatas[position].outQty += realQty
            }

        } else { // 没有匹配到就新增一行
            /*if(bt.caseId == 31 || bt.caseId == 32 || bt.caseId == 33) {
                Comm.showWarnDialog(mContext,"当前条码（"+bt.barcode+"）不能匹配到行列表！")
                return
            }*/

            val m = ShippingListEntry()
            m.id = 0
            m.fid = parent!!.fragment1.shippingList.id
            m.orderId = bt.forderBillId
            m.orderEntryId = bt.forderEntryId
            m.orderNo = bt.forderBillNo
            m.customerNo = listDatas[0].customerNo
            m.customerId = listDatas[0].customerId
            m.lineNumber = listDatas.size + 1
            m.materialId = bt.materialId
            m.materialNumber = bt.materialNumber
            m.productName = bt.materialName
            m.model = bt.materialSize
            m.qty = bt.remainQty
            m.outQty = bt.remainQty
            m.unitName = bt.unitName

            val entryBarcode = ShippingListEntryBarcode()
            entryBarcode.barcodeId = bt.id
            entryBarcode.barcodeQty = bt.remainQty
            entryBarcode.fqty = bt.remainQty
            entryBarcode.createUserId = user!!.id
            entryBarcode.createUserName = user!!.username
            // 添加条码
            m.shippingListEntryBarcodes.add(entryBarcode)

            listDatas.add(m)
        }

        mAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                WRITE_CODE -> { // 输入条码  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        setTexts(et_code, value.toUpperCase())
                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 调用华为扫码接口，返回的值
     */
    fun getScanData(barcode :String) {
        setTexts(et_code, barcode)
    }

    /**
     * 扫码查询对应的方法
     */
    private fun run_smDatas() {
        isTextChange = false
        showLoadDialog("加载中...", false)
        var mUrl = getURL("barcodeTable/findBarcode")
        val formBody = FormBody.Builder()
                .add("barcode", getValues(et_code).trim())
                .add("strCaseId", "30,31,32,33,34")
//                .add("searchStockInfo", "1")    // 查询仓库信息
                .add("searchUnitInfo", "1")     // 查询单位信息
                .build()

        val request = Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC1)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_smDatas --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC1, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC1, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 删除行的扫码信息
     */
    private fun run_removeByParentId(parentId :String) {
        showLoadDialog("加载中...", false)
        var mUrl = getURL("shippingList/removeByParentId")
        val formBody = FormBody.Builder()
            .add("parentId", parentId)
            .build()

        val request = Request.Builder()
            .addHeader("cookie", getSession())
            .url(mUrl)
            .post(formBody)
            .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC2)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_removeByParentId --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC2, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC2, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 得到用户对象
     */
    private fun getUserInfo() {
        if (user == null) user = showUserByXml()
    }

    override fun onDestroyView() {
        closeHandler(mHandler)
        mBinder!!.unbind()
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }
}