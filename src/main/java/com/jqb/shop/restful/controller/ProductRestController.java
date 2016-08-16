package com.jqb.shop.restful.controller;

import com.jqb.shop.*;
import com.jqb.shop.controller.shop.BaseController;
import com.jqb.shop.entity.*;
import com.jqb.shop.entity.Message;
import com.jqb.shop.entity.Order;
import com.jqb.shop.entity.Product.OrderType;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import com.jqb.shop.util.SettingUtils;
import com.jqb.shop.util.WebUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liubin on 2015/12/23.
 */
@Controller
@RequestMapping(value="rest")
public class ProductRestController  {

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

    @Resource(name = "searchServiceImpl")
    private SearchService searchService;

    @Resource(name = "areaServiceImpl")
    private AreaService areaService;

    @Resource(name = "orderItemServiceImpl")
    private OrderItemService orderItemService;



    @RequestMapping(value="/product")
    public @ResponseBody   String getProduct(Model model,HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String productId = request.getParameter("productId");
        Product product = null;
        String result = null;
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            if(productId != null && !"".equals(productId) ){
                long id = 0;
                try {
                    id = Long.parseLong(productId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    id = 0;
                }
                product = productService.find(id);
                if (product.getArea() != null) {
                    product.setAreaName(product.getArea().getFullName());
                    product.setArea(null);
                }
                product.countAmount();
                //introduction
                if (product.getIntroduction() != null){
                    String introduction = product.getIntroduction();
                    if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                        introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                    } else if(product.getIntroduction().indexOf("</") >= 0 || product.getIntroduction().indexOf("/>") >= 0){
                       Map<String,Object> introMpa = CommonUtils.dealProductIntroduction(product.getIntroduction());
                        product.setIntroductionImg((List)introMpa.get(CommonUtils.PRODUCT_IMG_KEY));
                        introduction = (String)introMpa.get(CommonUtils.PRODUCT_INFO_KEY);
                    }
                    Pattern p = Pattern.compile("\t|\r|\n");
                    Matcher m = p.matcher( introduction );
                    introduction = m.replaceAll("");
                    product.setIntroduction(introduction);
                }
                List<Product> siblings =  product.getSiblings();
                if (siblings != null && siblings.size() > 0 ) {
                    for (Product sblingProduct : siblings) {

                        if (sblingProduct.getArea() != null) {
                            sblingProduct.setAreaName(sblingProduct.getArea().getFullName());
                            sblingProduct.setArea(null);
                        }
                        //introduction
                        if (sblingProduct.getIntroduction() != null){
                            String introduction = "";
                            if (sblingProduct.getIntroduction().indexOf("com") > 0 && sblingProduct.getIntroduction().indexOf("com") + 3 < sblingProduct.getIntroduction().length()) {
                                introduction = sblingProduct.getIntroduction().substring(sblingProduct.getIntroduction().indexOf("com") + 3,sblingProduct.getIntroduction().lastIndexOf("\"") );
                            }
                            sblingProduct.setIntroduction(introduction);
                        }
                        sblingProduct.setPromotions(null);
                        sblingProduct.setBrand(null);
                        sblingProduct.setCartItems(null);
                        sblingProduct.setTags(null);
                        sblingProduct.setCartItems(null);
                        sblingProduct.setProductCategory(null);
                        sblingProduct.setConsultations(null);
                        sblingProduct.setFavoriteMembers(null);
                        sblingProduct.setGiftItems(null);
                        sblingProduct.setGoods(null);
                        sblingProduct.setMemberPrice(null);
                        sblingProduct.setOrderItems(null);
                        sblingProduct.setParameterValue(null);
                        sblingProduct.setProductImages(null);
                        sblingProduct.setProductNotifies(null);
                        sblingProduct.setReviews(null);
                        sblingProduct.setProductImages(null);
                        sblingProduct.setSpecificationValues(null);
                        sblingProduct.setSpecifications(null);
                        sblingProduct.setCommercial(null);
                        sblingProduct.setSiblings(null);
                        sblingProduct.initRestEntiy();
                    }
                }

                product.setPromotions(null);
                product.setBrand(null);
                product.setCartItems(null);
                product.setTags(null);
                product.setCartItems(null);
                product.setProductCategory(null);
                product.setConsultations(null);
                product.setFavoriteMembers(null);
                product.setGiftItems(null);
                product.setGoods(null);
                product.setMemberPrice(null);
                product.setOrderItems(null);
                product.setParameterValue(null);
    //            product.setProductImages(null);
                product.setProductNotifies(null);
                product.setReviews(null);
                product.setSpecificationValues(null);
                product.setSpecifications(null);
            }


            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if(obj instanceof Product && name.equals("handler")  ){//Prodect init
                        return true;
                    }else  if(obj instanceof Product && name.equals("hibernateLazyInitializer") ){//Prodect init
                        return true;
                    }
                    else  if(obj instanceof Specification && name.equals("products")){//Prodect init
                        return true;
                    } else  if(obj instanceof SpecificationValue && name.equals("specification")){//Prodect init
                        return true;
                    } else  if(obj instanceof SpecificationValue && name.equals("products")){//Prodect init
                        return true;
                    }else{
                        return false;
                    }

                }
            });
            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

// hibernate lazy
//        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            jsonConfig.setExcludes(new String[]{"handler","hibernateLazyInitializer"});


        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(product);
        return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }

    @RequestMapping(value="/product_list")
    public @ResponseBody   String getProductList(Model model,HttpServletRequest request, Long[] tagIds) {
        String result = "";
        String callback = request.getParameter("callback");
        String areaIdStr = request.getParameter("areaId");
        String brandIdStr = request.getParameter("brandId");
        String pageNumberStr = request.getParameter("pageNumber");
        String pageSizeStr = request.getParameter("pageSize");
        String startPriceStr = request.getParameter("startPrice");
        String endPriceStr = request.getParameter("endPrice");
        String orderTypeStr = request.getParameter("orderType");

        String promotionIdStr = request.getParameter("promotionId");

        long areaId = 0;
        Area area = null;
        if ( StringUtils.isNotBlank(areaIdStr) ) {
            areaId = Integer.parseInt(areaIdStr);
            area = areaService.find(areaId);
        }

        long brandId = 0;
        Brand brand = null;
        if ( StringUtils.isNotBlank(brandIdStr) ) {
            brandId = Integer.parseInt(brandIdStr);
             brand = brandService.find(brandId);
        }

        long promotionId = 0;
        Promotion promotion = null;
        if ( StringUtils.isNotBlank(promotionIdStr) ) {
            promotionId = Integer.parseInt(promotionIdStr);
            promotion = promotionService.find(promotionId);
        }
        List<Tag> tags = null;
        if ( tagIds != null && tagIds.length > 0) {
            tags = tagService.findList(tagIds);
        }

        Integer pageNumber = 1;
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer pageSize = 10;
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt( pageSizeStr );
        }


        BigDecimal startPrice = null;
        if (startPriceStr != null && !"".equals(startPriceStr)) {
            startPrice = new BigDecimal(Integer.parseInt(startPriceStr));
        }


        BigDecimal endPrice = null;
        if (endPriceStr != null && !"".equals(endPriceStr)) {
            endPrice = new BigDecimal(Integer.parseInt(endPriceStr));
        }
        OrderType orderType = null;
        if (orderTypeStr != null && !"".equals(orderTypeStr)) {
            orderType = OrderType.valueOf( orderTypeStr );
        } else {
            orderType =  OrderType.topDesc;
        }

        try {
            Pageable pageable = new Pageable(pageNumber, pageSize);
            Page<Product> pageProducts =   productService.findPage(area, null, brand, promotion, tags, null, startPrice, endPrice, true, true, null, false, null, null, orderType, pageable);

            for (Product product : pageProducts.getContent()) {
                if (product.getArea() != null) {
                    product.setAreaName(product.getArea().getFullName());
                    product.setArea(null);
                }
                //introduction
                if (product.getIntroduction() != null){
                    String introduction = "";
                    if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                        introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                    }
                    product.setIntroduction(introduction);
                }
                product.initRestEntiy();
            }
            JsonConfig jsonConfig = new JsonConfig();  //建立配置文件

            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if (obj instanceof Product && name.equals("handler")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("hibernateLazyInitializer")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("promotions")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("brand")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("cartItems")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("tags")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("cartItems")) {//Prodect init
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
                    } else if (obj instanceof Product && name.equals("productNotifies")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("reviews")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("specificationValues")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("specifications")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("commercial")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && (value == null || value.equals(""))) {//Prodect init
                        return true;
                    } else if (obj instanceof Specification && name.equals("products")) {//Prodect init
                        return true;
                    } else if (obj instanceof SpecificationValue && name.equals("specification")) {//Prodect init
                        return true;
                    } else if (obj instanceof SpecificationValue && name.equals("products")) {//Prodect init
                        return true;
                    } else {
                        return false;
                    }

                }
            });

            JSONArray jb = JSONArray.fromObject(pageProducts, jsonConfig);
            result = jb.toString();
        } catch (Exception e) {
            RestfulResult restfulResult = new RestfulResult();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        if (callback == null) {
            return result;
        } else {
            return callback + "('" +  result + "')";
        }
    }

    @RequestMapping(value="/search")
    public @ResponseBody   String searchProduct(Model model,HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String result = "";
        String callback = request.getParameter("callback");

        String keyword = request.getParameter("keyword");
        String pageNumberStr = request.getParameter("pageNumber");
        String pageSizeStr = request.getParameter("pageSize");
        String startPriceStr = request.getParameter("startPrice");
        String endPriceStr = request.getParameter("endPrice");
        String orderTypeStr = request.getParameter("orderType");


        if (StringUtils.isEmpty(keyword)) {
            result = "not found";
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        Integer pageNumber = null;
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer pageSize = null;
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt( pageSizeStr );
        }


        BigDecimal startPrice = null;
        if (startPriceStr != null && !"".equals(startPriceStr)) {
            startPrice = new BigDecimal(Integer.parseInt(startPriceStr));
        }


        BigDecimal endPrice = null;
        if (endPriceStr != null && !"".equals(endPriceStr)) {
            endPrice = new BigDecimal(Integer.parseInt(endPriceStr));
        }
        OrderType orderType = null;
        if (orderTypeStr != null && !"".equals(orderTypeStr)) {
           //todo
        } else {
            orderType =  OrderType.topDesc;
        }
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Pageable pageable = new Pageable(pageNumber, pageSize);
            Page<Product> pageProducts =  searchService.search(keyword, startPrice, endPrice, orderType, pageable);

            for (Product product : pageProducts.getContent()) {
                if (product.getArea() != null) {
                    product.setAreaName(product.getArea().getFullName());
                    product.setArea(null);
                }
                //introduction
                if (product.getIntroduction() != null){
                    String introduction = "";
                    if (product.getIntroduction().indexOf("com") > 0 && product.getIntroduction().indexOf("com") + 3 < product.getIntroduction().length()) {
                        introduction = product.getIntroduction().substring(product.getIntroduction().indexOf("com") + 3,product.getIntroduction().lastIndexOf("\"") );
                    }
                    product.setIntroduction(introduction);
                }
                product.initRestEntiy();
            }
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setReturnObj(pageProducts);

            jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略

            jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object obj, String name, Object value) {
                    if (obj instanceof Product && name.equals("handler")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("hibernateLazyInitializer")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("promotions")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("brand")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("cartItems")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("tags")) {//Prodect init
                        return true;
                    } else if (obj instanceof Product && name.equals("cartItems")) {//Prodect init
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
                    } else if (obj instanceof Product && (value==null || value.equals(""))) {//Prodect init
                        return true;
                    } else if (obj instanceof Specification && name.equals("products")) {//Prodect init
                        return true;
                    } else if (obj instanceof SpecificationValue && name.equals("specification")) {//Prodect init
                        return true;
                    } else if (obj instanceof SpecificationValue && name.equals("products")) {//Prodect init
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
       return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/mobilecart/pre_add", method = RequestMethod.GET)
    public @ResponseBody String preAdd2Cart(Long id, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String result="";

        try {
            Member member = null;
            member = memberService.getCurrent();
            Product product = productService.find(id);
            if (product.getIsRestriction()) {
                OrderResponder orderResponder = productService.checkProduct(member,product);
                if(!orderResponder.isValide()){
                    return CommonUtils.returnRestfulResult(callback,orderResponder.getRestfulResult());
                }
            }
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }

        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        return returnRestfulResult(callback, restfulResult);
    }
    /**
     * 添加
     */
    @RequestMapping(value = "/mobilecart/add", method = RequestMethod.GET)
    public @ResponseBody String add(Long id, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
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
        member = memberService.getCurrent();
        cart = cartService.getMobileCurrent();

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

            if (product.getIsRestriction()) {
                if (cart!=null && cart.contains(product)) {
                    CartItem cartItem = cart.getCartItem(product);
                    if(cartItem.getQuantity()+1 > product.getRestrictionNum()){
                        result = "此商品限购"+product.getRestrictionNum()+"份，已达到限购数量，不能购买！";
                        restfulResult.setResult(result);
                        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_PORDUCT_RESTRICTION_FAIL);
                        return returnRestfulResult(callback, restfulResult);
                    }
                }
                OrderResponder orderResponder = productService.checkProduct(member,product);
                if(!orderResponder.isValide()){
                    return CommonUtils.returnRestfulResult(callback,orderResponder.getRestfulResult());
                }
            }

            if (cart == null) {
                cart = new Cart();
                cart.setKey(UUID.randomUUID().toString() + DigestUtils.md5Hex(RandomStringUtils.randomAlphabetic(30)));
                cart.setMember(member);
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
            return returnRestfulResult(callback, restfulResult);
        }

        if (member == null) {
//            WebUtils.addCookie(request, response, Cart.ID_COOKIE_NAME, cart.getId().toString(), Cart.TIMEOUT);
//            WebUtils.addCookie(request, response, Cart.KEY_COOKIE_NAME, cart.getKey(), Cart.TIMEOUT);
            request.getSession().setAttribute(Cart.ID_COOKIE_NAME, cart.getId().toString());
            request.getSession().setAttribute(Cart.KEY_COOKIE_NAME, cart.getKey());
        }
        //result = com.jqb.shop.Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)).getContent();
        result = ""+cart.getQuantity();
        Cart returnCart = new Cart();
        returnCart.setId(cart.getId());
        restfulResult.setErrCode(1);
        restfulResult.setResult(result);
        restfulResult.setReturnObj(returnCart);
        return returnRestfulResult(callback, restfulResult);
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/mobilecart/removeWare")
    public @ResponseBody String removeWareFromCart(Long id, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
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
            cart = cartService.getMobileCurrent();
            member = memberService.getCurrent();

            if (cart == null || cart.isEmpty()) {
                result =  com.jqb.shop.Message.error("shop.cart.notEmpty").getContent();
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
                restfulResult.setResult(result);
                return returnRestfulResult(callback, restfulResult);
            }
            CartItem cartItem = cartItemService.find(id);
            Set<CartItem> cartItems = cart.getCartItems();
            if (cartItem == null || cartItems == null || !cartItems.contains(cartItem)) {
                result =  com.jqb.shop.Message.error("shop.cart.cartItemNotExsit").getContent();
            }
            cartItems.remove(cartItem);
            cartItemService.delete(cartItem);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        if (member == null) {
            WebUtils.addCookie(request, response, Cart.ID_COOKIE_NAME, cart.getId().toString(), Cart.TIMEOUT);
            WebUtils.addCookie(request, response, Cart.KEY_COOKIE_NAME, cart.getKey(), Cart.TIMEOUT);
        }
        //result = com.jqb.shop.Message.success("shop.cart.addSuccess", cart.getQuantity(), currency(cart.getEffectivePrice(), true, false)).getContent();
        result = ""+cart.getId();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult(result);
        return returnRestfulResult(callback, restfulResult);
    }

    /**
     * 选择支付
     */
    @RequestMapping(value = "/mobilecart/selectWare")
    public @ResponseBody String selectCartWare(Long id, Boolean toPay, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String result = "";
//        if (quantity == null || quantity < 1) {
//            result =   com.jqb.shop.Message.error("shop.message.error").getContent();
//            return returnResult(callback, result);
//        }
        Cart cart = null;
        Member member = null;
        try {
            cart = cartService.getMobileCurrent();
            member = memberService.getCurrent();

            if (cart == null || cart.isEmpty()) {
                result =  com.jqb.shop.Message.error("shop.cart.notEmpty").getContent();
            }
            id = Math.abs(id);
            CartItem cartItem = cartItemService.find(id);
            Set<CartItem> cartItems = cart.getCartItems();
            if (cartItem == null || cartItems == null || !cartItems.contains(cartItem)) {
                result =  com.jqb.shop.Message.error("shop.cart.cartItemNotExsit").getContent();
                restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_CART_EMPTY);
                restfulResult.setResult(result);
                return returnRestfulResult(callback, restfulResult);
            }
            if(quantity !=null && quantity > 0){
                cartItem.setQuantity(quantity);
            }
            if(toPay!=null){
                cartItem.setToPay(toPay);
            }
            cartItemService.update(cartItem);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        if (member == null) {
            WebUtils.addCookie(request, response, Cart.ID_COOKIE_NAME, cart.getId().toString(), Cart.TIMEOUT);
            WebUtils.addCookie(request, response, Cart.KEY_COOKIE_NAME, cart.getKey(), Cart.TIMEOUT);
        }
        result = ""+cart.getId();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult(result);
        return returnRestfulResult(callback, restfulResult);
    }

    /**
     * 选择全部
     */
    @RequestMapping(value = "/mobilecart/selectWare_all")
    public @ResponseBody String selectAllWare(boolean toPay, HttpServletRequest request, HttpServletResponse response) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String result = "";
        Cart cart = null;
        Member member = null;
        try {
            cart = cartService.getMobileCurrent();
            member = memberService.getCurrent();

            if (cart == null || cart.isEmpty()) {
                result =  com.jqb.shop.Message.error("shop.cart.notEmpty").getContent();
            }
            Set<CartItem> cartItems = cart.getCartItems();
            if (cartItems == null || cartItems.size()>0) {
              for (CartItem cartItem : cartItems) {
                  cartItem.setToPay(toPay);
                  cartItemService.update(cartItem);
              }
            }
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        if (member == null) {
            WebUtils.addCookie(request, response, Cart.ID_COOKIE_NAME, cart.getId().toString(), Cart.TIMEOUT);
            WebUtils.addCookie(request, response, Cart.KEY_COOKIE_NAME, cart.getKey(), Cart.TIMEOUT);
        }
        result = ""+cart.getId();
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult(result);
        return returnRestfulResult(callback, restfulResult);
    }

    /**
     * 选择全部
     */
    @RequestMapping(value = "/mobilecart/ware_number")
    public @ResponseBody String getCartWareCount(HttpServletRequest request, HttpServletResponse response) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String result = "";
        Cart cart = null;
        Member member = null;
        try {
            cart = cartService.getCurrent();
            member = memberService.getCurrent();

            if (cart == null || cart.isEmpty()) {
                result = "0";
            } else {
                result = ""+cart.getQuantity();
            }
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult(result);
        return returnRestfulResult(callback, restfulResult);
    }

    private String returnRestfulResult(String callback, RestfulResult restfulResult) {
        JSONArray jb = JSONArray.fromObject(restfulResult);
        String result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" + result + "')";
        }
    }

    /**
     * 货币格式化
     *
     * @param amount
     *            金额
     * @param showSign
     *            显示标志
     * @param showUnit
     *            显示单位
     * @return 货币格式化
     */
    protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
        Setting setting = SettingUtils.get();
        String price = setting.setScale(amount).toString();
        if (showSign) {
            price = setting.getCurrencySign() + price;
        }
        if (showUnit) {
            price += setting.getCurrencyUnit();
        }
        return price;
    }



}
