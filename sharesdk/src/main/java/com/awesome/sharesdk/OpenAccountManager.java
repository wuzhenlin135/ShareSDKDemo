package com.awesome.sharesdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.awesome.sharesdk.core.Config;
import com.awesome.sharesdk.core.Platform;
import com.awesome.sharesdk.core.PlatformEntity;
import com.awesome.sharesdk.core.ShareHookActivity;


public class OpenAccountManager {
    //
    @SuppressLint("StaticFieldLeak")
    private static OpenAccountManager _instance;

    //
    public static synchronized OpenAccountManager getInstance() {
        if (_instance == null) {
            _instance = new OpenAccountManager();
        }
        return _instance;
    }

    //
    private OpenAccountManager() {

    }

    /**
     * 分享到平台
     * @param context
     * @param platform 平台
     * @param params
     * @param callback
     */
    public void shareToPlatform(Context context, PlatformEntity platform, Platform.ShareParams params, ResultCallback callback) {

        Intent intent = new Intent(context, ShareHookActivity.class);
        //
        if (platform != null) {
            intent.putExtra(Config.KEY_PLATFORM, platform.id);
        }
        //
        intent.putExtra(Config.KEY_CALLBACK, callback);
        intent.putExtra(Config.KEY_TYPE, Config.ACTION_SHARE);
        intent.putExtras(params.toBundle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //
        context.startActivity(intent);
    }

    /**
     * 第三方登录
     * @param context
     * @param platform
     * @param callback
     */
    public void authorize(Context context, PlatformEntity platform, ResultCallback callback) {

        Intent intent = new Intent(context, ShareHookActivity.class);
        //
        if (platform != null) {
            intent.putExtra(Config.KEY_PLATFORM, platform.id);
        }
        //
        intent.putExtra(Config.KEY_CALLBACK, callback);
        intent.putExtra(Config.KEY_TYPE, Config.ACTION_AUTH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //
        context.startActivity(intent);
    }


}
