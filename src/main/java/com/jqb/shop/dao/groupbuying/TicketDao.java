package com.jqb.shop.dao.groupbuying;

import com.jqb.shop.Page;
import com.jqb.shop.Pageable;
import com.jqb.shop.dao.BaseDao;
import com.jqb.shop.entity.Ad;
import com.jqb.shop.entity.groupbuying.Ticket;

/**
 * Created by liubin on 2016/7/1.
 */

/**
 * Dao - Ticket
 *
 * @author JQB Team
 * @version 3.0
 */
public interface TicketDao extends BaseDao<Ticket, Long> {

    public Ticket findByCode(String code);
}
