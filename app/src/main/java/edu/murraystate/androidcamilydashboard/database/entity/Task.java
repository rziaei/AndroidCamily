package edu.murraystate.androidcamilydashboard.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static edu.murraystate.androidcamilydashboard.util.Const.TASK_TABLE_NAME;

@Entity(tableName = TASK_TABLE_NAME)
public class Task implements Serializable {

    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private int type;
    @ColumnInfo
    private int year;
    @ColumnInfo
    private int month;
    @ColumnInfo
    private int day;
    @ColumnInfo
    private int hour;
    @ColumnInfo
    private int minute;

    public Task(int id, String title, int type, int year, int month, int day, int hour, int minute) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    @Ignore
    public Task(String title, int type, int year, int month, int day, int hour, int minute) {
        this.title = title;
        this.type = type;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
