package com.awesome.sharesdkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.sharesdk.OpenAccountManager;
import com.awesome.sharesdk.ResultCallback;
import com.awesome.sharesdk.core.Config;
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
        PlatformEntity.QQ.setAppKey("222222").setIndex(1);
        PlatformEntity.QZONE.setAppKey("222222").setIndex(2);
        PlatformEntity.WECHAT.setAppKey("wxd930ea5d5a258f4f").setAppSecret("").setIndex(3);
        PlatformEntity.WECHAT_TIMELINE.setAppKey("wxd930ea5d5a258f4f").setAppSecret("").setIndex(4);
        PlatformEntity.WEIBO.setAppKey("2045436852").setRedirectUrl("http://www.sina.com").setIndex(5);


        final OpenAccountManager manager = OpenAccountManager.getInstance();

        // 授权回调
        final ResultCallback authCallback = new ResultCallback(SimpleActivity.this) {

            @Override
            protected void onError(int type, PlatformEntity platform, String errorMsg) {
                showMsg(platform.name + " 授权失败");
            }

            @Override
            protected void onCancel(int type, PlatformEntity platform) {
                showMsg(platform.name + " 取消");
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onComplete(int type, PlatformEntity platform, Bundle resultData) {
                String openid = resultData.getString(Config.PARAM_OPEN_ID);
                String accessToken = resultData.getString(Config.PARAM_ACCESS_TOKEN);
                authInfoText.setText("openId = " + openid + ", accessToken = " + accessToken);
                showMsg(platform.name + " 授权成功");
            }
        };
        // QQ授权登陆。
        findViewById(R.id.auth_qq).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.authorize(SimpleActivity.this,
                        PlatformEntity.QQ,
                        authCallback);
            }
        });

        findViewById(R.id.auth_wechat).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.authorize(SimpleActivity.this,
                        PlatformEntity.WECHAT,
                        authCallback);
            }
        });

        findViewById(R.id.auth_sina).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.authorize(SimpleActivity.this,
                        PlatformEntity.WEIBO,
                        authCallback);
            }
        });

        // 分享
        final Platform.ShareParams params = new Platform.ShareParams();
        params.setText("分享福利");
        params.setTitle("美女美女来啦！！啊哈哈哈哈哈哈,哦喝喝喝喝呵呵！");
        params.setUrl("http://www.baidu.com");
//        params.setImageUrl("http://image.tianjimedia.com/uploadImages/2014/113/24/2KD9S4P5P1HN_1000x500.jpg");


        final ResultCallback callback = new ResultCallback(this)
        {

            @Override
            public void onError(int type, PlatformEntity paltform, String errorMsg) {
                showMsg(paltform.name + " 分享失败：" + errorMsg);
            }

            @Override
            public void onComplete(int type, PlatformEntity paltform, Bundle bundle) {
                showMsg(paltform.name + " 分享成功");
            }

            @Override
            public void onCancel(int type, PlatformEntity paltform) {
                showMsg(paltform.name + " 分享取消");
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

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
