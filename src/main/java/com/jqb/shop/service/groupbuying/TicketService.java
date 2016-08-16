package com.jqb.shop.service.groupbuying;

import com.jqb.shop.entity.groupbuying.Ticket;
import com.jqb.shop.service.BaseService;

/**
 * Created by liubin on 2016/7/1.
 */
public interface TicketService  extends BaseService<Ticket, Long> {
    public Ticket findByCode(String code);
}
