package com.swufestu.homework;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
//import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExchangeRate extends AppCompatActivity implements Runnable{

    private static final String TAG = "ExchangeRate";
    MyHandler handler = new MyHandler(this);
    public String record_date;
    public float ex_rate1 = -1;
    public float ex_rate2 = -1;
    public float ex_rate3 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date currentTime = Calendar.getInstance().getTime();
        String actual_date = formatter.format(currentTime);
        Log.i(TAG, "onCreate: actual date=" + actual_date);

        SharedPreferences sp = getSharedPreferences("my_rate_1",MainActivity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        ex_rate1 = sp.getFloat("ex_rate_1",0.1547f);
        ex_rate2 = sp.getFloat("ex_rate_2",0.132f);
        ex_rate3 = sp.getFloat("ex_rate_3",182.4343f);
        record_date = sp.getString("record_date","20210930");

        Log.i(TAG, "onCreate: record=" + Long.parseLong(record_date));
        Log.i(TAG, "onCreate: actual=" + Long.parseLong(actual_date));
        if(Long.parseLong(record_date) < Long.parseLong(actual_date)){
            Thread t = new Thread(this);
            t.start();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("record_date", actual_date);
            editor.apply();
            Log.i(TAG, "create: need to update!\nThe exchange rate has been updated! time: " + actual_date);
        }

        //exchange dollar
        Button ex1 = (Button) findViewById(R.id.btn_dollar);
        ex1.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            exchange(ex_rate1,"美元");
        });
        //exchange euro
        Button ex2 = (Button) findViewById(R.id.btn_euro);
        ex2.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            exchange(ex_rate2,"欧元");
        });
        //exchange won
        Button ex3 = (Button) findViewById(R.id.btn_won);
        ex3.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            exchange(ex_rate3,"韩元");
        });
        //config exchange rate
        Button config = (Button) findViewById(R.id.config);
        config.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent config_interface = new Intent(ExchangeRate.this,Config.class);
            config_interface.putExtra("ex_rate1",ex_rate1);
            config_interface.putExtra("ex_rate2",ex_rate2);
            config_interface.putExtra("ex_rate3",ex_rate3);
            startActivityForResult(config_interface,1);
        });

    }

    void exchange(double ex_rate,String s){
        EditText yuan_input = (EditText) findViewById(R.id.input_rmb);
        if(yuan_input.getText().toString().length() == 0){
            Toast.makeText(ExchangeRate.this, "No valid data entered!Please retry!!", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView result = (TextView)findViewById(R.id.hello);
        float yuan = Float.parseFloat(yuan_input.getText().toString());
        result.setText(yuan+"人民币≈"+String.format("%.2f",ex_rate*yuan)+s);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // config update
                    final Bundle bdl = data.getExtras();
                    ex_rate1 = bdl.getFloat("new_rate1");
                    Toast.makeText(ExchangeRate.this,String.valueOf(ex_rate1), Toast.LENGTH_SHORT).show();
                    ex_rate2 = bdl.getFloat("new_rate2");
                    ex_rate3 = bdl.getFloat("new_rate3");
                    SharedPreferences sp = getSharedPreferences("my_rate_1",ExchangeRate.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat("ex_rate_1",ex_rate1);
                    editor.putFloat("ex_rate_2",ex_rate2);
                    editor.putFloat("ex_rate_3",ex_rate3);

                    editor.apply();
                }
                break;
            default:
        }
    }

    @Override
    public void run() {
        Message msg = handler.obtainMessage(5);
        try {
            Document doc = Jsoup.connect("https://www.usd-cny.com/bankofchina.htm").get();
            Elements rate_info  = doc.select("tr:matches(美元|欧元|韩元)");
            for(Element info:rate_info){
                Elements tds = info.getElementsByTag("td");
                String Currency = tds.get(0).text();
                float ex_rate = 100/Float.parseFloat(tds.get(5).text());
                Log.i(TAG, "run: Currency = " + Currency + "\tex_rate = " + (ex_rate));
                //进行汇率更新
                SharedPreferences sp = getSharedPreferences("my_rate_1",ExchangeRate.MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
                switch (Currency){
                    case "美元":
                        ex_rate1 = ex_rate;
                        editor.putFloat("ex_rate_1",ex_rate);
                        continue;
                    case "欧元":
                        ex_rate2 = ex_rate;
                        editor.putFloat("ex_rate_2",ex_rate);
                        continue;
                    case "韩元":
                        ex_rate3 = ex_rate;
                        editor.putFloat("ex_rate_3",ex_rate);
                }
                editor.apply();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.obj = "hello from run()";
        handler.sendMessage(msg);
    }


    static class MyHandler extends Handler {
        WeakReference<ExchangeRate> mActivity;

        MyHandler(ExchangeRate activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ExchangeRate theActivity = mActivity.get();
            if(msg.what == 5){
                //TODO SOMETHING
                TextView result = theActivity.findViewById(R.id.hello);
                result.setText(msg.obj.toString());

            }
            super.handleMessage(msg);
        }
    }
}