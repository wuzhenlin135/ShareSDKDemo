package com.awesome.sharesdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.awesome.sharesdk.core.Config;
import com.awesome.sharesdk.core.PlatformEntity;

/**
 * Created by wuzhenlin on 2018/6/12.
 */

public abstract class ResultCallback extends ResultReceiver {

    
    public ResultCallback(Context context) {
        super(new Handler(context.getMainLooper()));
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        int type = resultData.getInt(Config.KEY_TYPE);
        int platform = resultData.getInt(Config.KEY_PLATFORM);
        switch (resultCode) {
            case Config.RESLUT_CODE_COMPLETE:
                onComplete(type, PlatformEntity.findPlatformById(platform), resultData);
                break;
            case Config.RESLUT_CODE_CANCEL:
                onCancel(type, PlatformEntity.findPlatformById(platform));
                break;
            case Config.RESLUT_CODE_ERROR:
                String errorMsg = resultData.getString(Config.PARAM_MSG);
                onError(type, PlatformEntity.findPlatformById(platform), errorMsg);
                break;
            default:
                break;
        }
    }

    protected abstract void onError(int type, PlatformEntity platform, String errorMsg);

    protected abstract void onCancel(int type, PlatformEntity platform);

    protected abstract void onComplete(int type, PlatformEntity platform, Bundle resultData);
}
