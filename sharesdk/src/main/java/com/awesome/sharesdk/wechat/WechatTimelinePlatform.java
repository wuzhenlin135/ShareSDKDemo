package com.awesome.sharesdk.wechat;


import com.awesome.sharesdk.core.HookActivity;
import com.awesome.sharesdk.core.PlatformEntity;

public class WechatTimelinePlatform extends WechatCorePlatform {

    public WechatTimelinePlatform(HookActivity context, String appkey)
    {
        super(context, appkey);
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.WECHAT_TIMELINE;
    }

}
