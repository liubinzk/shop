/*
 * Copyright 2014-2015 jingqubao All rights reserved.
 * 
 * Support: http://www.jingqubao.com
 * 
 * License: licensed
 * 
 */
package com.jqb.shop.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;

import com.jqb.shop.Principal;
import com.jqb.shop.Setting;
import com.jqb.shop.dao.DepositDao;
import com.jqb.shop.dao.MemberDao;
import com.jqb.shop.entity.Admin;
import com.jqb.shop.entity.Cart;
import com.jqb.shop.entity.Deposit;
import com.jqb.shop.entity.Member;
import com.jqb.shop.service.CartService;
import com.jqb.shop.service.MemberService;
import com.jqb.shop.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service - 会员
 * 
 * @author JQB Team
 * @version 3.0
 */
@Service("memberServiceImpl")
public class MemberServiceImpl extends BaseServiceImpl<Member, Long> implements MemberService {

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;
	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "memberDaoImpl")
	public void setBaseDao(MemberDao memberDao) {
		super.setBaseDao(memberDao);
	}
	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return memberDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public boolean usernameDisabled(String username) {
		Assert.hasText(username);
		Setting setting = SettingUtils.get();
		if (setting.getDisabledUsernames() != null) {
			for (String disabledUsername : setting.getDisabledUsernames()) {
				if (StringUtils.containsIgnoreCase(username, disabledUsername)) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return memberDao.emailExists(email);
	}

	@Transactional(readOnly = true)
	public boolean emailUnique(String previousEmail, String currentEmail) {
		if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
			return true;
		} else {
			if (memberDao.emailExists(currentEmail)) {
				return false;
			} else {
				return true;
			}
		}
	}

	public void save(Member member, Admin operator) {
		Assert.notNull(member);
		memberDao.persist(member);
		if (member.getBalance().compareTo(new BigDecimal(0)) > 0) {
			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? Deposit.Type.adminRecharge : Deposit.Type.memberRecharge);
			deposit.setCredit(member.getBalance());
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
	}

	public void update(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) {
		Assert.notNull(member);

		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);

		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}

		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0 && member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) >= 0) {
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			if (modifyBalance.compareTo(new BigDecimal(0)) > 0) {
				deposit.setType(operator != null ? Deposit.Type.adminRecharge : Deposit.Type.memberRecharge);
				deposit.setCredit(modifyBalance);
				deposit.setDebit(new BigDecimal(0));
			} else {
				deposit.setType(operator != null ? Deposit.Type.adminChargeback : Deposit.Type.memberPayment);
				deposit.setCredit(new BigDecimal(0));
				deposit.setDebit(modifyBalance);
			}
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
	}

	@Transactional(readOnly = true)
	public Member findByUsername(String username) {
		return memberDao.findByUsername(username);
	}

	public Member findByUid(long uid) {
		return memberDao.findByUid(uid);
	}

	@Transactional(readOnly = true)
	public List<Member> findListByEmail(String email) {
		return memberDao.findListByEmail(email);
	}

	@Transactional(readOnly = true)
	public List<Object[]> findPurchaseList(Date beginDate, Date endDate, Integer count) {
		return memberDao.findPurchaseList(beginDate, endDate, count);
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Member getCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				Member member = null;
				try {
					member = memberDao.find(principal.getId());
				} catch (Exception e) {
					if(e.getCause().toString().contains("More than one row with the given identifier")){
						long memberId =  principal.getId();
						List<Cart> duplCart = cartService.findDuplCart( memberId );
						if(duplCart != null && duplCart.size()>1){
							for(int k=0; k<duplCart.size()-1; k++){
								cartService.evictExpired(duplCart.get(k).getId() , memberId);
							}
						}
					}
					member = memberDao.find(principal.getId());
				}
				return member;
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Member getMobileCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.MOBILE_PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return memberDao.find(principal.getId());
			}
		}
		return null;
	}



	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	public long getMemberId(long uid){
		return memberDao.getMemberId(uid);
	}

}
