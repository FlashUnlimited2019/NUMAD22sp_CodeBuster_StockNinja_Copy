package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable {

  private String stockSymbol;
  private String companyName;

  public SearchResult() {
  }

  public SearchResult(String stockSymbol, String companyName) {
    this.stockSymbol = stockSymbol;
    this.companyName = companyName;
  }

  protected SearchResult(Parcel in) {
    this.stockSymbol = in.readString();
    this.companyName = in.readString();
  }

  public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
    @Override
    public SearchResult createFromParcel(Parcel in) {
      return new SearchResult(in);
    }

    @Override
    public SearchResult[] newArray(int size) {
      return new SearchResult[size];
    }
  };

  public String getStockSymbol() {
    return this.stockSymbol;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public void setStockSymbol(String stockSymbol) {
    this.stockSymbol = stockSymbol;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(this.stockSymbol);
    parcel.writeString(this.companyName);
  }
}
