package com.awesome.sharesdk.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.awesome.sharesdk.ClientNotInstalledException;
import com.awesome.sharesdk.core.HookActivity;
import com.awesome.sharesdk.core.Platform;
import com.awesome.sharesdk.core.PlatformEntity;
import com.awesome.sharesdk.utils.HookUtil;
import com.awesome.sharesdk.utils.Utils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import  static  com.awesome.sharesdk.core.Config.*;

public abstract class WechatCorePlatform extends Platform {
    private static final int AUTH_COMPLETE = 0x11;
    private static final int AUTH_ERROR = 0x12;

    private IWXAPI mWxApi;
    private HookActivity activity;


    WechatCorePlatform(HookActivity context, String appkey) {
        super(context);
        activity = context;
        mWxApi = WXAPIFactory.createWXAPI(context, appkey, false);

        //
        IntentFilter filter = new IntentFilter();
        filter.addAction(WechatConfig.ACTION_WECHAT_RESP);
        context.registerReceiver(receiver, filter);
    }

    @Override
    public boolean isClientValid() {
        return mWxApi.isWXAppInstalled();
    }

    @Override
    public boolean isValid() {
        return mWxApi.isWXAppSupportAPI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // nothing
    }

    @Override
    protected void doAuthorize() {
        if (!isClientValid()) {
            notifyError(ACTION_AUTH, new ClientNotInstalledException("微信客户端没有安装"));
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = Utils.getApplicationName(context);
        mWxApi.sendReq(req);
    }

    @Override
    protected void doShare(ShareParams params) {
        int scene = SendMessageToWX.Req.WXSceneSession;
        if (getPlatformEntity() == PlatformEntity.WECHAT) {
            scene = SendMessageToWX.Req.WXSceneSession;
        } else if (getPlatformEntity() == PlatformEntity.WECHAT_TIMELINE) {
            scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        boolean result = false;
        if (!TextUtils.isEmpty(params.getUrl())) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = params.getUrl();
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(params.getImagePath())) {
                bitmap = Utils.scaleAndcompressBitmap(params.getImagePath());
            } else if (params.getBitmapData() != null) {
                bitmap = params.getBitmapData();
            }
            result = invokingWinxin(scene, webpage, params.getTitle(), params.getText(), bitmap, "webpage");
        } else if (!TextUtils.isEmpty(params.getImagePath()) || params.getBitmapData() != null
                || !TextUtils.isEmpty(params.getImageUrl())) {
            WXImageObject imageObj = new WXImageObject();
            if (!TextUtils.isEmpty(params.getImagePath())) {
                imageObj.imagePath = params.getImagePath();
            } else if (params.getBitmapData() != null) {
                imageObj.imageData = Utils.bmpToByteArray(params.getBitmapData(), true);
            } else if (!TextUtils.isEmpty(params.getImageUrl())) {
//                imageObj.imageUrl = params.getImageUrl();
            }
            result = invokingWinxin(scene, imageObj, params.getTitle(), params.getText(), null, "imagepage");
        } else if (!TextUtils.isEmpty(params.getText())) {
            WXTextObject textObj = new WXTextObject();
            textObj.text = params.getText();
            result = invokingWinxin(scene, textObj, params.getTitle(), params.getText(), null, "textpage");
        }
        //
        if (!result) {
            notifyError(ACTION_SHARE, new RuntimeException("未执行"));
        }
    }

    private boolean invokingWinxin(int scene, WXMediaMessage.IMediaObject obj, String title, String description, Bitmap thumb,
                                   String type) {
        WXMediaMessage msg = new WXMediaMessage(obj);
        msg.title = title;
        msg.description = description;
        if (thumb != null) {
            msg.thumbData = Utils.bmpToByteArray(thumb, false);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(type);
        req.message = msg;
        req.scene = scene;
        return mWxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle result = intent.getExtras();
            if(result == null) {
                notifyError(WechatConfig.ACTION_SHARE, new RuntimeException("result is null"));
                return;
            }

            int type = result.getInt(WechatConfig.RESP_WXAPI_BASERESP_TYPE);
            int errCode = result.getInt(WechatConfig.RESP_WXAPI_BASERESP_ERRCODE);
            String errStr = result.getString(WechatConfig.RESP_WXAPI_BASERESP_ERRSTR);
            String transaction = result.getString(WechatConfig.RESP_WXAPI_BASERESP_TRANSACTION);
            String openId = result.getString(WechatConfig.RESP_WXAPI_BASERESP_OPENID);
            Log.e("wuzhenlin","type = "+type);
            Log.e("wuzhenlin","errCode = "+errCode);
            Log.e("wuzhenlin","errStr = "+errStr);
            Log.e("wuzhenlin","transaction = "+transaction);
            Log.e("wuzhenlin","openId = "+openId);

            switch (errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if (WechatConfig.ACTION_SHARE == type) {
                        notifyComplete(type, null);
                    } else {
                        String code = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_TOKEN);
                        String state = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_STATE);
                        String url = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_URL);
                        String lang = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_LANG);
                        String country = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_COUNTRY);

                        if (!TextUtils.isEmpty(code)) {
                            requestAccessToken(code,new Handler(context.getMainLooper()){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    switch (msg.what) {
                                        case AUTH_COMPLETE:
                                            Bundle reslut = (Bundle) msg.obj;
                                            getPlatformDB().putUserId(reslut.getString(WechatConfig.PARAM_OPEN_ID));
                                            getPlatformDB().putToken(reslut.getString(WechatConfig.PARAM_ACCESS_TOKEN));
                                            getPlatformDB().putExpiresIn(Long.parseLong(reslut.getString(WechatConfig.PARAM_EXPIRES_IN)));
                                            notifyComplete(ACTION_AUTH, reslut);
                                            break;
                                        case AUTH_ERROR:
                                            Exception error = (Exception) msg.obj;
                                            notifyError(ACTION_AUTH, error);
                                            break;

                                        default:
                                            break;
                                    }
                                }
                            });
                        } else {
                            notifyError(ACTION_AUTH, new RuntimeException("授权失败,授权码为空。"));
                        }
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    notifyCancel(type);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                case BaseResp.ErrCode.ERR_COMM:
                case BaseResp.ErrCode.ERR_BAN:
                    notifyError(type, new RuntimeException("errorcode = " + errCode));
                    break;
                default:
                    notifyError(type, new RuntimeException("errorcode = " + errCode));
                    break;
            }

        }

    };

    private void requestAccessToken(final String code, final Handler handler) {
        HookUtil.startBackgroundJob(activity, "正在请求微信授权...", false, new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle reslut = AccessTokenRequest.request(code);
                    if (reslut != null) {
                        Message msg = handler.obtainMessage(AUTH_COMPLETE, reslut);
                        msg.sendToTarget();
                    } else {
                        Message msg = handler.obtainMessage(AUTH_ERROR, new RuntimeException("on reslut"));
                        msg.sendToTarget();
                    }
                } catch (Exception ex) {
                    Message msg = handler.obtainMessage(AUTH_ERROR, ex);
                    msg.sendToTarget();
                }
            }
        }, handler);
    }

    @Override
    public void release() {
        context.unregisterReceiver(receiver);
//        mWxApi.detach();
        mWxApi = null;
    }

}
