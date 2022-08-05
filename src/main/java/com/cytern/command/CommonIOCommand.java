package com.cytern.command;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.LoggerService;
import com.cytern.service.impl.load.AdviceLoadService;
import com.cytern.service.impl.load.FilterLoadService;
import com.cytern.util.CommCodeResultUtil;
import com.cytern.util.MessageSenderUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

/**
 * 基础消息服务
 */
@RobotCommand(activeService = "通用消息服务")
public class CommonIOCommand {

    /**
     * 最基本的消息服务
     */
    @RobotCommand(activeService = "通用消息")
    public static JSONObject baseCommandHandler (JSONObject commands) {
        //基本消息服务应该分以下几步走
        JSONArray preReturns = null;
        LoggerService.info("前置增强器 前 参数 ");
        //前置增强器
        commands = CommandExecutedService.preAdvice(commands);
        LoggerService.info("前置增强器 通过 参数 ");
        //筛选器
        preReturns = CommandExecutedService.filter(commands);
        LoggerService.info("筛选器 通过 参数 ");
        JSONObject repeatFilterReturn = null;

        //重复选择器
        try {
            repeatFilterReturn = CommandExecutedService.repeatFilter(preReturns,commands);
        } catch (RobotException e) {
            LoggerService.error(e.getMessage());
        }
        LoggerService.info("选择器 通过 参数 ");

        //增强器
        JSONObject newCommand = null;
        newCommand = CommandExecutedService.advice(repeatFilterReturn, null);


        LoggerService.info("增强器 通过 参数 ");
        MessageChain msg = null;

        //最终参数处理
        try {
            msg = CommandExecutedService.handleReturnMsg(newCommand);
        } catch (RobotException e) {
            LoggerService.error(e.getMessage());
        }
        MessageSenderUtil.normalSend( (Contact) commands.get("subject"),msg);
        return null;
    }




}
