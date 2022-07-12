package com.cytern;

import com.cytern.service.impl.load.base.RobotCommandLoadService;
import com.cytern.service.impl.load.base.ModLoadService;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("com.cytern.plugin", "1.0-demo")
                .name("robot")
                .author("cytern&miaomiao")
                .build());
    }

    @Override
    public void onEnable() {


    }

    public static void main(String[] args) {
        INSTANCE.onEnable();
    }
}