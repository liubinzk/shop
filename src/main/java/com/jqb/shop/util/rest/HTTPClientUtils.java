package com.jqb.shop.util.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;

public class HTTPClientUtils {
	
	private static DefaultHttpClient httpClient;
	

	
	
	
	 public static HttpClient getHttpClient() {
//		    httpClient.getCredentialsProvider().setCredentials(
//	        new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
		if(httpClient == null ){
			 httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
		}
	    return httpClient;
	  }

     public static String sendRequest(String postUrl, String param) {
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = null;
        httpPost = new HttpPost(postUrl);
        StringEntity entity = null;
        try {
            entity = new StringEntity(param, "text/html",
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 设置类型
        entity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(entity);
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = httpResponse.getEntity();
        if (resEntity != null) {
            try {
                result = EntityUtils.toString(resEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String sendRequest(String postUrl,List<NameValuePair> paramPair) {
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = null;
        httpPost = new HttpPost(postUrl);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(paramPair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = httpResponse.getEntity();
        if (resEntity != null) {
            try {
                result = EntityUtils.toString(resEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
