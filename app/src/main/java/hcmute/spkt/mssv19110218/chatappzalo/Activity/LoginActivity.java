package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.spkt.mssv19110218.chatappzalo.Adapters.UsersAdapter;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    //khai báo biến sử dụng
    ActivityLoginBinding binding; //Dùng để binding các view trong LoginActivity
    ProgressDialog dialog; //Khởi tạo dialog
    FirebaseAuth auth; //FirebaseAuth được gán trong auth
    ArrayList<User> users; //Khởi tạo ArrayList User
    FirebaseDatabase database; //FirebaseDatabase được gán trong database
    UsersAdapter usersAdapter; //Khởi tạo userAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance(); //Lấy FirebaseAuth hiện tại
        database = FirebaseDatabase.getInstance(); //Lấy FirebaseDatabase hiện tại

        //Hiện dialog process
        dialog = new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Đang đăng nhập....");
        dialog.setCancelable(false);
        //khởi tạo new user bằng arrayList
        users = new ArrayList<>();
        //Khởi tạo userAdapter
        usersAdapter = new UsersAdapter(this, users);
        btnContinue();
    }

    //Hàm để set sự kiện onClick cho btnContinue
    public void btnContinue(){
        binding.continueBtnLogin.setOnClickListener(new View.OnClickListener() {
            //Khi click btnContinue sẽ truy cập hàm Login
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    //hàm login
    private void login(){
        //Lấy chuỗi của sdt và mật khẩu
        final String phoneLogin = binding.editPhone.getText().toString().trim();
        final String passLogin = binding.editPassword.getText().toString().trim();

        //lấy users trong database
        DatabaseReference reference = database.getReference("users");


        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                usersAdapter.notifyDataSetChanged();

                for (User user : users) {
                    String phone = user.getPhoneNumber();
                    String pass = user.getPassword();
                    if (phone.equals(phoneLogin) && pass.equals(passLogin)) {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}