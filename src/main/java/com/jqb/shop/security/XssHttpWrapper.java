package com.jqb.shop.security;

import com.blogspot.radialmind.html.HTMLParser;
import com.blogspot.radialmind.xss.XSSFilter;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.util.HtmlUtils;
 
/**
 * XSS保护
 *
 * @author storezhang
 */
public class XssHttpWrapper extends HttpServletRequestWrapper {
 
    private HttpServletRequest orgRequest;
 
    public XssHttpWrapper(HttpServletRequest request) {
        super(request);
        orgRequest = request;
    }
 
    /**
     * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(xssEncode(name));
        if (value != null) {
            value = xssEncode(value);
        }
        return value;
    }
    public String[] getParameterValues(String name) {
    	String[] values = super.getParameterValues(xssEncode(name));
        if (values != null) {
        	for(int i=0;i<values.length;i++){
        		 values[i] = xssEncode(values[i]);
        	}
        }
        return values;
    }

 
//    /**由于StringEscapeUtils在commons-lang-2.6.jar中会把'/'转换为‘\\/’,所以头部先不做过滤
//     * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
//     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/> getHeaderNames 也可能需要覆盖
//     */
//    @Override
//    public String getHeader(String name) {
//        String value = super.getHeader(xssEncode(name));
//        if (value != null) {
//            value = xssEncode(value);
//        }
//        return value;
//    }
 
    /**
     * 将容易引起xss漏洞的半角字符直接替换成全角字符
     *
     * @param s
     * @return
     */
    private static String xssEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
 
        StringReader reader = new StringReader(s);
        StringWriter writer = new StringWriter();
        try {
        	
            HTMLParser.process(reader, writer, new XSSFilter(), true);
            return writer.toString();
        } catch (NullPointerException e) {
        	s=HtmlUtils.htmlEscape(s);//用StringEscapeUtils会把中文转换unicode
//        	s=StringEscapeUtils.escapeHtml(s);
//        	s=StringEscapeUtils.escapeJavaScript(s);
//        	s=StringEscapeUtils.escapeJava(s);
//        	s=StringEscapeUtils.escapeSql(s);
//        	s=StringEscapeUtils.escapeXml(s);
        	
            return s;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }
 
    /**
     * 获取最原始的request
     *
     * @return
     */
    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }
 
    /**
     * 获取最原始的request的静态方法
     *
     * @return
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
        if (req instanceof XssHttpWrapper) {
            return ((XssHttpWrapper) req).getOrgRequest();
        }
        return req;
    }
}