package com.jqb.shop.entity.user;

/**
 * Created by liubin on 2016/1/22.
 */
public class UserInfo {
    private String uid;
    private String uname;
    private int sex;
    private String mobile;
    private String email;
    private String location;
    private String photo;
    private String credit;
    private String intro;

    private int chat_group_id;
    private String jpush_reg_id;
    private String rc_token;

    private int follow_num;

    private int is_followed;

    private int province;

    private int city;

    private String area;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public int getChat_group_id() {
        return chat_group_id;
    }

    public void setChat_group_id(int chat_group_id) {
        this.chat_group_id = chat_group_id;
    }

    public String getJpush_reg_id() {
        return jpush_reg_id;
    }

    public void setJpush_reg_id(String jpush_reg_id) {
        this.jpush_reg_id = jpush_reg_id;
    }

    public String getRc_token() {
        return rc_token;
    }

    public void setRc_token(String rc_token) {
        this.rc_token = rc_token;
    }

    public int getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(int follow_num) {
        this.follow_num = follow_num;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getIs_followed() {
        return is_followed;
    }

    public void setIs_followed(int is_followed) {
        this.is_followed = is_followed;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
