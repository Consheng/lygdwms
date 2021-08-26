package ykk.xc.com.lygdwms.util.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import ykk.xc.com.lygdwms.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 选择年月日时分秒（）
 */
public class CustomDatePicker
{
    /**
     * 定义结果回调接口
     */
    public interface ResultHandler
    {
        void handle(String time);
    }

    public enum SCROLL_TYPE
    {
        HOUR(1),
        MINUTE(2),
        SECOND(4); //为了进行&运算，应该是4

        SCROLL_TYPE(int value)
        {
            this.value = value;
        }

        public int value;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    //private int scrollUnits = SCROLL_TYPE.HOUR.value + SCROLL_TYPE.MINUTE.value + SCROLL_TYPE.SECOND.value;
    private int scrollUnits = 0;
    private ResultHandler handler;
    private Context context;
    private boolean canAccess = false;

    private Dialog datePickerDialog;
    private DatePickerView year_pv, month_pv, day_pv, hour_pv, minute_pv, second_pv;

    private static final int MAX_SECOND = 59;
    private static final int MAX_MINUTE = 59;
    private static final int MAX_HOUR = 23;
    private static final int MIN_SECOND = 0;
    private static final int MIN_MINUTE = 0;
    private static final int MIN_HOUR = 0;
    private static final int MAX_MONTH = 12;

    private ArrayList<String> year, month, day, hour, minute, second;
    private int startYear, startMonth, startDay, startHour, startMinute, startSecond, endYear, endMonth, endDay, endHour, endMinute, endSecond;
    private boolean spanYear, spanMon, spanDay, spanHour, spanMin, spanSecond;
    private Calendar selectedCalender, startCalendar, endCalendar;
    private TextView tv_cancle, tv_confirm, hour_text, minute_text, second_text, tv_selectTime;

    public CustomDatePicker(Context context, ResultHandler resultHandler, String startDate, String endDate) {
        if (isValidDate(startDate, "yyyy-MM-dd HH:mm:ss") && isValidDate(endDate, "yyyy-MM-dd HH:mm:ss")) {
            canAccess = true;
            this.context = context;
            this.handler = resultHandler;
            selectedCalender = Calendar.getInstance();
            startCalendar = Calendar.getInstance();
            endCalendar = Calendar.getInstance();

            try {
                startCalendar.setTime(sdf.parse(startDate));
                endCalendar.setTime(sdf.parse(endDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            initDialog();
            initView();
        }
    }

    private void initDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = new Dialog(context, R.style.time_dialog);
            datePickerDialog.setCancelable(false);
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            datePickerDialog.setContentView(R.layout.custom_date_picker);

            Window window = datePickerDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        year_pv = datePickerDialog.findViewById(R.id.year_pv);
        month_pv = datePickerDialog.findViewById(R.id.month_pv);
        day_pv = datePickerDialog.findViewById(R.id.day_pv);
        hour_pv = datePickerDialog.findViewById(R.id.hour_pv);
        minute_pv = datePickerDialog.findViewById(R.id.minute_pv);
        second_pv = datePickerDialog.findViewById(R.id.second_pv);
        tv_cancle = datePickerDialog.findViewById(R.id.tv_cancle);
        tv_confirm = datePickerDialog.findViewById(R.id.tv_confirm);
        hour_text = datePickerDialog.findViewById(R.id.hour_text);
        minute_text = datePickerDialog.findViewById(R.id.minute_text);
        second_text = datePickerDialog.findViewById(R.id.second_text);
        tv_selectTime = datePickerDialog.findViewById(R.id.tv_selectTime);

        // 取消
        tv_cancle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                datePickerDialog.dismiss();
            }
        });

        // 确定
        tv_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handler.handle(sdf.format(selectedCalender.getTime()));
                datePickerDialog.dismiss();
            }
        });
    }

    private void initParameter() {
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        startMinute = startCalendar.get(Calendar.MINUTE);
        startSecond = startCalendar.get(Calendar.SECOND);

        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        endMinute = endCalendar.get(Calendar.MINUTE);
        endSecond = endCalendar.get(Calendar.SECOND);
        spanYear = startYear != endYear;
        spanMon = (!spanYear) && (startMonth != endMonth);
        spanDay = (!spanMon) && (startDay != endDay);
        spanHour = (!spanDay) && (startHour != endHour);
        spanMin = (!spanHour) && (startMinute != endMinute);
        spanSecond = (!spanMin) && (startSecond != endSecond);
        selectedCalender.setTime(startCalendar.getTime());
    }

    private void initTimer() {
        initArrayList();
        if (spanYear) {
            for (int i = startYear; i <= endYear; i++) {
                year.add(String.valueOf(i));
            }
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(formatTimeUnit(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
            if ((scrollUnits & SCROLL_TYPE.HOUR.value) != SCROLL_TYPE.HOUR.value) {
                hour.add(formatTimeUnit(startHour));

            } else {
                for (int i = startHour; i <= MAX_HOUR; i++) {
                    hour.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));

            } else {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.SECOND.value) != SCROLL_TYPE.SECOND.value) {
                second.add(formatTimeUnit(startSecond));

            } else {
                for (int i = startSecond; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
        }
        else if (spanMon) {
            year.add(String.valueOf(startYear));
            for (int i = startMonth; i <= endMonth; i++) {
                month.add(formatTimeUnit(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
            if ((scrollUnits & SCROLL_TYPE.HOUR.value) != SCROLL_TYPE.HOUR.value) {
                hour.add(formatTimeUnit(startHour));

            } else {
                for (int i = startHour; i <= MAX_HOUR; i++) {
                    hour.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));

            } else {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.SECOND.value) != SCROLL_TYPE.SECOND.value) {
                second.add(formatTimeUnit(startSecond));

            } else {
                for (int i = startSecond; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
        }
        else if (spanDay) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));

            for (int i = startDay; i <= endDay; i++) {
                day.add(formatTimeUnit(i));
            }
            if ((scrollUnits & SCROLL_TYPE.HOUR.value) != SCROLL_TYPE.HOUR.value) {
                hour.add(formatTimeUnit(startHour));

            } else {
                for (int i = startHour; i <= MAX_HOUR; i++) {
                    hour.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));

            } else {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.SECOND.value) != SCROLL_TYPE.SECOND.value) {
                second.add(formatTimeUnit(startSecond));

            } else {
                for (int i = startSecond; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
        } else if (spanHour) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            day.add(formatTimeUnit(startDay));

            if ((scrollUnits & SCROLL_TYPE.HOUR.value) != SCROLL_TYPE.HOUR.value) {
                hour.add(formatTimeUnit(startHour));
            } else {
                for (int i = startHour; i <= endHour; i++) {
                    hour.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));

            } else {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.SECOND.value) != SCROLL_TYPE.SECOND.value) {
                second.add(formatTimeUnit(startSecond));

            } else {
                for (int i = startSecond; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
        } else if (spanMin) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            day.add(formatTimeUnit(startDay));
            hour.add(formatTimeUnit(startHour));

            if ((scrollUnits & SCROLL_TYPE.MINUTE.value) != SCROLL_TYPE.MINUTE.value) {
                minute.add(formatTimeUnit(startMinute));

            } else {
                for (int i = startMinute; i <= endMinute; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            if ((scrollUnits & SCROLL_TYPE.SECOND.value) != SCROLL_TYPE.SECOND.value) {
                second.add(formatTimeUnit(startSecond));

            } else {
                for (int i = startSecond; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
        } else if (spanSecond) {
            year.add(String.valueOf(startYear));
            month.add(formatTimeUnit(startMonth));
            day.add(formatTimeUnit(startDay));
            hour.add(formatTimeUnit(startHour));
            minute.add(formatTimeUnit(startMinute));

            if ((scrollUnits & SCROLL_TYPE.SECOND.value) != SCROLL_TYPE.SECOND.value) {
                second.add(formatTimeUnit(startSecond));

            } else {
                for (int i = startSecond; i <= endSecond; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
        }
        loadComponent();
    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        if (day == null) day = new ArrayList<>();
        if (hour == null) hour = new ArrayList<>();
        if (minute == null) minute = new ArrayList<>();
        if (second == null) second = new ArrayList<>();
        year.clear();
        month.clear();
        day.clear();
        hour.clear();
        minute.clear();
        second.clear();
    }

    private void loadComponent() {
        year_pv.setData(year);
        month_pv.setData(month);
        day_pv.setData(day);
        hour_pv.setData(hour);
        minute_pv.setData(minute);
        second_pv.setData(second);
        year_pv.setSelected(0);
        month_pv.setSelected(0);
        day_pv.setSelected(0);
        hour_pv.setSelected(0);
        minute_pv.setSelected(0);
        second_pv.setSelected(0);
        executeScroll();
    }

    private void addListener() {
        // （年）滑动事件
        year_pv.setOnSelectListener(new DatePickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
            if(selectedCalender.get(Calendar.YEAR) != Integer.parseInt(text)) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                monthChange();

                tv_selectTime.setText(sdf.format(selectedCalender.getTime()));
            }
            }
        });

        // （月）滑动事件
        month_pv.setOnSelectListener(new DatePickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
            if(selectedCalender.get(Calendar.MONTH) != (Integer.parseInt(text)- 1)) {
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                //若日期在该月不存在，如31日，则会相应跳到下月开头，此时需要减一个月并将日期置为1日
                //参考 https://blog.csdn.net/freelk/article/details/78427207
                //参考 https://blog.csdn.net/xyx2999/article/details/79908575
                if(selectedCalender.get(Calendar.MONTH) == Integer.parseInt(text))
                {
                    selectedCalender.add(Calendar.MONTH, -1);
                    selectedCalender.set(Calendar.DAY_OF_MONTH, 1);
                }
                dayChange();

                tv_selectTime.setText(sdf.format(selectedCalender.getTime()));
            }
            }
        });

        // （日）滑动事件
        day_pv.setOnSelectListener(new DatePickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
            if(selectedCalender.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(text)) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
                hourChange();

                tv_selectTime.setText(sdf.format(selectedCalender.getTime()));
            }
            }
        });

        // （时）滑动事件
        hour_pv.setOnSelectListener(new DatePickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
            if(selectedCalender.get(Calendar.HOUR_OF_DAY) != Integer.parseInt(text)) {
                selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text));
                minuteChange();

                tv_selectTime.setText(sdf.format(selectedCalender.getTime()));
            }
            }
        });

        // （分）滑动事件
        minute_pv.setOnSelectListener(new DatePickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
            if(selectedCalender.get(Calendar.MINUTE) != Integer.parseInt(text)) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
                secondChange();

                tv_selectTime.setText(sdf.format(selectedCalender.getTime()));
            }
            }
        });

        // （秒）滑动事件
        second_pv.setOnSelectListener(new DatePickerView.onSelectListener()
        {
            @Override
            public void onSelect(String text)
            {
            if(selectedCalender.get(Calendar.SECOND) != Integer.parseInt(text)) {
                selectedCalender.set(Calendar.SECOND, Integer.parseInt(text));

                tv_selectTime.setText(sdf.format(selectedCalender.getTime()));
            }
            }
        });
    }

    private void monthChange() {
        month.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(formatTimeUnit(i));
            }

        } else if (selectedYear == endYear) {
            for (int i = 1; i <= endMonth; i++)
            {
                month.add(formatTimeUnit(i));
            }

        } else {
            for (int i = 1; i <= MAX_MONTH; i++) {
                month.add(formatTimeUnit(i));
            }
        }

        month_pv.setData(month);
        if(month.contains(formatTimeUnit(selectedMonth))) {
            month_pv.setSelected(formatTimeUnit(selectedMonth));

        } else {
            selectedCalender.set(Calendar.MONTH, Integer.parseInt(month.get(0)) - 1);
            month_pv.setSelected(0);
        }
        executeAnimator(month_pv);

        month_pv.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                dayChange();
            }
        }, 100);
    }

    private void dayChange() {
        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);

        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }

        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= endDay; i++) {
                day.add(formatTimeUnit(i));
            }

        } else {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
        }

        day_pv.setData(day);
        if(day.contains(formatTimeUnit(selectedDay))) {
            day_pv.setSelected(formatTimeUnit(selectedDay));

        } else {
            selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
            day_pv.setSelected(0);
        }
        executeAnimator(day_pv);

        day_pv.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                hourChange();
            }
        }, 100);
    }

    private void hourChange() {
        if ((scrollUnits & SCROLL_TYPE.HOUR.value) == SCROLL_TYPE.HOUR.value) {
            hour.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
            int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
                for (int i = startHour; i <= MAX_HOUR; i++) {
                    hour.add(formatTimeUnit(i));
                }

            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
                for (int i = MIN_HOUR; i <= endHour; i++) {
                    hour.add(formatTimeUnit(i));
                }

            } else {
                for (int i = MIN_HOUR; i <= MAX_HOUR; i++) {
                    hour.add(formatTimeUnit(i));
                }
            }
            hour_pv.setData(hour);
            //if(hour.size()>selectedHour)
            if(hour.contains(formatTimeUnit(selectedHour))) {
                hour_pv.setSelected(formatTimeUnit(selectedHour));

            } else {
                selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.get(0)));
                hour_pv.setSelected(0);
            }
            executeAnimator(hour_pv);
        }

        hour_pv.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                minuteChange();
            }
        }, 100);
    }

    private void minuteChange() {
        if ((scrollUnits & SCROLL_TYPE.MINUTE.value) == SCROLL_TYPE.MINUTE.value) {
            minute.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
            int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = selectedCalender.get(Calendar.MINUTE);
            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
                for (int i = startMinute; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }

            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
                for (int i = MIN_MINUTE; i <= endMinute; i++) {
                    minute.add(formatTimeUnit(i));
                }

            } else {
                for (int i = MIN_MINUTE; i <= MAX_MINUTE; i++) {
                    minute.add(formatTimeUnit(i));
                }
            }
            minute_pv.setData(minute);
            if(minute.contains(formatTimeUnit(selectedMinute))) {
                minute_pv.setSelected(formatTimeUnit(selectedMinute));

            } else {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(minute.get(0)));
                minute_pv.setSelected(0);
            }
            executeAnimator(minute_pv);
        }

        minute_pv.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                secondChange();
            }
        }, 100);
    }

    private void secondChange()
    {
        if ((scrollUnits & SCROLL_TYPE.SECOND.value) == SCROLL_TYPE.SECOND.value) {
            second.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
            int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = selectedCalender.get(Calendar.MINUTE);
            int selectedSecond = selectedCalender.get(Calendar.SECOND);

            if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour && selectedMinute == startMinute) {
                for (int i = startSecond; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }

            } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour && selectedMinute == endMinute) {
                for (int i = MIN_SECOND; i <= endSecond; i++) {
                    second.add(formatTimeUnit(i));
                }
            } else {
                for (int i = MIN_SECOND; i <= MAX_SECOND; i++) {
                    second.add(formatTimeUnit(i));
                }
            }
            second_pv.setData(second);
            if(second.contains(formatTimeUnit(selectedSecond))) {
                second_pv.setSelected(formatTimeUnit(selectedSecond));

            } else {
                selectedCalender.set(Calendar.SECOND, Integer.parseInt(second.get(0)));
                second_pv.setSelected(0);
            }
            executeAnimator(second_pv);
        }
        executeScroll();
    }

    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start();
    }

    private void executeScroll() {
        year_pv.setCanScroll(year.size() > 1);
        month_pv.setCanScroll(month.size() > 1);
        day_pv.setCanScroll(day.size() > 1);
        hour_pv.setCanScroll(hour.size() > 1 && (scrollUnits & SCROLL_TYPE.HOUR.value) == SCROLL_TYPE.HOUR.value);
        minute_pv.setCanScroll(minute.size() > 1 && (scrollUnits & SCROLL_TYPE.MINUTE.value) == SCROLL_TYPE.MINUTE.value);
        second_pv.setCanScroll(second.size() > 1 && (scrollUnits & SCROLL_TYPE.SECOND.value) == SCROLL_TYPE.SECOND.value);
    }

    private void disScrollUnit(SCROLL_TYPE... scroll_types) {
        if (scroll_types == null || scroll_types.length == 0) {
            //scrollUnits = SCROLL_TYPE.HOUR.value + SCROLL_TYPE.MINUTE.value + SCROLL_TYPE.SECOND.value;
            scrollUnits = 0;
        } else {
            for (SCROLL_TYPE scroll_type : scroll_types) {
                scrollUnits ^= scroll_type.value;
            }
        }
    }

    public void show(String time) {
        if (canAccess) {
            if (isValidDate(time, "yyyy-MM-dd")) {
                if (startCalendar.getTime().getTime() < endCalendar.getTime().getTime()) {
                    canAccess = true;
                    initParameter();
                    initTimer();
                    addListener();
                    setSelectedTime(time);
                    datePickerDialog.show();
                }
            } else {
                canAccess = false;
            }
        }
    }

    /**
     * 设置日期控件是否显示时分秒
     */
    public void showSpecificTime(boolean show) {
        if (canAccess) {
            if (show) {
                disScrollUnit(SCROLL_TYPE.HOUR, SCROLL_TYPE.MINUTE, SCROLL_TYPE.SECOND);
                hour_pv.setVisibility(View.VISIBLE);
                hour_text.setVisibility(View.VISIBLE);
                minute_pv.setVisibility(View.VISIBLE);
                minute_text.setVisibility(View.VISIBLE);
                second_pv.setVisibility(View.VISIBLE);
                second_text.setVisibility(View.VISIBLE);

            } else {
                //disScrollUnit();
                hour_pv.setVisibility(View.GONE);
                hour_text.setVisibility(View.GONE);
                minute_pv.setVisibility(View.GONE);
                minute_text.setVisibility(View.GONE);
                second_pv.setVisibility(View.GONE);
                second_text.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setIsLoop(boolean isLoop) {
        if (canAccess) {
            this.year_pv.setIsLoop(isLoop);
            this.month_pv.setIsLoop(isLoop);
            this.day_pv.setIsLoop(isLoop);
            this.hour_pv.setIsLoop(isLoop);
            this.minute_pv.setIsLoop(isLoop);
            this.second_pv.setIsLoop(isLoop);
        }
    }

    /**
     * 设置日期控件默认选中的时间
     */
    private void setSelectedTime(String time) {
        if (canAccess) {
            String[] str = time.split(" ");
            String[] dateStr = str[0].split("-");

            year_pv.setSelected(dateStr[0]);
            selectedCalender.set(Calendar.YEAR, Integer.parseInt(dateStr[0]));

            month.clear();
            int selectedYear = selectedCalender.get(Calendar.YEAR);
            if (selectedYear == startYear && selectedYear == endYear) {
                for (int i = startMonth; i <= endMonth; i++) {
                    month.add(formatTimeUnit(i));
                }

            } else if (selectedYear == startYear) {
                for (int i = startMonth; i <= MAX_MONTH; i++) {
                    month.add(formatTimeUnit(i));
                }

            } else if (selectedYear == endYear) {
                for (int i = 1; i <= endMonth; i++) {
                    month.add(formatTimeUnit(i));
                }

            } else {
                for (int i = 1; i <= MAX_MONTH; i++)
                {
                    month.add(formatTimeUnit(i));
                }
            }
            month_pv.setData(month);
            month_pv.setSelected(dateStr[1]);
            selectedCalender.set(Calendar.MONTH, Integer.parseInt(dateStr[1]) - 1);
            executeAnimator(month_pv);

            day.clear();
            int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
            if (selectedYear == startYear && selectedMonth == startMonth) {
                if (startYear == endYear && startMonth == endMonth) {
                    for (int i = startDay; i <= endDay; i++) {
                        day.add(formatTimeUnit(i));
                    }

                } else {
                    for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                        day.add(formatTimeUnit(i));
                    }
                }
            } else if (selectedYear == endYear && selectedMonth == endMonth) {
                for (int i = 1; i <= endDay; i++) {
                    day.add(formatTimeUnit(i));
                }

            } else {
                for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    day.add(formatTimeUnit(i));
                }
            }
            day_pv.setData(day);
            day_pv.setSelected(dateStr[2]);
            selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStr[2]));
            executeAnimator(day_pv);

            if (str.length == 2) {
                String[] timeStr = str[1].split(":");

                if ((scrollUnits & SCROLL_TYPE.HOUR.value) == SCROLL_TYPE.HOUR.value) {
                    hour.clear();
                    int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
                    if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
                        if (startDay == endDay) {
                            for (int i = startHour; i <= endHour; i++) {
                                hour.add(formatTimeUnit(i));
                            }

                        } else {
                            for (int i = startHour; i <= MAX_HOUR; i++) {
                                hour.add(formatTimeUnit(i));
                            }
                        }
                    } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
                        for (int i = MIN_HOUR; i <= endHour; i++) {
                            hour.add(formatTimeUnit(i));
                        }

                    } else {
                        for (int i = MIN_HOUR; i <= MAX_HOUR; i++) {
                            hour.add(formatTimeUnit(i));
                        }
                    }
                    hour_pv.setData(hour);
                    hour_pv.setSelected(timeStr[0]);
                    selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStr[0]));
                    executeAnimator(hour_pv);
                }

                if ((scrollUnits & SCROLL_TYPE.MINUTE.value) == SCROLL_TYPE.MINUTE.value) {
                    minute.clear();
                    int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
                    int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
                    if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
                        if(startHour == endHour) {
                            for (int i = startMinute; i <= endMinute; i++) {
                                minute.add(formatTimeUnit(i));
                            }

                        } else {
                            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                                minute.add(formatTimeUnit(i));
                            }
                        }
                    } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
                        for (int i = MIN_MINUTE; i <= endMinute; i++) {
                            minute.add(formatTimeUnit(i));
                        }
                    } else {
                        for (int i = MIN_MINUTE; i <= MAX_MINUTE; i++) {
                            minute.add(formatTimeUnit(i));
                        }
                    }
                    minute_pv.setData(minute);
                    minute_pv.setSelected(timeStr[1]);
                    selectedCalender.set(Calendar.MINUTE, Integer.parseInt(timeStr[1]));
                    executeAnimator(minute_pv);
                }

                if ((scrollUnits & SCROLL_TYPE.SECOND.value) == SCROLL_TYPE.SECOND.value) {
                    second.clear();
                    int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
                    int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
                    int selectedMinute = selectedCalender.get(Calendar.MINUTE);

                    if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour && selectedMinute == startMinute) {
                        if(startMinute == endMinute) {
                            for (int i = startSecond; i <= endSecond; i++) {
                                second.add(formatTimeUnit(i));
                            }

                        } else {
                            for (int i = startSecond; i <= MAX_SECOND; i++) {
                                second.add(formatTimeUnit(i));
                            }
                        }
                    } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour && selectedMinute == endMinute) {
                        for (int i = MIN_SECOND; i <= endSecond; i++) {
                            second.add(formatTimeUnit(i));
                        }

                    } else {
                        for (int i = MIN_SECOND; i <= MAX_SECOND; i++) {
                            second.add(formatTimeUnit(i));
                        }
                    }
                    second_pv.setData(second);
                    second_pv.setSelected(timeStr[2]);
                    selectedCalender.set(Calendar.SECOND, Integer.parseInt(timeStr[2]));
                    executeAnimator(second_pv);
                }
            }
            executeScroll();
        }
    }

    /**
     * 验证字符串是否是一个合法的日期格式
     */
    private boolean isValidDate(String date, String template) {
        boolean convertSuccess = true;
        // 指定日期格式
        SimpleDateFormat format = new SimpleDateFormat(template, Locale.CHINA);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2015/02/29会被接受，并转换成2015/03/01
            format.setLenient(false);
            format.parse(date);
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }
}