package edu.murraystate.androidcamilydashboard.util;

import edu.murraystate.androidcamilydashboard.R;

public class TaskBindingHelper {

    public static int typeColor(int type) {
        if (type == 0) {
            return R.color.colorMintDark;
        }
        if (type == 1) {
            return R.color.colorGrapeFruitDark;
        }
        return R.color.colorFlowerDark;
    }

    public static String DateToString(int year, int month, int day) {
        if (day <= 9 || month <= 9) {
            if (day <= 9 && month <= 9) {
                return year + "/0" + month + "/0" + day;
            }
            if (day <= 9) {
                return year + "/" + month + "/0" + day;
            }
            return year + "/0" + month + "/" + day;
        } else {
            return year + "/" + month + "/" + day;
        }
    }

    public static int[] StringToDate(String date) {
        String[] dateStrings = date.split("/");
        int year = Integer.parseInt(dateStrings[0]);
        int month = Integer.parseInt(dateStrings[1]);
        int day = Integer.parseInt(dateStrings[2]);
        return new int[]{year, month, day};
    }

    public static String TimeToString(int hour, int minute) {
        if (minute < 10) return hour + ":0" + minute;
        return hour + ":" + minute;
    }

    public static int[] StringToTime(String time) {
        String[] timeStrings = time.split(":");
        int hour = Integer.parseInt(timeStrings[0]);
        int minute = Integer.parseInt(timeStrings[1]);
        return new int[]{hour, minute};
    }

}
