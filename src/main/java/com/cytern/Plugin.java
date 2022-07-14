package com.cytern;

import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.load.base.RobotCommandLoadService;
import com.cytern.service.impl.load.base.ModLoadService;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("com.cytern.plugin", "1.0-demo")
                .name("robot")
                .author("cytern&miaomiao")
                .build());
    }

    private static void accept(MessageEvent event) {
        CommonMessageProcessor commonMessageProcessor = new CommonMessageProcessor(event);
        commonMessageProcessor.handlerMessageEvent();
    }

    @Override
    public void onEnable() {
//        GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class, Plugin::accept);
    }

}