package com.swufestu.homework;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ErcList extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {

    public Handler handler;
    private static final String TAG = "ErcList";
    public ListView myList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erc_list);

        myList2 = findViewById(R.id.my_list);
        myList2.setOnItemClickListener(this);

        Thread t=new Thread(this);
        t.start();
        handler=new Handler(Looper.myLooper()){
            public void handleMessage(Message msg){
                if(msg.what==5){
                    ArrayList<HashMap<String,String>> listItems = (ArrayList<HashMap<String,String>>) msg.obj;
//                    SimpleAdapter my_list = new SimpleAdapter(ErcListActivity2.this,listItems,R.layout.list_item,new String[]{"ItemTitle","ItemDetail"},new int[]{R.id.itemTitle,R.id.itemDetail});
                    MyAdapter my_list = new MyAdapter(ErcList.this,R.layout.list_item,listItems);

                    myList2.setAdapter(my_list);
                    ProgressBar my_bar = (ProgressBar) findViewById(R.id.progressBar);
                    my_bar.setVisibility(View.GONE);
                    myList2.setVisibility(View.VISIBLE);
                }
                super.handleMessage(msg);
            }
        };

    }
    @SuppressLint("DefaultLocale")
    public void run(){

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg=handler.obtainMessage(5);
        ArrayList<HashMap<String,String>> listItems =new ArrayList<HashMap<String,String>>();

        try {
//            String page = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get().select("div[class='turn_page'] p span").text();
//            Log.i(TAG, "run: total_page="+page);
//            for(int i=1;i<=Integer.parseInt(page);i++){
            Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Elements rate_info  = doc.getElementsByTag("table").get(1).getElementsByTag("tr");
            rate_info.remove(0);//Remove index row
            for(Element info:rate_info){
                Elements tds = info.getElementsByTag("td");
                String Currency = tds.get(0).text();
                float ex_rate = 100/Float.valueOf(tds.get(5).text());
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("ItemTitle",Currency);
                map.put("ItemDetail",""+ex_rate);
                listItems.add(map);
                Log.i(TAG, "run: Currency = " + String.format("%7s",Currency) + "\tex_rate = " + (ex_rate));
            }
//            }
            msg.obj=listItems;
            handler.sendMessage(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object itemAtPosition = myList2.getItemAtPosition(i);
        HashMap<String,String> map = (HashMap<String, String>) itemAtPosition;
        String Currency = map.get("ItemTitle");
        String ex_rate = map.get("ItemDetail");
        Intent show_interface = new Intent(ErcList.this,ErcListShow.class);
        show_interface.putExtra("Currency",Currency);
        show_interface.putExtra("ex_rate",ex_rate);
        startActivity(show_interface);
    }
}