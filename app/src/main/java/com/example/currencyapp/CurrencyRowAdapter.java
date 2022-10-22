package com.example.currencyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRowAdapter extends ArrayAdapter<CurrencyRow> {
    private Context mContext;
    private List<CurrencyRow> currencyRows;

    public CurrencyRowAdapter(@NonNull Context context, @NonNull List<CurrencyRow> objects) {
        super(context, 0, objects);
        currencyRows = objects;
        mContext = context;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.follow_list_item ,parent,false);

        CurrencyRow currentRow = currencyRows.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.currency_icon);
        image.setImageResource(currentRow.getmImageDrawable());

        TextView symbol = (TextView) listItem.findViewById(R.id.symbol_tv);
        symbol.setText(currentRow.getSymbol());

        TextView tag = (TextView) listItem.findViewById(R.id.tag_tv);
        tag.setText(currentRow.getTag());

        TextView worth = (TextView) listItem.findViewById(R.id.currency_worth_tv);
        worth.setText(String.valueOf(currentRow.getWorth()));

        TextView con = (TextView) listItem.findViewById(R.id.currency_con_tv);
        String conText = "1 " + currentRow.getSymbol() + " = " + String.format("%.6f", currentRow.getCon()) + " " + currentRow.getTag2();
        con.setText(conText);
        return listItem;

    }
}
