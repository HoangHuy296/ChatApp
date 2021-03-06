package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.logging.LogRecord;

import hcmute.spkt.mssv19110218.chatappzalo.R;

public class SpashActivity extends AppCompatActivity {

    FirebaseAuth auth; //khởi tạo firebaseAuth
    FirebaseUser user;
    android.os.Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);
        auth = FirebaseAuth.getInstance(); //mapping firebaseAuth để lấy trạng thái
        //delay 2000milis để chạy hàm
        new Handler().postDelayed(new Runnable() {
            //nếu đã có user cho vào main
            @Override
            public void run() {
                if(auth.getCurrentUser()!=null)
                {
                    Intent intent = new Intent(SpashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                //chưa có user cho vào StartActivity
                else {
                    startActivity(new Intent(SpashActivity.this, StartActivity.class));
                    finish();
                }
            }
        },2000);
    }
}