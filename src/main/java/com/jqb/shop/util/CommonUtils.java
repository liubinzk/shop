package com.jqb.shop.util;

import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liubin on 2015/12/31.
 */
public class CommonUtils {

    // 获取img标签正则
    public static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    public static final String IMGSRC_REG = "/upload/\"?(.*?)(\"|>|\\s+)";


    public static final String PRODUCT_IMG_KEY = "img";
    public static final String PRODUCT_INFO_KEY = "info";

    public static Object deepClone(Object object) throws IOException, OptionalDataException,
            ClassNotFoundException {
        // 将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(object);
        // 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        oo.close();
        oi.close();
        bo.close();
        bi.close();
        return (oi.readObject());
    }

    public static String returnRestfulResult(String callback, RestfulResult restfulResult) {
        JSONArray jb = JSONArray.fromObject(restfulResult);
        String result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    public static String returnRestfulResult(String callback, RestfulResult restfulResult,  JsonConfig jsonConfig) {
        JSONArray jb = null;
        String result = null;
        try {
            jb = JSONArray.fromObject(restfulResult, jsonConfig);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_JSON_CYCLE);
            e.printStackTrace();
            result = e.toString();
        }
        if(jb != null){
            result = jb.toString();
        }
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    public static String returnRestfulObjResult(String callback, RestfulResult restfulResult) {
        JSONObject jb = JSONObject.fromObject(restfulResult);
        String result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    public static String returnRestfulObjResult(String callback, RestfulResult restfulResult,  JsonConfig jsonConfig) {
        JSONObject jb = JSONObject.fromObject(restfulResult, jsonConfig);
        String result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void copyProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object sourceValue = readMethod.invoke(source);
                        Object targetValue = readMethod.invoke(target);
                        if (sourceValue != null && targetValue != null && targetValue instanceof Collection) {
                            Collection collection = (Collection) targetValue;
                            collection.clear();
                            collection.addAll((Collection) sourceValue);
                        } else {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, sourceValue);
                        }
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy properties from source to target", ex);
                    }
                }
            }
        }
    }

    public static String dealString(String str){
        StringBuffer returnStr = new StringBuffer();
        String[] strArray = str.split(">");
        if(strArray != null && strArray.length>0){
            for (String elem : strArray) {
                if( elem.indexOf("</") >= 0 ){
                    returnStr.append( elem.substring(0,elem.indexOf("</")));
                }
            }
        } else {
            returnStr.append(str);
        }
        String result = returnStr.toString().replace("\\r\\n","");

        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(result);
        result = m.replaceAll("");
        return result ;
    }

    public static Map<String,Object> dealProductIntroduction(String introduction){
        Map<String,Object> introMap = new HashMap<String,Object>();
        List<String> imgList = null;
        String info=introduction;
        if(introduction.indexOf("<img") >= 0){
            imgList = new ArrayList<String>();
            //IMGURL_REG
            Pattern p = Pattern.compile(IMGURL_REG);
            Matcher matcher = p.matcher(introduction);
            String img="";
            while (matcher.find()) {
                img = matcher.group();
                info = matcher.replaceAll("");
                Matcher srcMatcher = Pattern.compile(IMGSRC_REG).matcher(img);
                while(srcMatcher.find()){
                    img = srcMatcher.group();
                    if(img.indexOf("\"") >=0 ){
                        img = img.replaceAll("\"","");
                        imgList.add(img);
                    }
                }
            }
        }

        if(info.indexOf("</") >= 0){
            info = dealString(info);
        }
        introMap.put(PRODUCT_INFO_KEY,info);
        introMap.put(PRODUCT_IMG_KEY,imgList);
        return introMap ;
    }

    public static double formatAliFee(BigDecimal fee){
        DecimalFormat df=new DecimalFormat("#.00");
        return Double.parseDouble(df.format (fee.doubleValue()));
    }

    public static int formatWxFee(BigDecimal fee){
        BigDecimal step = new BigDecimal("100");
        return  fee.multiply(step).intValue();
    }
}
