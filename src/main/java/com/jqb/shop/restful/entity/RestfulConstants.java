package com.jqb.shop.restful.entity;

/**
 * Created by liubin on 2016/1/23.
 */
public class RestfulConstants {

    /** restful 成功统一返回码 */
    public static int RESTFUL_ERR_CODE_SUCCESS = 1;

    /** restful 失败统一返回码 */
    public static int RESTFUL_ERR_CODE_FAIL= -1;

    /** restful 失败统一返回码 */
    public static int RESTFUL_ERR_CODE_JSON_CYCLE= -2;

    /** restful no member返回码 */
    public static int RESTFUL_ERR_CODE_NO_MEMBER = -5;

    /** restful exception统一返回码 */
    public static int RESTFUL_ERR_CODE_EXCEPTION = -10;

    /** 登录错误码【100,200） */
    /** 登录失败 */
    public static int RESTFUL_ERR_CODE_LOGIN_FAIL = 100;

    /** 移动端no token */
    public static int RESTFUL_ERR_CODE_LOGIN_FAIL_NO_TOKEN = 101;

    /** php 后台获取userinfo失败 */
    public static int RESTFUL_ERR_CODE_LOGIN_FAIL_NO_USER = 102;

    public static int RESTFUL_ERR_CODE_LOGOUT_SUCCESS = 199;

    /** 订单错误码【200,300） */
    /**  */
    public static int RESTFUL_ERR_CODE_ORDER_FAIL = 200;

    /** 购物车错误码【300,400） */
    /**  */
    public static int RESTFUL_ERR_CODE_CART_FAIL = 300;

    /** 购物车空 */
    public static int RESTFUL_ERR_CODE_CART_EMPTY = 301;

    /** 购物车错误码【400,500） */
    /**  */
    public static int RESTFUL_ERR_CODE_PORDUCT_FAIL = 400;
    /** 商品超过限购次数 */
    public static int RESTFUL_ERR_CODE_PORDUCT_RESTRICTION_FAIL = 401;

    /** 地区错误码【500,600） */
    /**  */
    public static int RESTFUL_ERR_CODE_AREA_FAIL = 500;

    /** 广告错误码【600,700） */
    /**  */
    public static int RESTFUL_ERR_AD_CODE_FAIL = 600;

    /** 商品活动错误码【700,800） */
    /**  */
    public static int RESTFUL_ERR_PROMOTION_FAIL = 600;

    /** 退货错误码【900,1000） */
    /**  */
    public static int RESTFUL_ERR_CODE_RETURNS_FAIL = 900;

    /** 支付宝错误码【9000,9100） */
    /**  */
    public static int RESTFUL_ERR_ALIPAY_SUCCESS = 9000;
    /**  */
    public static int RESTFUL_ERR_ALIPAY_FAIL = 9001;

    /** 微信支付错误码 (-10,0】 */
    public static int RESTFUL_ERR_WXPAY_OK = 0;
    public static int RESTFUL_ERR_WXPAY_COMM = -1;
    public static int RESTFUL_ERR_WXPAY_USER_CANCEL = -2;
    public static int RESTFUL_ERR_WXPAY_SENT_FAILED = -3;
    public static int RESTFUL_ERR_WXPAY_AUTH_DENIED = -4;
    public static int RESTFUL_ERR_WXPAY_UNSUPPORT = -5;


    /** restful no member返回码 */
    public static String RESTFUL_ERR_CODE_NO_MEMBER_MSG = "请登录！";



}
