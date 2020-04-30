package edu.murraystate.androidcamilydashboard.util;


import androidx.annotation.NonNull;

public class Date {

    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @NonNull
    @Override
    public String toString() {
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
}
