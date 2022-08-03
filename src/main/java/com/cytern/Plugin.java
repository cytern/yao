package com.cytern;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.LoggerService;
import com.cytern.service.impl.load.*;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;
import com.cytern.service.impl.load.base.RobotCommandLoadService;
import com.cytern.service.impl.load.base.ModLoadService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private HashMap<String, HashMap<String, String>> assets;

    private HashMap<String, Object> config;

    private HashMap<String, JSONObject> itemMap;

    private AdviceLoadService adviceLoadService;

    private CommandLoadService commandLoadService;

    private RobotLoadService robotLoadService;

    private FilterLoadService filterLoadService;

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("com.cytern.plugin", "1.0-demo")
                .name("robot")
                .author("cytern&miaomiao")
                .build());
    }

    private static void accept(MessageEvent event) {
        try {
            CommonMessageProcessor commonMessageProcessor = new CommonMessageProcessor(event);
            commonMessageProcessor.handlerMessageEvent();
        } catch (RobotException e) {
            LoggerService.info("机器人服务错误: " +e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        super.onLoad($this$onLoad);
        this.assets = AssetsUnzipLoadService.getInstance().getAssets();
        this.config = ConfigLoadService.getInstance().getConfig();
        this.itemMap = ItemLoadService.getInstance().getItemMap();
        adviceLoadService = AdviceLoadService.getInstance();
        commandLoadService = CommandLoadService.getInstance();
        robotLoadService = RobotLoadService.getInstance();
        filterLoadService =FilterLoadService.getInstance();
    }

    @Override
    public void onEnable() {
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class, Plugin::accept);
    }

}