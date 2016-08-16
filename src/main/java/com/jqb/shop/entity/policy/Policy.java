package com.jqb.shop.entity.policy;

/**
 * Created by liubin on 2016/1/13.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jqb.shop.entity.BaseEntity;

/**
 * Entity - 保险
 *
 * @author JQB Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_policy")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_policy_sequence")
public class Policy extends BaseEntity {

    /** member id */
    private long uid;

    /** 开始日期 */
    private Date beginTime = new Date();
    
    @Transient
    private String beginTimeView="";
    @Transient
    private String endTimeView="";
    /** 结束日期 */
    private Date endTime = new Date();

    /** productCode */
    private String productCode;

    /** bkSerial */
    private String bkSerial;

    /** applyPolicyNo */
    private String applyPolicyNo;

    /** policyNo */
    private String policyNo;

    /** validateCode */
    private String validateCode;

    /** personnelName 用户名 */
    private String personnelName;

    /** sexCode 1：男 2：女*/
    private int sexCode;

    /** certificateNo */
    private String certificateNo;

    /** birthday */
    private String birthday;

    /** mobileTelephone */
    private String mobileTelephone;

    /** email */
    private String email;

    /** 状态 0：新建  1：生产保单成功  10：生产保单失败   2：取消保单成功  20：取消保单失败*/
    private int state;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBkSerial() {
        return bkSerial;
    }

    public void setBkSerial(String bkSerial) {
        this.bkSerial = bkSerial;
    }

    public String getApplyPolicyNo() {
        return applyPolicyNo;
    }

    public void setApplyPolicyNo(String applyPolicyNo) {
        this.applyPolicyNo = applyPolicyNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public int getSexCode() {
        return sexCode;
    }

    public void setSexCode(int sexCode) {
        this.sexCode = sexCode;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobileTelephone() {
        return mobileTelephone;
    }

    public void setMobileTelephone(String mobileTelephone) {
        this.mobileTelephone = mobileTelephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    @Transient
	public String getBeginTimeView() {
		return beginTimeView;
	}
    @Transient
	public void setBeginTimeView(String beginTimeView) {
		this.beginTimeView = beginTimeView;
	}
	 @Transient
	public String getEndTimeView() {
		return endTimeView;
	}
	 @Transient
	public void setEndTimeView(String endTimeView) {
		this.endTimeView = endTimeView;
	}

	public void convertView(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(beginTime);
		SimpleDateFormat insTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
		beginTimeView=insTimeFormat.format(beginTime);
		calendar.add(Calendar.DAY_OF_MONTH, 15);
		endTimeView=insTimeFormat.format(calendar.getTime());
		if(policyNo==null||policyNo.trim().equals("")){
			policyNo="保单生成中";
		}
		if(validateCode==null||validateCode.trim().equals("")){
			validateCode="";
		}		
		
	}
	
}
