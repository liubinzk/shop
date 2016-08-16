package com.jqb.shop.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jqb.shop.dao.PolicyDao;
import com.jqb.shop.entity.policy.Policy;
import com.jqb.shop.service.PolicyService;

/**
 * 保险服务接口
 *
 * Created by liubin on 2016/1/13.
 */
@Service("policyServiceImpl")
public class PolicyServiceImpl extends BaseServiceImpl<Policy, Long> implements
		PolicyService, InitializingBean {
	private static Logger Log = Logger.getLogger(PolicyServiceImpl.class);
	String keyStore = "/policyconfig/EXV_BIS_IFRONT_PCIS_ZNLX_001_PRD.PFX"; // 证书的路径，pfx格式
	String keyPass = "paic1234"; // pfx文件的密码
	String PAIS_HTTPS_URI = "https://202.69.19.43";
	int PAIS_HTTPS_PORT = 8007;
	KeyStore ks = null;
	KeyStore ts = null;
	SSLSocketFactory socketFactory = null;
	private static final String sendReg = "^.*<BK_SERIAL>(.*)</BK_SERIAL>.*<PA_RSLT_CODE>(.*)</PA_RSLT_CODE>.*<applyPolicyNo>(.*)</applyPolicyNo>.*<policyNo>(.*)</policyNo>.*<validateCode>(.*)</validateCode>.*$";
	private static final String cancelReg = "^.*<PA_RSLT_CODE>(.*)</PA_RSLT_CODE>.*$";

	@Resource(name = "policyDaoImpl")
	private PolicyDao policyDao;

	@Override
	@Transactional
	public void save(Policy policy) {
		super.save(policy);
	}

	@Override
	@Transactional
	public Policy update(Policy policy) {
		return super.update(policy);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		super.delete(id);
	}

	public Policy findByNo(String policyNo) {
		return policyDao.findByNo(policyNo);
	}

	public List<Policy> findByMemberId(Long memberId, String productCode) {
		return policyDao.findByMemberId(memberId, productCode);
	}
	/*
	 * 每次获取1000条没有生产保险单数据
	 */
	public List<Policy> findUnSend() {
		return policyDao.findUnSend();
	}
	@Transactional
	public boolean createPolicy(Policy policy) {
		if (policy.getState() != 0) {//不是新建保单
			return true;
		}
		String msg = getCreateMessage(policy);
		String result = connect(msg);
		Log.info(result);
		if (result == null)
			return false;
		Pattern p = Pattern.compile(sendReg);
		Matcher m = p.matcher(result);
		while (m.find()) {
			String code = m.group(2);
			if (code != null && code.trim().equals("999999")) {// 成功
				policy.setBkSerial(m.group(1));
				policy.setApplyPolicyNo(m.group(3));
				policy.setPolicyNo(m.group(4));
				policy.setValidateCode(m.group(5));
				policy.setState(1);
				this.update(policy);
				return true;
			}else{//失败
				policy.setApplyPolicyNo(result);
				policy.setState(10);
				this.update(policy);
			}
		}
		return false;
	}

	@Transactional
	public boolean cancelPolicy(Policy policy) {
		if (policy.getState() == 2) {// 已经取消
			return true;
		}
		String msg = getCancelMessage(policy.getBkSerial(),
				policy.getPolicyNo(), policy.getValidateCode());
		String result = connect(msg);
		if (result == null)
			return false;
		Pattern p = Pattern.compile(cancelReg);
		Matcher m = p.matcher(result);
		while (m.find()) {
			String code = m.group(1);
			if (code != null && code.trim().equals("999999")) {// 成功
				policy.setState(2);
				this.update(policy);
				return true;
			}
		}
		return false;
	}

	private String getCreateMessage(Policy policy) {
		String message = "";
		InputStream is = null;
		try {
			String productCode = policy.getProductCode();
			String personnelName = policy.getProductCode();
			String sexCode = policy.getSexCode()==2?"F":"M";
			String certificateNo = policy.getCertificateNo();
			String birthday = policy.getBirthday();
			String mobileTelephone = policy.getMobileTelephone();
			String email = policy.getEmail();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(policy.getBeginTime());
			SimpleDateFormat insTimeFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String insuranceBeginTime = insTimeFormat
					.format(calendar.getTime());// 保险起期
			calendar.add(Calendar.DAY_OF_MONTH, 15);
			policy.setEndTime(calendar.getTime());
			String insuranceEndTime = insTimeFormat.format(calendar.getTime());// 保险止期
			String msgFilePath = this.getClass()
					.getResource("/policyconfig/send.xml").getFile();
			String bodyText = FileUtils.readFileToString(new File(msgFilePath),
					"GB2312");
			StringBuffer strBuf = new StringBuffer(bodyText);
			Calendar calendar1 = Calendar.getInstance();
			SimpleDateFormat acctDateFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat acctTimeFormat = new SimpleDateFormat("HH:mm:ss");
			long serial = calendar1.getTimeInMillis();
			String BK_ACCT_DATE = acctDateFormat.format(calendar1.getTime());// 交易日期
			String BK_ACCT_TIME = acctTimeFormat.format(calendar1.getTime());// 交易时间
			String BK_SERIAL = (new Long(serial)).toString();// 交易流水号，每笔交易都不一样
			String partnerSystemSeriesNo = BK_SERIAL + (int) 10 * Math.random();// 业务流水号。实际业务中，这个字段不能随机生成，而是与业务流水号一致
			// 以下是对报文中指定此段插入值
			insertValue(strBuf, "<BK_ACCT_DATE>", BK_ACCT_DATE);
			insertValue(strBuf, "<BK_ACCT_TIME>", BK_ACCT_TIME);
			insertValue(strBuf, "<BK_SERIAL>", BK_SERIAL);
			insertValue(strBuf, "<insuranceBeginTime>", insuranceBeginTime);
			insertValue(strBuf, "<insuranceEndTime>", insuranceEndTime);
			insertValue(strBuf, "<partnerSystemSeriesNo>",
					partnerSystemSeriesNo);
			insertValue(strBuf, "<productCode>", productCode);
			insertValue(strBuf, "<personnelName>", personnelName);
			insertValue(strBuf, "<sexCode>", sexCode);
			insertValue(strBuf, "<certificateNo>", certificateNo);
			insertValue(strBuf, "<birthday>", birthday);
			insertValue(strBuf, "<mobileTelephone>",
					mobileTelephone == null ? "" : mobileTelephone);
			insertValue(strBuf, "<email>", email == null ? "" : email);
			message = strBuf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return message;
	}

	private String getCancelMessage(String BK_SERIAL, String policyNo,
			String validateCode) {
		String message = "";
		InputStream is = null;
		try {
			String msgFilePath = this.getClass()
					.getResource("/policyconfig/cancel.xml").getFile();
			String bodyText = FileUtils.readFileToString(new File(msgFilePath),
					"GB2312");
			StringBuffer strBuf = new StringBuffer(bodyText);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			SimpleDateFormat acctDateFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat acctTimeFormat = new SimpleDateFormat("HH:mm:ss");
			String BK_ACCT_DATE = acctDateFormat.format(calendar.getTime());// 交易日期
			String BK_ACCT_TIME = acctTimeFormat.format(calendar.getTime());// 交易时间

			// 以下是对报文中指定此段插入值
			// String BK_SERIAL = "1452653014967";// 交易流水号，每笔交易都不一样
			// String policyNo = "12509041900121549675";
			// String validateCode="lWlngiAjQyUzgFQbVY";

			// 以下是对报文中指定此段插入值
			insertValue(strBuf, "<BK_ACCT_DATE>", BK_ACCT_DATE);
			insertValue(strBuf, "<BK_ACCT_TIME>", BK_ACCT_TIME);
			insertValue(strBuf, "<BK_SERIAL>", BK_SERIAL + 1);
			insertValue(strBuf, "<BK_ORGN_SRIL>", BK_SERIAL);
			insertValue(strBuf, "<policyNo>", policyNo);
			insertValue(strBuf, "<validateCode>", validateCode);
			message = strBuf.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return message;
	}

	private static void insertValue(StringBuffer message, String keyName,
			String keyValue) {
		int beginIndex = message.indexOf(keyName);
		if (beginIndex > 0) {
			beginIndex = beginIndex + keyName.length();
			message.insert(beginIndex, keyValue);
		} else {
			System.out.println(keyName + ":not fonud");
		}

	}

	public void afterPropertiesSet() throws Exception {
		this.setBaseDao(policyDao);
		try {
			String trustStore = keyStore;
			String trustPass = keyPass;// "123456"; // jks文件的密码f
			InputStream keystoreInstream = this.getClass().getResourceAsStream(
					keyStore);
			InputStream trustStoreInstream = this.getClass()
					.getResourceAsStream(trustStore);
			ks = KeyStore.getInstance("PKCS12");
			// 加载pfx文件
			ks.load(keystoreInstream, keyPass.toCharArray());
			ts = KeyStore.getInstance("PKCS12");
			// 加载jks文件
			ts.load(trustStoreInstream, trustPass.toCharArray());
			socketFactory = new SSLSocketFactory(SSLSocketFactory.SSL, ks,
					keyPass, ts, null, new TrustStrategy() {
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public String connect(String bodyText) {
		BufferedReader in = null;
		String result = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			Scheme sch = new Scheme("https", PAIS_HTTPS_PORT, socketFactory);
			httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			HttpPost httpPost = null;
			httpPost = new HttpPost(PAIS_HTTPS_URI);
			StringEntity entity = new StringEntity(bodyText, "text/html",
					"GB2312");
			httpPost.setEntity(entity);
			HttpResponse httpResponse = httpclient.execute(httpPost);
			HttpEntity resEntity = httpResponse.getEntity();
			if (resEntity != null) {
				result = EntityUtils.toString(resEntity);
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
