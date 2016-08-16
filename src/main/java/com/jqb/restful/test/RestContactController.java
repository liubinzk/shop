package com.jqb.restful.test;

import com.jqb.restful.test.entity.JaxObj;
import com.jqb.restful.test.entity.UserInfo;
import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.controller.shop.SystemController;
import com.jqb.shop.entity.*;
import com.jqb.shop.service.*;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.logging.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
public class RestContactController {

    /**
     * Logging for this instance
     */
    private org.apache.commons.logging.Log log = LogFactory.getLog(RestContactController.class);

    /** Used to access properties*/
    private PropertyUtilsBean propertyUtilsBean;

    /** Used to perform conversions between object types when setting properties */
    private ConvertUtilsBean convertUtilsBean;


    @Resource(name = "productServiceImpl")
    private ProductService productService;
    @Resource(name = "productCategoryServiceImpl")
    private ProductCategoryService productCategoryService;
    @Resource(name = "brandServiceImpl")
    private BrandService brandService;
    @Resource(name = "promotionServiceImpl")
    private PromotionService promotionService;
    @Resource(name = "tagServiceImpl")
    private TagService tagService;

    @Resource(name = "commercialServiceImpl")
    private CommercialService commercialService;
    @Resource(name = "customerOrderServiceImpl")
    private CustomerOrderService customerOrderService;

    @Resource(name = "orderServiceImpl")
    private OrderService orderService;
    @Resource(name = "memberServiceImpl")
    private MemberService memberService;

    @Resource(name = "adminServiceImpl")
    private AdminService adminService;



    @RequestMapping(value="/myjson")
    public @ResponseBody String testRest(Model model,HttpServletRequest request) {
        String callback = request.getParameter("callback");
        model.addAttribute("message", "Hello World!");
        MyJson mj = new MyJson();
        mj.setName("latest version 最新");
        mj.setAge(2015423);
        mj.setDetail("latest version test ... rest");
        JSONObject jb = JSONObject.fromObject(mj);
        String result = jb.toString();
//        return callback+"(\"{'name':'key'}\")";
        return result;
    }

    @RequestMapping(value="/myObj")
    public @ResponseBody MyJson getObjRest(Model model,HttpServletRequest request) {
        String callback = request.getParameter("callback");
        model.addAttribute("message", "Hello World!");
        MyJson mj = new MyJson();
        mj.setName("latest version 最新");
        mj.setAge(2015423);
        mj.setDetail("latest version test ... rest");
        return mj;
    }

    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public @ResponseBody JaxObj getAll() throws Exception {
        JaxObj jo = new JaxObj();
        List<UserInfo> userList = new ArrayList<UserInfo>();
        for (int i=10; i>0 ; i--) {
            UserInfo user = new UserInfo();
            user.setName("user" + i);
            user.setAge(i*10);
            user.setAddress("华声国际大厦70" + i );
            userList.add(user);
        }
        jo.setList(userList);
        return jo;
    }


    @RequestMapping("/testRest")
    public @ResponseBody  String helloWorld(Model model, HttpServletRequest request) {
        String callback = request.getParameter("callback");
        testPojoService();
        JSONObject jb = JSONObject.fromObject( "{aaa}" );
        String result = jb.toString();


        return callback + "('" +  result + "')";
    }

    /**
     * ????
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Pageable pageable, ModelMap model) {
        ProductCategory productCategory = productCategoryService.find(productCategoryId);
        Brand brand = brandService.find(brandId);
        Promotion promotion = promotionService.find(promotionId);
        List<Tag> tags = tagService.findList(tagId);
        model.addAttribute("productCategoryTree", productCategoryService.findTree());
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("promotions", promotionService.findAll());
        model.addAttribute("tags", tagService.findList(Tag.Type.product));
        model.addAttribute("productCategoryId", productCategoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("promotionId", promotionId);
        model.addAttribute("tagId", tagId);
        model.addAttribute("isMarketable", isMarketable);
        model.addAttribute("isList", isList);
        model.addAttribute("isTop", isTop);
        model.addAttribute("isGift", isGift);
        model.addAttribute("isOutOfStock", isOutOfStock);
        model.addAttribute("isStockAlert", isStockAlert);
        model.addAttribute("page", productService.findPage(productCategory, brand, promotion, tags, null, null, null, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, Product.OrderType.dateDesc, pageable));
        return "/admin/product/list";
    }
    @RequestMapping("/testHtml")
    public String searchHtml(Model model) {
        model.addAttribute("message", "Hello World!");
        MyJson mj = new MyJson();
        mj.setName("latest version");
        mj.setAge(20151216);
        mj.setDetail("latest version test ... rest");
        JSONObject jb = JSONObject.fromObject( mj );
        String result = jb.toString();
        return "search/search";
    }

    @RequestMapping("/testJsp")
    public String searchJsp(Model model) {
        model.addAttribute("message", "Hello World!");
        MyJson mj = new MyJson();
        mj.setName("latest version");
        mj.setAge(20151217);
        mj.setDetail("latest version test ... rest");
        JSONObject jb = JSONObject.fromObject( mj );
        String result = jb.toString();
        return "search";
    }

    public void copyProperties(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.copyProperties(" + dest + ", " +
                    orig + ")");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors =
                    ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                // Need to check isReadable() for WrapDynaBean
                // (see Jira issue# BEANUTILS-61)
                if (getPropertyUtils().isReadable(orig, name) &&
                        getPropertyUtils().isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            Iterator entries = ((Map) orig).entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String name = (String)entry.getKey();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    copyProperty(dest, name, entry.getValue());
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor[] origDescriptors =
                    getPropertyUtils().getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (getPropertyUtils().isReadable(orig, name) &&
                        getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        Object value =
                                getPropertyUtils().getSimpleProperty(orig, name);
                        copyProperty(dest, name, value);
                    } catch (NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }
        }

    }
    /**
     * Gets the <code>PropertyUtilsBean</code> instance used to access properties.
     *
     * @return The ConvertUtils bean instance
     */
    public PropertyUtilsBean getPropertyUtils() {
        return propertyUtilsBean;
    }

    /**
     * <p>Copy the specified property value to the specified destination bean,
     * performing any type conversion that is required.  If the specified
     * bean does not have a property of the specified name, or the property
     * is read only on the destination bean, return without
     * doing anything.  If you have custom destination property types, register
     * {@link Converter}s for them by calling the <code>register()</code>
     * method of {@link ConvertUtils}.</p>
     *
     * <p><strong>IMPLEMENTATION RESTRICTIONS</strong>:</p>
     * <ul>
     * <li>Does not support destination properties that are indexed,
     *     but only an indexed setter (as opposed to an array setter)
     *     is available.</li>
     * <li>Does not support destination properties that are mapped,
     *     but only a keyed setter (as opposed to a Map setter)
     *     is available.</li>
     * <li>The desired property type of a mapped setter cannot be
     *     determined (since Maps support any data type), so no conversion
     *     will be performed.</li>
     * </ul>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void copyProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {

        // Trace logging (if enabled)
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("  copyProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            } else if (value instanceof String) {
                sb.append((String) value);
            } else if (value instanceof String[]) {
                String[] values = (String[]) value;
                sb.append('[');
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            } else {
                sb.append(value.toString());
            }
            sb.append(')');
            log.trace(sb.toString());
        }

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("    Target bean = " + target);
            log.trace("    Target name = " + name);
        }

        // Declare local variables we will require
        String propName = resolver.getProperty(name); // Simple name of target property
        Class type = null;                            // Java type of target property
        int index  = resolver.getIndex(name);         // Indexed subscript value (if any)
        String key = resolver.getKey(name);           // Mapped key value (if any)

        // Calculate the target property type
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return; // Skip this property setter
            }
            type = dynaProperty.getType();
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor =
                        getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return; // Skip this property setter
                }
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
            type = descriptor.getPropertyType();
            if (type == null) {
                // Most likely an indexed setter on a POJB only
                if (log.isTraceEnabled()) {
                    log.trace("    target type for property '" +
                            propName + "' is null, so skipping ths setter");
                }
                return;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("    target propName=" + propName + ", type=" +
                    type + ", index=" + index + ", key=" + key);
        }

        // Convert the specified value to the required type and store it
        if (index >= 0) {                    // Destination must be indexed
            value = convert(value, type.getComponentType());
            try {
                getPropertyUtils().setIndexedProperty(target, propName,
                        index, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException
                        (e, "Cannot set " + propName);
            }
        } else if (key != null) {            // Destination must be mapped
            // Maps do not know what the preferred data type is,
            // so perform no conversions at all
            // FIXME - should we create or support a TypedMap?
            try {
                getPropertyUtils().setMappedProperty(target, propName,
                        key, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException
                        (e, "Cannot set " + propName);
            }
        } else {                             // Destination must be simple
            value = convert(value, type);
            try {
                getPropertyUtils().setSimpleProperty(target, propName, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException
                        (e, "Cannot set " + propName);
            }
        }

    }
    /**
     * <p>Convert the value to an object of the specified class (if
     * possible).</p>
     *
     * @param value Value to be converted (may be null)
     * @param type Class of the value to be converted to
     * @return The converted value
     *
     * @exception ConversionException if thrown by an underlying Converter
     * @since 1.8.0
     */
    protected Object convert(Object value, Class type) {
        Converter converter = getConvertUtils().lookup(type);
        if (converter != null) {
            log.trace("USING CONVERTER " + converter);
            return converter.convert(type, value);
        } else {
            return value;
        }
    }
    /**
     * Gets the <code>ConvertUtilsBean</code> instance used to perform the conversions.
     *
     * @return The ConvertUtils bean instance
     */
    public ConvertUtilsBean getConvertUtils() {
        return convertUtilsBean;
    }

    private void testPojoService(){
        Admin admin = adminService.find(1L);
        System.out.println(admin.getCommercial().getCode());
    }

}