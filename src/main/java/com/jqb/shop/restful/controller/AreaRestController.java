package com.jqb.shop.restful.controller;

import com.jqb.shop.CommonAttributes;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.Principal;
import com.jqb.shop.entity.*;
import com.jqb.shop.plugin.wxpay.api.CommonUtils;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value="rest")
public class AreaRestController {

    /**
     * Logging for this instance
     */
    private org.apache.commons.logging.Log log = LogFactory.getLog(AreaRestController.class);

    @Resource(name = "areaServiceImpl")
    private AreaService areaService;

    @Resource(name = "productServiceImpl")
    private ProductService productService;


    /** 树路径分隔符 */
    private static final String TREE_PATH_SEPARATOR = ",";


    @RequestMapping("/product_areas")
    public @ResponseBody  String getProductAreaList(Model model, HttpServletRequest request) {
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
        Page<Area> areaPage = null;
        try {
            Pageable pageable = new Pageable(pageNumber, pageSize);
            areaPage = areaService.findLvl2Page(pageable);
            // check product ...
            restfulResult.setReturnObj(areaPage);
        } catch (Exception e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setResult(e.toString());
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
        }
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object obj, String name, Object value) {
                if (obj instanceof Area && name.equals("parent")) {
                    return true;
                } else if (obj instanceof Area && name.equals("children")) {
                    return true;
                } else if (obj instanceof Area && name.equals("members")) {
                    return true;
                } else if (obj instanceof Area && name.equals("receivers")) {
                    return true;
                } else if (obj instanceof Area && name.equals("orders")) {
                    return true;
                } else if (obj instanceof Area && name.equals("deliveryCenters")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult, jsonConfig);
    }

    @RequestMapping("/arealist")
    public @ResponseBody  String getAreaList(Model model, HttpServletRequest request) {
        AdPosition adPosition = null;
        String callback = request.getParameter("callback");

//        List<Area> areaList =  areaService.findRoots();
//        Map<String, Area> areaMap = new HashMap<String, Area>();
//        Map<String, Area> provinceMap = new HashMap<String, Area>();
//        StringBuffer data = new StringBuffer("[");
//        int hanoi = 1;
//        long startTime = System.currentTimeMillis();
//         for (Area area : areaList) {
//                 hanoi = 1;
//                if (area.getParent() == null) {
//                        data.append( packArea(area, hanoi++, 3)+",");
//                }
//        }
//            if(data.toString().endsWith(",")){
//                    data.deleteCharAt(data.length()-1);
//            }
//
//         data.append( "]" );
//        long enTime = System.currentTimeMillis();
//        System.out.println("pack Area use time: " + (enTime-startTime) );

        String result = null;
        try {
            String data = CommonUtils.readFileByLines("/data.json");

            JSONArray jb = JSONArray.fromObject (data);
            result = data.toString();
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

    @RequestMapping("/init_area")
    public @ResponseBody  String initArea(Model model, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        String cityId = request.getParameter("cityId");
        RestfulResult restfulResult = new RestfulResult();

        try {
            Area area = areaService.find( Long.parseLong( cityId) );
            StringBuffer[] idNameSb = {new StringBuffer(),new StringBuffer()};
            idNameSb = packAreaFromCity(area, idNameSb);
            if(idNameSb[0] != null){
                String[] areaIds = idNameSb[0].toString().split(" ");
                if(areaIds != null && areaIds.length < 3){
                    if( idNameSb[0].toString().endsWith(",")){
                        idNameSb[0].deleteCharAt(idNameSb[0].length()-1);
                        idNameSb[1].deleteCharAt(idNameSb[1].length() - 1);
                    }
                    for(int k=3; k>3-areaIds.length; k--){
                        idNameSb[0].append(",0");
                        idNameSb[1].append(" --");
                    }
                }
            }
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setReturnObj( idNameSb[0].toString()+";" +  idNameSb[1].toString() );
        } catch (NumberFormatException e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setReturnObj("110000,110100,0;北京市  北京市 --");
        }

        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping("/product_area")
    public @ResponseBody  String getProductArea(Model model, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        String cityId = request.getParameter("cityId");
        RestfulResult restfulResult = new RestfulResult();

        try {
            Area area = areaService.find( Long.parseLong( cityId) );
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setReturnObj(cityId + ";" + area.getName());
        } catch (NumberFormatException e) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_EXCEPTION);
            restfulResult.setReturnObj("110100;北京市");
        }
        return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping("/ts_arealist")
    public @ResponseBody  String getTsArea(Model model, HttpServletRequest request) {
        AdPosition adPosition = null;
        String callback = request.getParameter("callback");

        long startTime = System.currentTimeMillis();
        List<Area> areaList =  areaService.findRoots();
        if (areaList != null) {
            for (Area area : areaList) {
                if (area.getId() == 110000) {
                    System.out.println("城市" + area);
                }
                updateArea(area);
            }
        }
//        if (areaList != null) {
//            Area area = null;
//            for (TsArea tsArea : areaList) {
//                area = new Area();
////                area.setId(tsArea.getId());
////                area.setParent(tsArea.getPid());
//                area.setParent(null);
//                String areaName = "aaa";
//                try {
//                    areaName = new String(tsArea.getTitle().getBytes("gb2312"), "gbk");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                area.setName(tsArea.getTitle());
//                area.setCreateDate(new Date());
//                area.setTreePath(",");
//                area.setFullName("");
//                area.setModifyDate(new Date());
//                areaService.save(area);
//            }
//        }
        long enTime = System.currentTimeMillis();
        System.out.println("pack Area use time: " + (   enTime-startTime) );

        StringBuffer data = new StringBuffer("[]");
        JSONArray jb = JSONArray.fromObject (data);
        String result = jb.toString();
        if (callback == null) {
            return result;
        } else {
            return callback + "('" +  result + "')";
        }
    }

    private void updateArea(Area area){
        if (area.getId() == 110000) {
            System.out.println("城市" + area);
        }

        area.setCreateDate(new Date());
        area.setModifyDate(new Date());
        area.setTreePath(TREE_PATH_SEPARATOR);
        areaService.update(area);
        if (area.getChildren() != null && area.getChildren().size() > 0) {
            for (Area childArea : area.getChildren()) {
                childArea.setFullName(area.getFullName() + childArea.getName());
                childArea.setTreePath(area.getTreePath() + area.getId() + TREE_PATH_SEPARATOR);
                updateArea(childArea);
            }
        }
    }

    private StringBuffer[] packAreaFromCity(Area area,  StringBuffer[] sbArray){
        if(area != null){
            if(area.getParent()!=null){
                packAreaFromCity(area.getParent(), sbArray);
            }
            sbArray[0].append(area.getId()).append(",");
            sbArray[1].append(area.getName()).append(" ");
        }
       return sbArray;
    }
    private String packArea(Area area, int hanoi, int heighth){
            hanoi++;
            String areaJson = "";
            if (hanoi > heighth) {
                    return areaJson;
            }
            areaJson = "{";
            if (area != null ) {
                    areaJson += "id:\"" + area.getId() +"\"";
                    areaJson += ",name:\"" + area.getName() +"\"";
                    if ( area.getChildren() != null && area.getChildren().size() > 0) {
                            areaJson += ",child:[" ;
                            for (int i=0; i<area.getChildren().size(); i++) {
                                    String childJson = packArea((Area)area.getChildren().toArray()[i],  hanoi, heighth);
                                    if ( childJson != null && !"".equals(childJson)) {
                                            if (i==0) {
                                                    areaJson += childJson;
                                            } else {
                                                    areaJson += "," + childJson + "";
                                            }

                                    }
                            }
                            areaJson += "]" ;
                    }
            }
            areaJson += "}";
            return areaJson;
    }


    private String returnResult(String callback, String result) {
        RestfulResult restfulResult = new RestfulResult();
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

}