package ykk.xc.com.lygdwms.produce

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import kotlinx.android.synthetic.main.shippinglist_fragment1.*
import kotlinx.android.synthetic.main.shippinglist_main.viewPager
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.basics.ShippingListTitle_DialogActivity
import ykk.xc.com.lygdwms.bean.*
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.LogUtil
import ykk.xc.com.lygdwms.util.widget.CustomDatePicker
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * 表头
 */
class ShippingList_Fragment1 : BaseFragment() {

    companion object {
        private val SEL_TITLE = 60
        private val FIND_ENTRY = 200
        private val UNFIND_ENTRY = 500

        private val WRITE_CODE = 1
    }

    private val context = this
    private var user: User? = null
    private var mContext: Activity? = null
    private var okHttpClient: OkHttpClient? = null
    private var parent: ShippingList_MainActivity? = null
    var shippingList = ShippingList()
    var listEntry : List<ShippingListEntry>? = null
    private var timesTamp:String? = null // 时间戳
    private var writeFlag = 'A' // A：SO，B：柜号，C：封条，D：车牌，E：延时原因
    private var customDatePicker1: CustomDatePicker? = null
    private var customDatePicker2: CustomDatePicker? = null

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: ShippingList_Fragment1) : Handler() {
        private val mActivity: WeakReference<ShippingList_Fragment1>

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
                    FIND_ENTRY -> { // 查询分录信息 成功
                        val list = JsonUtil.strToList(msgObj, ShippingListEntry::class.java)
                        m.listEntry = list
                        m.parent!!.viewPager.setScanScroll(true); // 放开左右滑动
                        m.setEnables(m.tv_titleSel, R.drawable.back_style_gray2a,false)
                        // 滑动第二个页面
//                        m.parent!!.viewPager!!.setCurrentItem(1, false)
                        EventBus.getDefault().post(EventBusEntity(1)) // 发送指令到fragment3，查询分类信息
                    }
                    UNFIND_ENTRY -> { // 查询分录信息 失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "查询信息有错误！2秒后自动关闭..."
                        Comm.showWarnDialog(m.mContext, errMsg)
                        m.mHandler.postDelayed(Runnable {
                            m.mContext!!.finish()
                        },2000)
                    }
                }
            }
        }
    }

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.shippinglist_fragment1, container, false)
    }

    override fun initView() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                .writeTimeout(120, TimeUnit.SECONDS) // 设置写的超时时间
                .readTimeout(120, TimeUnit.SECONDS) //设置读取超时时间
                .build()
        }
        mContext = getActivity()
        parent = mContext as ShippingList_MainActivity

        customDatePicker1 = CustomDatePicker(mContext, CustomDatePicker.ResultHandler { time ->
            // 回调接口，获得选中的时间
            tv_actualArrivalTime.setText(time)

        }, "2000-01-01 00:00:00", "2099-12-31 23:59:59") // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1!!.showSpecificTime(true) // 显示时和分
        customDatePicker1!!.setIsLoop(true) // 允许循环滚动

        customDatePicker2 = CustomDatePicker(mContext, CustomDatePicker.ResultHandler { time ->
            // 回调接口，获得选中的时间
            tv_leaveTime.setText(time)

        }, "2000-01-01 00:00:00", "2099-12-31 23:59:59") // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2!!.showSpecificTime(true) // 显示时和分
        customDatePicker2!!.setIsLoop(true) // 允许循环滚动
    }

    override fun initData() {
        getUserInfo()
        timesTamp = user!!.getId().toString() + "-" + Comm.randomUUID()

        shippingList.shippingOutUserId = user!!.id
        shippingList.shippingOutTime = Comm.getSysDate(0)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
        }
    }

    @OnClick(R.id.tv_titleSel, R.id.tv_so, R.id.tv_cabinetNo, R.id.tv_seal, R.id.tv_carLicense, R.id.tv_needArrivalTime, R.id.tv_actualArrivalTime, R.id.tv_leaveTime, R.id.tv_delayReason)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.tv_titleSel -> { // 选择出库清单
                showForResult(ShippingListTitle_DialogActivity::class.java, SEL_TITLE, null)
            }
            R.id.tv_so -> { // SO
                writeFlag = 'A'
                showInputDialog("SO", isNULLS(shippingList.so), "none", WRITE_CODE)
            }
            R.id.tv_cabinetNo -> { // 柜号
                writeFlag = 'B'
                showInputDialog("柜号", isNULLS(shippingList.cabinetNo), "none", WRITE_CODE)
            }
            R.id.tv_seal -> { // 封条
                writeFlag = 'C'
                showInputDialog("封条", isNULLS(shippingList.seal), "none", WRITE_CODE)
            }
            R.id.tv_carLicense -> { // 车牌
                writeFlag = 'D'
                showInputDialog("车牌", isNULLS(shippingList.carLicense), "none", WRITE_CODE)
            }
            R.id.tv_needArrivalTime -> { // 要求货柜到厂时间
                Comm.showDateDialog(mContext, view, 0)
            }
            R.id.tv_actualArrivalTime -> { // 货柜到厂时间
//                Comm.showDateDialog(mContext, view, 0)
                customDatePicker1!!.show(Comm.getSysDate(0))
            }
            R.id.tv_leaveTime -> { // 货柜离厂时间
//                Comm.showDateDialog(mContext, view, 0)
                customDatePicker2!!.show(Comm.getSysDate(0))
            }
            R.id.tv_delayReason -> { // 延时原因
                writeFlag = 'E'
                showInputDialog("延时原因", isNULLS(shippingList.delayReason), "none", WRITE_CODE)
            }
        }
    }

    fun reset() {
        setEnables(tv_titleSel, R.drawable.back_style_blue2,true)
        tv_titleSel.text = ""
        tv_so.text = ""
        tv_cabinetNo.text = ""
        tv_seal.text = ""
        tv_carLicense.text = ""
        tv_needArrivalTime.text = ""
        tv_actualArrivalTime.text = ""
        tv_leaveTime.text = ""
        tv_delayReason.text = ""
        scrollView.smoothScrollTo(0, 0);

        shippingList.id = 0
        shippingList.so = ""
        shippingList.cabinetNo = ""
        shippingList.seal = ""
        shippingList.carLicense = ""
        shippingList.needArrivalTime = ""
        shippingList.actualArrivalTime = ""
        shippingList.leaveTime = ""
        shippingList.delayReason = ""
        shippingList.shippingOutUserId = user!!.id
        shippingList.shippingOutTime = Comm.getSysDate(0)
        listEntry = null

        timesTamp = user!!.getId().toString() + "-" + Comm.randomUUID()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SEL_TITLE -> {// 选择出库清单
                    val shippingListTitle = data!!.getSerializableExtra("obj") as ShippingListTitle
                    tv_titleSel.text = shippingListTitle.title
                    shippingList.id = shippingListTitle.shippingListId
                    shippingList.so = shippingListTitle.shippingList.so
                    shippingList.cabinetNo = shippingListTitle.shippingList.cabinetNo
                    shippingList.seal = shippingListTitle.shippingList.seal
                    shippingList.carLicense = shippingListTitle.shippingList.carLicense
                    shippingList.needArrivalTime = shippingListTitle.shippingList.needArrivalTime
                    shippingList.actualArrivalTime = shippingListTitle.shippingList.actualArrivalTime
                    shippingList.leaveTime = shippingListTitle.shippingList.leaveTime
                    shippingList.delayReason = shippingListTitle.shippingList.delayReason
                    // 显示数据
                    tv_so.text = isNULLS(shippingList.so)
                    tv_cabinetNo.text = isNULLS(shippingList.cabinetNo)
                    tv_seal.text = isNULLS(shippingList.seal)
                    tv_carLicense.text = isNULLS(shippingList.carLicense)
                    tv_needArrivalTime.text = isNULLS(shippingList.needArrivalTime)
                    tv_actualArrivalTime.text = isNULLS(shippingList.actualArrivalTime)
                    tv_leaveTime.text = isNULLS(shippingList.leaveTime)
                    tv_delayReason.text = isNULLS(shippingList.delayReason)
                    run_findEntryList()
                }
                WRITE_CODE -> {// 输入条码  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        when(writeFlag) {
                            'A' -> {
                                tv_so.text = value
                                shippingList.so = value
                            }
                            'B' -> {
                                tv_cabinetNo.text = value
                                shippingList.cabinetNo = value
                            }
                            'C' -> {
                                tv_seal.text = value
                                shippingList.seal = value
                            }
                            'D' -> {
                                tv_carLicense.text = value
                                shippingList.carLicense = value
                            }
                            'E' -> {
                                tv_delayReason.text = value
                                shippingList.delayReason = value
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 查询分录
     */
    private fun run_findEntryList() {
        showLoadDialog("加载中...", false)
        val mUrl = getURL("shippingList/findEntryList")

        val formBody = FormBody.Builder()
            .add("fid", shippingList.id.toString())
            .add("usabelQtyGt0", "1") // 可用数量大于0才显示
            .build()

        val request = Request.Builder()
            .addHeader("cookie", getSession())
            .url(mUrl)
            .post(formBody)
            .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNFIND_ENTRY)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNFIND_ENTRY, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(FIND_ENTRY, result)
                LogUtil.e("run_findEntryList --> onResponse", result)
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
        mBinder!!.unbind()
        customDatePicker1 = null
        customDatePicker2 = null
        closeHandler(mHandler)
        super.onDestroyView()
    }

}