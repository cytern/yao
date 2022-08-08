package com.cytern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.LoggerService;
import com.cytern.service.impl.load.CommandLoadService;
import com.cytern.service.impl.load.ConfigLoadService;
import com.cytern.service.impl.load.RobotLoadService;
import com.cytern.util.MessageSenderUtil;
import com.cytern.util.RobotCachedUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;

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
        //是否处在事件中
        JSONObject eventCommand = eventCommandActiveFilter(messageEvent);
        if (eventCommand != null) {
            JSONObject finalData = addEventData(messageEvent, eventCommand);
            finalData.put("sourceMessage",sourceMessage);
            //执行事件
            boolean b = eventMessageRouter(sourceMessage, eventCommand);
            if (b) {
                CommandExecutedService.handleCommand(finalData);
                destoryEvent(eventCommand);
            }
            return;
        }
        JSONObject localCommandActiveFilter = localCommandActiveFilter(sourceMessage);
        //执行完毕后需要返回
        //判断是否是本地指令
        if (localCommandActiveFilter != null) {
            localCommandActiveFilter.put("sourceMessage",sourceMessage);
            //执行本地指令
            CommandExecutedService.handleCommand(addEventData(messageEvent,localCommandActiveFilter));
        }
        JSONObject commonMessageActiveFilter = commonMessageActiveFilter(sourceMessage);
        if (commonMessageActiveFilter != null) {
            commonMessageActiveFilter.put("sourceMessage",sourceMessage);
            //执行机器人指令
            CommandExecutedService.handleCommand(addEventData(messageEvent,commonMessageActiveFilter));
            return;
        }

        if (robotMapActiveFilter(sourceMessage)) {
            //执行再次机器人指令但需要特殊处理入参
            JSONObject forceCommand = addForceEventData(messageEvent, new JSONObject());
            forceCommand.put("sourceMessage",sourceMessage);
            if (forceCommand.getBoolean("force")) {
                JSONObject forceCommandData = commonMessageActiveFilter(forceCommand.getString("newMessage"));
                if (forceCommandData != null && !forceCommandData.isEmpty()) {
                    forceCommandData.putAll(forceCommand);
                    CommandExecutedService.handleCommand(forceCommandData);
                }
            }


        }
    }

    /**
     * 校验入参是否合规
     */
    public boolean eventMessageRouter(String sourceMessage,JSONObject eventCommand) {
        JSONArray acceptWordRules = eventCommand.getJSONArray("acceptWordRules");
        if (acceptWordRules == null || acceptWordRules.size()<1) {
            LoggerService.info("异常 无效的事件");
            destoryEvent(eventCommand);
            return false;
        }
        boolean flag = false;
        StringBuilder acceptKeys = new StringBuilder();
        for (int i = 0; i < acceptWordRules.size(); i++) {
            String singleKey = acceptWordRules.getString(i);
            acceptKeys.append(singleKey).append(" ");
            if (singleKey.equals(sourceMessage)) {
                flag =true;
            }else if (sourceMessage.equals("退出")) {
                destoryEvent(eventCommand);
                MessageSenderUtil.fastSend(eventCommand,"事件结束 事件名：<" + eventCommand.getString("activeKey") + ">");
                return false;
            }
        }
        if (!flag) {
            LoggerService.info("异常 事件中的无效消息");
            acceptKeys.append("退出");
            MessageSenderUtil.fastSend(eventCommand,"你需要正确回复事件消息 当前事件名\n<" + eventCommand.getString("activeKey") + " > \n支持的选项为\n" + acceptKeys.toString());
            return false;
        }else {
            return true;
        }
    }

    public void destoryEvent(JSONObject eventCommand) {
        String qqId = eventCommand.getString("qqId");
        RobotCachedUtil.getInstance().getEventCache().remove(qqId);
    }

    public void handlerMessageEvent (JSONObject command) {
        String sourceMessage = command.getString("sourceMessage");
        //是否处于事件服务中
        JSONObject localCommandActiveFilter = localCommandActiveFilter(sourceMessage);
        //执行完毕后需要返回
        //判断是否是本地指令
        if (localCommandActiveFilter != null) {
            //执行本地指令
            CommandExecutedService.handleCommand(addForceEventData(command,localCommandActiveFilter));
        }
        JSONObject commonMessageActiveFilter = commonMessageActiveFilter(sourceMessage);
        if (commonMessageActiveFilter != null) {
            //执行机器人指令
            CommandExecutedService.handleCommand(addForceEventData(command,commonMessageActiveFilter));
            return;
        }

        if (robotMapActiveFilter(sourceMessage)) {
            //执行再次机器人指令但需要特殊处理入参
            JSONObject forceCommand = addForceEventData(command, new JSONObject());
            if (forceCommand.getBoolean("force")) {
                JSONObject forceCommandData = commonMessageActiveFilter(forceCommand.getString("newMessage"));
                if (forceCommandData != null && !forceCommandData.isEmpty()) {
                    forceCommandData.putAll(forceCommand);
                    CommandExecutedService.handleCommand(forceCommandData);
                }
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

    private JSONObject addForceEventData(JSONObject event,JSONObject commandData) {
        String s = event.getString("sourceMessage");
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
        commandData.put("subject","testSubject");
        commandData.put("qqId",event.getString("qqId"));
        commandData.put("currentBot",currentRobot);
        commandData.put("qqName",event.getString("qqName"));
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
     * 是否匹配缓存事件
     */
    public JSONObject eventCommandActiveFilter(MessageEvent event) {
        String s = String.valueOf(event.getSender().getId());
        return RobotCachedUtil.getInstance().getEventCache().get(s);
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
