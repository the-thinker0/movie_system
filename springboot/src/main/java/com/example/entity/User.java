package com.example.entity;

/**
 * 用户
 */
public class User extends Account {

    /** 性别 */
    private String sex;
    /** 电话 */
    private String phone;
    /** 邮箱 */
    private String email;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
