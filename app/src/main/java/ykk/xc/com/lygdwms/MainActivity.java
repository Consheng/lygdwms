package ykk.xc.com.lygdwms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ykk.xc.com.lygdwms.comm.Comm;
import ykk.xc.com.lygdwms.util.widget.CustomDatePicker;

public class MainActivity extends Activity implements View.OnClickListener
{
    private TextView currentDate, currentTime;
    private CustomDatePicker customDatePicker1, customDatePicker2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout selectTime = findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        RelativeLayout selectDate = findViewById(R.id.selectDate);
        selectDate.setOnClickListener(this);
        currentDate = findViewById(R.id.currentDate);
        currentTime = findViewById(R.id.currentTime);

        initDatePicker();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.selectDate:
            {
                // 日期格式为yyyy-MM-dd
                customDatePicker1.show(currentDate.getText().toString());
                break;
            }
            case R.id.selectTime:
            {
                // 日期格式为yyyy-MM-dd HH:mm
                customDatePicker2.show(Comm.getSysDate(0));
                break;
            }
        }
    }

    private void initDatePicker()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String now = sdf.format(new Date());
        currentDate.setText(now.split(" ")[0]);
        currentTime.setText(Comm.getSysDate(0));

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler()
        {
            @Override
            public void handle(String time)
            { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
            }
        }, "2017-01-01 00:00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(true); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler()
        {
            @Override
            public void handle(String time)
            { // 回调接口，获得选中的时间
                currentTime.setText(time);
            }
        }, "2000-01-01 00:00:00", "2099-12-31 23:59:59"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }
}
