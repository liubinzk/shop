package com.jqb.restful.test.entity;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "listObj")
public class JaxObj {
	
	private Class clazz;
	
    public JaxObj() {
    }

    public JaxObj(List<?> list) {
        this.list = list;
    }
    
    private List<?> list;

    @XmlElements({ @XmlElement(name = "user", type = UserInfo.class),
            @XmlElement(name = "hobby", type = String.class)
    })
    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}