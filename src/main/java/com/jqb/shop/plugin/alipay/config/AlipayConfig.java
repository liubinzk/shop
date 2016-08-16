package com.jqb.shop.plugin.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：1.0
 *日期：2016-01-08

 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”
 */

public class AlipayConfig {

	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String PARTNER = "2088311450568083";

	public static String TIPS_APPID = "2016010701072912";
	public static String TIPS_APPID_ANDROD = "2016010701072951";

	public static String SECRET_KEY = "h95bsuckrnounohizc2drtf82n5q7jh4";

	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	public static String SELLER_ID = PARTNER;
	// 商户的私钥
//	public static String PRIVATE_KEY = "h95bsuckrnounohizc2drtf82n5q7jh4";
	public static String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANyYnCMFF/JrpiH6" +
			"RpTr+M9yk5Al/7sqeJFKXf1I42A4DXuXn4FNMZhHBSiU2v8i9WYd8kV4T3xC8jUz" +
			"p/cwXOFapW0V3kKBIxMnomwt2OxW4GfB/QjoOqR5hE9zpINIB/aQpV3Esb2P2iXA" +
			"0BB09e5LHStbJFvAXGRrC57kNFRvAgMBAAECgYBbqmrPPpGZ4dOyMM7dmUTV5zWc" +
			"Y73lp5tcPHvcAUrCs+geYH4ee9ZNAc4J8/kgpgU1PuJqrbjR2PvO0b1NQWKGWHlm" +
			"jA08DgTIUzDXKhwGku8cgMzuyc0GBcEuzz3w6XCIdbYMm2XKK1weTVhH7oeMS5xH" +
			"wvg+6n+WZ3wIGTke8QJBAP24DhdfCEYUamSG5lUmDqm2tV2dXSij2mqghW6wooxy" +
			"fHaS/zLASOaMk5RZr6UCLBIy9kWQCNTuATovtMUjgRcCQQDelFI+NIzNExfMqB7k" +
			"6tkZTFVDd+ZEpLtPozZ3oUaEKiKcQw9vBGH41jVZpPuI3LXs13zc85j+Cge3HGLZ" +
			"B+5pAkAEccDtb+C5OYpTkHlgbHY9StIKfcMv/w7IZ19u/PEb/LVpblHCFdQxFa1Q" +
			"H/IPteYHu5TMyyUcdb7XwhYsjd6pAkA2ZqYbUhi/3tTITqcOaGAbkM6eKOX9DQ5D" +
			"Dq83WhG0J+BBtRpGlhmkCicL/AkGkHteoeSA1IEmRilQlf2K8idRAkEAlBBrcV7Z" +
			"q6WoskiwsUOp77RbK+kTSUax/Ztp36A9gLHot40boUo+Ftn36xvr5GmgZbzcUyYS" +
			"bsPJhLLDUsLkxQ==";

	// 支付宝的公钥，无需修改该值
	public static String ALIPAY_PUBLIC_KEY  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	public static String TIPU_ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	/**支付宝网关*/
	public static final String ALIPAY_GATEWAY    = "https://openapi.alipay.com/gateway.do";

	/**授权访问令牌的授权类型*/
	public static final String GRANT_TYPE        = "authorization_code";

	// 调试用，创建TXT日志文件夹路径
	public static String LOG_PATH = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String INPUT_CHARSET = "utf-8";

	// 签名方式 不需修改
	public static String SIGN_TYPE = "RSA";

	// 签名方式 不需修改
	public static String VERSION = "1.0";
	public static String APP_AUTH_TOKEN_ANDROID = "2c1573c16db9f28717daa404078866ed";
	public static String APP_AUTH_TOKEN_IOS = "2c1573c16db9f28717daa404078866ed";

}
