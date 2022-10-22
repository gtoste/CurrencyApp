package com.example.currencyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Currency>{
    public SpinnerAdapter(@NonNull Context context, @NonNull ArrayList<Currency> spinnerItemArrayList) {
        super(context, 0, spinnerItemArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.currency_spinner_item, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.text_view);
        ImageView imageView = convertView.findViewById(R.id.image_view);
        Currency currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getSymbol());
            imageView.setImageResource(currentItem.getDrawable_id());
        }
        return convertView;
    }
}
