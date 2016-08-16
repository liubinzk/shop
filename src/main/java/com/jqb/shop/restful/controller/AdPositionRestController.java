package com.jqb.shop.restful.controller;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.entity.*;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.beanutils.*;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(value="rest")
public class AdPositionRestController {

    /**
     * Logging for this instance
     */
    private org.apache.commons.logging.Log log = LogFactory.getLog(AdPositionRestController.class);

    /** Used to access properties*/
    private PropertyUtilsBean propertyUtilsBean;

    /** Used to perform conversions between object types when setting properties */
    private ConvertUtilsBean convertUtilsBean;

    @Resource(name = "adPositionServiceImpl")
    private AdPositionService adPositionService;

    @Resource(name = "adServiceImpl")
    private AdService adService;

    @RequestMapping("/adPosition")
    public @ResponseBody  String getMobileAdPosition(Model model, HttpServletRequest request) {
        AdPosition adPosition = null;
        String callback = request.getParameter("callback");
        adPosition = adPositionService.find(11L, null);
        for (Ad ad : adPosition.getAds()) {
            ad.setAdPosition(null);
        }
        JSONArray jb = JSONArray.fromObject(adPosition.getAds() );
        String result = jb.toString();
        if (callback == null) {
             return result;
        } else {
            return callback + "('" +  result + "')";
        }
    }

    @RequestMapping("/ad_promotion")
    public @ResponseBody  String getMobileAdPromotion(Model model, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        String pageNumberStr = request.getParameter("pageNumber");
        String pageSizeStr = request.getParameter("pageSize");


        RestfulResult restfulResult = new RestfulResult();
        Integer pageNumber = 1;
        if (pageNumberStr != null && !"".equals(pageNumberStr)) {
            pageNumber = Integer.parseInt(pageNumberStr);
        }

        Integer pageSize = 10;
        if (pageSizeStr != null && !"".equals(pageSizeStr)) {
            pageSize = Integer.parseInt( pageSizeStr );
        }
        Page<Ad> adPage = null;
        try {
            Pageable pageable = new Pageable(pageNumber, pageSize);
            adPage = adService.findPage(11L,pageable);
            restfulResult.setReturnObj(adPage);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Ad && name.equals("adPosition")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }




    @RequestMapping("/adPosition_back")
    public @ResponseBody  String getMobileAdPositionBackGround(Model model, HttpServletRequest request) {
        AdPosition adPosition = null;
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
//        adPosition = adService.findList();
//        Filter filter = Filter.eq( "adPosition", 3);
//        List<Filter> filterList = new ArrayList<Filter>();
//        filterList.add(filter);
//        List<Ad> adList = adService.findList(0, (int)adService.count(filter), filterList , null);
        adPosition = adPositionService.find(11L, null);
        for (Ad ad : adPosition.getAds()) {
            ad.setAdPosition(null);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setReturnObj(adPosition);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback,restfulResult);
    }

}