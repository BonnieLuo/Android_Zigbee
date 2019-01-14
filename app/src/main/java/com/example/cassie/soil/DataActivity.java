package com.example.cassie.soil;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Cassie on 2018/12/17.
 */

public class DataActivity extends AppCompatActivity{

    private TextView tvContent;
    Vibrator vibrator;
    private TextView tvErrorContent;

    private String baseUrl = "http://10.130.158.224:8081/Keshe_war_exploded/sensor/json";
    private String errorUrl = "http://10.130.158.224:8081/Keshe_war_exploded/Keshe_war_exploded/error/json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datalist_layout);

        tvContent = findViewById(R.id.tv_content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    initData();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        tvErrorContent = findViewById(R.id.tv_error_content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    initErrorData();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void initData(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(baseUrl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Error.error("onFailure"+e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Error.error("onResponse");
                final String res = response.body().string();
                Error.error(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            tvContent.setText("");  //清空标签的内容
                            JSONArray jsonArray = new JSONArray(res);
                            double t1 = Double.parseDouble(jsonArray.getJSONObject(0).getString("temp"));  //温度
                            double l1 = Double.parseDouble(jsonArray.getJSONObject(0).getString("light"));  //光照

                            if(t1 > 580 | l1 < 200){
                                System.out.println(t1 + " 数据超标啦！");
                                showNormalDialog();
                                vibrator.vibrate(2000);
                            }
                            //遍历JSON数组
                            for (int i = 0;i < jsonArray.length();i++){
                                //通过下标获取json数组元素——json对象
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //对JSON对象按键取值
                                String date = jsonObject.getString("time"); //时间
                                String humi = jsonObject.getString("humi"); //湿度
                                String temp = jsonObject.getString("temp"); //温度
                                String light = jsonObject.getString("light"); //光照

                                String data = date+"  "+humi+"  "+temp+"  "+light+"\n"; //拼接
                                tvContent.append(data);   //把拼接的数据追加到标签中
                            }
                            tvContent.setTextColor(Color.BLACK);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public void initErrorData(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(errorUrl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Error.error("onFailure" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Error.error("onResponse");
                final String res = response.body().string();
                Error.error(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       try{
                           tvErrorContent.setText("");  //清空标签的内容
                           JSONArray jsonArray = new JSONArray(res);  //基于content字符串创建json数组
                           //遍历json数组
                           for(int i = 0;i < jsonArray.length();i++){
                               //通过下标获取json数组元素——json对象
                               JSONObject jsonObject = jsonArray.getJSONObject(i);
                               //对json对象按键取值
                               String errorDate = jsonObject.getString("error_time");
                               String errorTemp = jsonObject.getString("error_temp");
                               String errorLight = jsonObject.getString("error_light");
                               String errorInfo = jsonObject.getString("error_info");

                               String data = errorDate+"  "+errorTemp+"  "+errorLight+"  "+errorInfo+"\n";
                               tvErrorContent.append(data);
                           }
                           tvErrorContent.setTextColor(Color.RED);
                       }catch (JSONException e){
                           e.printStackTrace();
                       }
                    }
                });
            }
        });

    }

    private void showNormalDialog(){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(DataActivity.this);
        normalDialog.setTitle("警告");
        normalDialog.setMessage("温度数据超标了！");
        normalDialog.setNegativeButton("我知道啦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        normalDialog.show();
    }
}
