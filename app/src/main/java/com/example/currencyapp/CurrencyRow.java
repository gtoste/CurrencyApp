package com.example.currencyapp;

public class CurrencyRow {
    private int mImageDrawable;
    private String symbol;
    private String tag;
    private String tag2;
    private double worth;
    private double con;

    public CurrencyRow(int mImageDrawable, String symbol, String tag, String tag2, double worth, double con) {
        this.mImageDrawable = mImageDrawable;
        this.symbol = symbol;
        this.tag = tag;
        this.tag2 = tag2;
        this.worth = worth;
        this.con = con;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public int getmImageDrawable() {
        return mImageDrawable;
    }

    public void setmImageDrawable(int mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getWorth() {
        return worth;
    }

    public void setWorth(double worth) {
        this.worth = worth;
    }

    public double getCon() {
        return con;
    }

    public void setCon(double con) {
        this.con = con;
    }
}
