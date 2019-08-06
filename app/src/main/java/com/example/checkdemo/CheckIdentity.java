package com.example.checkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CheckIdentity extends AsyncTask <Void,Void,Void> {

    private static final String TAG = "Task_CheckIdentity";

    String strRequestBody, strIsDr, strDptCode, strEMP_NAME;
    Boolean aBoolean = false;
    AlertDialog dialog;
    EditText editText;
    Button btn;
    Activity activity;

    public CheckIdentity(EditText editText, Activity activity) {
        this.editText = editText;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG,"==onPreExecute");
        aBoolean =false;
        strIsDr= null;
        strDptCode= null;
        strEMP_NAME = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.layout_progress);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        dialog= builder.create();
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d(TAG,"==onPostExecute");
        super.onPostExecute(aVoid);

        if(aBoolean == true){
            editText.setText(strEMP_NAME );
        }else{
            editText.setText("請輸入正確的麻醉醫師編號");
        }

        dialog.dismiss();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG,"==doInBackGround");
        try {
        Log.d(TAG,"==start Connection");

            JSONObject ob = new JSONObject();
            ob.put("EMP_NO", editText.getText().toString());
            strRequestBody = ob.toString();

            Log.d(TAG,"==REQUEST BODY:"+strRequestBody);

            //網址轉碼
            URL url = new URL("http://210.17.120.51/IMOR/json/APP01-queryEMPDATA.ashx");
            //取得連線
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type","application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true); //允許輸入流，即允許下載
            conn.setDoOutput(true); //允許輸出流，即允許上傳
            conn.setUseCaches(false); //設置是否使用緩存
            OutputStream os = conn.getOutputStream();
            DataOutputStream writer = new DataOutputStream(os);

            writer.writeBytes(strRequestBody);
            writer.flush();
            writer.close();
            os.close();
            //取得串流
            InputStream streamIn = conn.getInputStream();
            //準備開始解碼，首先，把剛剛的串流讀進來，製作一個串流讀取器(BufferReader)
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamIn));
            //做一個StringBuilder,接著不斷地去讀取串流，讀到他是NULL為止，在這之前則把每一行 append  到 StringBuilder 裡面
            StringBuilder html  = new StringBuilder();
            String line ;
            while ( (line = bufferedReader.readLine()) != null){
                html.append(line);
            }
            String strJson = html.toString();
            Log.d(TAG,"==RESPONSE BODY:"+strJson);
            JSONObject EMPResponse = new JSONObject(strJson);
            JSONArray EMPDataArray = EMPResponse.getJSONArray("data");
            final JSONObject empObject =  EMPDataArray.getJSONObject(0);
            strIsDr =  empObject.getString("IS_DR");
            strDptCode = empObject.getString("DPT_COD");
            strEMP_NAME = empObject.getString("EMP_NAME");


            if( "Y".equals(strIsDr) && "3300.0".equals(strDptCode)){
                aBoolean = true;
            }else{
                strIsDr= null;
                strDptCode= null;
                strEMP_NAME = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (ProtocolException e) {
            e.printStackTrace();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

}
