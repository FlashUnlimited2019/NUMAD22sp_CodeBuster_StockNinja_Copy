package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

public class User {

  String username;
  String password;
  Place place;
  double cash;

  public User() {
  }

  public User(String username, String password, Place place) {
    this.username = username;
    this.password = password;
    this.place = place;
    this.cash = 10000.0;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public Place getPlace() {
    return this.place;
  }

  public double getCash() {
    return this.cash;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setLocation(Place place) {
    this.place = place;
  }

  public void setCash(double cash) {
    this.cash = cash;
  }
}
