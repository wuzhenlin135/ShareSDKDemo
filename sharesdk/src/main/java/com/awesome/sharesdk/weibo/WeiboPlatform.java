package com.awesome.sharesdk.weibo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;

import com.awesome.sharesdk.core.Config;
import com.awesome.sharesdk.core.Platform;
import com.awesome.sharesdk.core.PlatformDB;
import com.awesome.sharesdk.core.PlatformEntity;
import com.awesome.sharesdk.utils.Utils;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import static com.awesome.sharesdk.weibo.WeiboConfig.*;
/**
 * Created by wuzhenlin on 2018/6/13.
 */

public class WeiboPlatform extends Platform {

    private SsoHandler mSsoHandler;
    private WbShareHandler shareHandler;
    private Activity context;

    public WeiboPlatform(Activity context, String appkey) {
        super(context);
        this.context = context;
        WbSdk.install(context, new AuthInfo(context, appkey, PlatformEntity.WEIBO.redirectUrl, SCOPE));
        mSsoHandler = new SsoHandler(context);

        shareHandler = new WbShareHandler(context);
        shareHandler.registerApp();
        shareHandler.setProgressColor(0xff33b5e5);

    }

    @Override
    public boolean isClientValid() {
        return WbSdk.isWbInstall(this.context);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.WEIBO;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareHandler.doResultIntent(new Intent(), new InnerWbShareCallback());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void release() {

    }

    @Override
    protected void doAuthorize() {
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    @Override
    protected void doShare(ShareParams params) {
        TextObject textObject = new TextObject();
        textObject.title = params.getTitle();
        textObject.text = params.getText();
        textObject.text = params.getUrl();
        ImageObject imageObject = null;
        if (!TextUtils.isEmpty(params.getImagePath())) {
            imageObject = new ImageObject();
            Bitmap bitmap = Utils.scaleAndcompressBitmap(params.getImagePath());
            imageObject.setImageObject(bitmap);
        } else if (params.getBitmapData() != null) {
            imageObject = new ImageObject();
            Bitmap bitmap = params.getBitmapData();
            imageObject.setImageObject(bitmap);
        } else if (!TextUtils.isEmpty(params.getImageUrl())) {
            // 不支持网络图片分享
        }
        // 多图
//        MultiImageObject multiImageObject = new MultiImageObject();
//        ArrayList<Uri> pathList = new ArrayList<Uri>();
//        pathList.add(Uri.fromFile(new File(getExternalFilesDir(null)+"/aaa.png")));
//        multiImageObject.setImageList(pathList);
        //视频
//        VideoSourceObject videoSourceObject = new VideoSourceObject();
//        videoSourceObject.videoPath = Uri.fromFile(new File(getExternalFilesDir(null)+"/eeee.mp4"));
        sendMultiMessage(textObject,imageObject,null,null);
    }
    private WebpageObject getWebpageObj(ShareParams params) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = params.getTitle();
        mediaObject.description = params.getText();
        if (!TextUtils.isEmpty(params.getImagePath())) {
            Bitmap bitmap = Utils.scaleAndcompressBitmap(params.getImagePath());
            mediaObject.setThumbImage(bitmap);
        } else if (params.getBitmapData() != null) {
            Bitmap bitmap = params.getBitmapData();
            mediaObject.setThumbImage(bitmap);
        } else if (!TextUtils.isEmpty(params.getImageUrl())) {
            // 不支持网络图片分享
        }

        mediaObject.actionUrl = params.getUrl();
        mediaObject.defaultText = Utils.getApplicationName(context);
        return mediaObject;
    }

    private void sendMultiMessage(TextObject textObject, ImageObject imageObject, MultiImageObject multiImageObject, VideoSourceObject videoSourceObject) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (textObject != null) {
            weiboMessage.textObject = textObject;
        }
        if (imageObject != null) {
            weiboMessage.imageObject = imageObject;
        }
        if(multiImageObject != null){
            weiboMessage.multiImageObject = multiImageObject;
        }
        if(videoSourceObject != null){
            weiboMessage.videoSourceObject = videoSourceObject;
        }
        shareHandler.shareMessage(weiboMessage, false);

    }

    private Oauth2AccessToken readAccessToken(PlatformDB db) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        token.setUid(db.getUserId());
        token.setToken(db.getToken());
        token.setExpiresTime(db.getExpiresIn());
        return token;
    }

    private void writeAccessToken(PlatformDB db, Oauth2AccessToken token) {
        db.putUserId(token.getUid());
        db.putToken(token.getToken());
        db.putExpiresIn(token.getExpiresTime());
    }

    private class InnerWbShareCallback implements WbShareCallback{

        @Override
        public void onWbShareSuccess() {
            notifyComplete(ACTION_SHARE, new Bundle());
        }

        @Override
        public void onWbShareCancel() {
            notifyCancel(ACTION_SHARE);
        }

        @Override
        public void onWbShareFail() {
            notifyError(ACTION_SHARE, new RuntimeException("error"));
        }
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener{
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (token.isSessionValid()) {
                        PlatformDB db = getPlatformDB();
                        writeAccessToken(db, token);
                        Bundle newBundle = new Bundle();
                        newBundle.putString(PARAM_ACCESS_TOKEN, db.getToken());
                        newBundle.putString(PARAM_OPEN_ID, db.getUserId());
                        newBundle.putString(PARAM_EXPIRES_IN, String.valueOf(db.getExpiresIn()));
                        // 授权成功
                        notifyComplete(ACTION_AUTH, newBundle);
                    }
                }
            });
        }

        @Override
        public void cancel() {
            notifyCancel(ACTION_AUTH);
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            notifyError(ACTION_AUTH, new RuntimeException(errorMessage.getErrorMessage()));
        }
    }
}
