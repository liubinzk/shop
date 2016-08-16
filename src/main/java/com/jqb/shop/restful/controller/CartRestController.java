package com.jqb.shop.restful.controller;

import com.jqb.shop.Principal;
import com.jqb.shop.entity.*;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by liubin on 2015/12/23.
 */
@Controller
@RequestMapping(value="rest")
public class CartRestController {

    @Resource(name = "promotionServiceImpl")
    private PromotionService promotionService;
    @Resource(name = "productServiceImpl")
    private ProductService productService;
    @Resource(name = "productCategoryServiceImpl")
    private ProductCategoryService productCategoryService;
    @Resource(name = "brandServiceImpl")
    private BrandService brandService;
    @Resource(name = "attributeServiceImpl")
    private AttributeService attributeService;
    @Resource(name = "tagServiceImpl")
    private TagService tagService;

    @Resource(name = "cartServiceImpl")
    private CartService cartService;

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;
    @Resource(name = "cartItemServiceImpl")
    private CartItemService cartItemService;

    @RequestMapping(value = "/cart")
    public
    @ResponseBody
    String getShoppingCart(Model model, HttpServletRequest request) {
//        request.getSession().setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME,
//                new Principal(12L, "zhu"));
        String callback = request.getParameter("callback");
        RestfulResult restfulResult = new RestfulResult();

        Cart cart = null;
        try {
            cart = cartService.getMobileCurrent();
            if (cart == null) {
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_CART_EMPTY);
                restfulResult.setReturnObj(cart);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (cart != null && cart.getMember() != null) {
                Member member = new Member();
                cart.getMember().initRestEntiy();
                member.setId(cart.getMember().getId());
                member.setIsLocked(cart.getMember().getIsLocked());
                member.setUsername(cart.getMember().getUsername());
                member.initRestEntiy();
                cart.setMember(null);
            }
            if (cart != null && cart.getCartItems() != null) {
                for (CartItem cartItem : cart.getCartItems()) {

                    Product product = new Product();
                    product.setProductImages(cartItem.getProduct().getProductImages());
                    product.setId(cartItem.getProduct().getId());
                    product.setAreaName(cartItem.getProduct().getAreaName());
                    product.setFullName(cartItem.getProduct().getFullName());
                    product.setPrice(cartItem.getProduct().getPrice());
                    product.setUnit(cartItem.getProduct().getUnit());
                    product.setImage(cartItem.getProduct().getImage());
                    product.setSn(cartItem.getProduct().getSn());
                    product.setTotalScore(cartItem.getProduct().getTotalScore());
                    product.setSeoTitle(cartItem.getProduct().getSeoTitle());
                    product.setSeoDescription(cartItem.getProduct().getSeoDescription());
                    product.setSeoKeywords(cartItem.getProduct().getSeoKeywords());
                    product.setCommercial(cartItem.getProduct().getCommercial());
                    product.setIsPayShipping(cartItem.getProduct().getIsPayShipping());
                    product.setShippingPrice(cartItem.getProduct().getShippingPrice());
                    product.setIsRestriction(cartItem.getProduct().getIsRestriction());
                    product.setRestrictionNum( cartItem.getProduct().getRestrictionNum() );
                    product.setSiblings(null);
                    product.initRestEntiy();
                    cartItem.setCart(null);
                    cartItem.setProduct(product);
                    cartItem.getProduct().initRestEntiy();
                }
            }
            cart.preGetCartShopItems();
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Cart && name.equals("handler")) {//Prodect init
                    return true;
                } else if (obj instanceof Cart && name.equals("hibernateLazyInitializer")) {//Prodect init
                    return true;
                } else if (obj instanceof CartItem && name.equals("cart")) {//Prodect init
                    return true;
                }  else if (obj instanceof Product && name.equals("cartItems")) {//Prodect init
                    return true;
                }  else if (obj instanceof Product && name.equals("promotions")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("brand")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("tags")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("productCategory")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("consultations")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("favoriteMembers")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("giftItems")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("goods")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("memberPrice")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("orderItems")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("parameterValue")) {//Prodect init
                    return true;
                }  else if (obj instanceof Product && name.equals("productNotifies")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("reviews")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("specificationValues")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("specifications")) {//Prodect init
                    return true;
                }  else if (obj instanceof Product && name.equals("commercial")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("siblings")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && (value==null || value.equals(""))) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("productImages")) {//Prodect init
                    return true;
                } else if (obj instanceof Product && name.equals("area")) {//Prodect init
                    return true;
                } else {
                    return false;
                }

            }
        });
        jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer"});
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(cart);
        return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }

    @RequestMapping(value = "/mobilecart/immediate_add")
    public
    @ResponseBody
    String addImmediateCart(Long id, Integer quantity, HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String username = request.getParameter("username");
        String result = "";
        if (quantity == null || quantity < 1) {
            result =   com.jqb.shop.Message.error("shop.message.error").getContent();
            restfulResult.setResult(result);
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        Cart cart = null;
        Member member = null;
        try {
            Product product = productService.find(id);
            if (product == null) {
                return com.jqb.shop.Message.warn("shop.cart.productNotExsit").getContent();
            }
            if (!product.getIsMarketable()) {
                result = com.jqb.shop.Message.warn("shop.cart.productNotMarketable").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }
            if (product.getIsGift()) {
                result = com.jqb.shop.Message.warn("shop.cart.notForSale").getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);
            }

            member = memberService.getCurrent();

            if (cart == null) {
                cart = new Cart();
                cart.setKey(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
                cart.setMember(null);
                cartService.save(cart);
                //            cart = cartService.getCurrent();
                //result = com.jqb.shop.Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)).getContent();
                if(cart != null) {
                    result = ""+cart.getId();
                }
            }

            if (Cart.MAX_PRODUCT_COUNT != null && cart.getCartItems().size() >= Cart.MAX_PRODUCT_COUNT) {
                    result =  com.jqb.shop.Message.warn("shop.cart.addCountNotAllowed", Cart.MAX_PRODUCT_COUNT).getContent();
                restfulResult.setResult(result);
                return CommonUtils.returnRestfulResult(callback, restfulResult);

            }

            if (cart.contains(product)) {
                CartItem cartItem = cart.getCartItem(product);
                if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() + quantity > CartItem.MAX_QUANTITY) {
                    result = com.jqb.shop.Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY).getContent();
                    restfulResult.setResult(result);
                    return CommonUtils.returnRestfulResult(callback, restfulResult);
                }
                if (product.getStock() != null && cartItem.getQuantity() + quantity > product.getAvailableStock()) {
                    result = com.jqb.shop.Message.warn("shop.cart.productLowStock").getContent();
                    restfulResult.setResult(result);
                    return CommonUtils.returnRestfulResult(callback, restfulResult);
                }
                cartItem.add(quantity);
                cartItemService.update(cartItem);
            } else {
                if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
                    result = com.jqb.shop.Message.warn("shop.cart.maxCartItemQuantity", CartItem.MAX_QUANTITY).getContent();
                    restfulResult.setResult(result);
                    return CommonUtils.returnRestfulResult(callback, restfulResult);
                }
                if (product.getStock() != null && quantity > product.getAvailableStock()) {
                    result = com.jqb.shop.Message.warn("shop.cart.productLowStock").getContent();
                    restfulResult.setResult(result);
                    return CommonUtils.returnRestfulResult(callback, restfulResult);
                }
                CartItem cartItem = new CartItem();
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cartItemService.save(cartItem);
                cart.getCartItems().add(cartItem);
            }
        } catch (Exception e) {

            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            restfulResult.setResult(result);
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }

        //result = com.jqb.shop.Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)).getContent();
        result = ""+cart.getQuantity();
        Cart returnCart = new Cart();
        returnCart.setId(cart.getId());

        restfulResult.setErrCode(1);
        restfulResult.setResult(result);
        restfulResult.setReturnObj(returnCart);
        return CommonUtils.returnRestfulResult(callback, restfulResult);
    }


}
