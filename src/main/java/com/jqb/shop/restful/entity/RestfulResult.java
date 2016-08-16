package com.jqb.shop.restful.entity;

import java.util.List;

/**
 * Created by liubin on 2016/1/21.
 */
public class RestfulResult {
    private int errCode;
    private String result;
    private Object returnObj;
    private List<Object> returnList;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object returnObj) {
        this.returnObj = returnObj;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Object> getReturnList() {
        return returnList;
    }

    public void setReturnList(List<Object> returnList) {
        this.returnList = returnList;
    }
}
