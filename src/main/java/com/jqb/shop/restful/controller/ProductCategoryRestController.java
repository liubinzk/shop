package com.jqb.shop.restful.controller;

import com.jqb.shop.entity.*;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import com.jqb.shop.util.CommonUtils;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by liubin on 2015/12/23.
 */
@Controller
@RequestMapping(value="rest")
public class ProductCategoryRestController {


    @Resource(name = "productCategoryServiceImpl")
    private ProductCategoryService productCategoryService;
    @Resource(name = "brandServiceImpl")
    private BrandService brandService;



    @RequestMapping(value = "/hot")
    public
    @ResponseBody
    String getHotCategoryList(Model model, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        RestfulResult restfulResult = new RestfulResult();

        try {
            List<ProductCategory> productCategoryList = productCategoryService.findRoots();
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setReturnObj(productCategoryList);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return CommonUtils.returnRestfulResult(callback, restfulResult);
        }

        JsonConfig jsonConfig = new JsonConfig();  //建立配置文件
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof ProductCategory && name.equals("parent")) {
                    return true;
                } else if (obj instanceof ProductCategory && name.equals("products")) {
                    return true;
                } else if (obj instanceof ProductCategory && name.equals("brands")) {
                    return true;
                } else if (obj instanceof ProductCategory && name.equals("parameterGroups")) {
                    return true;
                } else if (obj instanceof ProductCategory && name.equals("attributes")) {
                    return true;
                } else if (obj instanceof ProductCategory && name.equals("promotions")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        jsonConfig.setIgnoreDefaultExcludes(false);  //设置默认忽略
        jsonConfig.setExcludes(new String[]{"handler", "hibernateLazyInitializer"});
        return CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }


}
