package com.jqb.shop.restful.controller.policy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.policy.Policy;
import com.jqb.shop.restful.entity.RestfulConstants;
import com.jqb.shop.restful.entity.RestfulResult;
import com.jqb.shop.service.MemberService;
import com.jqb.shop.service.PolicyService;

@Controller
public class PolicyController {

	/**
	 * Logging for this instance
	 */
	private org.apache.commons.logging.Log log = LogFactory
			.getLog(PolicyController.class);

    @Resource(name = "policyServiceImpl")
    private PolicyService policyServiceImpl;

    @Resource(name = "memberServiceImpl")
    private MemberService memberService;
	@RequestMapping("/addPolicy")
	public @ResponseBody String addPolicy(Model model,
			HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
		String callback = request.getParameter("callback");
		try {
			Member member = memberService.getCurrent();
			List<Policy> ps=policyServiceImpl.findByMemberId(member.getId(), PolicyService.productCode_1);
			Policy policy=new Policy();
			if(ps!=null&&ps.size()>0){//已经生成保单
				policy=ps.get(0);
			}else{
				String name = request.getParameter("name");
				String id = request.getParameter("id");
				String mobile = request.getParameter("mobile");
				String beginDateStr = request.getParameter("date");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date beginDate=format.parse(beginDateStr);
				Date date=new Date();
				policy.setUid(member.getId());
				policy.setCreateDate(date);
				policy.setModifyDate(date);
				policy.setBeginTime(beginDate);
				policy.setProductCode(policyServiceImpl.productCode_1);
				policy.setPersonnelName(name);
				policy.setSexCode(getSexFromId(id));
				policy.setCertificateNo(id);
				policy.setBirthday(getBirthdayFromId(id));
				policy.setMobileTelephone(mobile);
				//policy.setEmail("zhu.ting.fa@163.com");
				policyServiceImpl.save(policy);
			}
			policy.convertView();
			restfulResult.setReturnObj(policy);
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setResult("生成保险成功");
		    return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback,restfulResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
            restfulResult.setResult("出错");
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback,restfulResult);
		}
	}
	
	@RequestMapping("/getPolicy")
	public @ResponseBody String getPolicy(Model model,
			HttpServletRequest request) {
        RestfulResult restfulResult = new RestfulResult();
		String callback = request.getParameter("callback");
		try {
			Member member = memberService.getCurrent();
			List<Policy> ps=policyServiceImpl.findByMemberId(member.getId(), PolicyService.productCode_1);
			Policy policy=new Policy();
			if(ps!=null&&ps.size()>0){//已经生成保单
				policy=ps.get(0);
				policy.convertView();
				restfulResult.setReturnObj(policy);
			}
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_SUCCESS);
            restfulResult.setResult("获取生成保险成功");
		    return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback,restfulResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
            restfulResult.setErrCode(RestfulConstants.RESTFUL_ERR_CODE_FAIL);
            restfulResult.setResult("出错");
            return com.jqb.shop.util.CommonUtils.returnRestfulResult(callback,restfulResult);
		}
	}
    public static void main(String[] args) {
        //String cardId="130503670401001";
        String cardId="370921198606061213";
    	//String cardId="110105199012279023";
        System.out.println(getBirthdayFromId(cardId));
        System.out.println(getSexFromId(cardId));
    }
    
    public static String getBirthdayFromId(String tIdNo)
    {
        String birthday = "";
        if (tIdNo.length() != 15 && tIdNo.length() != 18)
        {
            return "";
        }
        if (tIdNo.length() == 18)
        {
            birthday = tIdNo.substring(6, 14);
            birthday = birthday.substring(0, 4) + "-" + birthday.substring(4, 6) +
                       "-" + birthday.substring(6);
        }
        if (tIdNo.length() == 15)
        {
            birthday = tIdNo.substring(6, 12);
            birthday = birthday.substring(0, 2) + "-" + birthday.substring(2, 4) +
                       "-" + birthday.substring(4);
            birthday = "19" + birthday;
        }
        return birthday;
 
    }
 
    /**
     * 通过<a href="https://www.baidu.com/s?wd=%E8%BA%AB%E4%BB%BD%E8%AF%81%E5%8F%B7&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1YYP1I-rjP9rjnYPjfsmvub0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3EnW0LPjD3njRLPjc3n1c4P10vr0" target="_blank" class="baidu-highlight">身份证号</a>获取性别
     * @param tIdNo String
     * @return String 1-男 2-女
     */
    public static int getSexFromId(String tIdNo)
    {
        if (tIdNo.length() != 15 && tIdNo.length() != 18)
        {
            return 0;
        }
        String sex = "0";
        if (tIdNo.length() == 15)
        {
            sex = tIdNo.substring(14, 15);
        }
        else
        {
            sex = tIdNo.substring(16, 17);
        }
        try
        {
            int iSex = Integer.parseInt(sex);
//            iSex = iSex % 2;
            iSex %= 2;
            if (iSex == 0)
            {
                return 2;
            }
            if (iSex == 1)
            {
                return 1;
            }
        }
        catch (Exception ex)
        {
            return 0;
        }
        return 0;
    }
}