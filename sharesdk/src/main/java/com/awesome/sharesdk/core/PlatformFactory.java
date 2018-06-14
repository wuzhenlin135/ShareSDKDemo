package com.awesome.sharesdk.core;


import com.awesome.sharesdk.qq.QQPlatform;
import com.awesome.sharesdk.qq.QZonePlatform;
import com.awesome.sharesdk.wechat.WechatFriendPlatform;
import com.awesome.sharesdk.wechat.WechatTimelinePlatform;

class PlatformFactory {

    static Platform createPlatform(ShareHookActivity context, PlatformEntity entity) {
        if (!entity.isInited()) {
            throw new RuntimeException(entity.name + " ：：未设置app key");
        }
        switch (entity) {
            case QQ:
                return new QQPlatform(context, entity.appKey);
            case QZONE:
                return new QZonePlatform(context, entity.appKey);
            case WECHAT:
                return new WechatFriendPlatform(context, entity.appKey);
            case WECHAT_TIMELINE:
                return new WechatTimelinePlatform(context, entity.appKey);
            case WEIBO:
                return new WechatTimelinePlatform(context, entity.appKey);
            default:
                throw new UnsupportedOperationException(entity.name + " ：：暂不支持该平台");
        }
    }
}
