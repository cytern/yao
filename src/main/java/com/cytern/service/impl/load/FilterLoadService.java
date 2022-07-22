package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 筛选器加载器
 */
public class FilterLoadService {
    private static volatile FilterLoadService filterLoadService;
    private static  HashMap<String, Method> filters;
    private FilterLoadService() {
        HashMap<String, Method> waitFilter = new HashMap<>();
        try {
            Set<Class<?>> classByPackage = ClassUtils.getClasses("com.cytern.filter");
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

    /**
     * 处理筛选器
     */
    public  boolean handlerFilterExecuted(JSONObject command, String key, String[] params) {
        Method method = filters.get(key);
        if (method == null) {
            LoggerService.error("can not find the method of the key :" + key);
            return true;
        }
        ArrayList<Object> paramsObject = new ArrayList<>();
        paramsObject.add(command);
        Collections.addAll(paramsObject,params);
        boolean invoke = true;
        try {
            if (params.length == 0) {
                invoke =(boolean) method.invoke(null, command);
            }else if(params.length == 1) {
                invoke =(boolean) method.invoke(null, command,params[0]);
            }else if (params.length ==2) {
                invoke =(boolean) method.invoke(null, command,params[0],params[1]);
            }else if (params.length ==3) {
                invoke =(boolean) method.invoke(null, command,params[0],params[1],params[2]);
            }else {
                invoke = false;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return invoke;
    }

    /**
     * 处理筛选器
     */
    public  Integer handlerSelectExecuted(JSONObject command, String key, String[] params) {
        Method method = filters.get(key);
        if (method == null) {
            LoggerService.error("can not find the method of the key :" + key);
            return 0;
        }
        ArrayList<Object> paramsObject = new ArrayList<>();
        paramsObject.add(command);
        Collections.addAll(paramsObject,params);
        Integer invoke = 0;
        try {
            if (params.length == 0) {
                invoke =(Integer) method.invoke(null, command);
            }else if(params.length == 1) {
                invoke =(Integer) method.invoke(null, command,params[0]);
            }else if (params.length ==2) {
                invoke =(Integer) method.invoke(null, command,params[0],params[1]);
            }else if (params.length ==3) {
                invoke =(Integer) method.invoke(null, command,params[0],params[1],params[2]);
            }else {
                invoke = 0;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return invoke;
    }
}
