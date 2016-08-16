import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jqb.shop.entity.Cart;
import com.jqb.shop.util.CommonUtils;
import com.jqb.shop.util.DateFormateUtil;
import org.apache.commons.codec.digest.DigestUtils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sun.security.provider.MD5;


public class Test {
    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "/upload/\"?(.*?)(\"|>|\\s+)";
	   
    /* 
     * 1.一个运用基本类的实例 
     * MessageDigest 对象开始被初始化。该对象通过使用 update 方法处理数据。 
     * 任何时候都可以调用 reset 方法重置摘要。 
     * 一旦所有需要更新的数据都已经被更新了，应该调用 digest 方法之一完成哈希计算。 
     * 对于给定数量的更新数据，digest 方法只能被调用一次。 
     * 在调用 digest 之后，MessageDigest 对象被重新设置成其初始状态。  
     */  
    public String encrypByMd5(String context) {  
    	StringBuffer buf = new StringBuffer("");  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(context.getBytes());//update处理  
            byte [] encryContext = md.digest();//调用该方法完成计算  
  
            int i;  
            for (int offset = 0; offset < encryContext.length; offset++) {//做相应的转化（十六进制）  
                i = encryContext[offset];  
                if (i < 0) i += 256;  
                if (i < 16) buf.append("0");  
                buf.append(Integer.toHexString(i));  
           }  
           System.out.println("32result: " + buf.toString());// 32位的加密  
           System.out.println("16result: " + buf.toString().substring(8, 24));// 16位的加密  
        } catch (NoSuchAlgorithmException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return buf.toString();
    }  
      
    /* 
     * 2.使用开发的jar直接应用 
     *  使用外部的jar包中的类：import org.apache.commons.codec.digest.DigestUtils; 
     *  对上面内容的一个封装使用方便 
     */  
    public String encrypByMd5Jar(String context) {  
        String md5Str = DigestUtils.md5Hex(context);  
        System.out.println("32result: " + md5Str);      
        return md5Str;
    }  
  
    public static void main(String[] args) {
     testLog4j();
    }

    private String dealString(String str){
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

    public  String[] dealProductIntroduction(String introduction){
        String[] introductions = new String[2];
        String reg = "img";
        //IMGURL_REG
        Pattern p = Pattern.compile(IMGURL_REG);
        Matcher matcher = p.matcher(introduction);
        String img="";
        String info=introduction;
        if (matcher.find()) {
            img = matcher.group();
            info = matcher.replaceAll("");
            Matcher srcMatcher = Pattern.compile(IMGSRC_REG).matcher(img);
            if(srcMatcher.find()){
                img = srcMatcher.group();
                if(img.indexOf("\"") >=0 ){
                    img = img.replaceAll("\"","");
                }
            }
        }
        if(info.indexOf("</") >= 0){
            info = dealString(info);
        }
        introductions[0] = img;
        introductions[1] = info;
        return introductions ;
    }

    public static void testLog4j(){
        PropertyConfigurator.configure("D:\\ideawork\\shop\\src\\main\\resources\\log4j.properties");
        Logger logger  =  Logger.getLogger(Test.class);
        logger.debug( " debug " );
        logger.error( " error " );
    }


}
