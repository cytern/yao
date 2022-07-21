package com.cytern.util;

import cn.hutool.core.util.RandomUtil;

public class MathUtil {

    public static Integer randomFactorInt(Integer min,Integer max,Double factor) {
        if (factor > 0.5) {
            double v = max * (0.5 / (1 - factor));
            int i = RandomUtil.randomInt( min, (int) v);
            if (i < (max -min)/2) {
                return i;
            }else {
                if (i<max) {
                    return i;
                }
                double v1 = min + (max-min)*((double)i/(v*2) + 0.5);

                return (int) v1;
            }
        }else if (factor == 0.5){
           return RandomUtil.randomInt(min,max);
        }else {
            double v = min - (max-min) * (0.5 / factor);
            int i = RandomUtil.randomInt((int) v, max);
            if (i> (max -min)/2) {
                return i;
            }else {
                if (i>min) {
                    return i;
                }
                    double v1 = min + (max-min)*(Math.abs(Math.abs((double)i/(v*2)) - 0.5));
                    return Math.abs((int) v1);
            }
        }
    }
}
