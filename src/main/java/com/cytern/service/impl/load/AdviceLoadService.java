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
import java.util.Set;

/**
 * 增强器加载器
 */
public class AdviceLoadService {
    private static volatile AdviceLoadService adviceLoadService;

    private  final HashMap<String,Method> advices;

    private AdviceLoadService () {
        HashMap<String, Method> waitAdvice = new HashMap<>();
        try {

            Set<Class<?>> classByPackage = ClassUtils.getClasses("com.cytern.advice");
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
    public  JSONObject handlerAdviceExecuted(JSONObject command, String key, String[] params,int i) throws InvocationTargetException, IllegalAccessException {
        Method method = advices.get(key);
        JSONObject result = new JSONObject(command);
        if (method == null) {
            LoggerService.error("can not find the method of the key :" + key);
            result.put("msg","哇(脑子里好像出现了个bug)");
            return result;
        }
        ArrayList<Object> paramsObject = new ArrayList<>();
        paramsObject.add(command);
        Collections.addAll(paramsObject,params);
        JSONObject invoke;
            if (params.length == 0 || (params.length == 1&& params[0].equals("") )) {
                invoke =(JSONObject) method.invoke(null, command);
            }else if(params.length == 1) {
                invoke =(JSONObject) method.invoke(null, command,params[0]);
            }else if (params.length ==2) {
                invoke =(JSONObject) method.invoke(null, command,params[0],params[1]);
            }else if (params.length ==3) {
                invoke =(JSONObject) method.invoke(null, command,params[0],params[1],params[2]);
            }else {
                invoke = new JSONObject();
            }
            result.put("爻服务" + (i+1),  invoke);
        return result;
    }
}
