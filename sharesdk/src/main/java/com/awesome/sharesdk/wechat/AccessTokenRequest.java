package com.awesome.sharesdk.wechat;

import android.os.Bundle;
import android.text.TextUtils;

import com.awesome.sharesdk.core.PlatformEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AccessTokenRequest {
    private static final String BASE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    public static Bundle request(String code) throws IOException, JSONException {
        String urlStr = String.format(BASE_URL, PlatformEntity.WECHAT.appKey, PlatformEntity.WECHAT.appSecret, code);
        String result = "";
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(6 * 1000);
        connection.setUseCaches(false);
        connection.connect();
        if (connection.getResponseCode() == 200) {
            InputStream is = connection.getInputStream();
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            result = sb.toString();
        }
        connection.disconnect();
        return passerResult(result);
    }

    public static Bundle passerResult(String result) throws JSONException {
        JSONObject resObj = new JSONObject(result);
        String accessToken = resObj.optString("access_token");
        String openid = resObj.optString("openid");
        String expiresIn = resObj.optString("expires_in");
//        String refreshToken = resObj.optString("refresh_token");
//        String scope = resObj.optString("scope");
//        String unionid = resObj.optString("unionid");
        if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(openid) && !TextUtils.isEmpty(expiresIn)) {
            Bundle bundle = new Bundle();
            bundle.putString(WechatConfig.PARAM_ACCESS_TOKEN, accessToken);
            bundle.putString(WechatConfig.PARAM_OPEN_ID, openid);
            bundle.putString(WechatConfig.PARAM_EXPIRES_IN, expiresIn);
            return bundle;
        }
        return null;
    }
}
