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
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
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
import kotlinx.android.synthetic.main.ware_other_out_stock.*
import okhttp3.*
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.*
import ykk.xc.com.lygdwms.comm.BaseActivity
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.produce.adapter.OtherOutStock_Adapter
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.blueTooth.*
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * 其他出库
 */
class OtherOutStockActivity : BaseActivity() {

    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SAVE = 202
        private val UNSAVE = 502

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
        private val RESULT_QTY = 4
    }
    private val context = this
    private val listDatas = ArrayList<BarcodeTable>()
    private var mAdapter: OtherOutStock_Adapter? = null
    private var user: User? = null
    private val okHttpClient = OkHttpClient()
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var curPos = -1
    private var listPrintData :ArrayList<BarcodeTable>? = null
    private var smFlag = '1' // 扫描类型 1：物料，2：位置
    private var type = 'A'   // 来源类型：A：报废，B：盘亏，C：返工发料，D：其他领用

    private val id = 0 // 设备id
    private var threadPool: ThreadPool? = null
    private var isConnected: Boolean = false // 蓝牙是否连接标识
    private val CONN_STATE_DISCONN = 0x007 // 连接状态断开
    private val PRINTER_COMMAND_ERROR = 0x008 // 使用打印机指令错误
    private val CONN_PRINTER = 0x12

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: OtherOutStockActivity) : Handler() {
        private val mActivity: WeakReference<OtherOutStockActivity>

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
                        val bt = JsonUtil.strToObject(msg.obj as String, BarcodeTable::class.java)
                        m.setRowData(bt)
                    }
                    UNSUCC1 -> { // 扫码    失败！
                        m.listDatas.clear()
                        m.mAdapter!!.notifyDataSetChanged()
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有找到数据！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SAVE -> { // 保存 成功
                        val strId_pdaNo = JsonUtil.strToString(msgObj)
                        m.reset()
                        m.toasts("保存成功")
                    }
                    UNSAVE -> { // 保存  失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "保存失败！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SETFOCUS -> { // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.et_getFocus)
                        m.setFocusable(m.et_code)
                    }
                    SAOMA -> { // 扫码之后
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
        return R.layout.ware_other_out_stock
    }

    override fun initView() {
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = OtherOutStock_Adapter(context, listDatas)
        recyclerView.adapter = mAdapter
        // 设值listview空间失去焦点
        recyclerView.isFocusable = false

        // 行事件
        mAdapter!!.setCallBack(object : OtherOutStock_Adapter.MyCallBack {
            override fun onDelete(entity: BarcodeTable, pos: Int) {
                listDatas.removeAt(pos)
                mAdapter!!.notifyDataSetChanged()
            }
        })
        // 点击输入数量
        mAdapter!!.setOnItemClickListener { adapter, holder, view, pos ->
            curPos = pos
            showInputDialog("数量", listDatas[pos].smFqty.toString(), "0", RESULT_QTY)
        }

        // 长按选择仓库信息
        mAdapter!!.setOnItemLongClickListener { adapter, holder, view, pos ->
            /*
            curPos = pos;
            val bundle = Bundle()
            bundle.putSerializable("stock", listDatas[pos].stock)
            bundle.putSerializable("stockArea", listDatas[pos].stockArea)
            bundle.putSerializable("storageRack", listDatas[pos].storageRack)
            bundle.putSerializable("stockPosition", listDatas[pos].stockPosition)
            showForResult(Stock_GroupDialogActivity::class.java, SEL_POSITION, bundle)
            */
        }

    }

    override fun initData() {
        getUserInfo()
        hideSoftInputMode(et_code)
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_type, R.id.btn_scan, R.id.btn_save, R.id.btn_clone)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                closeHandler(mHandler)
                context.finish()
            }
            R.id.btn_type -> { // 来源单据选择
                pop_type(view)
                popWindow!!.showAsDropDown(view)
            }
            R.id.btn_scan -> { // 调用摄像头扫描（物料）
                smFlag = '2'
                ScanUtil.startScan(context, BaseFragment.CAMERA_SCAN, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
            }
            R.id.btn_save -> {  // 保存
                if(listDatas.size == 0) {
                    Comm.showWarnDialog(context, "请扫描物料条码！")
                    return
                }
                run_add()
            }
            R.id.btn_clone -> { // 重置
                if (listDatas.size > 0) {
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
        }
    }

    /**
     * 创建PopupWindow 【 单据类型选择 】
     */
    private var popWindow: PopupWindow? = null
    private fun pop_type(v: View) {
        if (null != popWindow) {//不为空就隐藏
            popWindow!!.dismiss()
            return
        }
        // 获取自定义布局文件popupwindow_left.xml的视图
        val popV = layoutInflater.inflate(R.layout.popwindow_outstock_type, null)
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
                    btn_type.text = "报废 ▼"
                    type = 'A'
                }
                R.id.btn2 -> {
                    btn_type.text = "盘亏 ▼"
                    type = 'B'
                }
                R.id.btn3 -> {
                    btn_type.text = "返工发料 ▼"
                    type = 'C'
                }
                R.id.btn4 -> {
                    btn_type.text = "其他领用 ▼"
                    type = 'D'
                }
            }
            popWindow!!.dismiss()
        }
        popV.findViewById<View>(R.id.btn1).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn2).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn3).setOnClickListener(click)
        popV.findViewById<View>(R.id.btn4).setOnClickListener(click)
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
                    smFlag = '2'
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
        // 物料---长按输入条码
        et_code.setOnLongClickListener {
            smFlag = '2'
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

    }

    /**
     * 重置
     */
    private fun reset() {
        type = 'A'
        btn_type.text = "报废 ▼"
        et_code.setText("")
        listDatas.clear()
        mAdapter!!.notifyDataSetChanged()
        smFlag = '1'
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 设置行数据
     */
    private fun setRowData(bt :BarcodeTable) {
        var pos = -1
        listDatas.forEachIndexed { index, it ->
            if(bt.id == it.id) {
                pos = index
            }
        }
        if(pos > -1) {
            listDatas[pos].smFqty = listDatas[pos].remainQty
        } else {
            bt.smFqty = bt.remainQty
            listDatas.add(bt)
        }

        mAdapter!!.notifyDataSetChanged()
        // 跳转到物料扫描
        smFlag = '2'
        mHandler.sendEmptyMessage(SETFOCUS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            if (data == null) return
            when (requestCode) {
                BaseFragment.CAMERA_SCAN -> {// 扫一扫成功  返回
                    val hmsScan = data!!.getParcelableExtra(ScanUtil.RESULT) as HmsScan
                    if (hmsScan != null) {
                        setTexts(et_code, hmsScan.originalValue)
                    }
                }
                WRITE_CODE -> {// 输入条码  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        setTexts(et_code, value.toUpperCase())
                    }
                }
                RESULT_QTY -> {// 输入生码数  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        val fqty = parseInt(value)
                        if (fqty <= 0) {
                            Comm.showWarnDialog(context, "出库数不能为0，（出库数）必须小于或等于（结存数）！")
                            return
                        }
                        if (fqty > listDatas[curPos].remainQty) {
                            Comm.showWarnDialog(context, "（出库数）不能大于（结存数）！")
                            return
                        }
                        listDatas[curPos].smFqty = fqty.toDouble()
                        mAdapter!!.notifyDataSetChanged()
                    }
                }
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
        val mUrl = getURL("barcodeTable/findBarcode")
        val formBody = FormBody.Builder()
                .add("barcode", getValues(et_code).trim())
                .add("strCaseId", "30,31,32,33,34")
                .add("searchStockInfo", "1")    // 查询仓库信息
                .add("searchUnitInfo", "1")     // 查询单位信息
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
                Log.e("run_smDatas --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 保存
     */
    private fun run_add() {
        isTextChange = false
        showLoadDialog("加载中...", false)

        val otherOutStock = OtherOutStock()
        otherOutStock.type = type
        otherOutStock.createUserId = user!!.createrId
        otherOutStock.createUserName = user!!.username
        val listResult = ArrayList<OtherOutStockEntry>()
        listDatas.forEach {
            val entry = OtherOutStockEntry()
            entry.barcodeTableId = it.id
            entry.barcode = it.barcode
            entry.mtlId = it.materialId
            entry.sourceQty = it.remainQty
            entry.fqty = it.smFqty

            listResult.add(entry)
        }
        if(listResult.size == 0) {
            Comm.showWarnDialog(context, "请至少扫描一个物料条码！")
            return
        }
        val formBody = FormBody.Builder()
                .add("strJson", JsonUtil.objectToString(otherOutStock))
                .add("strJsonEntry", JsonUtil.objectToString(listResult))
                .build()
        val mUrl = getURL("otherOutStock/save")

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
