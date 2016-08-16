package com.jqb.shop.entity.groupbuying;

/**
 * Created by liubin on 2016/7/1.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity - groupbuying ticket
 *
 * @author JQB Team
 * @version 4.0
 */
@Entity
@Table(name = "grpb_tttuangou_ticket")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "grpb_tttuangou_ticket_sequence")
public class Ticket {
    private static final long serialVersionUID = -3158109459123036968L;

    private Integer id;

    @Column(name="uid")
    private Integer uid;
    @Column(name="productid")
    private Integer productid;
    @Column(name="orderid")
    private Long orderid;
    @Column(name="guid")
    private String guid;
    @Column(name="number")
    private String number;
    @Column(name="password")
    private String password;

    /** 创建日期 */
    private Date usetime = new Date();
    @Column(name="status")
    private Integer status;
    @Column(name="mutis")
    private Integer mutis;

    @Id
    @Column(name="ticketid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequenceGenerator")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Field(store = Store.YES)
    @DateBridge(resolution = Resolution.SECOND)
    @Column(name="usetime")
    public Date getUsetime() {
        return usetime;
    }

    public void setUsetime(Date usetime) {
        this.usetime = usetime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMutis() {
        return mutis;
    }
    public void setMutis(Integer mutis) {
        this.mutis = mutis;
    }
}
