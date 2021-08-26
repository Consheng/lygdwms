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
import kotlinx.android.synthetic.main.prod_barcode_change_fragment1.*
import okhttp3.*
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.BarcodeTable
import ykk.xc.com.lygdwms.bean.BarcodeTableChange
import ykk.xc.com.lygdwms.bean.User
import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.produce.adapter.Prod_Barcode_Change_Fragment1_Adapter
import ykk.xc.com.lygdwms.util.BigdecimalUtil
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.LogUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * 条码组装
 */
class Prod_Barcode_Change_Fragment1 : BaseFragment() {

    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SAVE = 202
        private val UNSAVE = 502

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
    }
    private val context = this
    private var okHttpClient: OkHttpClient? = null
    private var user: User? = null
    private var mContext: Activity? = null
    private var parent: Prod_Barcode_Change_MainActivity? = null
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var listDatas = ArrayList<BarcodeTableChange>()
    private var mAdapter: Prod_Barcode_Change_Fragment1_Adapter? = null
    private var barcodeTable :BarcodeTable? = null

    // 消息处理
    private val mHandler = MyHandler(this)
    private class MyHandler(activity: Prod_Barcode_Change_Fragment1) : Handler() {
        private val mActivity: WeakReference<Prod_Barcode_Change_Fragment1>

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
                        val bt = JsonUtil.strToObject(msgObj, BarcodeTable::class.java)
                        if(m.listDatas.size > 0 && m.listDatas[0].id > 0) {
                            Comm.showWarnDialog(m.mContext,"请点击重置按钮！")
                            return
                        }
                        m.barcodeTable = bt
                        m.setRowData(bt)
                    }
                    UNSUCC1 -> { // 扫码失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有找到数据！"
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                    SAVE -> { // 保存成功 进入
                        val list = JsonUtil.strToList(msg.obj as String, BarcodeTableChange::class.java)
                        m.listDatas.clear()
                        m.listDatas.addAll(list)
                        m.mAdapter!!.notifyDataSetChanged()

                        m.btn_save.visibility = View.GONE
                    }
                    UNSAVE -> { // 保存失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "保存失败！"
                        Comm.showWarnDialog(m.mContext, errMsg)
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

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.prod_barcode_change_fragment1, container, false)
    }

    override fun initView() {
        mContext = getActivity()
        parent = mContext as Prod_Barcode_Change_MainActivity

        recyclerView.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = Prod_Barcode_Change_Fragment1_Adapter(mContext!!, listDatas)
        recyclerView.adapter = mAdapter
        // 设值listview空间失去焦点
        recyclerView.isFocusable = false

        // 行事件
        mAdapter!!.setCallBack(object : Prod_Barcode_Change_Fragment1_Adapter.MyCallBack {
            override fun onDelete(entity: BarcodeTableChange, position: Int) {
                listDatas.removeAt(position)
                mAdapter!!.notifyDataSetChanged()
            }
        })
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
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
        }
    }

    @OnClick(R.id.btn_scan, R.id.btn_clone, R.id.btn_save)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_scan -> { // 调用摄像头扫描（物料）
                ScanUtil.startScan(mContext, BaseFragment.CAMERA_SCAN, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
            }
            R.id.btn_save -> { // 保存
                if(listDatas.size == 0) {
                    Comm.showWarnDialog(mContext, "请扫描条码！")
                    return
                }
                if(listDatas.size == 1) {
                    Comm.showWarnDialog(mContext, "一个条码不能进行组码，必须多个条码！")
                    return
                }
                run_save()
            }
            R.id.btn_clone -> { // 重置
                if (listDatas.size > 0 && listDatas[0].id == 0) {
                    val build = AlertDialog.Builder(mContext)
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

    override fun setListener() {
        val click = View.OnClickListener { v ->
            setFocusable(et_getFocus)
            when (v.id) {
                R.id.et_code -> setFocusable(et_code)
            }
        }
        et_code!!.setOnClickListener(click)

        // 物料---数据变化
        et_code!!.addTextChangedListener(object : TextWatcher {
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
        et_code!!.setOnLongClickListener {
            showInputDialog("输入条码号", getValues(et_code), "none", WRITE_CODE)
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
     * 重置
     */
    private fun reset() {
        et_code.setText("")
        btn_save.visibility = View.VISIBLE
        listDatas.clear()
        barcodeTable = null
        mAdapter!!.notifyDataSetChanged()
        parent!!.isChange = false
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 设置行数据
     */
    private fun setRowData(bt :BarcodeTable) {
        if(listDatas.size > 0 && listDatas[0].mtlId != bt.materialId) {
            Comm.showWarnDialog(mContext,"扫描的物料必须一致！")
            return
        }
        var isAddRow = true
        listDatas.forEach {
            if(bt.barcode.equals(it.scanBarcode)) {
                isAddRow = false
            }
        }
        if(!isAddRow) {
            Comm.showWarnDialog(mContext,"请扫描不同的条码进行组码！")
            return
        }
        val material = Material_K3()
        material.fmaterialId = bt.materialId
        material.fnumber = bt.materialNumber
        material.fname = bt.materialName
        material.materialSize = bt.materialSize

        val btChange = BarcodeTableChange()
        btChange.mtlId = bt.materialId
        btChange.scanBarcodeTableId = bt.id
        btChange.scanBarcode = bt.barcode
        btChange.scanQty = bt.barcodeQty
        btChange.newBarcodeTableId = 0
        btChange.newBarcode = ""
        btChange.newQty = 0.0
        btChange.type = 'A'
        btChange.createUserId = user!!.id
        btChange.createUserName = user!!.username

        btChange.material = material
        btChange.unitName = bt.unitName

        listDatas.add(btChange)

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
                .add("barcode", getValues(et_code))
                .add("caseId", "20")
                .add("searchStockInfo", "1")    // 查询仓库信息
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
     * 保存
     */
    private fun run_save() {
        showLoadDialog("保存中...", false)
        var mUrl = getURL("barcodeTableChange/addBind")

        var sumScanQty = 0.0    // 总的扫码数
        listDatas.forEach {
            sumScanQty = BigdecimalUtil.add(sumScanQty, it.scanQty)
        }

        barcodeTable!!.relationBillId = 0
        barcodeTable!!.relationBillEntryId = 0
        barcodeTable!!.relationBillNumber = ""
        barcodeTable!!.barcodeQty = sumScanQty
        barcodeTable!!.createUserId = user!!.id
        barcodeTable!!.createUserName = user!!.username

        val formBody = FormBody.Builder()
                .add("strJson", JsonUtil.objectToString(listDatas))
                .add("strBarcodeTable", JsonUtil.objectToString(barcodeTable!!))
                .build()

        val request = Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
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
                LogUtil.e("run_save --> onResponse", result)
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

}