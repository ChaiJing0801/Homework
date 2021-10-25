package com.swufestu.homework;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Runnable{
    public static final String TAG = "MainActivity";

    float dollar_rate = 0.1547f;
    float euro_rate = 0.1320f;
    float won_rate = 182.4343f;
    Handler handler;
    TextView resText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resText = findViewById(R.id.hello);

//        //接收save后结果
//        Intent intent = getIntent();
//        dollar_rate = intent.getDoubleExtra("dollar_rate_key2", 0.1547);
//        euro_rate = intent.getDoubleExtra("euro_rate_key2", 0.1320);
//        won_rate = intent.getDoubleExtra("won_rate_key2", 182.4343);

        //使用SharedPreferences对象接受当前汇率数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);

        dollar_rate = sharedPreferences.getFloat("dollar_rate_key2", 0.0f);
        euro_rate = sharedPreferences.getFloat("euro_rate_key2", 0.0f);
        won_rate = sharedPreferences.getFloat("won_rate_key2", 0.0f);

        Log.i(TAG, "currencyOnCreate: dollar_rate=" + dollar_rate);
        Log.i(TAG, "currencyOnCreate: euro_rate=" + euro_rate);
        Log.i(TAG, "currencyOnCreate: won_rate=" + won_rate);

        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i(TAG, "handleMessage: 收到消息");
                if(msg.what==6){
                    //接收消息
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: str=" + str);
                    resText.setText(str);
                }
                super.handleMessage(msg);
            }
        };
        //启动线程
        Thread thread = new Thread(this);
        thread.start(); //this.run()
    }

    //计算汇率转化
    public void clickCurrency(View btn) {

        Log.i(TAG, "click1");

        //获取当前用户输入RMB
        EditText rmb = findViewById(R.id.input_rmb);
        String rmb_temp = rmb.getText().toString();
        Log.i(TAG, "click: rmb="+rmb_temp);

        //获取转换后位置数据
//        TextView resText = findViewById(R.id.welcomeText);
        if(rmb_temp.equals("")){
            //用户尚未输入，报错提示
            Toast.makeText(this, "请输入需要换算的人民币数值", Toast.LENGTH_SHORT).show();
            resText.setText(R.string.hello);
        }
        else{
            float rmb_num = Float.valueOf(rmb_temp);

            if(btn.getId()==R.id.btn_dollar){
                rmb_num *= dollar_rate;
            }else if(btn.getId()==R.id.btn_euro){
                rmb_num *= euro_rate;
            }else if(btn.getId()==R.id.btn_won){
                rmb_num *= won_rate;
            }
            Log.i(TAG, "click: res="+rmb_num);

            //返回汇率换算后结果
            resText.setText(String.valueOf(rmb_num));
        }

    }

    //打开新页面，发送当前汇率值
    public void clickConfig(View v){
        Log.i(TAG, "open: config");
        openConfig();

    }

    private void openConfig() {
        //打开新窗口
        Intent config = new Intent(this, Config.class);
        config.putExtra("dollar_rate_key", dollar_rate);
        config.putExtra("euro_rate_key", euro_rate);
        config.putExtra("won_rate_key", won_rate);

        Log.i(TAG, "clickConfig: dollarRate=" + dollar_rate);
        Log.i(TAG, "clickConfig: euroRate=" + euro_rate);
        Log.i(TAG, "clickConfig: wonRate=" + won_rate);

//        startActivity(config);
        startActivityForResult(config, 1);
    }

    //接收save传值结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //requestCode是哪个窗口带回来的, resultCode表明窗口带回来的哪个数据
        if(requestCode==1 && resultCode==3){
            //不打包方式带回数据
            dollar_rate = data.getFloatExtra("dollar_rate_key2", 0.1547f);
            euro_rate = data.getFloatExtra("euro_rate_key2", 0.1320f);
            won_rate = data.getFloatExtra("won_rate_key2", 182.4343f);

            Log.i(TAG, "onActivityResult: dollar_rate=" + dollar_rate);
            Log.i(TAG, "onActivityResult: euro_rate=" + euro_rate);
            Log.i(TAG, "onActivityResult: won_rate=" + won_rate);
        }else if(requestCode==1 && resultCode==5){
            //打包方式带回数据
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //menu菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymeau, menu);
        return true;
    }

    //选中菜单项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            //打开新窗口
            openConfig();
        }

        return super.onOptionsItemSelected(item);
    }

    //将输入流InputStream转换为String
    private String inputStream2String(InputStream inputStream) throws IOException{

        final int bufferSize=1024;
        final char[] buffer= new char[bufferSize];
        final StringBuilder out= new StringBuilder();
        Reader in= new InputStreamReader(inputStream,"gb2312");
        while (true){
            int rsz = in.read(buffer, 0, buffer.length);
            if(rsz < 0)
                break;
            out.append(buffer, 0 ,rsz);
        }
        return out.toString();
    }

    @Override
    public void run(){
        Log.i(TAG, "run: 111");
        //延迟
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取网络数据
        URL url =null;
        try{
            url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);
            Log.i(TAG, "run: html=" + html);

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        //发送消息
        Message msg = handler.obtainMessage(6);
//        msg.what=6;
        msg.obj = "Hello from run";
        handler.sendMessage(msg);
        Log.i(TAG, "run: 消息已发送");
    }
}