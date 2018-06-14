package com.awesome.sharesdk.qq;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.awesome.sharesdk.core.PlatformEntity;
import com.awesome.sharesdk.utils.Utils;
import com.tencent.connect.share.QzoneShare;

import java.util.ArrayList;


public class QZonePlatform extends QQCorePlatform {

    private Activity context;

    public QZonePlatform(Activity context, String appKey)
    {
        super(context, appKey);
        this.context = context;
    }

    protected Bundle getShareParams(ShareParams params) {
        final Bundle bundle = new Bundle();
//        bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_APP);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, params.getTitle());
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, params.getText());
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, params.getUrl());
        bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, Utils.getApplicationName(context));
        //
        if (!TextUtils.isEmpty(params.getImagePath()) || !TextUtils.isEmpty(params.getImageUrl())) {
            String imageUrl = params.getImagePath();
            imageUrl = TextUtils.isEmpty(imageUrl) ? params.getImageUrl() : imageUrl;
            bundle.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        } else {
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>());
        }
        return bundle;
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.QZONE;
    }

}
