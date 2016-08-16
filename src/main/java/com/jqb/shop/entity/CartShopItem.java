package com.jqb.shop.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liubin on 2016/4/7.
 */
public class CartShopItem {

    private  Commercial commercial;

    private  Long commercialId;
    /** 购物车项 */
    private Set<CartItem> cartItems = new HashSet<CartItem>();

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Commercial getCommercial() {
        return commercial;
    }

    public void setCommercial(Commercial commercial) {
        this.commercial = commercial;
    }

    public Long getCommercialId() {
        return commercialId;
    }

    public void setCommercialId(Long commercialId) {
        this.commercialId = commercialId;
    }
}
