package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import hcmute.spkt.mssv19110218.chatappzalo.Adapters.UsersAdapter;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    //khai báo biến sử dụng
    ActivityLoginBinding binding; //Dùng để binding các view trong LoginActivity
    ProgressDialog dialog; //Khởi tạo dialog
    ProgressDialog dialog2; //Khởi tạo dialog
    FirebaseAuth auth; //FirebaseAuth được gán trong auth
    ArrayList<User> users; //Khởi tạo ArrayList User
    FirebaseDatabase database; //FirebaseDatabase được gán trong database
    UsersAdapter usersAdapter; //Khởi tạo userAdapter
    OtpView otpView; //khởi tạo OtpView sử dụng thư viện GitHub
    String verificationID; //Khởi tạo verificationID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance(); //Lấy FirebaseAuth hiện tại
        database = FirebaseDatabase.getInstance(); //Lấy FirebaseDatabase hiện tại

        //Hiện dialog process
        dialog = new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Đang đăng nhập....");
        dialog.setCancelable(false);
        //khởi tạo new user bằng arrayList
        users = new ArrayList<>();
        //Khởi tạo userAdapter
        usersAdapter = new UsersAdapter(this, users);
        //ko cho ấn password
        binding.editPassword.setEnabled(false);
        //ko cho ấn btn continue
        binding.continueBtnLogin.setEnabled(false);
        binding.getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2 = new ProgressDialog(LoginActivity.this);
                dialog2.setTitle("OTP");
                dialog2.setMessage("Đang gửi OTP....");
                dialog2.setCancelable(false);
                String phoneNumber = binding.editPhone.getText().toString();
                sendVerificationCodeToUser(phoneNumber);
            }
        });
        onComplete();
        btnContinue();
    }

    //Hàm để set sự kiện onClick cho btnContinue
    public void btnContinue(){
        binding.continueBtnLogin.setOnClickListener(new View.OnClickListener() {
            //Khi click btnContinue sẽ truy cập hàm Login
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    //hàm login
    private void login(){
        //Lấy chuỗi của sdt và mật khẩu
        final String phoneLogin = binding.editPhone.getText().toString().trim();
        final String passLogin = binding.editPassword.getText().toString().trim();

        //lấy users trong database
        DatabaseReference reference = database.getReference("users");


        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                usersAdapter.notifyDataSetChanged();

                for (User user : users) {
                    String phone = user.getPhoneNumber();
                    String pass = user.getPassword();
                    if (phone.equals(phoneLogin) && pass.equals(passLogin)) {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //hàm xử lí mcallback
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //hàm khi gửi code
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //ẩn dialog
            dialog.dismiss();
            //gán chuỗi verificationID bằng s
            verificationID = s;
        }

        //hàm khi hoành thành nhập code otp
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Khởi tạo chuỗi otp
            //nhận mã xác minh SMS tự động truy xuất nếu có. Khi sử dụng xác minh SMS, bạn sẽ được gọi lại trước
            final String otp = phoneAuthCredential.getSmsCode();
            if (otp != null) {
                //Tự set OTP
                otpView.setText(otp);
                verifyOTP(otp);
            }
        }

        //Khi thất bại
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            //hiện toast cho người dùng với e.message
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ẩn dialog
            dialog.dismiss();
        }
    };
    //hàm verifyOTP
    private void verifyOTP(String otp) {
        //Khởi tạo PhoneAuthCredential để gói số điện thoại và thông tin xác minh cho mục đích xác thực.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);
        //gọi hàm signInWithPhoneAuthCredential
        signInWithPhoneAuthCredential(credential);
    }

    //hàm đăng kí với sđt
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                //lắng nghe sự kiện hoàn tất
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    //Nếu hoàn tất
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Hiện toast
                            Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                            //cho phép truy cập CreateAccountActivity
                        }
                        //Nếu thất bại
                        else {
                            //hiện Toast
                            Toast.makeText(LoginActivity.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    //hàm gửi otp để xác nhận
    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    //Hàm hoàn tất task
    private void onComplete(){
        //set sự kiện lắng nghe việc hoàn thành của otpView với 6 chữ số
        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //Khởi tạo PhoneAuthCredential như trên
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            binding.tick.setVisibility(View.VISIBLE);
                            binding.untick.setVisibility(View.GONE);
                            binding.editPassword.setEnabled(true);
                            binding.editPassword.requestFocus();
                            binding.continueBtnLogin.setEnabled(true);
                        } else {
                            Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                            binding.untick.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}