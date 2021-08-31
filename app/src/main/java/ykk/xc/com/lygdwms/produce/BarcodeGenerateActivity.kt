package ykk.xc.com.lygdwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import butterknife.OnClick
import com.gprinter.command.EscCommand
import com.gprinter.command.LabelCommand
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.ab_item4_inventorynow_search.*
import kotlinx.android.synthetic.main.barcode_generate.*
import okhttp3.*
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.basics.Stock_GroupDialogActivity
import ykk.xc.com.lygdwms.bean.*
import ykk.xc.com.lygdwms.bean.k3Bean.*
import ykk.xc.com.lygdwms.comm.BaseActivity
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.produce.adapter.BarcodeGenerate_Adapter
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.blueTooth.*
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * 物料条码生成并打印
 */
class BarcodeGenerateActivity : BaseActivity() {

    companion object {
        private val SEL_POSITION = 61
        private val SUCC1 = 200
        private val UNSUCC1 = 501
        private val SUCC2 = 202
        private val UNSUCC2 = 502
        private val SAVE = 203
        private val UNSAVE = 503

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
        private val RESULT_QTY = 4
    }
    private val context = this
    private val listDatas = ArrayList<BarcodeTable>()
    private var mAdapter: BarcodeGenerate_Adapter? = null
    private var user: User? = null
    private val okHttpClient = OkHttpClient()
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var curPos = -1
    private var listPrintData :ArrayList<BarcodeTable>? = null
    private var checkAfterUpdateData = true    // 全选改变状态是否要改变行数据
    private var sourceType = 1   // 来源类型：1：生产入库单，2：采购入库单，3：其他入库单，4：直接调拨单，5：销售订单
    private var isCreateCode = false // 查询是否有生码
    private var smFlag = '1' // 扫描类型 1：物料，2：位置
    private var stock: Stock? = null
    private var stockArea: StockArea? = null
    private var storageRack: StorageRack? = null
    private var stockPosition: StockPosition? = null

    private val id = 0 // 设备id
    private var threadPool: ThreadPool? = null
    private var isConnected: Boolean = false // 蓝牙是否连接标识
    private val CONN_STATE_DISCONN = 0x007 // 连接状态断开
    private val PRINTER_COMMAND_ERROR = 0x008 // 使用打印机指令错误
    private val CONN_PRINTER = 0x12

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: BarcodeGenerateActivity) : Handler() {
        private val mActivity: WeakReference<BarcodeGenerateActivity>

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
                    SUCC1 -> { // 扫码    成功
                        when(m.smFlag) {
                            '1'-> { // 物料
                                m.listDatas.clear()
                                val strJson = JsonUtil.strToString(msgObj)
                                when(m.sourceType) {
                                    1 -> m.setRowData_prdInStock(strJson)   // 生产入库单
                                    2 -> m.setRowData_purInStock(strJson)   // 采购入库单
                                    3 -> m.setRowData_otherInStock(strJson) // 其他入库单
                                    4 -> m.setRowData_stkTransfer(strJson)  // 直接调拨单
                                    5 -> m.setRowData_salOrder(strJson)     // 销售订单
                                }
                                m.cbAll.isChecked = true
                                // 查询是否有生码
                                m.run_findIsCreateCode()
                            }
                            '2'-> { // 仓库位置
                                m.resetStockGroup()
                                m.getStockGroup(msgObj)
                            }
                        }
                    }
                    UNSUCC1 -> { // 扫码    失败！
                        when(m.smFlag) {
                            '1' -> {
                                m.listDatas.clear()
                                m.mAdapter!!.notifyDataSetChanged()
                            }
                            '2' -> {
                                m.tv_positionName.text = ""
                            }
                        }
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有找到数据！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SUCC2 -> { // 查询是否有生码    成功
                        m.isCreateCode = true
                    }
                    UNSUCC2 -> { // 查询是否有生码    失败！
                        m.isCreateCode = false
                    }
                    SAVE -> { // 保存 成功
                        val list = JsonUtil.strToList(msg.obj as String, BarcodeTable::class.java)
                        val map = HashMap<String, String>()
                        list.forEach {
//                            it.isCheck = false
                            map.put(it.relationBillId.toString()+"_"+it.relationBillEntryId.toString()+"_id", it.id.toString())
                            map.put(it.relationBillId.toString()+"_"+it.relationBillEntryId.toString()+"_barcode", it.barcode)
                        }
                        m.listDatas.forEach {
                            it.isCheck = false
                            if(map.containsKey(it.relationBillId.toString()+"_"+it.relationBillEntryId.toString()+"_id")) {
                                it.id = m.parseInt(map.get(it.relationBillId.toString()+"_"+it.relationBillEntryId.toString()+"_id"))
                                it.barcode = map.get(it.relationBillId.toString()+"_"+it.relationBillEntryId.toString()+"_barcode")
                            }
                        }
                        m.cbAll.isChecked = false
//                        m.listDatas.clear()
//                        m.listDatas.addAll(list)
                        m.mAdapter!!.notifyDataSetChanged()
                        m.isCreateCode = true
                        m.smFlag = '1'
                        m.mHandler.sendEmptyMessage(SETFOCUS)
                    }
                    UNSAVE -> { // 保存  失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "保存失败！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SETFOCUS -> { // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.et_getFocus)
                        when(m.smFlag) {
                            '1' -> m.setFocusable(m.et_code)
                            '2' -> m.setFocusable(m.et_positionCode)
                        }
                    }
                    SAOMA -> { // 扫码之后
                        // 执行查询方法
                        m.run_smDatas()
                    }
                    m.CONN_STATE_DISCONN -> if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id] != null) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id].closePort(m.id)
                    }
                    m.PRINTER_COMMAND_ERROR -> Utils.toast(m.context, m.getString(R.string.str_choice_printer_command))
                    m.CONN_PRINTER -> Utils.toast(m.context, m.getString(R.string.str_cann_printer))
                    Constant.MESSAGE_UPDATE_PARAMETER -> {
                        val strIp = msg.data.getString("Ip")
                        val strPort = msg.data.getString("Port")
                        //初始化端口信息
                        DeviceConnFactoryManager.Build()
                                //设置端口连接方式
                                .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI)
                                //设置端口IP地址
                                .setIp(strIp)
                                //设置端口ID（主要用于连接多设备）
                                .setId(m.id)
                                //设置连接的热点端口号
                                .setPort(Integer.parseInt(strPort))
                                .build()
                        m.threadPool = ThreadPool.getInstantiation()
                        m.threadPool!!.addTask(Runnable { DeviceConnFactoryManager.getDeviceConnFactoryManagers()[m.id].openPort() })
                    }
                }
            }
        }
    }

    override fun setLayoutResID(): Int {
        return R.layout.barcode_generate
    }

    override fun initView() {
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = BarcodeGenerate_Adapter(context, listDatas)
        recyclerView.adapter = mAdapter
        // 设值listview空间失去焦点
        recyclerView.isFocusable = false

        // 行事件
        mAdapter!!.setCallBack(object : BarcodeGenerate_Adapter.MyCallBack {
            override fun onCheck(entity: BarcodeTable, pos: Int) {
                if(isNULLS(listDatas[pos].barcode).length > 0) {
                    return
                }
                val isCheck = listDatas[pos].isCheck
                if(isCheck) listDatas[pos].isCheck = false
                else listDatas[pos].isCheck = true
                var isAllCheck = true   // 是否全部选中
                listDatas.forEach {
                    if(!it.isCheck) {
                        isAllCheck = false
                    }
                }
                checkAfterUpdateData = false
                if(isAllCheck && !cbAll.isChecked) {
                    cbAll.isChecked = true
                } else if(!isAllCheck) cbAll.isChecked = false

                mAdapter!!.notifyDataSetChanged()

                checkAfterUpdateData = true
            }
        })

        // 点击输入数量
        mAdapter!!.setOnItemClickListener { adapter, holder, view, pos ->
            if(isNULLS(listDatas[pos].barcode).length > 0) {
                return@setOnItemClickListener
            }
            curPos = pos
            showInputDialog("数量", listDatas[pos].barcodeQty.toString(), "0", RESULT_QTY)
        }

        // 长按选择仓库信息
        /*mAdapter!!.setOnItemLongClickListener { adapter, holder, view, pos ->
            curPos = pos
            val bundle = Bundle()
            bundle.putSerializable("stock", listDatas[pos].stock)
            bundle.putSerializable("stockArea", listDatas[pos].stockArea)
            bundle.putSerializable("storageRack", listDatas[pos].storageRack)
            bundle.putSerializable("stockPosition", listDatas[pos].stockPosition)
            showForResult(Stock_GroupDialogActivity::class.java, SEL_POSITION, bundle)
        }*/

    }

    override fun initData() {
        getUserInfo()
        hideSoftInputMode(et_code)
        hideSoftInputMode(et_positionCode)
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_reset, R.id.btn_sourceType, R.id.btn_scan, R.id.btn_positionScan, R.id.btn_positionSel, R.id.btn_save)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                closeHandler(mHandler)
                context.finish()
            }
            R.id.btn_reset -> { // 重置
                if (listDatas.size > 0 && listDatas[0].barcode == null) {
                    val build = AlertDialog.Builder(context)
                    build.setIcon(R.drawable.caution)
                    build.setTitle("系统提示")
                    build.setMessage("您有未保存的数据，继续重置吗？")
                    build.setPositiveButton("是") { dialog, which -> reset() }
                    build.setNegativeButton("否", null)
                    build.setCancelable(false)
                    build.show()

                } else {
                    reset()
                }
            }
            R.id.btn_sourceType -> { // 来源单据选择
                if(listDatas.size > 0) {
                    Comm.showWarnDialog(context,"请先对当前数据生码！")
                    return
                }
                pop_sourceType(view)
                popWindow!!.showAsDropDown(view)
            }
            R.id.btn_scan -> { // 调用摄像头扫描（物料）
                smFlag = '1'
                ScanUtil.startScan(context, BaseFragment.CAMERA_SCAN, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
            }
            R.id.btn_positionScan -> { // 调用摄像头扫描（位置）
                smFlag = '2'
                ScanUtil.startScan(context, BaseFragment.CAMERA_SCAN, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
            }
            R.id.btn_positionSel -> { // 选择仓库
                smFlag = '2'
                val bundle = Bundle()
                bundle.putSerializable("stock", stock)
                bundle.putSerializable("stockArea", stockArea)
                bundle.putSerializable("storageRack", storageRack)
                bundle.putSerializable("stockPosition", stockPosition)
                showForResult(Stock_GroupDialogActivity::class.java, SEL_POSITION, bundle)
            }
            R.id.btn_save -> {  // 生码
                if(listDatas.size == 0) {
                    Comm.showWarnDialog(context, "请扫描条码！")
                    return
                }
                var isSelected = false
                listDatas.forEach {
                    if(it.isCheck) {
                        isSelected = true

                        if(listDatas[0].stockId == 0) {
                            Comm.showWarnDialog(context, "请选择或扫描位置！")
                            return
                        }
                    }
                }
                if(!isSelected) {
                    Comm.showWarnDialog(context, "请选中行，进行生码打印！")
                    return
                }
                if (isCreateCode) {
                    val build = AlertDialog.Builder(context)
                    build.setIcon(R.drawable.caution)
                    build.setTitle("系统提示")
                    build.setMessage("您已经生成过条码，还要生成吗？")
                    build.setPositiveButton("是") { dialog, which -> run_add() }
                    build.setNegativeButton("否", null)
                    build.setCancelable(false)
                    build.show()

                } else {
                    run_add()
                }
//                run_add()
            }
        }
    }

    override fun setListener() {
        val click = View.OnClickListener { v ->
            setFocusable(et_getFocus)
            when (v.id) {
                R.id.et_code -> setFocusable(et_code)
                R.id.et_positionCode -> setFocusable(et_positionCode)
            }
        }
        et_code.setOnClickListener(click)
        et_positionCode.setOnClickListener(click)

        // 物料---数据变化
        et_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) return
                if (!isTextChange) {
                    isTextChange = true
                    smFlag = '1'
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
        // 物料---长按输入条码
        et_code.setOnLongClickListener {
            smFlag = '1'
            showInputDialog("输入条码号", getValues(et_code).trim(), "none", WRITE_CODE)
            true
        }
        // 物料---焦点改变
        et_code.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                lin_focusMtl.setBackgroundResource(R.drawable.back_style_red_focus)
            } else {
                if (lin_focusMtl != null) {
                    lin_focusMtl!!.setBackgroundResource(R.drawable.back_style_gray4)
                }
            }
        }

        // 位置---数据变化
        et_positionCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) return
                if (!isTextChange) {
                    isTextChange = true
                    smFlag = '2'
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
        // 位置---长按输入条码
        et_positionCode!!.setOnLongClickListener {
            smFlag = '2'
            showInputDialog("输入条码号", getValues(et_positionCode).trim(), "none", WRITE_CODE)
            true
        }
        // 位置---焦点改变
        et_positionCode.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                lin_focusPosition.setBackgroundResource(R.drawable.back_style_red_focus)
            } else {
                if (lin_focusPosition != null) {
                    lin_focusPosition!!.setBackgroundResource(R.drawable.back_style_gray4)
                }
            }
        }

        cbAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if(checkAfterUpdateData) {
                if (isChecked) {
                    listDatas.forEach {
                        it.isCheck = true
                    }
                } else {
                    listDatas.forEach {
                        it.isCheck = false
                    }
                }
                if (listDatas.size > 0) mAdapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * 重置
     */
    private fun reset() {
        smFlag = '1'
//        sourceType = 1
//        btn_sourceType.text = "生产入库单 ▼"
        et_code.setText("")
//        et_code.setHint("请扫描生产入库单条码")
        et_positionCode.setText("")
        listDatas.clear()
        mAdapter!!.notifyDataSetChanged()
        cbAll.isChecked = false
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 设置行数据（生产入库单）
     */
    private fun setRowData_prdInStock(strJson :String) {
        val list = JsonUtil.stringToList(strJson, PrdInStockEntry::class.java)
        cbAll.isChecked = false
        list.forEach {
            if((it.fqty.toInt()) > 0) {
                val bt = BarcodeTable()
                bt.caseId = 30
                bt.relationBillId = it.fid
                bt.relationBillEntryId = it.fentryId
                bt.relationBillNumber = it.prdInStock.fbillNo
                bt.relationBillQty = it.fqty
                bt.materialId = it.fmaterialId
                bt.materialNumber = it.material.fnumber
                bt.materialName = it.material.fname
                bt.materialSize = it.material.materialSize
                bt.unitName = it.unit.fname
                bt.barcodeQty = (it.fqty.toInt()).toDouble()
                bt.remainQty = (it.fqty.toInt()).toDouble()
                if (it.salOrder != null) {
                    bt.forderBillId = it.freqBillId
                    bt.forderEntryId = it.freqEntryId
                    bt.forderBillNo = it.freqBillNo
                    bt.custNumber = it.salOrder.cust.fnumber
                    bt.custName = it.salOrder.cust.fname
                }
                bt.createUserId = user!!.id
                bt.createUserName = user!!.username
                // 加入仓库
                if (stock != null) {
                    bt.stock = stock
                    bt.stockId = stock!!.id
                }
                if (stockArea != null) {
                    bt.stockArea = stockArea
                    bt.stockAreaId = stockArea!!.id
                }
                if (storageRack != null) {
                    bt.storageRack = storageRack
                    bt.storageRackId = storageRack!!.id
                }
                if (stockPosition != null) {
                    bt.stockPosition = stockPosition
                    bt.stockPositionId = stockPosition!!.id
                }

                listDatas.add(bt)
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * 设置行数据（采购入库单）
     */
    private fun setRowData_purInStock(strJson :String) {
        val list = JsonUtil.stringToList(strJson, StkInStockEntry::class.java)
        cbAll.isChecked = false
        list.forEach {
            if((it.fqty.toInt()) > 0) {
                val bt = BarcodeTable()
                bt.caseId = 31
                bt.relationBillId = it.fid
                bt.relationBillEntryId = it.fentryId
                bt.relationBillNumber = it.stkInStock.fbillNo
                bt.relationBillQty = it.fqty
                bt.materialId = it.fmaterialId
                bt.materialNumber = it.material.fnumber
                bt.materialName = it.material.fname
                bt.materialSize = it.material.materialSize
                bt.unitName = it.unit.fname
                bt.barcodeQty = (it.fqty.toInt()).toDouble()
                bt.remainQty = (it.fqty.toInt()).toDouble()
                bt.createUserId = user!!.id
                bt.createUserName = user!!.username
                // 加入仓库
                if (stock != null) {
                    bt.stock = stock
                    bt.stockId = stock!!.id
                }
                if (stockArea != null) {
                    bt.stockArea = stockArea
                    bt.stockAreaId = stockArea!!.id
                }
                if (storageRack != null) {
                    bt.storageRack = storageRack
                    bt.storageRackId = storageRack!!.id
                }
                if (stockPosition != null) {
                    bt.stockPosition = stockPosition
                    bt.stockPositionId = stockPosition!!.id
                }

                listDatas.add(bt)
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * 设置行数据（其他入库单）
     */
    private fun setRowData_otherInStock(strJson :String) {
        val list = JsonUtil.stringToList(strJson, OtherInStockEntry::class.java)
        cbAll.isChecked = false
        list.forEach {
            if((it.fqty.toInt()) > 0) {
                val bt = BarcodeTable()
                bt.caseId = 32
                bt.relationBillId = it.fid
                bt.relationBillEntryId = it.fentryId
                bt.relationBillNumber = it.otherInStock.fbillNo
                bt.relationBillQty = it.fqty
                bt.materialId = it.fmaterialId
                bt.materialNumber = it.material.fnumber
                bt.materialName = it.material.fname
                bt.materialSize = it.material.materialSize
                bt.unitName = it.unit.fname
                bt.barcodeQty = (it.fqty.toInt()).toDouble()
                bt.remainQty = (it.fqty.toInt()).toDouble()
                bt.createUserId = user!!.id
                bt.createUserName = user!!.username
                // 加入仓库
                if (stock != null) {
                    bt.stock = stock
                    bt.stockId = stock!!.id
                }
                if (stockArea != null) {
                    bt.stockArea = stockArea
                    bt.stockAreaId = stockArea!!.id
                }
                if (storageRack != null) {
                    bt.storageRack = storageRack
                    bt.storageRackId = storageRack!!.id
                }
                if (stockPosition != null) {
                    bt.stockPosition = stockPosition
                    bt.stockPositionId = stockPosition!!.id
                }

                listDatas.add(bt)
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * 设置行数据（直接调拨单）
     */
    private fun setRowData_stkTransfer(strJson :String) {
        val list = JsonUtil.stringToList(strJson, StkTransferEntry::class.java)
        cbAll.isChecked = false
        list.forEach {
            if((it.fqty.toInt()) > 0) {
                val bt = BarcodeTable()
                bt.caseId = 33
                bt.relationBillId = it.fid
                bt.relationBillEntryId = it.fentryId
                bt.relationBillNumber = it.stkTransfer.fbillNo
                bt.relationBillQty = it.fqty
                bt.materialId = it.fmaterialId
                bt.materialNumber = it.material.fnumber
                bt.materialName = it.material.fname
                bt.materialSize = it.material.materialSize
                bt.unitName = it.unit.fname
                bt.barcodeQty = (it.fqty.toInt()).toDouble()
                bt.remainQty = (it.fqty.toInt()).toDouble()
                bt.createUserId = user!!.id
                bt.createUserName = user!!.username
                // 加入仓库
                if (stock != null) {
                    bt.stock = stock
                    bt.stockId = stock!!.id
                }
                if (stockArea != null) {
                    bt.stockArea = stockArea
                    bt.stockAreaId = stockArea!!.id
                }
                if (storageRack != null) {
                    bt.storageRack = storageRack
                    bt.storageRackId = storageRack!!.id
                }
                if (stockPosition != null) {
                    bt.stockPosition = stockPosition
                    bt.stockPositionId = stockPosition!!.id
                }

                listDatas.add(bt)
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * 设置行数据（销售订单）
     */
    private fun setRowData_salOrder(strJson :String) {
        val list = JsonUtil.stringToList(strJson, SalOrderEntry::class.java)
        cbAll.isChecked = false
        list.forEach {
            if((it.fqty.toInt()) > 0) {
                val bt = BarcodeTable()
                bt.caseId = 34
                bt.relationBillId = it.fid
                bt.relationBillEntryId = it.fentryId
                bt.relationBillNumber = it.salOrder.fbillNo
                bt.relationBillQty = it.fqty
                bt.materialId = it.fmaterialId
                bt.materialNumber = it.material.fnumber
                bt.materialName = it.material.fname
                bt.materialSize = it.material.materialSize
                bt.unitName = it.unit.fname
                bt.barcodeQty = (it.fqty.toInt()).toDouble()
                bt.remainQty = (it.fqty.toInt()).toDouble()
                if (it.salOrder != null) {
                    bt.forderBillId = it.fid
                    bt.forderEntryId = it.fentryId
                    bt.forderBillNo = it.salOrder.fbillNo
                    bt.custNumber = it.salOrder.cust.fnumber
                    bt.custName = it.salOrder.cust.fname
                }
                bt.createUserId = user!!.id
                bt.createUserName = user!!.username
                // 加入仓库
                if (stock != null) {
                    bt.stock = stock
                    bt.stockId = stock!!.id
                }
                if (stockArea != null) {
                    bt.stockArea = stockArea
                    bt.stockAreaId = stockArea!!.id
                }
                if (storageRack != null) {
                    bt.storageRack = storageRack
                    bt.storageRackId = storageRack!!.id
                }
                if (stockPosition != null) {
                    bt.stockPosition = stockPosition
                    bt.stockPositionId = stockPosition!!.id
                }

                listDatas.add(bt)
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    fun resetStockGroup() {
        stock = null
        stockArea = null
        storageRack = null
        stockPosition = null
    }

    /**
     * 得到仓库组
     */
    fun getStockGroup(msgObj : String?) {
        // 重置数据
        tv_positionName.text = ""
        if(listDatas.size > 0) {
            listDatas.forEach {
                it.stock = null
                it.stockId = 0
                it.stockArea = null
                it.stockAreaId = 0
                it.storageRack = null
                it.storageRackId = 0
                it.stockPosition = null
                it.stockPositionId = 0
            }
        }

        if(msgObj != null) {
            stock = null
            stockArea = null
            storageRack = null
            stockPosition = null

            var caseId:Int = 0
            if(msgObj.indexOf("Stock_CaseId=1") > -1) {
                caseId = 1
            } else if(msgObj.indexOf("StockArea_CaseId=2") > -1) {
                caseId = 2
            } else if(msgObj.indexOf("StorageRack_CaseId=3") > -1) {
                caseId = 3
            } else if(msgObj.indexOf("StockPosition_CaseId=4") > -1) {
                caseId = 4
            }

            when(caseId) {
                1 -> {
                    stock = JsonUtil.strToObject(msgObj, Stock::class.java)
                }
                2 -> {
                    stockArea = JsonUtil.strToObject(msgObj, StockArea::class.java)
                    if(stockArea!!.stock != null) stock = stockArea!!.stock
                }
                3 -> {
                    storageRack = JsonUtil.strToObject(msgObj, StorageRack::class.java)
                    if(storageRack!!.stock != null) stock = storageRack!!.stock
                    if(storageRack!!.stockArea != null) stockArea = storageRack!!.stockArea
                }
                4 -> {
                    stockPosition = JsonUtil.strToObject(msgObj, StockPosition::class.java)
                    if(stockPosition!!.stock != null) stock = stockPosition!!.stock
                    if(stockPosition!!.stockArea != null) stockArea = stockPosition!!.stockArea
                    if(stockPosition!!.storageRack != null) storageRack = stockPosition!!.storageRack
                }
            }
        }

        if(stock != null ) {
            tv_positionName.text = Html.fromHtml("仓库:&nbsp;<font color='#6a5acd'>" + stock!!.fname + "</font>")
        }
        if(stockArea != null ) {
            tv_positionName.text = Html.fromHtml("库区:&nbsp;<font color='#6a5acd'>" + stockArea!!.fname + "</font>")
        }
        if(storageRack != null ) {
            tv_positionName.text = Html.fromHtml("货架:&nbsp;<font color='#6a5acd'>" + storageRack!!.fnumber + "</font>")
        }
        if(stockPosition != null ) {
            tv_positionName.text = Html.fromHtml("库位:&nbsp;<font color='#6a5acd'>" + stockPosition!!.fnumber + "</font>")
        }
        // 保存到list
        if(listDatas.size > 0) {
            listDatas.forEach {
                if (stock != null) {
                    it.stock = stock
                    it.stockId = stock!!.id
                }
                if (stockArea != null) {
                    it.stockArea = stockArea
                    it.stockAreaId = stockArea!!.id
                }
                if (storageRack != null) {
                    it.storageRack = storageRack
                    it.storageRackId = storageRack!!.id
                }
                if (stockPosition != null) {
                    it.stockPosition = stockPosition
                    it.stockPositionId = stockPosition!!.id
                }
            }
            mAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * 创建PopupWindow 【 单据类型选择 】
     */
    private var popWindow: PopupWindow? = null
    private fun pop_sourceType(v: View) {
        if (null != popWindow) {//不为空就隐藏
            popWindow!!.dismiss()
            return
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        val popV = layoutInflater.inflate(R.layout.popwindow_billtype, null)
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popWindow = PopupWindow(popV, v.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        // 设置动画效果
        // popWindow.setAnimationStyle(R.style.AnimationFade)
        popWindow!!.setBackgroundDrawable(BitmapDrawable())
        popWindow!!.isOutsideTouchable = true
        popWindow!!.isFocusable = true

        // 点击其他地方消失
        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.btn1 -> {
                    btn_sourceType.text = "生产入库单 ▼"
                    et_code.setHint("请扫描生产入库单条码")
                    sourceType = 1
                }
                R.id.btn2 -> {
                    btn_sourceType.text = "采购入库单 ▼"
                    et_code.setHint("请扫描采购入库单条码")
                    sourceType = 2
                }
                R.id.btn3 -> {
                    btn_sourceType.text = "其他入库单 ▼"
                    et_code.setHint("请扫描其他入库单条码")
                    sourceType = 3
                }
                R.id.btn4 -> {
                    btn_sourceType.text = "直接调拨单 ▼"
                    et_code.setHint("请扫描直接调拨单条码")
                    sourceType = 4
                }
                R.id.btn5 -> {
                    btn_sourceType.text = "销售订单 ▼"
                    et_code.setHint("请扫描销售订单条码")
                    sourceType = 5
                }
            }
            popWindow!!.dismiss()
        }
        popV.findViewById<View>(R.id.btn1).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn2).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn3).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn4).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn5).setOnClickListener(click)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            if (data == null) return
            when (requestCode) {
                BaseFragment.CAMERA_SCAN -> {// 扫一扫成功  返回
                    val hmsScan = data!!.getParcelableExtra(ScanUtil.RESULT) as HmsScan
                    when (smFlag) {
                        '1' -> setTexts(et_code, hmsScan.originalValue)
                        '2' -> setTexts(et_positionCode, hmsScan.originalValue)
                    }
                }
                WRITE_CODE -> {// 输入条码  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        when (smFlag) {
                            '1' -> setTexts(et_code, value.toUpperCase())
                            '2' -> setTexts(et_positionCode, value.toUpperCase())
                        }
                    }
                }
                RESULT_QTY -> {// 输入生码数  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        val fqty = parseInt(value)
                        if (fqty <= 0) {
                            Comm.showWarnDialog(context, "生码数不能为0，（生码数）必须小于或等于（入库数）！")
                            return
                        }
                        if (fqty > listDatas[curPos].relationBillQty) {
                            Comm.showWarnDialog(context, "（生码数）不能大于（入库数）！")
                            return
                        }
                        listDatas[curPos].barcodeQty = fqty.toDouble()
                        listDatas[curPos].remainQty = fqty.toDouble()
                        mAdapter!!.notifyDataSetChanged()
                    }
                }
                SEL_POSITION -> {// 选择位置  返回
                    resetStockGroup()
                    stock = data!!.getSerializableExtra("stock") as Stock
                    if (data!!.getSerializableExtra("stockArea") != null) {
                        stockArea = data!!.getSerializableExtra("stockArea") as StockArea
                    }
                    if (data!!.getSerializableExtra("storageRack") != null) {
                        storageRack = data!!.getSerializableExtra("storageRack") as StorageRack
                    }
                    if (data!!.getSerializableExtra("stockPosition") != null) {
                        stockPosition = data!!.getSerializableExtra("stockPosition") as StockPosition
                    }
                    getStockGroup(null)
                }
                /*SEL_POSITION -> {// 选择位置  返回
                if (resultCode == Activity.RESULT_OK) {
                    listDatas[curPos].stock = null
                    listDatas[curPos].stockId = 0
                    listDatas[curPos].stockArea = null
                    listDatas[curPos].stockAreaId = 0
                    listDatas[curPos].storageRack = null
                    listDatas[curPos].storageRackId = 0
                    listDatas[curPos].stockPosition = null
                    listDatas[curPos].stockPositionId = 0

                    val stock = data!!.getSerializableExtra("stock") as Stock
                    listDatas[curPos].stock = stock
                    listDatas[curPos].stockId = stock.id

                    if (data!!.getSerializableExtra("stockArea") != null) {
                        val stockArea = data!!.getSerializableExtra("stockArea") as StockArea
                        listDatas[curPos].stockArea = stockArea
                        listDatas[curPos].stockAreaId = stockArea.id
                    }
                    if (data!!.getSerializableExtra("storageRack") != null) {
                        val storageRack = data!!.getSerializableExtra("storageRack") as StorageRack
                        listDatas[curPos].storageRack = storageRack
                        listDatas[curPos].storageRackId = storageRack.id
                    }
                    if (data!!.getSerializableExtra("stockPosition") != null) {
                        val stockPosition = data!!.getSerializableExtra("stockPosition") as StockPosition
                        listDatas[curPos].stockPosition = stockPosition
                        listDatas[curPos].stockPositionId = stockPosition.id
                    }
                    mAdapter!!.notifyDataSetChanged()
                }
            }*/
                Constant.BLUETOOTH_REQUEST_CODE -> {
                    /*获取蓝牙mac地址*/
                    val macAddress = data!!.getStringExtra(BluetoothDeviceListDialog.EXTRA_DEVICE_ADDRESS)
                    //初始化话DeviceConnFactoryManager
                    DeviceConnFactoryManager.Build()
                        .setId(id)
                        //设置连接方式
                        .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                        //设置连接的蓝牙mac地址
                        .setMacAddress(macAddress)
                        .build()
                    //打开端口
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort()
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 通过okhttp加载数据
     */
    private fun run_smDatas() {
        isTextChange = false
        showLoadDialog("加载中...", false)
        var mUrl :String? = null
        var barcode = ""
        when(smFlag) {
            '1' -> {
                mUrl = getURL("barcodeTable/findBarcodeBySource")
                barcode = getValues(et_code).trim()
            }
            '2' -> {
                mUrl = getURL("stockPosition/findBarcodeGroup")
                barcode = getValues(et_positionCode).trim()
            }
        }
        val formBody = FormBody.Builder()
                .add("barcode", barcode)
                .add("sourceType", sourceType.toString()) // 来源类型：1：生产入库单，2：采购入库单，3：其他入库单，4：直接调拨单，5：销售订单
                .build()
        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC1)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC1, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC1, result)
                Log.e("run_okhttpDatas --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 查询是否有已生码
     */
    private fun run_findIsCreateCode() {
        var caseId = 30
        when(sourceType) {
            1 -> caseId = 30
            2 -> caseId = 31
            3 -> caseId = 32
            4 -> caseId = 33
            5 -> caseId = 34
        }
        val mUrl = getURL("barcodeTableGenerateCount/findIsCreateCode")
        val formBody = FormBody.Builder()
                .add("billNo", getValues(et_code).trim())
                .add("caseId", caseId.toString())
                .build()
        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC2)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC2, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC2, result)
                Log.e("run_findIsCreateCode --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 生码
     */
    private fun run_add() {
        isTextChange = false
        showLoadDialog("加载中...", false)

        val listResult = ArrayList<BarcodeTable>()
        listDatas.forEach {
            if(it.isCheck) {
                listResult.add(it)
            }
        }
        val formBody = FormBody.Builder()
                .add("strJson", JsonUtil.objectToString(listResult))
                .build()
        val mUrl = getURL("barcodeTable/add")

        val request = Request.Builder()
                .addHeader("cookie", session)
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSAVE)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSAVE, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SAVE, result)
                Log.e("run_add --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 打印方法fragment1
     */
    fun clickPrint(printData : ArrayList<BarcodeTable>) {
        listPrintData = printData
        if (isConnected) {
            setBeginPrint()
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(Intent(this, BluetoothDeviceListDialog::class.java), Constant.BLUETOOTH_REQUEST_CODE)
        }
    }

    /**
     * 设置生产装箱清单打印格式
     */
    private fun setBeginPrint() {
        // 打印箱码
        setBoxFormat1()
//        // 绘制箱子条码
//        var i = 0
//        val size = listMbr!!.size
//        while (i < size) {
//            setProdBoxListFormat2(i)
//            i++
//        }
    }

    /**
     * 打印箱码信息
     */
    private fun setBoxFormat1() {
        val tsc = LabelCommand()
        setTscBegin(tsc)
        // --------------- 打印区-------------Begin

        val beginXPos = 20 // 开始横向位置
        val beginYPos = 12 // 开始纵向位置
        var rowHigthSum = 0 // 纵向高度的叠加
        val rowSpacing = 30 // 每行之间的距离

        val mbr = listPrintData!!.get(0)
        val boxBarCode = listDatas[curPos]
        // 绘制箱子条码
        rowHigthSum = beginYPos + 18
//        tsc.addQRCode(beginXPos, beginXPos, LabelCommand.EEC.LEVEL_L, 10, LabelCommand.ROTATION.ROTATION_0, boxBarCode.barCode)
//        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "箱码： \n")
//        tsc.add1DBarcode(115, rowHigthSum - 18, LabelCommand.BARCODETYPE.CODE39, 65, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, boxBarCode.getBarCode())
        rowHigthSum = beginYPos + 96
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "物流公司：" + 10000 + " \n")
        rowHigthSum = rowHigthSum + rowSpacing
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "客户名称：" + 1000 + " \n")
        rowHigthSum = rowHigthSum + rowSpacing
        //        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"订单编号："+isNULLS(mbr.getSalOrderNo())+" \n");
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "订单编号：" + 123 + " \n")
        tsc.addText(280, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "订单日期：" + 1000 + " \n")

        // --------------- 打印区-------------End
        setTscEnd(tsc)
    }

    /**
     * 打印物料信息2
     */
    private fun setProdBoxListFormat2(pos: Int) {
        val tsc = LabelCommand()
        setTscBegin(tsc)
        // --------------- 打印区-------------Begin

        val beginXPos = 20 // 开始横向位置
        val beginYPos = 0 // 开始纵向位置
        var rowHigthSum = 0 // 纵向高度的叠加
        val rowSpacing = 35 // 每行之间的距离

        val mbr = listPrintData!!.get(pos)
//        val prodOrder = JsonUtil.stringToObject(mbr.getRelationObj(), ProdOrder::class.java)

        tsc.addText(beginXPos, beginYPos, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "------------------------------------------------- \n")
        rowHigthSum = beginYPos + rowSpacing
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "物料编码：" + 100 + " \n")
        rowHigthSum = rowHigthSum + rowSpacing
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "物料名称：" + 1000 + " \n")

        //        String leaf = isNULLS(prodOrder.getLeaf());
        //        String leaf2 = isNULLS(prodOrder.getLeaf1());
        //        String strTmp = "";
        //        if (leaf.length() > 0 && leaf2.length() > 0) strTmp = leaf + " , " + leaf2;
        //        else if (leaf.length() > 0) strTmp = leaf;
        //        else if (leaf2.length() > 0) strTmp = leaf2;
        //        rowHigthSum = rowHigthSum + rowSpacing;
        //        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"面料："+strTmp+" \n");
        rowHigthSum = rowHigthSum + rowSpacing
        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "数量：" + 100 + " \n")
        //        tsc.addText(200, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"宽："+isNULLS(prodOrder.getWidth())+" \n");
        //        tsc.addText(360, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"高："+isNULLS(prodOrder.getHigh())+" \n");
        //        rowHigthSum = rowHigthSum + rowSpacing;
        //        tsc.addText(beginXPos, rowHigthSum, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"------------------------------------------------- \n");

        // --------------- 打印区-------------End
        setTscEnd(tsc)
    }

    /**
     * 打印前段配置
     * @param tsc
     */
    private fun setTscBegin(tsc: LabelCommand) {
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(60, 78)
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        //        tsc.addGap(10);
        tsc.addGap(0)
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL)
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON)
        // 设置原点坐标
        tsc.addReference(0, 0)
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON)
        // 清除打印缓冲区
        tsc.addCls()
    }

    /**
     * 打印后段配置
     * @param tsc
     */
    private fun setTscEnd(tsc: LabelCommand) {
        // 打印标签
        tsc.addPrint(1, 1)
        // 打印标签后 蜂鸣器响

        tsc.addSound(2, 100)
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255)
        val datas = tsc.command
        // 发送数据
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            return
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas)
    }


    override fun onStart() {
        super.onStart()
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE)
        registerReceiver(receiver, filter)
    }

    /**
     * 蓝牙监听广播
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                // 蓝牙连接断开广播
                UsbManager.ACTION_USB_DEVICE_DETACHED, BluetoothDevice.ACTION_ACL_DISCONNECTED -> mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget()
                DeviceConnFactoryManager.ACTION_CONN_STATE -> {
                    val state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1)
                    val deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1)
                    when (state) {
                        DeviceConnFactoryManager.CONN_STATE_DISCONNECT -> if (id == deviceId) {
                            tv_connState.setText(getString(R.string.str_conn_state_disconnect))
                            tv_connState.setTextColor(Color.parseColor("#666666")) // 未连接-灰色
                            isConnected = false
                        }
                        DeviceConnFactoryManager.CONN_STATE_CONNECTING -> {
                            tv_connState.setText(getString(R.string.str_conn_state_connecting))
                            tv_connState.setTextColor(Color.parseColor("#6a5acd")) // 连接中-紫色
                            isConnected = false
                        }
                        DeviceConnFactoryManager.CONN_STATE_CONNECTED -> {
                            //                            tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
                            tv_connState.setText(getString(R.string.str_conn_state_connected))
                            tv_connState.setTextColor(Color.parseColor("#008800")) // 已连接-绿色
                            setBeginPrint()
                            isConnected = true
                        }
                        DeviceConnFactoryManager.CONN_STATE_FAILED -> {
                            Utils.toast(context, getString(R.string.str_conn_fail))
                            tv_connState.setText(getString(R.string.str_conn_state_disconnect))
                            tv_connState.setTextColor(Color.parseColor("#666666")) // 未连接-灰色
                            isConnected = false
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    /**
     * 得到用户对象
     */
    private fun getUserInfo() {
        if (user == null) user = showUserByXml()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish()
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        closeHandler(mHandler)
        DeviceConnFactoryManager.closeAllPort()
        if (threadPool != null) {
            threadPool!!.stopThreadPool()
        }
    }

}
