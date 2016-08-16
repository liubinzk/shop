package com.jqb.shop.entity;

import com.jqb.shop.restful.entity.RestfulResult;

import java.util.List;

/**
 * Created by liubin on 2016/4/28.
 */
public class OrderResponder {
    private  boolean valide;
    private RestfulResult restfulResult;

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public RestfulResult getRestfulResult() {
        return restfulResult;
    }

    public void setRestfulResult(RestfulResult restfulResult) {
        this.restfulResult = restfulResult;
    }
}
