package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityOtpcheckBinding;

public class OTPCheckActivity extends AppCompatActivity {

    ActivityOtpcheckBinding binding; //Dùng để binding các view trong OTPCheckActivity
    FirebaseAuth auth; //FirebaseAuth được gán trong auth
    OtpView otpView; //khởi tạo OtpView sử dụng thư viện GitHub
    String verificationID; //Khởi tạo verificationID
    
    ProgressDialog dialog; //Khởi tạo dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpcheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        otpView = findViewById(R.id.otpView); //set OtpView là otpView để truy vấn
        //show dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang gửi OTP....");
        dialog.setCancelable(false);
        dialog.show();

        //lấy FirebaseAuth hiện tại
        auth = FirebaseAuth.getInstance();
        //lấy phoneNumber từ intent trước là insertPhone
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        //Set text lại cho phoneNumberRetext là phoneNumber
        binding.phoneNumberRetext.setText(phoneNumber);
        //hàm gửi code OTP về điện thoại
        sendVerificationCodeToUser(phoneNumber);
        callNextScreenFromOTP(otpView);
        onComplete();
    }

    public void callNextScreenFromOTP(View view) {
        String otp = Objects.requireNonNull(otpView.getText()).toString();
        if (otp.isEmpty() || otp.length() < 6) {
            otpView.setError("Enter code...");
            otpView.requestFocus();
            return;
        }
        verifyOTP(otp);
    }

    //hàm verifyOtp để signin with phone
    private void verifyOTP(String otp) {
        //Khởi tạo PhoneAuthCredential để gói số điện thoại và thông tin xác minh cho mục đích xác thực.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);
        //gọi hàm signInWithPhoneAuthCredential
        signInWithPhoneAuthCredential(credential);
    }

    //hàm gửi OTP về sđt
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

    //hàm xử lí mCallbacks
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //hàm khi gửi code
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //ẩn dialog
            dialog.dismiss();
            //request Focus vào otpView
            otpView.requestFocus();
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
            Toast.makeText(OTPCheckActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ẩn dialog
            dialog.dismiss();
        }
    };

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
                            Toast.makeText(OTPCheckActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                            //cho phép truy cập CreateAccountActivity
                            startActivity(new Intent(OTPCheckActivity.this, CreateAccountActivity.class));
                        }
                        //Nếu thất bại
                        else {
                            //hiện Toast
                            Toast.makeText(OTPCheckActivity.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
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
                            Intent intent = new Intent(OTPCheckActivity.this, CreateAccountActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(OTPCheckActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}