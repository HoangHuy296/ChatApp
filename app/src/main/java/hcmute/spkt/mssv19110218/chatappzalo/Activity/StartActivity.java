package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hcmute.spkt.mssv19110218.chatappzalo.R;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void login(View view) {
        startActivity(new Intent(StartActivity.this,LoginActivity.class));
    }

    public void register(View view) {
        startActivity(new Intent(StartActivity.this,InsertNameActivity.class));
    }
}