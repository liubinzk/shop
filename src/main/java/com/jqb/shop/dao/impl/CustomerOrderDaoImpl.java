package com.jqb.shop.dao.impl;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.CustomerOrderDao;
import com.jqb.shop.entity.CustomerOrder;
import com.jqb.shop.entity.Member;
import com.jqb.shop.entity.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

/**
 * Created by liubin on 2016/4/7.
 */
@Repository("customerOrderDaoImpl")
public class CustomerOrderDaoImpl extends BaseDaoImpl<CustomerOrder, Long> implements CustomerOrderDao {


    public Page<CustomerOrder> findPage(Member member,CustomerOrder.OrderStatus orderStatus, CustomerOrder.PaymentStatus paymentStatus, CustomerOrder.ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerOrder> criteriaQuery = criteriaBuilder.createQuery(CustomerOrder.class);
        Root<CustomerOrder> root = criteriaQuery.from(CustomerOrder.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();

        restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.get("member"), member) );

        if (orderStatus != null) {
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
        }
        if (paymentStatus != null) {
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
        }
        if (shippingStatus != null) {
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
        }
        if (hasExpired != null) {
            if (hasExpired) {
                restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
            } else {
                restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
            }
        }
        restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.notEqual(root.get("orderStatus"), CustomerOrder.OrderStatus.deleted) );
        criteriaQuery.where(restrictions);
        return super.findPage(criteriaQuery, pageable);
    }

    public CustomerOrder findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select customerOrder from CustomerOrder customerOrder where lower(customerOrder.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, CustomerOrder.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
