package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hcmute.spkt.mssv19110218.chatappzalo.GetUserName.Common;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityInsertNameBinding;

public class InsertNameActivity extends AppCompatActivity {

    ActivityInsertNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editName.requestFocus();

        binding.continueBtnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.editName.getText().toString().isEmpty()){
                    String name = binding.editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        binding.editName.setError("Please type a name");
                        return;
                    }
                    Intent intent = new Intent(InsertNameActivity.this, InsertPhoneActivity.class);
                    Common.currentUser = new User();
                    Common.currentUser.setName(binding.editName.getText().toString());;
                    startActivity(intent);
                }else{

                }
            }
        });
    }
}