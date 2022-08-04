package com.cytern.advice;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.network.api.ItemFeign;
import com.cytern.util.RobotCachedUtil;

@RobotAdvice(name = "祈祷运势增强")
public class PrayFor {

    @RobotAdvice(name = "七夕乞巧")
    public static JSONObject prayForSeven(JSONObject params) {
        JSONObject backJson = new JSONObject();
        String qqId = params.getString("qqId");
        TimedCache<String, String> prayForResult = RobotCachedUtil.getInstance().getPrayForResult();
        if (prayForResult.containsKey(qqId)) {
            String s = prayForResult.get(qqId);
            backJson.put("返回结果","嗯？ 好像已经乞巧过了，已经忘记了吗?   那就在告诉你一遍吧！\n" + s);
            backJson.put("图片","爻图片.HellyRobot.Gunther_r1_c0.png");
            return backJson;
        }else {
            int times = 0;
            for (int i = 0; i < 9; i++) {
                int total = 100;
                total = total -10*i;
                int i1 = RandomUtil.randomInt(100);
                if (total>=i1) {
                    times ++;
                }else {
                    break;
                }
            }
            if (times == 0) {
                int i = RandomUtil.randomInt(100);
                if (i>10) {
                    times = 1;
                }
            }
            String backMsg = "gunther帮你往碗里投入了一枚银针~ 银针的影子在碗底摇曳 冥冥中预示着什么: \n \n";
            switch (times) {
                case 0: {
                    backMsg= backMsg + "针影在水底笔笔直直 看来今年织女并不眷顾你 天行健君子需自强不息";//0.01
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r0_c1.png");
                    break;
                }
                case 1:{
                    backMsg= backMsg + "针影看起来并没有什么变化 织女不太关注你呢 看来你需要更加努力了";//0.9
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r0_c1.png");
                    break;
                }
                case 2:{
                    backMsg= backMsg + "针影中似有异像 你的运气看起来并不算差 但远不算好 但还是要吾辈自强哦";//0.72
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r0_c1.png");
                    break;
                }
                case 3:{
                    backMsg= backMsg + "针影在在中间处弯折了 这是个好迹象 织女今年会保佑你 加油哦";//0.504
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r0_c0.png");
                    break;
                }
                case 4: {
                    backMsg= backMsg + "针银在两端弯折了 织女眷顾着你 你会得到她的帮助！";//0.3
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r0_c0.png");
                    break;
                }
                case 5:{
                    backMsg= backMsg + "恐怕要是巧手才能得到这等气运 十里挑一 你是唯一 心想事成哦";//0.15
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r1_c0.png");
                    break;
                }
                case 6:{
                    backMsg= backMsg + "碗底似乎有异像 你是真正的被眷顾者 好运与你同在";//0.06
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r1_c0.png");
                    break;
                     }
                case 7:{

                    backMsg= backMsg + "碗底似乎有绿色光芒涌动 织女赐予了你祝福 很好 百里挑一者 你获得了: 许愿星";//0.0181
                    ItemFeign.itemAddOne("许愿星","1",qqId);
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r1_c1.png");
                    break;
                }
                case 8:{
                    backMsg= backMsg + "天呐 碗底射出耀眼的光芒 我从未见过这个 你就是天选 获得: 蓝色许愿星";//0.003
                    ItemFeign.itemAddOne("蓝色许愿星","1",qqId);
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r1_c1.png");
                    break;
                }
                case 9: {
                    backMsg= backMsg + "天地异动 真正的气运之子 万里挑一的神选 获得: 金色许愿星";//0.0003
                    ItemFeign.itemAddOne("金色许愿星","1",qqId);
                    backJson.put("图片","爻图片.HellyRobot.Gunther_r1_c1.png");
                    break;
                }
            }
            prayForResult.put(qqId,backMsg);
            backJson.put("返回结果",backMsg);
            return backJson;
        }
        //进行九次乞巧
    }
}
