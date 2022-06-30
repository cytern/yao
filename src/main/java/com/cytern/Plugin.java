package com.cytern;

import com.cytern.service.impl.load.RobotCommandLoadService;
import com.cytern.service.impl.load.ModLoadService;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();
    private RobotCommandLoadService robotCommandLoadService;
    private ModLoadService modLoadService;

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("com.cytern.plugin", "1.0-demo")
                .name("robot")
                .author("cytern&miaomiao")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("爻 框架 开始加载!");
        getLogger().info("爻 指令集 开始加载!");
        robotCommandLoadService = RobotCommandLoadService.getInstance();
        getLogger().info("爻 指令集 加载完毕!");
        getLogger().info("爻 模组 开始加载!");
        modLoadService = ModLoadService.getInstance();
        getLogger().info("爻 模组 加载完毕!");
    }
}