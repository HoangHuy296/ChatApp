package hcmute.spkt.mssv19110218.chatappzalo.GetUserName;

import hcmute.spkt.mssv19110218.chatappzalo.Models.User;

// Tạo class sử dụng chung cho toàn app
public class Common {
    public static boolean isLogin = false;// Khởi tạo biến isLogin để kiểm tra tình trạng đăng nhập của người dùng
    public static User currentUser = null;//Tạo một user để có thể tham chiếu đến toàn ứng dụng khi đăng nhập thành công
}
