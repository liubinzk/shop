package com.jqb.shop.entity.user;

import org.apache.poi.ss.formula.functions.T;

/**
 * Created by liubin on 2016/1/22.
 */
public class JQBPlatEntity {
    private int status;
    private String msg;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
