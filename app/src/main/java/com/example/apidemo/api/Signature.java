package com.example.apidemo.api;

import android.util.Base64;

import org.json.JSONObject;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Signature {

    //App_key_header
    static final String HEADER_APPLICATION_KEY = "X-NCMB-Application-Key";
    //timestamp header
    static final String HEADER_TIMESTAMP = "X-NCMB-Timestamp";
    //signature method
    private static final String SIGNATURE_METHOD_KEY = "SignatureMethod";
    //method value
    private static final String SIGNATURE_METHOD_VALUE = "HmacSHA256";
    //signature version
    private static final String SIGNATURE_VERSION_KEY = "SignatureVersion";
    // version value
    private static final String SIGNATURE_VERSION_VALUE = "2";

    // region property
    /** APIリクエストを行うURL */
    private URL url = null;
    /** APIリクエストのHTTPメソッド */
    private String method = "GET";
    private String applicationKey = "2bfb444423219ff54256bbe41ff270c5d8c3e81eaa3121c18603363e99b0b673";
    private String timestamp = "";

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String createSignature(String data, String key) {
        String result = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), SIGNATURE_METHOD_VALUE);

            Mac mac = Mac.getInstance(SIGNATURE_METHOD_VALUE);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));

            result = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalArgumentException("signature");
        }
        return result;
    }

    //シグネチャのためハッシュ化するデータの生成
   public String createSignatureHashData(String path, List<String> parameterList) {

        // シグネチャメソッド
        parameterList.add(SIGNATURE_METHOD_KEY + "=" + SIGNATURE_METHOD_VALUE);
        // シグネチャバージョン
        parameterList.add(SIGNATURE_VERSION_KEY + "=" + SIGNATURE_VERSION_VALUE);
        // アプリケーションキー
        parameterList.add(HEADER_APPLICATION_KEY + "=" + this.applicationKey);
        // タイムスタンプ
        parameterList.add(HEADER_TIMESTAMP + "=" + this.timestamp);
        // 自然昇順でソート
        Collections.sort(parameterList);

        // ハッシュかするデータの生成
        StringBuilder data = new StringBuilder();
        // リクエストメソッド
        data.append(this.method).append("\n");
        // FQDN
        data.append(this.url.getHost()).append("\n");
        // APIパス
        data.append(path).append("\n");

        // パラメーター
        Iterator<?> it = parameterList.iterator();
        while (it.hasNext()) {
            data.append(it.next());
            if (it.hasNext()) {
                data.append("&");// 最後以外を「&」で区切る
            }
        }
        return data.toString();
    }
}
