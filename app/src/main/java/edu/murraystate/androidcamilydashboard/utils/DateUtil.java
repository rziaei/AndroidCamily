package edu.murraystate.androidcamilydashboard.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * Receive today's date in yyyyyMMdd format.
     * @return
     */
    public static String getTodayyyyyMMdd() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    /**
     * Receive today's date in EEYYYYMMdd format.
     * @return
     */
    public static String getTodaEEEyyyyyMMdd() {
        return new SimpleDateFormat("(EEE) yyyyMMdd", Locale.ENGLISH).format(new Date());
    }

    /**
     * 특정 date포멧을 원하는 date포멧으로 변경한다.
     * Change the specific date format to the desired date format.
     * @param intputForm
     * @param outputForm
     * @param date
     * @return
     */
    public static String convertDateFormat(String intputForm, String outputForm, String date) {

        String convertDate = "";

        SimpleDateFormat inputFormat = new SimpleDateFormat(intputForm);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputForm, Locale.ENGLISH);
        try {

            if (TextUtils.isEmpty(date))
                return convertDate;

            Date outputDate = inputFormat.parse(date);
            convertDate = outputFormat.format(outputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertDate;
    }

    /**
     * 입력된 날짜를 calendar 형으로 받아온다
     * Receive the date entered in the calendar format
     * @param format
     * @param dateString
     * @return
     */
    public static Calendar getCalendar(String format, String dateString) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datesformat = new SimpleDateFormat(format);
        try {
            Date date = datesformat.parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }
}
