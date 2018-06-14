package com.awesome.sharesdk.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.awesome.sharesdk.core.Platform;
import com.awesome.sharesdk.core.PlatformEntity;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import static com.awesome.sharesdk.qq.QQConfig.*;

public abstract class QQCorePlatform extends Platform {
    private Tencent mTencent;
    private Activity mActivity;

    public QQCorePlatform(Activity context, String appkey)
    {
        super(context);
        this.mActivity = context;
        this.mTencent = Tencent.createInstance(appkey, context);
    }

    @Override
    protected void doAuthorize() {
        mTencent.login(mActivity, QQConfig.SCOPE, authListener);
    }

    @Override
    protected void doShare(ShareParams params) {
        Bundle shareParams = getShareParams(params);
        if (getPlatformEntity() == PlatformEntity.QQ) {
            mTencent.shareToQQ(mActivity, shareParams, shareListener);
        } else if (getPlatformEntity() == PlatformEntity.QZONE) {
            mTencent.shareToQzone(mActivity, shareParams, shareListener);
        }
    }

    @Override
    public boolean isClientValid() {
        return mTencent.isSupportSSOLogin(mActivity);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    protected abstract Bundle getShareParams(ShareParams params);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("QQCorePlatform", "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, authListener);
        }

        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        }

        if (requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    private IUiListener authListener = new IUiListener()
    {

        @Override
        public void onError(UiError error) {
            notifyError(ACTION_AUTH, new RuntimeException(error.errorMessage));
        }

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                notifyError(ACTION_AUTH, new RuntimeException("返回为空"));
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;

            if (jsonResponse.length() == 0) {
                notifyError(ACTION_AUTH, new RuntimeException("返回为空"));
                return;
            }
            Log.e("wuzhenlin",jsonResponse.toString());
            try {
                String token = jsonResponse.getString(QQConfig.PARAM_ACCESS_TOKEN);
                String openId = jsonResponse.getString(QQConfig.PARAM_OPEN_ID);
                String expires = jsonResponse.getString(QQConfig.PARAM_EXPIRES_IN);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
                }
                getPlatformDB().putUserId(openId);
                getPlatformDB().putToken(token);
                getPlatformDB().putExpiresIn(Long.parseLong(expires));
                Bundle bundle = new Bundle();
                bundle.putString(QQConfig.PARAM_ACCESS_TOKEN, token);
                bundle.putString(QQConfig.PARAM_OPEN_ID, openId);
                bundle.putString(QQConfig.PARAM_EXPIRES_IN, expires);

                notifyComplete(ACTION_AUTH, bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancel() {
            notifyCancel(ACTION_AUTH);
        }
    };

    private IUiListener shareListener = new IUiListener()
    {

        @Override
        public void onError(UiError error) {
            notifyError(ACTION_SHARE, new RuntimeException(error.errorMessage));
        }

        @Override
        public void onComplete(Object obj) {
            Bundle bundle = new Bundle();
            if (obj != null) {
                bundle.putString(QQConfig.PARAM_MSG, obj.toString());
            }
            notifyComplete(ACTION_SHARE, bundle);
        }

        @Override
        public void onCancel() {
            notifyCancel(ACTION_SHARE);
        }
    };

    @Override
    public void release() {
        mTencent.releaseResource();
        mTencent.logout(mActivity);
        mTencent = null;
    }
}
