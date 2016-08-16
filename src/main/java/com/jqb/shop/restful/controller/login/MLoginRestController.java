package com.jqb.shop.restful.controller.login;

import com.jqb.shop.Message;
import com.jqb.shop.Principal;
import com.jqb.shop.Setting;
import com.jqb.shop.entity.Cart;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.user.UserInfo;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.CartService;
import com.jqb.shop.service.MemberService;
import com.jqb.shop.util.JsonUtils;
import com.jqb.shop.util.SettingUtils;
import com.jqb.shop.util.SpringUtils;
import com.jqb.shop.util.WebUtils;
import com.jqb.shop.util.rest.HTTPClientUtils;
import com.wordnik.swagger.annotations.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

@Api(value = "login-api", description = "用户登录的操作")
@Controller
@RequestMapping(value="rest")
public class MLoginRestController {

    public static String USER_INFO_URL_POST = "http://v2.jingqubao.com/api_v3/User/get_user_info";

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;

    @Resource(name = "cartServiceImpl")
    private CartService cartService;

    /**
     * Logging for this instance
     */
    private org.apache.commons.logging.Log log = LogFactory.getLog(MLoginRestController.class);
    @RequestMapping("/m_login")
    public @ResponseBody  String mlogin(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        List<NameValuePair> paramPair = new ArrayList<NameValuePair>();
        String oauth_token = request.getParameter("oauth_token");
        String oauth_token_secret = request.getParameter("oauth_token_secret");
        String oldToken = (String)session.getAttribute("oauth_token");
        //新token为空则清空
        if (oauth_token == null || "".equals(oauth_token)) {
            session.removeAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
            WebUtils.removeCookie(request, response, Member.USERNAME_COOKIE_NAME);
            restfulResult.setErrCode( RestfulConstants.RESTFUL_ERR_CODE_LOGIN_FAIL_NO_TOKEN);
            restfulResult.setResult("No token.");
            return  returnRestfulResult(callback, restfulResult);
        }
        //用户切换
        if(oldToken != null && !"".equals(oldToken) && oauth_token != null && !"".equals(oauth_token)){
            if(!oauth_token.equalsIgnoreCase(oldToken)){
                session.removeAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
                WebUtils.removeCookie(request, response, Member.USERNAME_COOKIE_NAME);
            }
        }
        if (this.check()) {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setResult(" login success .");
            return  returnRestfulResult(callback, restfulResult);
        }

        paramPair.add(new BasicNameValuePair("oauth_token",oauth_token));
        paramPair.add(new BasicNameValuePair("oauth_token_secret", oauth_token_secret));
        String result = HTTPClientUtils.sendRequest(USER_INFO_URL_POST, paramPair);

        JSONObject responseJson = JSONObject.fromObject(result);
        System.out.println("user json : "  + responseJson.get("data"));
        UserInfo userInfo = null;
        if ( responseJson.get("status").equals("1") ) {
            userInfo = new UserInfo();
           // userInfo = (UserInfo) JSONObject.toBean(responseJson, UserInfo.class);
            userInfo  = JsonUtils.toObject(((JSONObject)responseJson.get("data")).toString(), UserInfo.class);
        } else{
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_LOGIN_FAIL_NO_USER);
            restfulResult.setResult("获取userinfo fail.");
            return  returnRestfulResult(callback, restfulResult);
        }
        Member member = null;
        if (userInfo != null && userInfo.getUid() != null) {
            try {
                member = memberService.findByUid( Long.parseLong( userInfo.getUid() ) );
            } catch (Exception e) {
                if(e.getCause().toString().contains("More than one row with the given identifier")){
                    long memberId = memberService.getMemberId(  Long.parseLong( userInfo.getUid() ) );
                    List<Cart> duplCart = cartService.findDuplCart( memberId );
                    if(duplCart != null && duplCart.size()>1){
                        for(int k=0; k<duplCart.size()-1; k++){
                            cartService.evictExpired(duplCart.get(k).getId() , memberId);
                        }
                    }
                }
                member = memberService.findByUid( Long.parseLong( userInfo.getUid() ) );
            }
            if (member == null) {
                member  = new Member();
                if (userInfo.getUname() != null) {
                    member.setUsername(userInfo.getUname().toLowerCase());
                }
                member.setPassword(DigestUtils.md5Hex(userInfo.getUname()));
                member.setEmail(userInfo.getEmail());
                member.setAmount(new BigDecimal(0));
                member.setBalance(new BigDecimal(0));
                member.setIsEnabled(true);
                member.setIsLocked(false);
                member.setLoginFailureCount(0);
                member.setLockedDate(null);
                member.setRegisterIp(request.getRemoteAddr());
                member.setLoginIp(request.getRemoteAddr());
                member.setLoginDate(new Date());
                member.setSafeKey(null);
                member.setMobile( userInfo.getMobile() );
                if (userInfo.getUid() != null) {
                    member.setUid(Long.parseLong(userInfo.getUid()));
                }
//            member.setMemberRank(memberRankService.findDefault());
                member.setFavoriteProducts(null);
                memberService.save(member);
            }
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_LOGIN_FAIL_NO_USER);
            restfulResult.setResult("获取userinfo fail.");
            return  returnRestfulResult(callback, restfulResult);
        }
        //设置登录信息
        if (member != null) {
            this.userLogin(userInfo.getUname(), member, request, response, session);
            session.setAttribute("oauth_token", oauth_token);
            session.setAttribute("oauth_token_secret",oauth_token_secret);
        }
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult(" login success .");
        return  returnRestfulResult(callback, restfulResult);
    }
    @RequestMapping(value="/m_check_login",method = RequestMethod.POST)
    @ApiOperation(value = "校验登录", httpMethod = "POST",response = RestfulResult.class, notes = "传入token校验登录")
    public @ResponseBody  String mCheckLogin(Model model,@ApiParam(required = true, name = "oauth_token", value = "oauth_token")
            @RequestParam(value = "oauth_token") String oauth_token,@ApiParam(required = true, name = "oauth_token_secret", value = "oauth_token_secret")
            @RequestParam(value = "oauth_token_secret") String oauth_token_secret, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String oldToken = (String)session.getAttribute("oauth_token");
        //用户切换
        if( oauth_token != null && !"".equals(oauth_token)){
            if(oldToken==null||"".equals(oldToken)||!oauth_token.equalsIgnoreCase(oldToken)){
                session.removeAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
                WebUtils.removeCookie(request, response, Member.USERNAME_COOKIE_NAME);
                this.mlogin(model, request, response,session);
            }
        }
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        if(this.check()){
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setResult("login!" );
            restfulResult.setReturnObj(request.getSession().getId());
        } else {
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
            restfulResult.setResult("no login!" );
        }
        return  returnRestfulResult(callback, restfulResult);
    }



    @RequestMapping("/m_session")
    public @ResponseBody  String getSession(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");

        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("Get session id success!" );
        restfulResult.setReturnObj(request.getSession().getId());
        return  returnRestfulResult(callback, restfulResult);
    }

    @RequestMapping("/m_logout")
    public @ResponseBody  String mLogout(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        RestfulResult restfulResult = new RestfulResult();
        String callback = request.getParameter("callback");
        session.removeAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
        WebUtils.removeCookie(request, response, Member.USERNAME_COOKIE_NAME);
        restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
        restfulResult.setResult("Logout success!");
        return  returnRestfulResult(callback, restfulResult);
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
    public static void main(String[] args) {
//        String postParam = "{\"oauth_token\":\"2335758381347701632b986d939a3576\",\"oauth_token_secret\":\"a576d9af2ebf74ed79bf4a2612d1bcda\"}";

//"oauth_token=2335758381347701632b986d939a3576&oauth_token_secret=a576d9af2ebf74ed79bf4a2612d1bcda";
        List<NameValuePair> paramPair = new ArrayList<NameValuePair>();
//        paramPair.add(new BasicNameValuePair("oauth_token", "fa04f5156e5250fde717fd2f96903a7a"));
//        paramPair.add(new BasicNameValuePair("oauth_token_secret", "1ed03dc118a425a2ad2563c126364856"));
        paramPair.add(new BasicNameValuePair("oauth_token", "fe7bf5f7a733aae0d2901d4f0ccc7787"));

        paramPair.add(new BasicNameValuePair("oauth_token_secret", "0f8b4cfe9be8932f9ccdc2d833d1b933"));
        String postParam= "oauth_token=fe7bf5f7a733aae0d2901d4f0ccc7787&oauth_token_secret=0f8b4cfe9be8932f9ccdc2d833d1b933";;


        String testMLogin  = "http://localhost:8086/shopmobile/rest/m_login"; //USER_INFO_URL_POST
        JSONObject responseJson = JSONObject.fromObject(HTTPClientUtils.sendRequest(USER_INFO_URL_POST, postParam));
        System.out.println("user json : "  + responseJson.get("data"));
        UserInfo userInfo = JsonUtils.toObject(((JSONObject) responseJson.get("data")).toString(), UserInfo.class);
//        System.out.println("user info : "  + userInfo);

    }

    Boolean check() {
        return memberService.isAuthenticated();
    }

    private String userLogin(String password, Member member, HttpServletRequest request, HttpServletResponse response,HttpSession session){
        Setting setting = SettingUtils.get();
        if (!member.getIsEnabled()) {
            return SpringUtils.getMessage("shop.login.disabledAccount");
        }
//        if (member.getIsLocked()) {
//            if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
//                int loginFailureLockTime = setting.getAccountLockTime();
//                if (loginFailureLockTime == 0) {
//                    return SpringUtils.getMessage("shop.login.lockedAccount");
//                }
//                Date lockedDate = member.getLockedDate();
//                Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
//                if (new Date().after(unlockDate)) {
//                    member.setLoginFailureCount(0);
//                    member.setIsLocked(false);
//                    member.setLockedDate(null);
//                    memberService.update(member);
//                } else {
//                    return SpringUtils.getMessage("shop.login.lockedAccount");
//                }
//            } else {
//                member.setLoginFailureCount(0);
//                member.setIsLocked(false);
//                member.setLockedDate(null);
//                memberService.update(member);
//            }
//        }

//        if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
//            int loginFailureCount = member.getLoginFailureCount() + 1;
//            if (loginFailureCount >= setting.getAccountLockCount()) {
//                member.setIsLocked(true);
//                member.setLockedDate(new Date());
//            }
//            member.setLoginFailureCount(loginFailureCount);
//            memberService.update(member);
//            if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
//                return SpringUtils.getMessage("shop.login.accountLockCount", setting.getAccountLockCount());
//            } else {
//                return SpringUtils.getMessage("shop.login.incorrectCredentials");
//            }
//        }
        member.setLoginIp(request.getRemoteAddr());
        member.setLoginDate(new Date());
        member.setLoginFailureCount(0);
        memberService.update(member);

        Cart cart = cartService.getCurrent();
        if (cart != null) {
            if (cart.getMember() == null) {
                cartService.merge(member, cart);
                WebUtils.removeCookie(request, response, Cart.ID_COOKIE_NAME);
                WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
            }
        }

        Map<String, Object> attributes = new HashMap<String, Object>();
        Enumeration<?> keys = session.getAttributeNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            attributes.put(key, session.getAttribute(key));
        }
//        session.invalidate();
        session = request.getSession();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }

        session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
        WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());

        return  SpringUtils.getMessage("shop.message.success");
    }

}