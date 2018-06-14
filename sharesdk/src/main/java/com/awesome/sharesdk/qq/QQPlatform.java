package com.awesome.sharesdk.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.awesome.sharesdk.core.PlatformEntity;
import com.awesome.sharesdk.utils.Utils;
import com.tencent.connect.share.QQShare;

public class QQPlatform extends QQCorePlatform {

    private Activity context;
    public QQPlatform(Activity context, String appKey)
    {
        super(context, appKey);
        this.context = context;
    }


    @Override
    protected Bundle getShareParams(ShareParams params) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(params.getUrl()) && !TextUtils.isEmpty(params.getImagePath())) {
            // 纯图片分享
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, params.getImagePath());
        } else {
            // 图文分享
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            if (!TextUtils.isEmpty(params.getTitle())) {
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, params.getTitle());
            }
            if (!TextUtils.isEmpty(params.getText())) {
                bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, params.getText());
            }
            if (!TextUtils.isEmpty(params.getUrl())) {
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, params.getUrl());
            }
            if (!TextUtils.isEmpty(params.getImagePath()) || !TextUtils.isEmpty(params.getImageUrl())) {
                String imageUrl = params.getImagePath();
                imageUrl = TextUtils.isEmpty(imageUrl) ? params.getImageUrl() : imageUrl;
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            }
        }
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, Utils.getApplicationName(context));
        // bundle.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");

        return bundle;
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.QQ;
    }

}
