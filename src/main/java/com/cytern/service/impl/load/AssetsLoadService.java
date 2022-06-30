package com.cytern.service.impl.load;

public class AssetsLoadService {
    private static volatile AssetsLoadService assetsLoadService;

    /**
     * 这位大哥是真正的mod资源加载的消费方 他会解析所有的图片文件 如果需要拆分 会进行拆分
     */
    private AssetsLoadService() {

    }

    public static AssetsLoadService getInstance() {
        if (assetsLoadService == null) {
            synchronized (AssetsLoadService.class) {
                if (assetsLoadService == null) {
                    assetsLoadService = new AssetsLoadService();
                }
            }
        }
        return assetsLoadService;
    }
}
