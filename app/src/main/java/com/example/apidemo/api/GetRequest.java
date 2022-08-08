package com.example.apidemo.api;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.example.apidemo.MainActivity;
import com.example.apidemo.Model.Item;
import com.google.gson.Gson;
import com.nifcloud.mbaas.core.NCMB;
import com.nifcloud.mbaas.core.NCMBException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;

import javax.net.ssl.HttpsURLConnection;

public class GetRequest extends AsyncTask<String,Void,String>{
    static final String HEADER_APPLICATION_KEY = "X-NCMB-Application-Key";
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    static final String HEADER_CONTENT_TYPE_JSON = "application/json";
    static final String HEADER_TIMESTAMP = "X-NCMB-Timestamp";
    static final String HEADER_SIGNATURE = "X-NCMB-Signature";
    static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    static final String HEADER_SDK_VERSION = "X-NCMB-SDK-Version";
    static final String HEADER_OS_VERSION = "X-NCMB-OS-Version";


    TextView view;
    String urlString = "https://mbaas.api.nifcloud.com/2013-09-01/classes/Item/";
    URL url = null;
    String timestamp = null;
    String hashData="";
    String signature="";
    String queryParamString ="";


    public GetRequest(String urlString, String queryParamString,TextView view) throws UnsupportedEncodingException {
        this.view = view;
        this.urlString = urlString;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            try {
                throw new NCMBException(NCMBException.INVALID_FORMAT, e.getMessage());
            } catch (NCMBException ex) {
                ex.printStackTrace();
            }
        }
        String query = "";
        this.queryParamString=queryParamString;
        List<String> parameterList = new ArrayList<String>();
        JSONObject queryParam = null;
        try {
            queryParam = new JSONObject(this.queryParamString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (queryParam != null && queryParam.length() > 0) {
            try {
                query = query + "?";//検索条件 連結
                Iterator<?> keys = queryParam.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    //String value = queryParam.get(key).toString();
                    //Log.v("tag", "KEY:" + key + " VALUE:" + value);
                    String param = key + "=" + URLEncoder.encode(queryParam.get(key).toString(), "UTF-8");

                    parameterList.add(param);//シグネチャ生成で使用

                    query = query + param;
                    if (keys.hasNext()) {
                        query = query + "&";//検索条件 区切り
                    }
                }
                this.url = new URL(this.url.toString() + query);
                Log.d("url", this.url.toString());
            } catch (UnsupportedEncodingException | JSONException | MalformedURLException e) {
                try {
                    throw new NCMBException(e);
                } catch (NCMBException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (this.timestamp == null) {
            //timestamp引数なしコンストラクタの場合は現在時刻で生成する
            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
            df.setTimeZone(new SimpleTimeZone(0, "GMT"));
            this.timestamp =df.format(new Date());
        }
        Signature signatureGenerator = new Signature();
        signatureGenerator.setUrl(url);
        signatureGenerator.setTimestamp(this.timestamp);
        this.hashData = signatureGenerator.createSignatureHashData(this.url.getPath(), parameterList);
        System.out.println(hashData);
        this.signature = signatureGenerator.createSignature(hashData, "2e0167555ae06b73a73a8b2ef1ea9614d566b17cb7c0d191da80797221088bf2");
        Log.d("signature", signature);

    }

    public void getInBackground(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


            }
        });
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpsURLConnection myConnection =
                    (HttpsURLConnection) url.openConnection();
            Log.d("conURL",myConnection.getURL().toString());
            myConnection.setRequestProperty(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_JSON);
            myConnection.setRequestProperty(HEADER_APPLICATION_KEY, "2bfb444423219ff54256bbe41ff270c5d8c3e81eaa3121c18603363e99b0b673");
            myConnection.setRequestProperty(HEADER_TIMESTAMP,timestamp );
            myConnection.setRequestProperty(HEADER_SIGNATURE, signature);
            myConnection.setRequestProperty(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            myConnection.setRequestProperty(HEADER_SDK_VERSION, "android-" + NCMB.SDK_VERSION);
            myConnection.setRequestProperty(HEADER_OS_VERSION, "android-" + Build.VERSION.RELEASE);

            if (myConnection.getResponseCode() == 200) {
                Log.d("response","success");
                InputStream in = new BufferedInputStream(myConnection.getInputStream());
                String response = convertStreamToString(in);
                myConnection.disconnect();
                Log.d("result",response);

               return response;


            } else {
                Log.d("response","failed"+ myConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Gson gson = new Gson();
        Item item = gson.fromJson(s.substring(12,s.length()-3),Item.class);
        Log.d("item",item.getName());
        view.setText(item.toString());
    }
}
