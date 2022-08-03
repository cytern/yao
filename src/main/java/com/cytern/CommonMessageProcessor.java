package com.cytern;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.LoggerService;
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
            return;
        }

        if (robotMapActiveFilter(sourceMessage)) {
            //执行再次机器人指令但需要特殊处理入参
            JSONObject forceCommand = addForceEventData(messageEvent, new JSONObject());
            if (forceCommand.getBoolean("force")) {
                JSONObject forceCommandData = commonMessageActiveFilter(forceCommand.getString("newMessage"));
                forceCommandData.putAll(forceCommand);
                CommandExecutedService.handleCommand(forceCommandData);
            }


        }
    }


    private JSONObject addEventData(MessageEvent event,JSONObject commandData) {
        commandData.put("subject",event.getSubject());
        commandData.put("qqId",event.getSender().getId());
        commandData.put("currentBot",currentRobot);
        commandData.put("qqName",event.getSender().getNick());
        return commandData;
    }

    private JSONObject addForceEventData(MessageEvent event,JSONObject commandData) {
        String s = event.getMessage().contentToString();
        StringBuilder newString = new StringBuilder();
        if (s.contains(" ")) {
            String[] s1 = s.split(" ");
            newString.append(s1[0]).append(" ");
            for (int i = 1; i < s1.length; i++) {
                if (i != s1.length-1) {
                    newString.append("《爻入参》").append(" ");
                }else {
                    newString.append("《爻入参》");
                }
                commandData.put("爻入参" + i,s1[i]);
            }
            commandData.put("force",true);
            commandData.put("newMessage",newString.toString());
        }else {
            commandData.put("force",false);
        }
        commandData.put("subject",event.getSubject());
        commandData.put("qqId",event.getSender().getId());
        commandData.put("currentBot",currentRobot);
        commandData.put("qqName",event.getSender().getNick());
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
        sourceMessage = sourceMessage.replace("《机器人名》", ConfigLoadService.getInstance().getDefaultRobotName());
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
            if (sourceMessage.contains(key)) {
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
