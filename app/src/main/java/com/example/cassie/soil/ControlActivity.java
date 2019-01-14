package com.example.cassie.soil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Cassie on 2018/12/17.
 */

public class ControlActivity extends AppCompatActivity{

    Switch switch1;
    Switch switch2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_layout);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChild()){
                    Toast.makeText(ControlActivity.this,"打开了风扇",Toast.LENGTH_SHORT).show();
                    String url = "http://10.130.158.224:8081/Keshe_war_exploded/sensor/send?request=01";
                    new MyAsyncTask().execute(url);
                }else{
                    Toast.makeText(ControlActivity.this,"关闭了风扇",Toast.LENGTH_SHORT).show();
                    String url = "http://10.130.158.224:8081/sensor/send?request=01";
                    new MyAsyncTask().execute(url);
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChild()){
                    Toast.makeText(ControlActivity.this,"打开了灯",Toast.LENGTH_SHORT).show();
                    String url = "http://10.130.158.224:8081/Keshe_war_exploded/sensor/send?request=08";
                    new MyAsyncTask().execute(url);
                }else{
                    Toast.makeText(ControlActivity.this,"关闭了灯",Toast.LENGTH_SHORT).show();
                    String url = "http://10.130.158.224:8081/Keshe_war_exploded/sensor/send?request=08";
                    new MyAsyncTask().execute(url);
                }
            }
        });
    }

    public static class MyAsyncTask extends AsyncTask<String,Integer,String>{
        public MyAsyncTask(){

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.w("wang","task doInBackground()");
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try{
                URL url = new URL(strings[0]);    //声明一个URL，注意如果用百度首页实验，请使用https开头，否则获取不到返回的报文
                connection = (HttpURLConnection) url.openConnection();  //打开URL连接
                connection.setRequestMethod("GET");  //设置请求方法，POST或者GET，这里用GET
                connection.setConnectTimeout(80000);  //设置连接建立的超时时间
                connection.setReadTimeout(80000);  //设置网络报文收发超时的时间
                InputStream in = connection.getInputStream();  //通过连接的输入流获取下发报文，然后就是Java的流处理
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null){
                    response.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.w("Wang","task onPreExecute()");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String s) {
            Log.w("wang","task onPostExecute()");
            Log.d("Dang","onPostExecute()" + s);
        }
    }
}
