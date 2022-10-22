package com.example.currencyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //layout
    private Spinner currencySpinner;
    private EditText amountEt;
    private ListView watchListLv;
    private FloatingActionButton addFloatingBtn;

    //api
    ICurrencyApi apiInterface;

    private int initialPosition = 0;
    private int currentPosition = 0;
    JsonObject ratesJson;
    ArrayList<String> symbols;
    ArrayList<String> followedList;
    ArrayList<Currency> currencies;
    SpinnerAdapter adapter;
    Double amount = 1d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init layout
        currencySpinner = findViewById(R.id.currency_spinner);
        amountEt = findViewById(R.id.amount);
        watchListLv = findViewById(R.id.watch_list_currency);
        addFloatingBtn = findViewById(R.id.floating_add);
        findViewById(R.id.floating_changeView).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            startActivity(intent);
        });


        //functions
        currencySpinner.setOnItemSelectedListener(getCurrencySpinnerListener());
        addFloatingBtn.setOnClickListener(this::setAddFloatingBtn);
        watchListLv.setOnItemClickListener(onWatchListItemClick());


        amountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String amount_t = s.toString();
                try{
                    amount = Double.parseDouble(amount_t);
                    reloadListView();
                }catch(Exception ignored){};
            }
        });


        //api
        apiInterface = CurrencyApi.getClient().create(ICurrencyApi.class);

        symbols = new ArrayList<>();
        currencies = new ArrayList<>();
        followedList = getFollowedList();
        amountEt.setText("1.00");

        getCurrencies();
    }

    private AdapterView.OnItemClickListener onWatchListItemClick()
    {
        Context mContext = this;
        return (parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setPositiveButton("Unfollow", (dialog, id2) -> {
                       followedList.remove(followedList.get((int) id));
                       saveFollowedList(followedList);
                       reloadListView();
                    })
                    .setNegativeButton("Cancel", (dialog, id2) -> {
                        // User cancelled the dialog
                    });
            builder.show();
        };
    }

    private AdapterView.OnItemSelectedListener getCurrencySpinnerListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentPosition = i;
                getRates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void setAddFloatingBtn(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(adapter);
        spinner.setSelection(initialPosition);

        builder.setView(spinner);
        builder.setMessage("Follow")
                .setPositiveButton("Add", (dialog, id) -> {
                    String item = symbols.get(spinner.getSelectedItemPosition());
                    if(!followedList.contains(item))
                    {
                        followedList.add(item);
                        saveFollowedList(followedList);
                        getRates();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });
        builder.show();

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
                initialPosition = symbols.lastIndexOf("USD");
                currentPosition = initialPosition;
                adapter = new SpinnerAdapter(mContext, currencies);
                currencySpinner.setAdapter(adapter);
                currencySpinner.setSelection(initialPosition);
                getRates();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("currencies", t.getLocalizedMessage());
            }
        });
    }

    private void getRates()
    {
        Context mContext = this;
        String base = symbols.get(currentPosition);
        String body = String.join(",", followedList);;
        if(!body.isEmpty())
        {
            Call<JsonObject> getRates = apiInterface.getRates(base, body);
            getRates.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject res = response.body();
                    ratesJson = res.getAsJsonObject("rates");
                    reloadListView();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(mContext, "Can't load rates. Try again later...", Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    private void reloadListView()
    {
        ArrayList<CurrencyRow> currencyRows = new ArrayList<>();
        for(int i = 0; i < followedList.size(); i++)
        {
            Currency currency = findCurrencyBySymbol(followedList.get(i));
            Double worth = ratesJson.get(followedList.get(i)).getAsDouble() * amount;
            Double con = 1 / worth;
            CurrencyRow currencyRow = new CurrencyRow(
                    currency.getDrawable_id(),
                    currency.getSymbol(),
                    currency.getDescription(),
                    currencies.get(currentPosition).getSymbol(),
                    worth,
                    con
                    );

            currencyRows.add(currencyRow);
        }
        CurrencyRowAdapter currencyRowAdapter = new CurrencyRowAdapter(this, currencyRows);
        watchListLv.setAdapter(currencyRowAdapter);
    }

    private Currency findCurrencyBySymbol(String symbol)
    {
        for(int i = 0; i < currencies.size(); i++)
        {
            if(Objects.equals(currencies.get(i).getSymbol(), symbol))
            {return currencies.get(i);}
        }
        return null;
    }



    public void saveFollowedList(ArrayList<String> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("followed_list", json);
        editor.apply();

    }

    public ArrayList<String> getFollowedList(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("followed_list", null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}