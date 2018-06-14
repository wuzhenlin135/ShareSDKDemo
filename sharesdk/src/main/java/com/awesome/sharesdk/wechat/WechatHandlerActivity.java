package com.awesome.sharesdk.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.awesome.sharesdk.core.PlatformEntity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, PlatformEntity.WECHAT.appKey, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e("wuzhenlin", "resp  = " + req.toString());
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("wuzhenlin", "resp  = " + resp.toString());
        Bundle respBundle = new Bundle();
        resp.toBundle(respBundle);
        int type = resp.getType();
        Intent intent = new Intent(WechatConfig.ACTION_WECHAT_RESP);
        intent.putExtras(respBundle);
        intent.putExtra(WechatConfig.RESP_WXAPI_BASERESP_TYPE, type);
        sendBroadcast(intent);
        finish();
    }

}
