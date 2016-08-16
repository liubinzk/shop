package com.jqb.shop.plugin.alipay.factory;


import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.jqb.shop.plugin.alipay.config.AlipayConfig;


/**
 * API调用客户端工厂
 *
 * @author taixu.zqq
 * @version $Id: AlipayAPIClientFactory.java, v 0.1 2014年7月23日 下午5:07:45 taixu.zqq Exp $
 */
public class AlipayAPIClientFactory {

    /** API调用客户端 */
    private static AlipayClient alipayClient;

    /**
     * 获得API调用客户端
     *
     * @return
     */
    public static AlipayClient getAlipayClient(){

        if(null == alipayClient){
            alipayClient = new DefaultAlipayClient(AlipayConfig.ALIPAY_GATEWAY, AlipayConfig.TIPS_APPID,
                    AlipayConfig.PRIVATE_KEY, "json", AlipayConfig.INPUT_CHARSET,AlipayConfig.ALIPAY_PUBLIC_KEY);
        }
        return alipayClient;
    }


}
