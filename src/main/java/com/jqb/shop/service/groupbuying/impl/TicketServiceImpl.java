package com.jqb.shop.service.groupbuying.impl;

import com.jqb.shop.dao.groupbuying.TicketDao;
import com.jqb.shop.entity.groupbuying.Ticket;
import com.jqb.shop.service.groupbuying.TicketService;
import com.jqb.shop.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by liubin on 2016/7/1.
 */
@Service("ticketServiceImpl")
public class TicketServiceImpl extends BaseServiceImpl<Ticket,Long> implements TicketService {

    @Resource(name = "ticketDaoImpl")
    private TicketDao ticketDao;

    @Resource(name = "ticketDaoImpl")
    public void setBaseDao(TicketDao ticketDao) {
        super.setBaseDao(ticketDao);
    }

    public Ticket findByCode(String code){
        return ticketDao.findByCode(code);
    }
}
