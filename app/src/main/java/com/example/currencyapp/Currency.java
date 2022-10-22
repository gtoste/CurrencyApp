package com.example.currencyapp;

public class Currency {
    private int drawable_id;
    private String symbol;
    private String description;

    public Currency(int drawable_id, String symbol, String description) {
        this.drawable_id = drawable_id;
        this.symbol = symbol;
        this.description = description;
    }

    public int getDrawable_id() {
        return drawable_id;
    }

    public void setDrawable_id(int drawable_id) {
        this.drawable_id = drawable_id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}