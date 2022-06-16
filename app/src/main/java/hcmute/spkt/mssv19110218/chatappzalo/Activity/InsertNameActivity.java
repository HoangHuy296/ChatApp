package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hcmute.spkt.mssv19110218.chatappzalo.GetUserName.Common;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityInsertNameBinding;

public class InsertNameActivity extends AppCompatActivity {

    ActivityInsertNameBinding binding; //Dùng để binding các view trong InsertNameActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Yêu cầu request vào editName
        binding.editName.requestFocus();
        //set sự kiện onClick cho nút continueBtnName
        binding.continueBtnName.setOnClickListener(new View.OnClickListener() {
            //hàm check cho continueBtnName
            @Override
            public void onClick(View view) {
                //kiểm tra editName
                if(!binding.editName.getText().toString().isEmpty()){
                    String name = binding.editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        //báo lỗi nếu empty
                        binding.editName.setError("Please type a name");
                        return;
                    }
                    //hoàn thành thì nhảy vào insertPhone
                    Intent intent = new Intent(InsertNameActivity.this, InsertPhoneActivity.class);
                    Common.currentUser = new User(); //khởi tạo currentUser
                    Common.currentUser.setName(binding.editName.getText().toString()); //set name cho currentUser
                    startActivity(intent);
                }else{

                }
            }
        });
    }
}