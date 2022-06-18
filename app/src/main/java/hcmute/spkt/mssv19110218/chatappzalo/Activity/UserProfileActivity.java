package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import hcmute.spkt.mssv19110218.chatappzalo.GetUserName.Common;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding; //khởi tạo các view trong UserProfileActivity
    FirebaseAuth auth; //khởi tạo FirebaseAuth
    FirebaseUser user; //Khởi tạo FirebaseUser
    FirebaseDatabase database; //khởi tạo database
    FirebaseStorage storage; //khởi tạo FirebaseStorage
    StorageReference storageReference; //khởi tạo StorageReference
    DatabaseReference databaseReference; //khởi tạo DatabaseReference

    ProgressDialog dialog;
    Uri imageUri; //Khởi tạo imageUri
    String ImageProfile, nameProfile; //khởi tạo ImageProfile, nameProfile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set action bar là toolbar trong layout
        setSupportActionBar(binding.toolbar);
        //lấy trangjh thái hiện tại của database
        auth = FirebaseAuth.getInstance();
        //lấy user hiện tại
        user = auth.getCurrentUser();
        //khởi tạo database và lấy trạng thái
        database = FirebaseDatabase.getInstance();
        //khởi tạo storage và lấy trạng thái
        storage = FirebaseStorage.getInstance();
        //sự kiện nút back
        btnBack();
        //lấy userprofile
        getProfileCurrentUser(user);
        //mở folder set avatar
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            //gọi hàm addNewImage
            @Override
            public void onClick(View v) {
                addNewImage();
            }
        });
        //nút save để lưu ảnh mới
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            //gọi hàm uploadProfileImage
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });

    }
    //tương tự hàm set avatar ở Create Account
    private void addNewImage() {
        Intent intent = new Intent();
        //Sử dụng để người dùng có thể select image từ library
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //set type
        intent.setType("image/*");
        //Gọi hàm startActivityForResult để gọi máy ảnh
        startActivityForResult(intent, 25);
    }

    //Hàm back
    private void btnBack(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Nếu data && data.getData() != null
        if (data != null && data.getData() != null) {
            //setImageURI cho imgAvt
            binding.avatar.setImageURI(data.getData());
            //Gán giá trị cho imageUri bằng data.getData()
            imageUri = data.getData();
            //Gọi hàm setEnableButtonSave()
        }
    }
    private void uploadProfileImage() {
        //mapping lại dialog đã được khởi tạo cục bộ
        dialog = new ProgressDialog(this);
        //set text cho dialog để thông báo
        dialog.setMessage("Uploading image...");
        //tắt Cancel
        dialog.setCancelable(false);
        //Show dialog
        dialog.show();
        //Nêu imageUri != null
        if (imageUri != null) {
            //gán giá đường dẫn cho storageReference
            storageReference = storage.getReference().child("Profiles").child(user.getUid());
            //thực hiện put hình ảnh lên firebase
            storageReference.putFile(imageUri).addOnCompleteListener(task -> {
                //nếu việc upload thành công
                if (task.isSuccessful()) {
                    //tắt thông báo show của dialog
                    dialog.dismiss();
                    //sử dụng storageReference và getDownloadUrl()
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        //khởi tạo biến uimageUrl để lấy đường dẫn của hình vừa được upload
                        String imageUrl = uri.toString();
                        //sử dụng databaseReference để lấy đường dẫn trên firebase
                        databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        //sử dụng databaseReference để tham chiếu đến children trong nut cha "user"
                        databaseReference.child(user.getUid()) //lấy giá trị uid của current user
                                .child("profileImage") //lấy giá tị của proifleImage
                                .setValue(imageUrl) //set giá trị cho imageUrl cho "profileImage"
                                .addOnSuccessListener(unused -> {
                                });
                    });
                    binding.btnSave.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    private void getProfileCurrentUser(FirebaseUser user) {
        if(user!=null)
        {
            //tạo phoneProfile kiểu string
            String phoneProfile = user.getPhoneNumber();
            //tạo uid kiểu string
            String uid = user.getUid();
            //sử dụng databaseReference và gét giá trị đến đường dẫn trên firebase
            databaseReference = database.getReference("users").child(uid);
            //sử dụng databaseReference để lắng nghe sự kiện thay đổi
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //sử dụng model user để nhận giá trị từ realtime database
                    User user = snapshot.getValue(User.class);
                    //lấy giá trị name của curent user và gán vào nameProfile
                    nameProfile = Objects.requireNonNull(user).getName();
                    //lấy đường dẫn của hình ảnh và gán vào urlImageProfile
                    ImageProfile = user.getAvatar();
                    //gọi hàm setDataForCurrentUser và truyền vào các tham số đã được khởi tạo ở trên
                    setDataForCurrentUser(ImageProfile, nameProfile, phoneProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void setDataForCurrentUser(String urlImageProfile, String nameProfile, String
            phoneProfile) {
        //Sử dụng Glide để add image từ đường dẫn vào trang UserProfileActivity
        Glide.with(UserProfileActivity.this)
                .load(urlImageProfile)
                .placeholder(R.drawable.avatar)
                .into(binding.avatar);
        //gán giá trị nameProfile cho name
        binding.name.setText(nameProfile);
        //gán giá trị cho phoneProfile cho editPhone
        binding.editPhone.setText(phoneProfile);
    }
}