package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hcmute.spkt.mssv19110218.chatappzalo.R;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    //Nếu đã có tài khoản sẽ cho phép vào main activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    //Bấm nút login để vào trang login
    public void login(View view) {
        startActivity(new Intent(StartActivity.this,LoginActivity.class));
    }

    //Bấm nút đăng kí để vào trang đăng kí
    public void register(View view) {
        startActivity(new Intent(StartActivity.this,InsertNameActivity.class));
    }
}