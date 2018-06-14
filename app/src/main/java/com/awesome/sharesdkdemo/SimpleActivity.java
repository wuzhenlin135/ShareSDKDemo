package com.awesome.sharesdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.awesome.sharesdk.OpenAccountManager;
import com.awesome.sharesdk.ResultCallback;
import com.awesome.sharesdk.core.Platform;
import com.awesome.sharesdk.core.PlatformEntity;

public class SimpleActivity extends Activity {

    TextView authInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        authInfoText = findViewById(R.id.auth_info_text);

        // 设置第三方平台appkey secret 等
        PlatformEntity.QQ.setAppKey("").setAppSecret("").setIndex(1);
        PlatformEntity.QZONE.setAppKey("").setAppSecret("").setIndex(2);
        PlatformEntity.WECHAT.setAppKey("").setAppSecret("").setIndex(3);
        PlatformEntity.WECHAT_TIMELINE.setAppKey("").setAppSecret("").setIndex(4);
        PlatformEntity.WEIBO.setAppKey("").setRedirectUrl("").setIndex(5);

        // QQ授权登陆。
        findViewById(R.id.auth_qq).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                OpenAccountManager.getInstance().authorize(SimpleActivity.this, PlatformEntity.QQ,
                        new ResultCallback(SimpleActivity.this) {

                            @Override
                            protected void onError(int type, PlatformEntity platform, String errorMsg) {
                                Log.e("wuzhenlin","onError");
                            }

                            @Override
                            protected void onCancel(int type, PlatformEntity platform) {
                                Log.e("wuzhenlin","onCancel");
                            }

                            @Override
                            protected void onComplete(int type, PlatformEntity platform, Bundle resultData) {
                                Log.e("wuzhenlin","onComplete");
                            }
                        });
            }
        });

        findViewById(R.id.auth_wechat).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                OpenAccountManager.getInstance().authorize(SimpleActivity.this, PlatformEntity.WECHAT,
                        new ResultCallback(SimpleActivity.this) {

                    @Override
                    protected void onError(int type, PlatformEntity platform, String errorMsg) {
                        Log.e("wuzhenlin","onError");
                    }

                    @Override
                    protected void onCancel(int type, PlatformEntity platform) {
                        Log.e("wuzhenlin","onCancel");
                    }

                    @Override
                    protected void onComplete(int type, PlatformEntity platform, Bundle resultData) {
                        Log.e("wuzhenlin","onComplete");
                    }
                });
            }
        });

        findViewById(R.id.auth_sina).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                OpenAccountManager.getInstance().authorize(SimpleActivity.this, PlatformEntity.WEIBO,
                        new ResultCallback(SimpleActivity.this) {

                            @Override
                            protected void onError(int type, PlatformEntity platform, String errorMsg) {
                                Log.e("wuzhenlin","onError");
                            }

                            @Override
                            protected void onCancel(int type, PlatformEntity platform) {
                                Log.e("wuzhenlin","onCancel");
                            }

                            @Override
                            protected void onComplete(int type, PlatformEntity platform, Bundle resultData) {
                                Log.e("wuzhenlin","onComplete");
                            }
                        });
            }
        });

        final Platform.ShareParams params = new Platform.ShareParams();
        params.setText("分享福利");
        params.setTitle("美女美女来啦！！啊哈哈哈哈哈哈,哦喝喝喝喝呵呵！");
        params.setUrl("http://www.baidu.com");
//        params.setImageUrl("http://image.tianjimedia.com/uploadImages/2014/113/24/2KD9S4P5P1HN_1000x500.jpg");

        final OpenAccountManager manager = OpenAccountManager.getInstance();

        final ResultCallback callback = new ResultCallback(this)
        {

            @Override
            public void onError(int type, PlatformEntity paltform, String errorMsg) {
//                showMsg(paltform.name + " 分享失败：" + errorMsg);
            }

            @Override
            public void onComplete(int type, PlatformEntity paltform, Bundle bundle) {
//                showMsg(paltform.name + " 分享成功");
            }

            @Override
            public void onCancel(int type, PlatformEntity paltform) {
//                showMsg("取消 " + paltform.name + " 分享");
            }
        };

        // 分享
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(SimpleActivity.this, null, params, callback);
            }
        });

        findViewById(R.id.share_to_QQ).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(SimpleActivity.this, PlatformEntity.QQ, params, callback);
            }
        });

        findViewById(R.id.share_to_QZone).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(SimpleActivity.this, PlatformEntity.QZONE, params, callback);
            }
        });

        findViewById(R.id.share_to_wechat).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(SimpleActivity.this, PlatformEntity.WECHAT, params, callback);
            }
        });

        findViewById(R.id.share_to_wechat_timeline).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(SimpleActivity.this, PlatformEntity.WECHAT_TIMELINE, params, callback);
            }
        });

        findViewById(R.id.share_to_sina).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(SimpleActivity.this, PlatformEntity.WEIBO, params, callback);
            }
        });
    }
}
