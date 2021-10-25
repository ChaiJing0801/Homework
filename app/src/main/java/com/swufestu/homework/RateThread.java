package com.swufestu.homework;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RateThread implements Runnable{
    private static final String TAG="MyThread";
    private Handler handler;
    Bundle bundle;
    List<String> retlist=new ArrayList<String>();

    public RateThread(Handler handler){
        this.handler=handler;
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "run: run()");

        //获取网络数据
        String url=null;
        bundle=new Bundle();
        try {
//                //获得表格内容
//                Elements tables=doc.getElementsByTag("table");//集合对象
//                Element table2=tables.get(1);
//                Log.i(TAG, "run: table="+table2);
//
////            Elements trs=table2.getElementsByTag("tr");
////            for(Element tr:trs){
////                Log.i(TAG, "run: tr="+tr);
////            }
//
//                Elements hrefs=table2.getElementsByTag("a");
//                for(Element href:hrefs){
//                    Log.i(TAG, "run: href="+href.text());
//                }
//
//
//                Elements trs=table2.getElementsByTag("tr");
//                trs.remove(0);
//                for(Element tr:trs){
//                    Elements tds=tr.getElementsByTag("td");
//                    String cname=tds.get(0).text();
//                    String cval=tds.get(5).text();
//                    Log.i(TAG, "run: "+cname+"==>"+cval);
//                    retlist.add(cname+"==>"+cval);
//
//                }\
            url=" https://www.boc.cn/sourcedb/whpj/ ";
            Document doc= Jsoup.connect(url).get();
            //获得页面元素
            Log.i(TAG, "run: title="+doc.title());

            Elements h4s=doc.getElementsByTag("h4");
            for(Element h4:h4s){
                Log.i(TAG, "run: h4="+h4.text());
            }
            //获得表格内容
            Elements tables=doc.getElementsByTag("table");//集合对象
            Element table2=tables.get(1);
            Log.i(TAG, "run: table="+table2);
            Elements trs=table2.getElementsByTag("tr");
            trs.remove(0);
            for(Element tr:trs){
                Elements tds=tr.getElementsByTag("td");
                String cname=tds.get(0).text();
                String cval=tds.get(5).text();
                Log.i(TAG, "run: "+cname+"==>"+cval);
                retlist.add(cname+"==>"+cval);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //发送消息给主线程
        Message msg=handler.obtainMessage();
        msg.what=7;
        //msg.obj="Hello from run";
        msg.obj=retlist;

        handler.sendMessage(msg);

        Log.i(TAG, "run: 消息已发送");
    }
}