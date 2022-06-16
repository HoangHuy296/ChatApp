package hcmute.spkt.mssv19110218.chatappzalo.Models;

public class User {
    private String userid, name, phoneNumber, avatar, token, password; //khởi tạo lớp user có những thuộc tính trên

    public User() {
        
    }

    public User(String userid, String name, String phoneNumber, String avatar, String password) {
        this.userid = userid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.password = password;
    }

    //các hàm getter và setter tạo ra
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
