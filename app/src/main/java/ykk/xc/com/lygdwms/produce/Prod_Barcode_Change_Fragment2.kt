package ykk.xc.com.lygdwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.prod_barcode_change_fragment2.*
import okhttp3.*
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.BarcodeTable
import ykk.xc.com.lygdwms.bean.BarcodeTableChange
import ykk.xc.com.lygdwms.bean.User
import ykk.xc.com.lygdwms.bean.k3Bean.Material_K3
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.produce.adapter.Prod_Barcode_Change_Fragment2_Adapter
import ykk.xc.com.lygdwms.util.BigdecimalUtil
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.LogUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * 条码拆解
 */
class Prod_Barcode_Change_Fragment2 : BaseFragment() {

    companion object {
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SAVE = 202
        private val UNSAVE = 502

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
        private val RESULT_SIZE = 4
        private val RESULT_QTY = 5
    }
    private val context = this
    private var okHttpClient: OkHttpClient? = null
    private var user: User? = null
    private var mContext: Activity? = null
    private var parent: Prod_Barcode_Change_MainActivity? = null
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var listDatas = ArrayList<BarcodeTableChange>()
    private var mAdapter: Prod_Barcode_Change_Fragment2_Adapter? = null
    private var barcodeTable :BarcodeTable? = null
    private val df = DecimalFormat("#.####")

    // 消息处理
    private val mHandler = MyHandler(this)
    private class MyHandler(activity: Prod_Barcode_Change_Fragment2) : Handler() {
        private val mActivity: WeakReference<Prod_Barcode_Change_Fragment2>

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
                        if(m.listDatas.size > 0) {
                            Comm.showWarnDialog(m.mContext,"只能扫描一个条码进行拆码！")
                            return
                        }
                        m.barcodeTable = bt

                        m.tv_mtlName.text = Html.fromHtml("物料名称:&nbsp;<font color='#6a5acd'>" + bt.materialName + "</font>")
                        m.tv_mtlNumber.text = Html.fromHtml("物料代码:&nbsp;<font color='#6a5acd'>" + bt.materialNumber + "</font>")
                        m.tv_fmodel.text = Html.fromHtml("规格型号:&nbsp;<font color='#6a5acd'>" + bt.materialSize + "</font>")
                        m.tv_barcode.text = Html.fromHtml("条码:&nbsp;<font color='#000000'>" + bt.barcode + "</font>")
                        m.tv_barcodeQty.text = Html.fromHtml("数量:&nbsp;<font color='#6a5acd'>" + m.df.format(bt.barcodeQty) + "</font>")
                        m.tv_barcodeSize.text = "1"
                        m.tv_newQty.text = ""
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
                        m.lin_chaima.visibility = View.GONE
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
        return inflater.inflate(R.layout.prod_barcode_change_fragment2, container, false)
    }

    override fun initView() {
        mContext = getActivity()
        parent = mContext as Prod_Barcode_Change_MainActivity

        recyclerView.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = Prod_Barcode_Change_Fragment2_Adapter(mContext!!, listDatas)
        recyclerView.adapter = mAdapter
        // 设值listview空间失去焦点
        recyclerView.isFocusable = false

        // 行事件
        mAdapter!!.setCallBack(object : Prod_Barcode_Change_Fragment2_Adapter.MyCallBack {
            override fun onDelete(entity: BarcodeTableChange, position: Int) {
                listDatas.removeAt(position)
                mAdapter!!.notifyDataSetChanged()
                var sumQty = 0.0
                listDatas.forEach {
                    sumQty = BigdecimalUtil.add(sumQty, it.newQty)
                }
                var remainQty = BigdecimalUtil.sub(barcodeTable!!.barcodeQty, sumQty)
                if(remainQty <= 0) remainQty = 0.0
                // 已拆解条码个数：0，拆解总数量：0
                tv_changeInfo.text = Html.fromHtml("已拆码个数:&nbsp;<font color='#6a5acd'>" + listDatas.size + "</font>，" +
                        "拆码总数:&nbsp;<font color='#6a5acd'>"+ df.format(sumQty) +"</font>，" +
                        "剩余数：&nbsp;<font color='#000000'>"+ df.format(remainQty) +"</font>")
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

    @OnClick(R.id.btn_scan, R.id.tv_barcodeSize, R.id.tv_newQty, R.id.btn_addRow, R.id.btn_clone, R.id.btn_save)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_scan -> { // 调用摄像头扫描（物料）
                ScanUtil.startScan(mContext, BaseFragment.CAMERA_SCAN, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
            }
            R.id.tv_barcodeSize -> { // 个数
                showInputDialog("个数", getValues(tv_barcodeSize).trim(), "0", RESULT_SIZE)
            }
            R.id.tv_newQty -> { // 数量
                showInputDialog("数量", getValues(tv_newQty).trim(), "0", RESULT_QTY)
            }
            R.id.btn_addRow -> { // 添加行
                addRow()
            }
            R.id.btn_save -> { // 保存.
                if(barcodeTable == null) {
                    Comm.showWarnDialog(mContext, "请扫描条码！")
                    return
                }
                if(listDatas.size == 0) {
                    Comm.showWarnDialog(mContext, "请先进行拆码操作！")
                    return
                }
                var sumQty = 0.0
                listDatas.forEach {
                    sumQty = BigdecimalUtil.add(sumQty, it.newQty)
                }
                if(sumQty > barcodeTable!!.barcodeQty) {
                    Comm.showWarnDialog(mContext,"当前拆码的总数大于扫码数量，请检查！")
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
     * 新增行
     */
    private fun addRow() {
        if(barcodeTable == null) {
            Comm.showWarnDialog(mContext, "请扫描条码！")
            return
        }
        val size = parseDouble(getValues(tv_barcodeSize).trim())
        val fqty = parseDouble(getValues(tv_newQty).trim())
        if(size == 0.0) {
            Comm.showWarnDialog(mContext,"请输入个数！")
            return
        }
//                if(size == 1.0 && fqty == 0.0) {
        if(fqty == 0.0) {
            Comm.showWarnDialog(mContext,"请输入拆解的数量！")
            return
        }
        if(size == 1.0 && fqty == barcodeTable!!.barcodeQty) {
            Comm.showWarnDialog(mContext,"当前拆码数量与扫码数量一致，（个数）必须要大于1！")
            return
        }
        var sumQty = 0.0
        listDatas.forEach {
            sumQty = BigdecimalUtil.add(sumQty, it.newQty)
        }
        var mulVal = BigdecimalUtil.mul(size, fqty)
        var remainQty = BigdecimalUtil.sub(barcodeTable!!.barcodeQty, sumQty)
        if(mulVal > remainQty) {
            Comm.showWarnDialog(mContext,"当前拆码数量（"+mulVal.toInt()+"）不能大于剩余数（"+remainQty.toInt()+"）！")
            return
        }

        val count = size.toInt()
        for (i in 0 until count){
            // 新增行
            setRowData(barcodeTable!!, fqty)
        }
        sumQty = 0.0
        listDatas.forEach {
            sumQty = BigdecimalUtil.add(sumQty, it.newQty)
        }
        remainQty = BigdecimalUtil.sub(barcodeTable!!.barcodeQty, sumQty)
        if(remainQty <= 0) remainQty = 0.0
        // 已拆解条码个数：0，拆解总数量：0
        tv_changeInfo.text = Html.fromHtml("已拆码个数:&nbsp;<font color='#6a5acd'>" + listDatas.size + "</font>，" +
                "拆码总数:&nbsp;<font color='#6a5acd'>"+ df.format(sumQty) +"</font>，" +
                "剩余数：&nbsp;<font color='#000000'>"+ df.format(remainQty) +"</font>")
        // 重置个数，数量
        tv_barcodeSize.text = "1"
        tv_newQty.text = ""
    }

    /**
     * 重置
     */
    private fun reset() {
        et_code.setText("")
        tv_mtlName.text = "物料名称："
        tv_mtlNumber.text = "物料代码："
        tv_fmodel.text = "规格型号："
        tv_barcode.text = "条码："
        tv_barcodeQty.text = "数量：0"
        tv_barcodeSize.text = "1"
        tv_newQty.text = ""
        tv_changeInfo.text = "已拆码个数：0，拆码总数:0，剩余数：0"

        btn_save.visibility = View.VISIBLE
        lin_chaima.visibility = View.VISIBLE
        listDatas.clear()
        barcodeTable = null
        mAdapter!!.notifyDataSetChanged()
        parent!!.isChange = false
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 设置行数据
     */
    private fun setRowData(bt :BarcodeTable, fqty :Double) {
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
        btChange.newQty = fqty
        btChange.type = 'B'
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
                RESULT_SIZE -> { // 个数  返回
                    val bundle = data!!.getExtras()
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        val size = parseDouble(value)
                        tv_barcodeSize.text = df.format(size)
                        // 自动打开数量填写界面
                        val strQty = getValues(tv_newQty).trim()
                        if(strQty.length == 0) {
                            showInputDialog("数量", getValues(tv_newQty).trim(), "0", RESULT_QTY)
                        }
                    }
                }
                RESULT_QTY -> { // 数量  返回
                    val bundle = data!!.getExtras()
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        val fqty = parseInt(value)
                        tv_newQty.text = fqty.toString()
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
        var mUrl = getURL("barcodeTableChange/addUnBind")

        var sumNewQty = 0.0    // 总的扫码数
        listDatas.forEach {
            sumNewQty = BigdecimalUtil.add(sumNewQty, it.newQty)
        }

        barcodeTable!!.barcodeQty = sumNewQty
        barcodeTable!!.remainQty = sumNewQty
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