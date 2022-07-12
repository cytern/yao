package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.aspect.RobotFilter;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 增强器加载器
 */
public class AdviceLoadService {
    private static volatile AdviceLoadService adviceLoadService;

    private  final HashMap<String,Method> advices;

    private AdviceLoadService () {
        HashMap<String, Method> waitAdvice = new HashMap<>();
        try {
            Class[] classByPackage = ClassUtils.getClassByPackage("com.cytern.filter");
            try {
                for (Class singleClass : classByPackage) {
                    //类里是否有加了注解的方法 加了 就并入到里面
                    Method[] methods = singleClass.getMethods();
                    for (Method method : methods) {
                        RobotAdvice annotation = method.getAnnotation(RobotAdvice.class);
                        if (annotation != null) {
                            String serviceName = annotation.name();
                            serviceName = serviceName + "(";
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            for (int i = 1; i < parameterTypes.length; i++) {
                                if (i !=1) {
                                    serviceName = serviceName + ",";
                                }
                                serviceName = serviceName + parameterTypes[i].getSimpleName();
                            }
                            serviceName = serviceName + ")";
                            waitAdvice.put(serviceName,method);

                        }


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        advices = waitAdvice;

    }



    public static AdviceLoadService getInstance() {
        if (adviceLoadService == null) {
            synchronized (AdviceLoadService.class) {
                if (adviceLoadService == null) {
                    adviceLoadService = new AdviceLoadService();
                }
            }
        }
        return adviceLoadService;
    }


    /**
     *  处理增强器
     */
    public  JSONObject handlerAdviceExecuted(JSONObject command, String key, String[] params) {
        Method method = advices.get(key);
        JSONObject result = new JSONObject();
        result.put("sender",command.get("sender"));
        if (method == null) {
            LoggerService.error("can not find the method of the key :" + key);
            result.put("msg","哇(脑子里好像出现了个bug)");
            return result;
        }
        ArrayList<Object> paramsObject = new ArrayList<>();
        paramsObject.add(command);
        Collections.addAll(paramsObject,params);
        try {
            result.putAll((JSONObject)  method.invoke(null, paramsObject));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            result.put("msg","哇(脑子里好像出现了个bug)");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            result.put("msg","哇(脑子里好像出现了个bug)");
        }
        return result;
    }
}
