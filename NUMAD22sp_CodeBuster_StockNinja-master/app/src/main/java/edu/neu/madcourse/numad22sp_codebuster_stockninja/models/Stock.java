package edu.neu.madcourse.numad22sp_codebuster_stockninja.models;

public class Stock {
    private String stockSymbol;
    private String companyName;
    private double averagePrice;
    private double currPrice;
    private int shares;

    public Stock(String stockSymbol, String companyName, double averagePrice, double currPrice, int shares) {
        this.stockSymbol = stockSymbol;
        this.companyName = companyName;
        this.averagePrice = averagePrice;
        this.currPrice = currPrice;
        this.shares = shares;
    }

    public Stock() {

    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getCompanyName() { return companyName; }

    public double getAveragePrice() {
        return averagePrice;
    }

    public double getCurrPrice() { return currPrice; }

    public void setCurrPrice(double currPrice) { this.currPrice = currPrice; }

    public int getShares() { return shares; }


    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setCompanyName(String companyName) {this.companyName = companyName;}

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
