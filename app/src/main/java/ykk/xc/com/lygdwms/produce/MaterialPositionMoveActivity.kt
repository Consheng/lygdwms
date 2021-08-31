package ykk.xc.com.lygdwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.material_position_move.*
import okhttp3.*
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.basics.Stock_GroupDialogActivity
import ykk.xc.com.lygdwms.bean.*
import ykk.xc.com.lygdwms.comm.BaseActivity
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.util.JsonUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.DecimalFormat

/**
 * 物料位置移动
 */
class MaterialPositionMoveActivity : BaseActivity() {

    companion object {
        private val SEL_POSITION = 61
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SUCC2 = 201
        private val UNSUCC2 = 501
        private val SAVE = 202
        private val UNSAVE = 502

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
    }
    private val context = this
    private var user: User? = null
    private val okHttpClient = OkHttpClient()
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var curPos = -1
    private var smFlag = '1' // 扫描类型 1：物料，2：位置
    private var materialPositionMove = MaterialPositionMove()
    private val df = DecimalFormat("#.####")

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: MaterialPositionMoveActivity) : Handler() {
        private val mActivity: WeakReference<MaterialPositionMoveActivity>

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
                    SUCC1 -> { // 扫码物料    成功
                        val bt = JsonUtil.strToObject(msg.obj as String, BarcodeTable::class.java)
                        m.setMoveData(bt)
                    }
                    UNSUCC1 -> { // 扫码物料    失败！
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有找到数据！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SUCC2 -> { // 扫码位置    成功
                        m.getStockGroup(msgObj,null,null,null,null)
                    }
                    UNSUCC2 -> { // 扫码位置    失败！
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有找到数据！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SAVE -> { // 保存 成功
                        val barocode = m.materialPositionMove.barcode
                        m.reset()
                        m.setTexts(m.et_code, barocode)
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
                        when(m.smFlag) {
                            '1' -> m.run_smDatas()
                            '2' -> m.run_findBarcodeGroup()
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置移动类数据
     */
    private fun setMoveData(bt :BarcodeTable) {
        materialPositionMove.mtlId = bt.materialId
        materialPositionMove.barcodeTableId = bt.id
        materialPositionMove.barcode = bt.barcode
        materialPositionMove.oldStockId = bt.stockId
        materialPositionMove.oldStockAreaId = bt.stockAreaId
        materialPositionMove.oldStorageRackId = bt.storageRackId
        materialPositionMove.oldStockPositionId = bt.stockPositionId
        materialPositionMove.newStockId = 0
        materialPositionMove.newStockAreaId = 0
        materialPositionMove.newStorageRackId = 0
        materialPositionMove.newStockPositionId = 0
        materialPositionMove.createUserId = user!!.id
        materialPositionMove.createUserName = user!!.username

        tv_mtlName.text = Html.fromHtml("物料名称:&nbsp;<font color='#6a5acd'>" + bt.materialName + "</font>")
        tv_mtlNumber.text = Html.fromHtml("物料代码:&nbsp;<font color='#6a5acd'>" + bt.materialNumber + "</font>")
        tv_fmodel.text = Html.fromHtml("规格型号:&nbsp;<font color='#6a5acd'>" + bt.materialSize + "</font>")
        tv_barcode.text = Html.fromHtml("条码:&nbsp;<font color='#000000'>" + bt.barcode + "</font>")
        tv_barcodeQty.text = Html.fromHtml("数量:&nbsp;<font color='#6a5acd'>" + df.format(bt.barcodeQty) + "</font>")
        if(bt.stock != null) {
            tv_stockName.text = Html.fromHtml("仓库:&nbsp;<font color='#6a5acd'>" + bt.stock.fname + "</font>")
        } else {
            tv_stockName.text = "仓库："
        }
        if(bt.stockArea != null) {
            tv_stockAreaName.text = Html.fromHtml("库区:&nbsp;<font color='#6a5acd'>" + bt.stockArea.fname + "</font>")
        } else {
            tv_stockAreaName.text = "库区："
        }
        if(bt.storageRack != null) {
            tv_storageRackName.text = Html.fromHtml("货架:&nbsp;<font color='#6a5acd'>" + bt.storageRack.fnumber + "</font>")
        } else {
            tv_storageRackName.text = "货架："
        }
        if(bt.stockPosition != null) {
            tv_stockPosName.text = Html.fromHtml("库位:&nbsp;<font color='#6a5acd'>" + bt.stockPosition.fname + "</font>")
        } else {
            tv_stockPosName.text = "库位："
        }

        // 跳到位置焦点
        smFlag = '2'
        mHandler.sendEmptyMessage(SETFOCUS)
    }

    override fun setLayoutResID(): Int {
        return R.layout.material_position_move
    }

    override fun initView() {

    }

    override fun initData() {
        getUserInfo()
        hideSoftInputMode(et_code)
        hideSoftInputMode(et_positionCode)
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_scan,  R.id.btn_positionScan, R.id.btn_positionSel, R.id.btn_clone, R.id.btn_save)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                closeHandler(mHandler)
                context.finish()
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
                bundle.putSerializable("stock", materialPositionMove.newStock)
                bundle.putSerializable("stockArea", materialPositionMove.newStockArea)
                bundle.putSerializable("storageRack", materialPositionMove.newStorageRack)
                bundle.putSerializable("stockPosition", materialPositionMove.newStockPosition)
                showForResult(Stock_GroupDialogActivity::class.java, SEL_POSITION, bundle)
            }
            R.id.btn_clone -> {
                if (materialPositionMove.newStockId == 0) {
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
            R.id.btn_save -> {  // 确认移动
                if(materialPositionMove.mtlId == 0) {
                    Comm.showWarnDialog(context,"请扫描物料条码")
                    return
                }
                if(materialPositionMove.newStockId == 0) {
                    Comm.showWarnDialog(context,"请扫描要移动的位置条码！")
                    return
                }
                val oldPosition = materialPositionMove.oldStockId.toString() +"-"+ materialPositionMove.oldStockAreaId.toString() +"-"+ materialPositionMove.oldStorageRackId.toString() +"-"+ materialPositionMove.oldStockPositionId.toString()
                val newPosition = materialPositionMove.newStockId.toString() +"-"+ materialPositionMove.newStockAreaId.toString() +"-"+ materialPositionMove.newStorageRackId.toString() +"-"+ materialPositionMove.newStockPositionId.toString()
                if(oldPosition.equals(newPosition)) {
                    Comm.showWarnDialog(context,"条码的位置和移动的位置不能相同！")
                    return
                }
                run_add()
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
    }

    /**
     * 重置
     */
    private fun reset() {
        et_code.setText("")
        et_positionCode.setText("")

        tv_mtlName.text = "物料名称："
        tv_mtlNumber.text = "物料代码："
        tv_fmodel.text = "规格型号："
        tv_barcode.text = "条码："
        tv_barcodeQty.text = "数量：0"
        tv_stockName.text = "仓库："
        tv_stockAreaName.text = "库区："
        tv_storageRackName.text = "货架："
        tv_stockPosName.text = "库位："
        tv_newStockName.text = "仓库："
        tv_newStockAreaName.text = "库区："
        tv_newStorageRackName.text = "货架："
        tv_newStockPosName.text = "库位："
        materialPositionMove.mtlId = 0
        materialPositionMove.barcodeTableId = 0
        materialPositionMove.barcode = null
        materialPositionMove.oldStockId = 0
        materialPositionMove.oldStockAreaId = 0
        materialPositionMove.oldStorageRackId = 0
        materialPositionMove.oldStockPositionId = 0
        materialPositionMove.newStockId = 0
        materialPositionMove.newStockAreaId = 0
        materialPositionMove.newStorageRackId = 0
        materialPositionMove.newStockPositionId = 0
        materialPositionMove.newStock = null
        materialPositionMove.newStockArea = null
        materialPositionMove.newStorageRack = null
        materialPositionMove.newStockPosition = null

        smFlag = '1'
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 得到仓库组
     */
    fun getStockGroup(msgObj : String?, stock :Stock?, stockArea :StockArea?, storageRack :StorageRack?, stockPos :StockPosition?) {
        // 重置数据
        materialPositionMove.newStockId = 0
        materialPositionMove.newStockAreaId = 0
        materialPositionMove.newStorageRackId = 0
        materialPositionMove.newStockPositionId = 0
        materialPositionMove.newStock = null
        materialPositionMove.newStockArea = null
        materialPositionMove.newStorageRack = null
        materialPositionMove.newStockPosition = null
        tv_newStockName.text = ""
        tv_newStockAreaName.text = ""
        tv_newStorageRackName.text = ""
        tv_newStockPosName.text = ""

        var stock :Stock? = stock
        var stockArea :StockArea? = stockArea
        var storageRack :StorageRack? = storageRack
        var stockPosition :StockPosition? = stockPos

        if(msgObj != null) {
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
                    if(stockArea.stock != null) stock = stockArea.stock
                }
                3 -> {
                    storageRack = JsonUtil.strToObject(msgObj, StorageRack::class.java)
                    if(storageRack.stock != null) stock = storageRack.stock
                    if(storageRack.stockArea != null) stockArea = storageRack.stockArea
                }
                4 -> {
                    stockPosition = JsonUtil.strToObject(msgObj, StockPosition::class.java)
                    if(stockPosition.stock != null) stock = stockPosition.stock
                    if(stockPosition.stockArea != null) stockArea = stockPosition.stockArea
                    if(stockPosition.storageRack != null) storageRack = stockPosition.storageRack
                }
            }
        }

        if(stock != null ) {
            tv_newStockName.text = Html.fromHtml("仓库:&nbsp;<font color='#6a5acd'>" + stock.fname + "</font>")
            materialPositionMove.newStockId = stock.id
            materialPositionMove.newStock = stock
        }
        if(stockArea != null ) {
            tv_newStockAreaName.text = Html.fromHtml("库区:&nbsp;<font color='#6a5acd'>" + stockArea.fname + "</font>")
            materialPositionMove.newStockAreaId = stockArea.id
            materialPositionMove.newStockArea = stockArea
        }
        if(storageRack != null ) {
            tv_newStorageRackName.text = Html.fromHtml("货架:&nbsp;<font color='#6a5acd'>" + storageRack.fnumber + "</font>")
            materialPositionMove.newStorageRackId = storageRack.id
            materialPositionMove.newStorageRack = storageRack
        }
        if(stockPosition != null ) {
            tv_newStockPosName.text = Html.fromHtml("库位:&nbsp;<font color='#6a5acd'>" + stockPosition.fnumber + "</font>")
            materialPositionMove.newStockPositionId = stockPosition.id
            materialPositionMove.newStockPosition = stockPosition
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            if (data == null) return
            when (requestCode) {
                BaseFragment.CAMERA_SCAN -> {// 扫一扫成功  返回
                    val hmsScan = data!!.getParcelableExtra(ScanUtil.RESULT) as HmsScan
                    if (hmsScan != null) {
                        when (smFlag) {
                            '1' -> setTexts(et_code, hmsScan.originalValue)
                            '2' -> setTexts(et_positionCode, hmsScan.originalValue)
                        }
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
                SEL_POSITION -> {// 选择位置  返回
                    var stock: Stock? = null
                    var stockArea: StockArea? = null
                    var storageRack: StorageRack? = null
                    var stockPosition: StockPosition? = null

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
                    getStockGroup(null, stock, stockArea, storageRack, stockPosition)
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
     * 扫描查询位置
     */
    private fun run_findBarcodeGroup() {
        isTextChange = false
        showLoadDialog("加载中...", false)
        val mUrl = getURL("stockPosition/findBarcodeGroup")
        val formBody = FormBody.Builder()
                .add("barcode", getValues(et_positionCode).trim())
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
                Log.e("run_findBarcodeGroup --> onResponse", result)
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

        val formBody = FormBody.Builder()
                .add("strJson", JsonUtil.objectToString(materialPositionMove))
                .build()
        val mUrl = getURL("materialPositionMove/add")

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
     * 得到用户对象
     */
    private fun getUserInfo() {
        if (user == null) user = showUserByXml()
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
    }

}
