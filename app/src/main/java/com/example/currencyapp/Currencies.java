package com.example.currencyapp;

import java.util.ArrayList;

public class Currencies {
    ArrayList<Currency> currencies;

    public Currencies(ArrayList<Currency> currencies) {
        this.currencies = currencies;
    }

    public ArrayList<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayList<Currency> currencies) {
        this.currencies = currencies;
    }

    public ArrayList<String> getCurrenciesSymbols()
    {
        ArrayList<String> currencies_symbol = new ArrayList<>();

        for(int i = 0; i < currencies.size(); i++)
        {
            currencies_symbol.add(currencies.get(i).getSymbol());
        }
        return currencies_symbol;
    }

}
