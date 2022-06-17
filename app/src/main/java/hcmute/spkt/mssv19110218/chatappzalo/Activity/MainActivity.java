package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.Adapters.UsersAdapter;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding; //Dùng để binding các view trong MainnActivity
    FirebaseDatabase database; //FirebaseDatabase được gán trong database
    ArrayList<User> users; //Khởi tạo Arraylist user
    UsersAdapter usersAdapter; //Khởi tạo userAdapter
    FirebaseAuth auth; //FirebaseAuth được gán trong auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Lấy database hiện tại
        database = FirebaseDatabase.getInstance();
        //sử dụng dịch vụ FirebaseMessaging
        FirebaseMessaging.getInstance()
                //lấy token của user
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    //update lại HashMap với token
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", token);
                        database.getReference()
                                .child("users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                    }
                });
        //Hàm đổi màu background
        changeBackgroundAcionbar();
        //Lấy database hiện tại
        database = FirebaseDatabase.getInstance();
        //khởi tạo users
        users = new ArrayList<>();
        //khởi tạo userAdapter
        usersAdapter = new UsersAdapter(this, users);
        //setAdapter lại cho listChat
        binding.listChat.setAdapter(usersAdapter);
        //theo dõi thay đổi của hàm users
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear user
                users.clear();
                //nếu trùng với id của chính mình thì ko hiện profile
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUserid().equals(FirebaseAuth.getInstance().getUid()))
                        users.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Hàm on Resume
    @Override
    protected void onResume() {
        super.onResume();
        //lấy current Uid
        String currentId = FirebaseAuth.getInstance().getUid();
        //khi current Uid sửu dụng app thì set value cho currentId là online
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //lấy current Uid
        String currentId = FirebaseAuth.getInstance().getUid();
        //khi current Uid không dụng app thì set value cho currentId là offline
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    //Hàm hiển thị navigationTop
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigationtop,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Hàm đổi màu action bar
    public void changeBackgroundAcionbar(){
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1766EB"));
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    //Hàm chọn item trong menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //khi id chọn là addfriend thì vào contactActivity
        if(id == R.id.addfriend){
            startActivity(new Intent(MainActivity.this, ContactActivity.class));
            return true;
        }
        //khi id chọn là userprofile thì vào UserProfileActivity
        else if (id==R.id.userprofile){
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            return true;
        }
        //khi id chọn là logout thì trả về StartActivity
        else if (id==R.id.logout){
            Map<String,Object> map = new HashMap<>();
            auth=FirebaseAuth.getInstance();
            if(auth.getCurrentUser()!=null)
            {
                auth.signOut();
                //set lại hàm users với hàm sign out
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(auth.getUid())
                        .updateChildren(map,((error, ref) -> {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            finish();
                        }));
            }
        }
        return super.onOptionsItemSelected(item);
    }

}