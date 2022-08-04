package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

public class MyDateTime {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minus;
    private int second;

    public MyDateTime(int year, int month, int day, int hour, int minus, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minus = minus;
        this.second = second;
    }
    public MyDateTime() {

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

    public int getMinus() {
        return minus;
    }

    public void setMinus(int minus) {
        this.minus = minus;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
