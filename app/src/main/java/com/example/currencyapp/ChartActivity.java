package com.example.currencyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.extensions.builders.SciChartBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {

    ICurrencyApi apiInterface;
    private Spinner baseSpinner;
    private Spinner currencySpinner;
    private Button linear;
    private Button column;

    private TextView day_tv;
    private TextView day_7_tv;
    private TextView month_tv;
    private TextView month_3_tv;
    private TextView month_6_tv;
    private TextView year_tv;

    private Date startDate;
    private Date today;
    private String currency = "EUR";
    private String base = "USD";
    private String chartType = "L";

    private boolean isLoading = false;

    final int blue = Color.rgb(82,145,255);
    final int gray = Color.rgb(161,161,161);
    final int green = Color.rgb(52,217,112);

    ArrayList<String> symbols;
    ArrayList<Currency> currencies;
    SpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart2);

        try {
            SciChartSurface.setRuntimeLicenseKey("7QCUz/LH3lIkR9PapqaEI59aRNhtd2AXLTL2oXhlwXqn7A0ai5J/ZunF9U8MWCUVUTTgusGVdK5fUGYGR3CXpt3g02tslpClyeOqkx+y55tIatb6T3iYMOCzcYPmaKWj9i4Up1oFqJb6HLE7eRWVFOm3RbMVOudNpACJrcuEEregLAEfuNyrciIBm+FRkm6NqgYu7oRhyhjtraV+hK1V9vHN4rBgDm9y+13YTFINJ+BmRDY/N16ZWXbW2/Ibu/lA5bhhvWYdXstuyKGq4fTKVa0PCztB/NIsbmUErZFpzLsw8rrv/xufMtMnMP+9s0mSzJ2inXiED8GzmWOE1pHaUQVR7I2sJPXuAlocAnPZt6vnX5m9bG1D3R6BWEhkEmLGmKjBzCsgFZ8Jjn8YMTJi3h2rk04oeJ4zVIIqiTZfjp60PP1eXNFRHythck70PxFmaSBmHVy6R8lm19tiqzmAII/MA///f+vFSNTj+cQeGscDYx0H8Z2fYEny31oYu3FEUlfpl+gracMBVO2PlzgZnUxkkclXcObD4nUqODBXE1PLj5aKa9QbZZamjP+mBQ/AhT+G6J6xm3UQfnY=");
        } catch (Exception e) {
            e.printStackTrace();
        }

        baseSpinner = findViewById(R.id.base_spinner);
        baseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                base = symbols.get(position);
                if(!isLoading)
                getTimeSeries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        currencySpinner = findViewById(R.id.currency_spinner);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currency = symbols.get(position);
                if(!isLoading)
                getTimeSeries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.floating_changeView).setOnClickListener(v -> {
            Intent intent = new Intent(ChartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        apiInterface = CurrencyApi.getClient().create(ICurrencyApi.class);


        {
            today = new Date(System.currentTimeMillis());
            symbols = new ArrayList<>();
            currencies = new ArrayList<>();
            day_tv = findViewById(R.id.day);
            day_tv.setOnClickListener(this::click);
            day_7_tv = findViewById(R.id.week);
            day_7_tv.setOnClickListener(this::click);
            month_tv = findViewById(R.id.month);
            month_tv.setOnClickListener(this::click);
            month_3_tv = findViewById(R.id.month_3);
            month_3_tv.setOnClickListener(this::click);
            month_6_tv = findViewById(R.id.month_6);
            month_6_tv.setOnClickListener(this::click);


            linear = findViewById(R.id.btn_linear);
            column = findViewById(R.id.btn_column);

            resetColors();
            startDate = new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000);
            day_tv.setTextColor(blue);
            linear.setTextColor(green);
            linear.setOnClickListener(this::setChartType);
            column.setOnClickListener(this::setChartType);
        }
        getCurrencies();
    }

    private void setChartType(View view) {
        if(view.getId() == R.id.btn_linear)
        {
            chartType = "L";
            linear.setTextColor(green);
            column.setTextColor(Color.WHITE);
        }else if(view.getId() == R.id.btn_column)
        {
            chartType = "C";
            column.setTextColor(green);
            linear.setTextColor(Color.WHITE);
        }
        getTimeSeries();
    }

    private void resetColors()
    {
        day_tv.setTextColor(gray);
        day_7_tv.setTextColor(gray);
        month_tv.setTextColor(gray);
        month_3_tv.setTextColor(gray);
        month_6_tv.setTextColor(gray);
    }

    private void click(View view) {
        resetColors();
        switch (view.getId())
        {
            case R.id.day:
                startDate = new Date(System.currentTimeMillis() - 3*24*60*60 * 1000);
                day_tv.setTextColor(blue);
                break;
            case R.id.week:
                startDate = new Date(System.currentTimeMillis() - 7*24*60*60 * 1000);
                day_7_tv.setTextColor(blue);
                break;
            case R.id.month:
                startDate = new Date(System.currentTimeMillis() - 30L *24*60*60 * 1000);
                month_tv.setTextColor(blue);
                break;
            case R.id.month_3:
                startDate = new Date(System.currentTimeMillis() - 91L *24*60*60 * 1000);
                month_3_tv.setTextColor(blue);
                break;
            case R.id.month_6:
                startDate = new Date(System.currentTimeMillis() - 182L *24*60*60 * 1000);
                month_6_tv.setTextColor(blue);
                break;
        }

        getTimeSeries();
    }

    private void getCurrencies()
    {
        Context mContext = this;
        Call<JsonObject> getCurrencies = apiInterface.getCurrencies();
        getCurrencies.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                JsonObject symbolsJson = res.getAsJsonObject("symbols");
                symbols = new ArrayList<>(symbolsJson.keySet());


                for(int i = 0; i < symbols.size(); i++)
                {
                    String drawableName = "flags_" + symbols.get(i).toLowerCase();
                    int drawable_id = mContext.getResources().getIdentifier(drawableName, "drawable", mContext.getPackageName());
                    if(drawable_id == 0) drawable_id = R.drawable.flags_unknown;
                    String symbol = symbols.get(i);
                    String description = symbolsJson.getAsJsonObject(symbol).get("description").getAsString();
                    Currency currency = new Currency(drawable_id, symbol,description);
                    currencies.add(currency);
                }
                adapter = new SpinnerAdapter(mContext, currencies);
                baseSpinner.setAdapter(adapter);
                currencySpinner.setAdapter(adapter);
                baseSpinner.setSelection(symbols.lastIndexOf("USD"));
                currencySpinner.setSelection(symbols.lastIndexOf("EUR"));

                getTimeSeries();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("currencies", t.getLocalizedMessage());
            }
        });
    }

    private String dateToString(Date date)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    private Date stringToDate(String date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getTimeSeries(){
        isLoading = true;
        String start = dateToString(startDate);
        String end = dateToString(today);

        Context mContext = this;

        Call<JsonObject> getData = apiInterface.getTimeSeries(start, end, base, currency);
        getData.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject res = response.body();
                JsonObject ratesJson = res.getAsJsonObject("rates");
                ArrayList<String> dates = new ArrayList<>(ratesJson.keySet());

                SciChartSurface surface = new SciChartSurface(mContext);
                LinearLayout chartLayout = (LinearLayout) findViewById(R.id.chart);
                chartLayout.removeAllViews();
                chartLayout.addView(surface);

                SciChartBuilder.init(mContext);
                final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();
                final IAxis xAxis = sciChartBuilder.newDateAxis()
                        .withAxisTitle("Date")
                        .build();
                final IAxis yAxis = sciChartBuilder.newNumericAxis().withVisibleRange(0.0, 10.0).build();
                XyDataSeries lineData = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();

                yAxis.setMinimalZoomConstrain(0d);
                yAxis.setMaximumZoomConstrain(100000d);

                for(int i = 0; i < dates.size(); i++)
                {
                    String key = dates.get(i);
                    Date date = stringToDate(key);
                    Double price = ratesJson.getAsJsonObject(key).get(currency).getAsDouble();
                    lineData.append(date, price);
                }

                if(Objects.equals(chartType, "L"))
                {
                    IRenderableSeries lineSeries = sciChartBuilder.newLineSeries()
                            .withDataSeries(lineData)
                            .withStrokeStyle(ColorUtil.LightBlue, 2f, true)
                            .build();

                    Collections.addAll(surface.getYAxes(), yAxis);
                    Collections.addAll(surface.getXAxes(), xAxis);
                    surface.getRenderableSeries().add(lineSeries);
                    surface.zoomExtents();
                }else{
                    FastColumnRenderableSeries lineSeries = sciChartBuilder.newColumnSeries()
                            .withStrokeStyle(0xFF232323, 0.4f)
                            .withDataPointWidth(0.7)
                            .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                            .withDataSeries(lineData)
                            .build();

                    Collections.addAll(surface.getYAxes(), yAxis);
                    Collections.addAll(surface.getXAxes(), xAxis);
                    surface.getRenderableSeries().add(lineSeries);
                    surface.zoomExtents();
                }




                isLoading = false;
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
