package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

import java.time.Instant;
import java.time.LocalDateTime;

public class Transaction {

  String username;
  String symbol;
  String action;
  double price;
  int shares;
  MyDateTime timestamp;

  public Transaction() {
  }



  public Transaction(String username, String symbol, String action, double price, int shares,
                     MyDateTime timestamp) {
    this.username = username;
    this.symbol = symbol;
    this.action = action;
    this.price = price;
    this.shares = shares;
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return this.username;
  }

  public String getSymbol() {
    return this.symbol;
  }

  public String getAction() { return action; }

  public double getPrice() {
    return this.price;
  }

  public int getShares() {
    return this.shares;
  }

  public MyDateTime getTimestamp() {
    return this.timestamp;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public void setAction(String action) { this.action = action; }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setShares(int shares) {
    this.shares = shares;
  }

  public void setTimestamp(MyDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
