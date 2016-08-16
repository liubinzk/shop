package com.jqb.shop.util;

import org.apache.commons.beanutils.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by liubin on 2015/12/29.
 */
public class CustomDateConverter implements Converter {
    public Object convert(Class arg0, Object arg1) {
        String p = (String)arg1;
        if(p== null || p.trim().length()==0){
            return null;
        }
        try{
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse(p.trim());
        }
        catch(Exception e){
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                return df.parse(p.trim());
            } catch (ParseException ex) {
                return null;
            }
        }

    }
}
