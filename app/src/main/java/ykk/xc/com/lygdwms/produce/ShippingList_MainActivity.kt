package ykk.xc.com.lygdwms.produce

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import kotlinx.android.synthetic.main.shippinglist_fragment1.*
import kotlinx.android.synthetic.main.shippinglist_main.*
import okhttp3.*
import ykk.xc.com.lygdwms.R
import ykk.xc.com.lygdwms.bean.ShippingListEntry
import ykk.xc.com.lygdwms.comm.BaseActivity
import ykk.xc.com.lygdwms.comm.BaseFragment
import ykk.xc.com.lygdwms.comm.Comm
import ykk.xc.com.lygdwms.util.JsonUtil
import ykk.xc.com.lygdwms.util.LogUtil
import ykk.xc.com.lygdwms.util.adapter.BaseFragmentAdapter
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 条码变更 组码和拆码
 */
class ShippingList_MainActivity : BaseActivity() {

    companion object {
        private val SAVE = 200
        private val UNSAVE = 500

    }
    private val context = this
    private val TAG = "Prod_InStockMainActivity"
    private var curRadio: View? = null
    private var curRadioName: TextView? = null
    private var okHttpClient: OkHttpClient? = null

    val fragment1 = ShippingList_Fragment1()
    val fragment2 = ShippingList_Fragment2()
    var pageId = 0

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: ShippingList_MainActivity) : Handler() {
        private val mActivity: WeakReference<ShippingList_MainActivity>

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
                    SAVE -> { // 保存 成功
                        m.toasts("保存成功")
                        m.reset()
                    }
                    UNSAVE -> { // 保存 失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "保存失败！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                }
            }
        }
    }

    override fun setLayoutResID(): Int {
        return R.layout.shippinglist_main;
    }

    override fun initView() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                .writeTimeout(120, TimeUnit.SECONDS) // 设置写的超时时间
                .readTimeout(120, TimeUnit.SECONDS) //设置读取超时时间
                .build()
        }
    }

    override fun initData() {
        bundle()
        curRadio = viewRadio1
        curRadioName = tv_radioName1
        val listFragment = ArrayList<Fragment>()
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数
//        Pur_ScInFragment1 fragment1 = new Pur_ScInFragment1();
//        Sal_OutFragment2 fragment2 = new Sal_OutFragment2();
//        Sal_OutFragment3 fragment3 = new Sal_OutFragment3();

        listFragment.add(fragment1)
        listFragment.add(fragment2)
        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(BaseFragmentAdapter(supportFragmentManager, listFragment))
        //设置ViewPage缓存界面数，默认为1
        viewPager.offscreenPageLimit = 2
        //ViewPager显示第一个Fragment
        viewPager!!.setCurrentItem(0)

        //ViewPager页面切换监听
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> tabChange(viewRadio1!!, tv_radioName1, "表头", 0)
                    1 -> tabChange(viewRadio2!!, tv_radioName2, "分录", 1)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    private fun bundle() {
        val bundle = context.intent.extras
        if (bundle != null) {
        }
    }

    @OnClick(R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.btn_clone, R.id.btn_save)
    fun onViewClicked(view: View) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        when (view.id) {
            R.id.btn_close // 关闭
            -> {
                if (fragment1.shippingList.id > 0) {
                    val build = AlertDialog.Builder(context)
                    build.setIcon(R.drawable.caution)
                    build.setTitle("系统提示")
                    build.setMessage("您有未保存的数据，继续关闭吗？")
                    build.setPositiveButton("是", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            context.finish()
                        }
                    })
                    build.setNegativeButton("否", null)
                    build.setCancelable(false)
                    build.show()

                } else {
                    context.finish()
                }
            }
            R.id.btn_search -> { // 查询
            }
            R.id.lin_tab1 -> {
                tabChange(viewRadio1!!, tv_radioName1, "表头", 0)
            }
            R.id.lin_tab2 -> {
                if(fragment1.shippingList.id > 0) {
                    tabChange(viewRadio2!!, tv_radioName2, "分录", 1)
                } else {
                    Comm.showWarnDialog(context,"请先选择出货清单！")
                }
            }
            R.id.btn_save -> { // 保存
                if(fragment2.listDatas.size == 0) {
                    Comm.showWarnDialog(context,"列表信息不完善，不能保存！")
                    return
                }
                val listResult = ArrayList<ShippingListEntry>()
                var unFinishPosition = -1 // 是否有数量未扫完
                fragment2.listDatas.forEachIndexed { index, it ->
                    if(it.outQty > 0) {
                        listResult.add(it)
                        if(it.usableQty > it.outQty) {
                            unFinishPosition = index
                        }
                    }
                }
                if(listResult.size == 0) {
                    Comm.showWarnDialog(context,"请至少扫描一个有效条码来匹配数量！")
                    return
                }
                if(unFinishPosition > -1) {
                    val build = AlertDialog.Builder(context)
                    build.setIcon(R.drawable.caution)
                    build.setTitle("系统提示")
                    build.setMessage("第（"+(unFinishPosition+1)+"）行，数量未扫完，是否保存？")
                    build.setPositiveButton("是") { dialog, which -> run_save(listResult) }
                    build.setNegativeButton("否", null)
                    build.setCancelable(false)
                    build.show()
                } else {
                    run_save(listResult)
                }
            }
            R.id.btn_clone -> { // 重置
                if (fragment1.shippingList.id > 0) {
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
     * 选中之后改变样式
     */
    private fun tabSelected(v: View, tv: TextView) {
        curRadio!!.setBackgroundResource(R.drawable.check_off2)
        v.setBackgroundResource(R.drawable.check_on)
        curRadio = v
        curRadioName!!.setTextColor(Color.parseColor("#000000"))
        tv.setTextColor(Color.parseColor("#FF4400"))
        curRadioName = tv
    }

    private fun tabChange(view: View, tv: TextView, str: String, page: Int) {
        pageId = page
        tabSelected(view, tv)
//        tv_title.text = str
        viewPager!!.setCurrentItem(page, false)
    }

    fun reset() {
        // 滑动第一个页面
        viewPager!!.setCurrentItem(0, false)
        viewPager.setScanScroll(false) // 禁止滑动
        fragment1.reset()
        fragment2.reset()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                BaseFragment.CAMERA_SCAN -> {// 扫一扫成功  返回
                    val hmsScan = data!!.getParcelableExtra(ScanUtil.RESULT) as HmsScan
                    if (hmsScan != null) {
                        when(pageId) {
                            1 -> fragment2.getScanData(hmsScan.originalValue)
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存
     */
    private fun run_save(listResult : ArrayList<ShippingListEntry>) {
        fragment1.shippingList.needArrivalTime = getValues(tv_needArrivalTime)
        fragment1.shippingList.actualArrivalTime = getValues(tv_actualArrivalTime)
        fragment1.shippingList.leaveTime = getValues(tv_leaveTime)
        showLoadDialog("加载中...", false)
        val mUrl = getURL("shippingList/save")

        val formBody = FormBody.Builder()
            .add("strJson", JsonUtil.objectToString(fragment1.shippingList))
            .add("strJsonEntry", JsonUtil.objectToString(listResult))
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish()
        }
        return false
    }

}