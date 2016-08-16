package com.jqb.shop.dao.groupbuying.impl;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.BaseDao;
import com.jqb.shop.dao.groupbuying.TicketDao;
import com.jqb.shop.dao.impl.BaseDaoImpl;
import com.jqb.shop.entity.groupbuying.Ticket;
import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

/**
 * Created by liubin on 2016/7/1.
 */
@Repository("ticketDaoImpl")
public class TicketDaoImpl extends BaseDaoImpl<Ticket, Long> implements TicketDao {
    public Ticket findByCode(String code) {
        if (code == null) {
            return null;
        }
        try {
            String jpql = "select ticket from Ticket ticket where lower(ticket.number) = lower(:code)";
            return entityManager.createQuery(jpql, Ticket.class).setFlushMode(FlushModeType.COMMIT).setParameter("code", code).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
