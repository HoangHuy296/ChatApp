package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import hcmute.spkt.mssv19110218.chatappzalo.GetUserName.Common;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityCreateAccountBinding;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding binding; //Dùng để binding các view trong CreateAccountActivity
    FirebaseAuth auth; //FirebaseAuth được gán trong auth
    FirebaseDatabase database; //Firebasedatabase được gán trong database
    FirebaseStorage storage; //FirebaseStorage được gán trong storage
    Uri selectedImage; //Khởi tạo Uro SelectedImage để lấy avatar
    ProgressDialog dialog; //khởi tạo dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //dialog khi tạo tài khoản
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tạo tài khoản...");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance(); //Lấy FirebaseAuth hiện tại để mapping
        database = FirebaseDatabase.getInstance(); //Lấy FirebaseDatabase hiện tại để mapping
        storage = FirebaseStorage.getInstance(); //Lấy FirebaseStorage hiện tại để mapping
        binding.userNameRetext.setText(Common.currentUser.getName()); //setText bằng tên đã khai báo ở hàm commom

        //set avatar khi bấm vào sẽ mở folder cho chọn ảnh
        //mở intent để thêm ảnh
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //set Type cho intent
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });

        //set sự kiện onclick cho continueBtnAvatar
        binding.continueBtnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.userNameRetext.setText(Common.currentUser.getName());
                //Hiển thị dialog
                dialog.show();
                //kiểm tra tình trạng
                if(selectedImage!=null){
                    //thực hiện reference để lấy Uid rồi set avatar
                    StorageReference reference = storage.getReference().child("Avatar").child(auth.getUid());
                    //putfile lên Storage
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //Truy xuất không đồng bộ URL tải xuống tồn tại lâu dài với mã thông báo có thể thu hồi.
                            //Phải có quyền truy cập firebase để xem được file với hàm getDownloadUrl()
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //khởi tạo imageUrl bằng uri
                                        String imageUrl = uri.toString();
                                        //Khởi tạo uid bằng hàm getUid của firebase
                                        String uid = auth.getUid();
                                        //khởi tạo phone bằng hàm của firebase và user model để getPhone
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        //lấy chuỗi của tên
                                        String name = binding.userNameRetext.getText().toString();
                                        //lấy chuỗi của password
                                        String password = binding.editPassword.getText().toString();
                                        //Thêm new user với uid, name, phone,imageUrl,password
                                        User user = new User(uid, name, phone,imageUrl,password);
                                        //thêm dữ liệu vào giá trị users của database
                                        database.getReference()
                                                .child("users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    //hàm success của createAccount
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        //ẩn dialog
                                                        dialog.dismiss();
                                                        //vào main
                                                        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                } //nếu ko có hình ảnh thì tương tự với ảnh, set lại hình ảnh bằng "No Avatar"
                else {
                    String uid = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();
                    String name = binding.userNameRetext.getText().toString();
                    String password = binding.editPassword.getText().toString();

                    User user = new User(uid,name, phone, "No Avatar", password);
                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //set hình ảnh của avatar
        if(data != null){
            if(data.getData() !=null){
                binding.avatar.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}