package com.awesome.sharesdk.core;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


public class ShareHookActivity extends HookActivity implements PlatformActionListener, ShareActionView.PlatformSelectListener {
    String TAG = getClass().getSimpleName();
    private Platform platform;
    private Platform.ShareParams shareParams;
    private int type;
    private ResultReceiver resultCallback;
    private boolean isCallback = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        if (params == null) {
            finish();
            return;
        }
        isCallback = false;
        int platformId = params.getInt(Config.KEY_PLATFORM, -1);
        resultCallback = params.getParcelable(Config.KEY_CALLBACK);

        type = params.getInt(Config.KEY_TYPE);
        PlatformEntity entity = PlatformEntity.findPlatformById(platformId);
        if (entity != null) {
            platform = PlatformFactory.createPlatform(this, entity);
        }
        shareParams = Platform.ShareParams.formBundle(params);
        if (platform == null) {
            ShareActionView views = new ShareActionView(this);
            views.setListener(this);
        } else {
            doAction();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        platform.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 解决，留在微信当前透明Activity未关闭的问题。
        Log.e(TAG,"onRestart .....");
        mainHandler.sendEmptyMessageDelayed(WHAT_FINISH_ACTIVITY, 200);
    }

    private void doAction() {
        if (platform == null) {
            finish();
            return;
        }
        if (Config.ACTION_SHARE == type) {
            platform.setPlatformActionListener(this);
            platform.share(shareParams);
        } else if (Config.ACTION_AUTH == type) {
            platform.setPlatformActionListener(this);
            platform.authorize();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (platform != null) {
            platform.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeMessages(WHAT_FINISH_ACTIVITY);
        if (platform != null) {
            platform.release();
        }
    }

    public void onSelectPlatform(PlatformEntity platformEntity) {
        platform = PlatformFactory.createPlatform(this, platformEntity);
        doAction();
    }

    @Override
    public void onComplete(Platform platform, int type, Bundle bundle) {
        if (resultCallback != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt(Config.KEY_TYPE, type);
            bundle.putInt(Config.KEY_PLATFORM, platform.getPlatformEntity().id);
            resultCallback.send(Config.RESLUT_CODE_COMPLETE, bundle);
        }
        isCallback = true;
        finish();
    }

    @Override
    public void onError(Platform platform, int type, Throwable error) {
        if (resultCallback != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(Config.KEY_TYPE, type);
            bundle.putInt(Config.KEY_PLATFORM, platform.getPlatformEntity().id);
            bundle.putString(Config.PARAM_MSG, error.getMessage());
            resultCallback.send(Config.RESLUT_CODE_ERROR, bundle);
        }
        isCallback = true;
        finish();
    }

    @Override
    public void onCancel(Platform platform, int type) {
        if (resultCallback != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(Config.KEY_TYPE, type);
            bundle.putInt(Config.KEY_PLATFORM, platform.getPlatformEntity().id);
            resultCallback.send(Config.RESLUT_CODE_CANCEL, bundle);
        }
        isCallback = true;
        finish();
    }

    public static final int WHAT_FINISH_ACTIVITY = 0x11;

    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == WHAT_FINISH_ACTIVITY) {
                if (!isCallback) {
                    onCancel(platform, type);
                }
                isCallback = true;
                finish();
            }
        }
    };
}
