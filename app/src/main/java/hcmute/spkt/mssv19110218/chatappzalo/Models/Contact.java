package hcmute.spkt.mssv19110218.chatappzalo.Models;

public class Contact {

    private String name; //Tên được lưu trong danh bạ của phone
    private String phoneNo; //số điện thoại được lưu trong contact của phone

    public String getPhoneNo() {
        //khởi tạo biến phoneNoChange = phoneNo;
        String phoneNoChange = phoneNo;
        //cắt bỏ số 0 đầu và thêm subString để gán chuỗi +84 và sđt
        phoneNoChange = phoneNoChange.substring(1);
        phoneNoChange = "+84" + phoneNoChange;
        //Sử dụng replaceAll để xóa toàn bộ khoảng trắng trong phoneNo
        return phoneNoChange.replaceAll("\\s+", "");
    }

    //trả về tên
    public String getName() {
        return name;
    }

    //gán lại tên
    public void setName(String name) {
        this.name = name;
    }

    //gán lại phoneNo
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
