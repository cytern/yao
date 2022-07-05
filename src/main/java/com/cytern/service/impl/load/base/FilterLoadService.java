package com.cytern.service.impl.load.base;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;
import com.cytern.aspect.RobotFilter;
import com.cytern.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 筛选器加载器
 */
public class FilterLoadService {
    private static volatile FilterLoadService filterLoadService;
    private FilterLoadService() {
        HashMap<String, JSONObject> waitCommands = new HashMap<>();
        try {
            Class[] classByPackage = ClassUtils.getClassByPackage("com.cytern.filter");
            try {
                for (Class singleClass : classByPackage) {
                    //类里是否有加了注解的方法 加了 就并入到里面
                    Method[] methods = singleClass.getMethods();
                    for (Method method : methods) {
                        RobotFilter annotation = method.getAnnotation(RobotFilter.class);
                        if (annotation != null) {
                            String activeService = annotation.serviceName();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("class",singleClass);
                            jsonObject.put("methodName",method.getName());
                            waitCommands.put(activeService,jsonObject);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static FilterLoadService getInstance() {
        if (filterLoadService == null) {
            synchronized (FilterLoadService.class) {
                if (filterLoadService == null) {
                    filterLoadService = new FilterLoadService();
                }
            }
        }
        return filterLoadService;
    }
}
