package com.awesome.sharesdk.wechat;


import com.awesome.sharesdk.core.HookActivity;
import com.awesome.sharesdk.core.PlatformEntity;

public class WechatFriendPlatform extends WechatCorePlatform {

    public WechatFriendPlatform(HookActivity context, String appkey)
    {
        super(context, appkey);
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.WECHAT;
    }

}
