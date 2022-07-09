package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 筛选器加载器
 */
public class FilterLoadService {
    private static volatile FilterLoadService filterLoadService;
    private static  HashMap<String, Method> filters;
    private FilterLoadService() {
        HashMap<String, Method> waitFilter = new HashMap<>();
        try {
            Class[] classByPackage = ClassUtils.getClassByPackage("com.cytern.filter");
            try {
                for (Class singleClass : classByPackage) {
                    //类里是否有加了注解的方法 加了 就并入到里面
                    Method[] methods = singleClass.getMethods();
                    for (Method method : methods) {
                        RobotFilter annotation = method.getAnnotation(RobotFilter.class);
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
                            waitFilter.put(serviceName,method);

                        }


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
           filters = waitFilter;
    }

    public static void main(String[] args) {
        FilterLoadService filterLoadService = new FilterLoadService();
        System.out.println();
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

    public  boolean handlerFilterExecuted(JSONObject command, String key, String[] params) {
        Method method = filters.get(key);
        if (method == null) {
            LoggerService.error("can not find the method of the key :" + key);
            return true;
        }
        ArrayList<Object> paramsObject = new ArrayList<>();
        paramsObject.add(command);
        paramsObject.addAll(List.of(params));
        boolean invoke = true;
        try {
             invoke =(boolean) method.invoke(null, paramsObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return invoke;
    }
}
