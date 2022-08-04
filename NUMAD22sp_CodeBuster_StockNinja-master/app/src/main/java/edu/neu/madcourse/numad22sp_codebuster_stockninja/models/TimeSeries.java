package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeSeries implements Parcelable, Comparable<TimeSeries> {

  private String timestamp;
  private double open;
  private double high;
  private double low;
  private double close;

  public TimeSeries() {
  }

  public TimeSeries(String timestamp, double open, double high, double low, double close) {
    this.timestamp = timestamp;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
  }

  protected TimeSeries(Parcel in) {
    this.timestamp = in.readString();
    this.open = in.readDouble();
    this.high = in.readDouble();
    this.low = in.readDouble();
    this.close = in.readDouble();
  }

  public static final Creator<TimeSeries> CREATOR = new Creator<TimeSeries>() {
    @Override
    public TimeSeries createFromParcel(Parcel in) {
      return new TimeSeries(in);
    }

    @Override
    public TimeSeries[] newArray(int size) {
      return new TimeSeries[size];
    }
  };

  public String getTimestamp() {
    return this.timestamp;
  }

  public double getOpen() {
    return this.open;
  }

  public double getHigh() {
    return this.high;
  }

  public double getLow() {
    return this.low;
  }

  public double getClose() {
    return this.close;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public void setOpen(double open) {
    this.open = open;
  }

  public void setHigh(double high) {
    this.high = high;
  }

  public void setLow(double low) {
    this.low = low;
  }

  public void setClose(double close) {
    this.close = close;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(this.timestamp);
    parcel.writeDouble(this.open);
    parcel.writeDouble(this.high);
    parcel.writeDouble(this.low);
    parcel.writeDouble(this.close);
  }

  @Override
  public int compareTo(TimeSeries that) {
    return that.getTimestamp().compareTo(this.getTimestamp());
  }
}
