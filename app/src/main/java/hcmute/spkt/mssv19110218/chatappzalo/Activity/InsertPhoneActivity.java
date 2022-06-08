package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityInsertPhoneBinding;

public class InsertPhoneActivity extends AppCompatActivity {

    ActivityInsertPhoneBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editPhone.requestFocus();

        binding.continueBtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.editPhone.getText().toString().trim();
                if(phone.isEmpty()){
                    binding.continueBtnPhone.setEnabled(false);
                } else {
                    binding.continueBtnPhone.setEnabled(true);
                }
                Intent intent = new Intent(InsertPhoneActivity.this, OTPCheckActivity.class);
                intent.putExtra("phoneNumber",binding.editPhone.getText().toString());
                startActivity(intent);
            }
        });
    }
}