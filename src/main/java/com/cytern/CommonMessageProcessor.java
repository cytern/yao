package com.cytern;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.load.CommandLoadService;
import com.cytern.service.impl.load.ConfigLoadService;
import com.cytern.service.impl.load.RobotLoadService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基础消息处理器
 */
public class CommonMessageProcessor {
    private  String sourceMessage;
    private final MessageEvent messageEvent;

    private Bot currentRobot;
    public CommonMessageProcessor(MessageEvent messageEvent) {
        this.messageEvent = messageEvent;
        currentRobot = messageEvent.getBot();
    }

    public CommonMessageProcessor() {
        this.messageEvent = null;
    }

    /**
     * 全局消息处理器
     */
    public void handlerMessageEvent () {
        String sourceMessage = convertMessageEvent(messageEvent);
        JSONObject localCommandActiveFilter = localCommandActiveFilter(sourceMessage);
        //执行完毕后需要返回
        //判断是否是本地指令
        if (localCommandActiveFilter != null) {
            //执行本地指令
            CommandExecutedService.handleCommand(addEventData(messageEvent,localCommandActiveFilter));
        }
        JSONObject commonMessageActiveFilter = commonMessageActiveFilter(sourceMessage);
        if (commonMessageActiveFilter != null) {
            //执行机器人指令
            CommandExecutedService.handleCommand(addEventData(messageEvent,commonMessageActiveFilter));
        }

        if (robotMapActiveFilter(sourceMessage)) {
            //执行自动ai指令

        }
    }

    public void handlerMessageEvent (JSONObject rowData) {
        String sourceMessage = convertMessageEvent(rowData);
        JSONObject localCommandActiveFilter = localCommandActiveFilter(sourceMessage);
        //执行完毕后需要返回
        //判断是否是本地指令
        if (localCommandActiveFilter != null) {
            //执行本地指令
            CommandExecutedService.handleCommand(addEventData(rowData,localCommandActiveFilter));
        }
        JSONObject commonMessageActiveFilter = commonMessageActiveFilter(sourceMessage);
        if (commonMessageActiveFilter != null) {
            //执行机器人指令
            CommandExecutedService.handleCommand(addEventData(rowData,commonMessageActiveFilter));
        }

        if (robotMapActiveFilter(sourceMessage)) {
            //执行自动ai指令

        }
    }

    private JSONObject addEventData(JSONObject event,JSONObject commandData) {
        commandData.put("subject",event.getJSONObject("subject"));
        commandData.put("qqId",event.getJSONObject("qqId"));
        return commandData;
    }

    private JSONObject addEventData(MessageEvent event,JSONObject commandData) {
        commandData.put("subject",event.getSubject());
        commandData.put("qqId",event.getSender().getId());
        commandData.put("currentBot",currentRobot);
        return commandData;
    }

    /**
     * 基础转化处理基础消息
     * @param event 消息事件
     * @return 组装了源message消息的jsonObject
     * @throws com.cytern.exception.RobotException
     */
    public String convertMessageEvent (MessageEvent event) {
        String sourceMessage = event.getMessage().contentToString();
        if (sourceMessage.contains("[mirai:image") ||
                sourceMessage.contains("[mirai:atall]") ||
                sourceMessage.contains("[mirai:flash") ||
                sourceMessage.contains("[mirai:file:")
        ) {
            throw new RobotException("鉴定为图片消息");
        }
        //处理@ 信息
        if (sourceMessage.contains("[mirai:at")) {
            sourceMessage = sourceMessage.substring(sourceMessage.indexOf("]") +1,sourceMessage.length());
        }
        //处理通用码值
        sourceMessage = sourceMessage.replace("《机器人名》", (String)ConfigLoadService.getInstance().getConfig().get("defaultRobotName"));
        return sourceMessage;
    }

    public String convertMessageEvent (JSONObject event) {
        String sourceMessage = event.getString("message");
        if (sourceMessage.contains("[mirai:image") ||
                sourceMessage.contains("[mirai:atall]") ||
                sourceMessage.contains("[mirai:flash") ||
                sourceMessage.contains("[mirai:file:")
        ) {
            throw new RobotException("鉴定为图片消息");
        }
        //处理@ 信息
        if (sourceMessage.contains("[mirai:at")) {
            sourceMessage = sourceMessage.substring(sourceMessage.indexOf("]") +1,sourceMessage.length());
        }
        //处理通用码值
        sourceMessage = sourceMessage.replace("《机器人名》", (String)ConfigLoadService.getInstance().getConfig().get("defaultRobotName"));
        return sourceMessage;
    }

    /**
     * 是否匹配机器人触发 key 关键词
     */
    public boolean robotMapActiveFilter(String sourceMessage) {
        HashMap<String, String> robotMap = RobotLoadService.getInstance().getRobotMap();
        AtomicBoolean flag = new AtomicBoolean(false);
        robotMap.forEach((key,value) -> {
            if (sourceMessage.contains(value)) {
                flag.set(true);
            }
        });
        return flag.get();
    }

    /**
     * 是否匹配本地指令
     */
    public JSONObject localCommandActiveFilter(String sourceMessage) {
        return CommandLoadService.getInstance().getLocalCommands().getOrDefault(sourceMessage, null);
    }

    /**
     * 是否匹配机器人规则
     */
    public JSONObject commonMessageActiveFilter(String sourceMessage) {
         return RobotLoadService.getInstance().getRobotCommand().getOrDefault(sourceMessage,null);
    }
    public void dealWithGroupMessageEvent(GroupMessageEvent messageEvent) {

    }
    public void dealWithFriendMessageEvent(FriendMessageEvent messageEvent) {

    }
 }
