package com.cytern.service.impl.load;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 机器人与各类资源绑定关系服务 可以用于做权限鉴定
 */
public class BindLoadService {
    private final HashMap<String, HashSet<String>> assets;
    private final HashMap<String, HashSet<String>> commands;
    private final HashMap<String, String> robots;

    private static volatile BindLoadService bindLoadService;

    private BindLoadService(){
        HashMap<String, HashSet<String>> assets = new HashMap<>();
        HashMap<String, HashSet<String>> commands = new HashMap<>();
        HashMap<String, String> robots = new HashMap<>();
        this.assets = assets;
        this.commands = commands;
        this.robots = robots;
    }

    public static BindLoadService getInstance() {
        if (bindLoadService == null) {
            synchronized (BindLoadService.class) {
                if (bindLoadService == null) {
                    bindLoadService = new BindLoadService();
                }
            }
        }
        return bindLoadService;
    }
}
