package com.jqb.shop.restful.controller;

import com.jqb.shop.*;
import com.jqb.shop.entity.*;
import com.jqb.shop.entity.Order;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by liubin on 2015/12/23.
 */
@Controller(value="aftService")
@RequestMapping(value="rest")
public class AfterServiceRestController {

    @Resource(name = "adminServiceImpl")
    private AdminService adminService;



}
