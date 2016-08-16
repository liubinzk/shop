package com.jqb.shop.restful.controller;

import com.jqb.shop.Filter;
import com.jqb.shop.entity.*;
import com.jqb.shop.entity.Product.OrderType;
import com.jqb.shop.Filter;
import com.jqb.shop.Order;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.Product.OrderType;
import com.jqb.shop.restful.entity.RestPromotion;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 2015/12/23.
 */
@Controller
@RequestMapping(value="rest")
public class PromotionRestController {

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
    @Resource(name = "adServiceImpl")
    private AdService adService;

    @RequestMapping(value="/promotion")
    public @ResponseBody   Object getPromotionList(Model model,HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        String promotionIdstr = request.getParameter("promotionId");
        String adIdstr = request.getParameter("adId");
        String pageNumberStr = request.getParameter("pageNumber");
        String pageSizeStr = request.getParameter("pageSize");
        String orderTypeStr = request.getParameter("orderType");

        long adId = 0;
        if (adIdstr != null && !"".equals(adIdstr)) {
            adId = Long.parseLong(adIdstr);
        }
        //promotion began and not end count=2
        // promotionService.findList(true,false, 3, null,null)
        List<Promotion> promotionList =  new ArrayList<Promotion>() ;

//        productService.f
        //

        String result = null;
        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        try {
            Ad ad = adService.find(adId);
            if(ad != null){
                ad.setAdPosition(null);
                List<Object> adList = new ArrayList<Object>();
                adList.add(ad);
                restfulResult.setReturnList(adList);
            }
            long promotionId = 0L;
            if (promotionIdstr != null && !"".equals(promotionIdstr)) {
                if(promotionIdstr.indexOf(",") >= 0){
                    String[] ids = promotionIdstr.split(",");
                    for (String idStr : ids ){
                        promotionId = Long.parseLong(idStr);
                        promotionList.add( promotionService.find(promotionId));
                    }
                } else {
                    promotionId = Long.parseLong(promotionIdstr);
                    promotionList.add( promotionService.find(promotionId));
                }
            }
            long productCategoryId = -1L;
            long brandId = 0;
            long tagId = 0;

            boolean isMarketable = true;
            boolean isList = true;
            boolean isTop = true;
            boolean isGift = false;
            boolean isOutOfStock = false;
            boolean isStockAlert = false;
            ProductCategory productCategory = productCategoryService.find(productCategoryId);
            Brand brand = brandService.find(brandId);
            Map<Attribute, String> attributeValueMap = new HashMap<Attribute, String>();

            List<Tag> tags = tagService.findList(tagId);
            Page<Product> productPage = new Page<Product>();
            for (Promotion promotion  : promotionList) {
                if(promotion != null){
                    Integer count = 5;
                    BigDecimal startPrice = null;
                    BigDecimal endPrice  = null;
                    List<Filter> filters = null;
                    List<Order> orders = new ArrayList<Order>();
                    OrderType orderType = null;
                    if (orderTypeStr != null && !"".equals(orderTypeStr)) {
                        orderType = OrderType.valueOf( orderTypeStr );
                    } else {
                        orderType =  OrderType.topDesc;
                    }
                    Pageable pageable = new Pageable(1, 20);
                    productPage = productService
                            .findPage(null, null, promotion, null, null, startPrice, endPrice, true, true, null, false, null, null, orderType, pageable);

                }
            }
            for (Product product : productPage.getContent()) {
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
            restfulResult.setReturnObj(productPage);
        } catch (Exception e) {

            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        return CommonUtils.returnRestfulResult(callback, restfulResult,jsonConfig);
    }


}
