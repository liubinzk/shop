/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.jqb.shop.Principal;
import com.jqb.shop.dao.CartDao;
import com.jqb.shop.dao.CartItemDao;
import com.jqb.shop.dao.MemberDao;
import com.jqb.shop.entity.Cart;
import com.jqb.shop.entity.CartItem;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Product;
import com.jqb.shop.service.CartService;
import com.jqb.shop.util.WebUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service - 购物车
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("cartServiceImpl")
public class CartServiceImpl extends BaseServiceImpl<Cart, Long> implements CartService {

	@Resource(name = "cartDaoImpl")
	private CartDao cartDao;
	@Resource(name = "cartItemDaoImpl")
	private CartItemDao cartItemDao;
	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "cartDaoImpl")
	public void setBaseDao(CartDao cartDao) {
		super.setBaseDao(cartDao);
	}

	public Cart getCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			Member member = principal != null ? memberDao.find(principal.getId()) : null;
			if (member != null) {
				Cart cart = member.getCart();
				if (cart != null) {
					if (!cart.hasExpired()) {
						if (!DateUtils.isSameDay(cart.getModifyDate(), new Date())) {
							cart.setModifyDate(new Date());
							cartDao.merge(cart);
						}
						return cart;
					} else {
						cartDao.remove(cart);
					}
				}
			} else {
				String id = WebUtils.getCookie(request, Cart.ID_COOKIE_NAME);
				String key = WebUtils.getCookie(request, Cart.KEY_COOKIE_NAME);
				if (StringUtils.isNotEmpty(id) && StringUtils.isNumeric(id) && StringUtils.isNotEmpty(key)) {
					Cart cart = cartDao.find(Long.valueOf(id));
					if (cart != null && cart.getMember() == null && StringUtils.equals(cart.getKey(), key)) {
						if (!cart.hasExpired()) {
							if (!DateUtils.isSameDay(cart.getModifyDate(), new Date())) {
								cart.setModifyDate(new Date());
								cartDao.merge(cart);
							}
							return cart;
						} else {
							cartDao.remove(cart);
						}
					}
				}
			}
		}
		return null;
	}

	public Cart getMobileCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			Member member = null;
			try {
				member = principal != null ? memberDao.find(principal.getId()) : null;
			} catch (Exception e) {
				if(e.getCause().toString().contains("More than one row with the given identifier")){
					long memberId = principal.getId();
					List<Cart> duplCart = this.findDuplCart( memberId );
					if(duplCart != null && duplCart.size()>1){
						for(int k=0; k<duplCart.size()-1; k++){
							this.evictExpired(duplCart.get(k).getId() , memberId);
						}
					}
				}
				member = memberDao.find(principal.getId());
			}
			if (member != null) {
				Cart cart = member.getCart();
				if (cart != null) {
					if (!cart.hasExpired()) {
						if (!DateUtils.isSameDay(cart.getModifyDate(), new Date())) {
							cart.setModifyDate(new Date());
							cartDao.merge(cart);
						}
						return cart;
					} else {
						cartDao.remove(cart);
					}
				}
			} else {
				//String id = WebUtils.getCookie(request, Cart.ID_COOKIE_NAME);//request.getParameter("cartId"); //
				//String key = WebUtils.getCookie(request, Cart.KEY_COOKIE_NAME);// request.getParameter("cartKey");//

				//get from session
				String id = (String) request.getSession().getAttribute(Cart.ID_COOKIE_NAME);
				String key = (String) request.getSession().getAttribute(Cart.KEY_COOKIE_NAME);
				if (StringUtils.isNotEmpty(id) && StringUtils.isNumeric(id) && StringUtils.isNotEmpty(key)) {
					Cart cart = cartDao.find(Long.valueOf(id));
					if (cart != null && cart.getMember() == null && StringUtils.equals(cart.getKey(), key)) {
						if (!cart.hasExpired()) {
							if (!DateUtils.isSameDay(cart.getModifyDate(), new Date())) {
								cart.setModifyDate(new Date());
								cartDao.merge(cart);
							}
							return cart;
						} else {
							cartDao.remove(cart);
						}
					}
				}


			}
		}
		return null;
	}

	public void merge(Member member, Cart cart) {
		if (member != null && cart != null && cart.getMember() == null) {
			Cart memberCart = member.getCart();
			if (memberCart != null) {
				for (Iterator<CartItem> iterator = cart.getCartItems().iterator(); iterator.hasNext();) {
					CartItem cartItem = iterator.next();
					Product product = cartItem.getProduct();
					if (memberCart.contains(product)) {
						if (Cart.MAX_PRODUCT_COUNT != null && memberCart.getCartItems().size() > Cart.MAX_PRODUCT_COUNT) {
							continue;
						}
						CartItem item = memberCart.getCartItem(product);
						item.add(cartItem.getQuantity());
						cartItemDao.merge(item);
					} else {
						if (Cart.MAX_PRODUCT_COUNT != null && memberCart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
							continue;
						}
						iterator.remove();
						cartItem.setCart(memberCart);
						memberCart.getCartItems().add(cartItem);
						cartItemDao.merge(cartItem);
					}
				}
				cartDao.remove(cart);
			} else {
				member.setCart(cart);
				cart.setMember(member);
				cartDao.merge(cart);
			}
		}
	}

	public void evictExpired() {
		cartDao.evictExpired();
	}
	public List<Cart> findExpired(){
		return cartDao.findExpired();
	}
	public void evictExpired(long cartId, long memberId) {
		cartDao.evictExpired(cartId);
		if(memberId != 0){
			Member member = memberDao.find(memberId);
			member.setCart(null);
			memberDao.merge(member);
		}
	}
	public List<Cart> findDuplCart(long memberId){
		return cartDao.findDuplCart(memberId);
	}
}
