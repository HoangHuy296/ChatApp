package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityInsertPhoneBinding;

public class InsertPhoneActivity extends AppCompatActivity {

    ActivityInsertPhoneBinding binding; //Dùng để binding các view trong InsertNameActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //yêu cầu request vào editPhone
        binding.editPhone.requestFocus();
        //set sự kiện onClick
        binding.continueBtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tạo phone gán bằng dữ liệu của editPhone
                String phone = binding.editPhone.getText().toString().trim();
                if(phone.isEmpty()){
                    //nếu rỗng thì ko hieenjh nút bấm
                    binding.continueBtnPhone.setEnabled(false);
                } else {
                    //hiện nút bấm
                    binding.continueBtnPhone.setEnabled(true);
                }
                //Hoàn thành thì cho phép qua OTP check Activity
                Intent intent = new Intent(InsertPhoneActivity.this, OTPCheckActivity.class);
                intent.putExtra("phoneNumber",binding.editPhone.getText().toString()); //put phoneNumber bằng dữ liệu editPhone vào intent sau
                startActivity(intent);
            }
        });
    }
}