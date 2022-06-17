package hcmute.spkt.mssv19110218.chatappzalo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import hcmute.spkt.mssv19110218.chatappzalo.Adapters.MessageAdapter;
import hcmute.spkt.mssv19110218.chatappzalo.Controller.AudioRecorder;
import hcmute.spkt.mssv19110218.chatappzalo.Models.Message;
import hcmute.spkt.mssv19110218.chatappzalo.Permission.Permissions;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityChatBinding;
public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding; //Dùng để binding các view trong ChatActivity
    MessageAdapter adapter; //Gán MessageAdapter vào biến adapter
    ArrayList<Message> messages; //Gán model Message vào biến messages

    String senderRoom, receiverRoom; //Phòng của người gửi và người nhận
    FirebaseDatabase database; //Firebasedatabase được gán trong database
    FirebaseStorage storage; //FirebaseStorage được gán trong storafe

    ProgressDialog dialog; //dialog của activity
    String senderUid; //Uid của người gửi
    String receiverUid; //Uid của người nhận
    String name; //Tên của user
    String profile; //Chuỗi string của image
    String token; //Tạo biến token để lưu token của người dùng
    String uid; //Uid của user
    String currentUserName; //khởi tạo nameCurrentUser đang chat với người dùng
    String mFileName = null; //file name của record

    private StorageReference mStorage; //Khởi tạo storage
    private AudioRecorder audioRecord; //khởi tạo audiorecord
    private File recordFile; //khởi tạo recordfile


    //Hàm oncreate để tạo các view cho ChatActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //@param inflater dùng để map các view vào ChatActivity
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //thay thế thanh actionbar bằng toolbar trong xml
        setSupportActionBar(binding.toolbar);

        database = FirebaseDatabase.getInstance(); //Lấy FirebaseDatabase hiện tại
        storage = FirebaseStorage.getInstance(); //Lấy FirebaseStorage hiện tại
        mStorage = FirebaseStorage.getInstance().getReference(); //được khởi tạo tại vị trí lưu trữ Firebase gốc
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath(); //trả lại đường dẫn tuyệt đối của file
        mFileName += "/recorded_audio.3gp"; //thêm đuôi của file name

        //progress dialog của Upload Image
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);
        audioRecord = new AudioRecorder(); //khởi tạo audioRecord bằng new Controller của AudioRecorder

        messages = new ArrayList<>(); //khởi tạo new arraylist cho message
        adapter = new MessageAdapter(this, messages); //khởi tạo messageAdapter cho activity
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this)); //set lại layoutManager với recyclerView
        binding.recyclerView.setAdapter(adapter); //Set adapter cho recyclerView bằng MessageAdapter đã khai báo ở trên

        profile = getIntent().getStringExtra("image"); //nhận chuỗi image từ intent trước gán vào profile
        name = getIntent().getStringExtra("name"); //nhận chuỗi name từ intent trước gán vào name
        uid = getIntent().getStringExtra("uid"); //nhận chuỗi uid từ intent trước gán vào uid
        token = getIntent().getStringExtra("token"); //nhận token từ intent trước gán vào token
        currentUserName = getIntent().getStringExtra("currentUserName"); //nhận currentUserName từ intent trước gán vào currentUser
        //Toast.makeText(this,token, Toast.LENGTH_SHORT).show();

        binding.name.setText(name); //set Text của textview Name với name đã nhận ở trên
        Glide.with(ChatActivity.this)//Một singleton để trình bày một giao diện tĩnh đơn giản để xây dựng các yêu cầu với RequestBuilder và duy trì Engine, BitmapPool, DiskCache và MemoryCache.
                .load(profile) //Tương đương với việc gọi asDrawable () và sau đó là RequestBuilder.load (String).
                .placeholder(R.drawable.avatar) //hiển thị trong khi tài nguyên đang tải thay thế mọi lệnh gọi trước đó đến phương thức với id là avatar
                .into(binding.image); //Đặt ImageView là nơi sẽ được tải vào bằng cách binding đến image trong view, hủy mọi tải hiện có vào chế độ xem và giải phóng mọi tài nguyên mà Glide có thể đã tải trước đó vào chế độ xem để chúng có thể được sử dụng lại.

        //đặt sự kiện click cho button back
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            } //kết thúc
        });

        receiverUid = getIntent().getStringExtra("uid"); //lấy reveicerUid đưa vào uid
        senderUid = FirebaseAuth.getInstance().getUid(); //getUid từ FirebaseAuth gán vào senderUid

        //Nhận tham chiếu đến locate liên quan đến vị trí này thêm hàm addValueEventListener để theo dõi thay đổi đến locate này
        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            //Bản sao DataSnapshot chứa dữ liệu từ vị trí Cơ sở dữ liệu Firebase. Bất kỳ lúc nào bạn đọc dữ liệu Cơ sở dữ liệu, sẽ nhận được dữ liệu dưới dạng Ảnh chụp dữ liệu.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //truyền dữ liệu vào snapshot và lưu vào biến status với hàm getValue hàm String, ràng buộc cần xác định các getters công khai
                    String status = snapshot.getValue(String.class);
                    assert status != null; //kiếm tra biến truyền vào của status
                    if (!status.isEmpty()) {
                        //nếu status rỗng thì set status là offline
                        if (status.equals("Offline")) {
                            //ẩn status đi
                            binding.status.setVisibility(View.GONE);
                        } else {
                            //nếu status không rỗng thì set status là status
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE); //hiện thanh status
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid; //senderRoom bằng chuỗi string của senderUid và ReceiverUid
        receiverRoom = receiverUid + senderUid; //ReceiverRoom bằng chuỗi string của ReceiverUid và senderUid

        //set dialog của sending image
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải hình ảnh...");
        dialog.setCancelable(false);

        //tham chiếu đến vị trí chat trong database
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                //thêm hàm addValueEventListener để theo dõi thay đổi
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear messages cho data
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            //gán message lại bằng cách getValue và truyền vào Message
                            Message message = snapshot1.getValue(Message.class);
                            //add message vào messages
                            messages.add(message);
                        }
                        //Thông báo cho bất kỳ quan sát viên đã đăng ký nào rằng tập dữ liệu đã thay đổi.
                        //Có hai lớp khác nhau về các sự kiện thay đổi dữ liệu, thay đổi mục và thay đổi cấu trúc.
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Theo dõi textchange của msgToSend để bắt sự kiện
        binding.msgToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //khi textchange thay đổi
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //kiểm tra nếu msgToSend rỗng
                if (binding.msgToSend.getText().toString().isEmpty()) {
                    //ẩn nút btnSend
                    binding.btnsend.setVisibility(View.GONE);
                    //hiện nút btnPhoto
                    binding.btnphoto.setVisibility(View.VISIBLE);
                } else {
                    //hiện nút btnsend
                    binding.btnsend.setVisibility(View.VISIBLE);
                    //ẩn nút btnphoto
                    binding.btnphoto.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //set sự kiện hàm onclick cho nút btnsend
        binding.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lấy dữ liệu từ khung msgToSend
                String messageTxt = binding.msgToSend.getText().toString();

                //Lấy dữ liệu date
                Date date = new Date();
                //lưu vào message model với msgToSend, senderUid và Date
                Message message = new Message(messageTxt, senderUid, date.getTime());
                //setText lại cho msgToSend bằng rỗng khi đã gửi
                binding.msgToSend.setText("");

                //Hashmap Object để lấy lastMsg
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                //put message vào lastMsg
                lastMsgObj.put("lastMsg", message.getMessage());
                //put dateTime vào LastMsgTime
                lastMsgObj.put("lastMsgTime", date.getTime());

                //lưu Hashmap lastMsg lastMsgTime vào child của chats
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                //Tương tự hàm ở receiverRoom
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push()
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            //Khi success sẽ gửi notification về điện thoại bằng token của user
                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification(currentUserName, message.getMessage(), token);
                            }
                        });
                    }
                });
            }
        });

        //set sự kiện onClick cho btnPhoto
        binding.btnphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mở intent mới
                Intent intent = new Intent();
                //permission đã set để vào photo
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //setype cho intent
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

        //Một handler cho phép xử lí các đối tượng của message và Runnable được liên kết với MessageQueue
        final Handler handler = new Handler();
        //theo dõi msgToSend trong handler
        binding.msgToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Hàm sau khi nhập msgToSend
            @Override
            public void afterTextChanged(Editable s) {
                //sau khi nhập thì setValue của presence trong status bằng Đang Nhập ....
                database.getReference().child("presence").child(senderUid).setValue("Đang nhập...");
                //Xóa mọi trạng thái và tin nhắn đã gửi có đối tượng là mã thông báo. Nếu mã thông báo rỗng, tất cả các lệnh gọi lại và tin nhắn sẽ bị xóa.
                handler.removeCallbacksAndMessages(null);
                //setdelaytime
                handler.postDelayed(userStoppedTyping, 1000);
            }
            //khi user ngừng type
            Runnable userStoppedTyping = new Runnable() {
                //set lại status của sender là online
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });

        //setRecordView cho btnVoice
        binding.btnvoice.setRecordView(binding.recordView);
        //set hàm ít hơn 1s cho RecordView
        binding.recordView.setLessThanSecondAllowed(false);
        //set sự kiện record cho recordView
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            //khi bắt đầu
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                //gán recordFile bằng chuỗi path và gán vào child
                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".3gp");
                try {
                    //gàn chuỗi path vào RecordFile và chạy audioRecord
                    audioRecord.start(recordFile.getPath());
                } catch (IOException e) {
                    //báo throwable cho luồng
                    e.printStackTrace();
                }
//                Toast.makeText(ChatActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();
                //khi bật record thì ẩn và hiện các button và view
                binding.msgToSend.setVisibility(View.GONE); //ẩn msgToSend
                binding.msgToSend.setText(""); //setText msgToSend bằng rỗng
                binding.btnphoto.setVisibility(View.GONE); //ẩn nút btnPhoto
            }

            //hàm oncancel khi ko muốn gửi recordFile
            @Override
            public void onCancel() {
                //On Swipe To Cancel
                stopRecording(true);
                Log.d("RecordView", "onCancel");
                //hiện msgToSend
                binding.msgToSend.setVisibility(View.VISIBLE);
                //Hiện btnPhoto
                binding.btnphoto.setVisibility(View.VISIBLE);
            }

            //Hàm onFinish của record
            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.
                //String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");
                //set false của StopRecord
                stopRecording(false);
                //gán chuỗi time để lấy lastMsg
                String time = getHumanTimeText(recordTime);
                //hiện msgToSend
                binding.msgToSend.setVisibility(View.VISIBLE);
                //Hiện btnPhoto
                binding.btnphoto.setVisibility(View.VISIBLE);
                //Up file record
                uploadRecord();
                //Log.d("RecordTime", time);
            }

            //Hàm ít hơn 1s
            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                //Hiện msgToSend
                binding.msgToSend.setVisibility(View.VISIBLE);
                //Hiện btnPhoto
                binding.btnphoto.setVisibility(View.VISIBLE);
            }
        });
        changeBackgroundAcionbar(); //thay đổi background của ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false); //Tắt tittle của ActionBar
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //hàm dừng record
    private void stopRecording(boolean deleteFile) {
        //Khi dừng audioRecord
        audioRecord.stop();
        //Nếu khi dừng mà file record rỗng thì xóa file
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    //Hàm lấy thời gian của LastMsg
    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        //chỉnh format của chuỗi Strings
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    //Hàm uploadRecord
    private void uploadRecord() {
        //Lấy path Uri
        Uri selectedVoice = Uri.fromFile(new File(recordFile.getPath()));
        //lưu file audio với path vừa rồi vào child của audio và lưu thêm đuôi .3gp
        StorageReference reference = mStorage.child("Audio").child(UUID.randomUUID().toString() + ".3gp");

        //dialog khi gửi record
        dialog.setMessage("Upload voice...");
        dialog.show();
        //Khi complete ẩn dialog
        reference.putFile(selectedVoice).addOnCompleteListener(task -> {
            dialog.dismiss();
            //Nếu task thành công
            if (task.isSuccessful()) {
                //Truy xuất không đồng bộ URL tải xuống tồn tại lâu dài với mã thông báo có thể thu hồi.
                //Phải có quyền truy cập firebase để xem được file với hàm getDownloadUrl()
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    //gắn chuỗi uri vào chuỗi filepath
                    String filePath = uri.toString();
                    //set messageTxt = [voice]
                    String messageTxt = "[voice]";
                    //khởi tạo hàm date
                    Date date = new Date();
                    //lưu message vào message data
                    Message message = new Message(messageTxt, senderUid, date.getTime());
                    //đưa hashmap để lấy lastMsg
                    HashMap<String, Object> lastMsgObj = new HashMap<>();

                    //put lastMsg vào database
                    lastMsgObj.put("lastMsg", message.getMessage());
                    lastMsgObj.put("lastMsgTime", date.getTime());

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                    message.setMessage("[voice*]");
                    message.setVoiceUrl(filePath);
                    binding.msgToSend.setText("");
                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push()
                            .setValue(message).addOnSuccessListener(avoid -> database.getReference().child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .push()
                            .setValue(message).addOnSuccessListener(avoid1 -> {

                            }));
                });
            }
        });
    }

    //Hàm gửi thông báo
    void sendNotification(String name, String message, String token) {
        try {
            //RequestQueue gửi yêu cầu đồng bộ
            RequestQueue queue = Volley.newRequestQueue(this);
            //get API của notificatioon vào url
            String url = "https://fcm.googleapis.com/fcm/send";

            //Lớp này có thể ép buộc các giá trị sang một kiểu khác khi được yêu cầu.
            JSONObject data = new JSONObject();
            //put thêm tittle và body của message notification
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationData = new JSONObject();
            //thêm data và token của người nhận
            notificationData.put("notification", data);
            notificationData.put("to", token);

            //JsonObjectRequest gửi notification cho 1 URL nhất định
            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                //Thêm map key ở Firebase để sử dụng app gửi noti
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAARnZaHzg:APA91bGHYY-gOuh4JzDv641jRHPQ_nlACvhWeKjO44Vixgj2-U5yHk1_x5bEXWwl-22UKQQ-vb1a3qgKHD41DN33UjYl2Fhq0z3etRvq-0avV_N0dfglkGgWecNXluzNfabAmk9qXdNr";
                    map.put("Content-Type", "application/json");
                    //Chứng thực bằng key được cấp ở firebase
                    map.put("Authorization", key);

                    return map;
                }
            };
            //Thêm Yêu cầu vào hàng đợi gửi.
            queue.add(request);

        } catch (Exception ex) {

        }
    }

    //hàm để gửi hình ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode = 25 để gửi hình ảnhq
        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    //khởi tạo uri bằng getdata của Image
                    Uri selectedImage = data.getData();
                    //khởi tạo calender để lấy timeLastMsg
                    Calendar calendar = Calendar.getInstance();
                    //StorageReference để lưu hình ảnh bằng storage
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    //Hiển thị dialog
                    dialog.show();
                    //put file lên storage của firebase
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //Đóng dialog
                            dialog.dismiss();
                            //Truy xuất không đồng bộ URL tải xuống tồn tại lâu dài với mã thông báo có thể thu hồi.
                            //Phải có quyền truy cập firebase để xem được file với hàm getDownloadUrl()
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //khơi tạo filepath bằng uri.toString
                                        String filePath = uri.toString();
                                        //gán messageTxt bằng image để get LastMsg của phôto
                                        String messageTxt = "image";
                                        //Khởi tạo Date để lấy lastMsgTime
                                        Date date = new Date();
                                        //Lưu vào message model với messagetxt, senderUid và lastMsgTime
                                        Message message = new Message(messageTxt, senderUid, date.getTime());
                                        //set message lại là [photo]
                                        //gán như vậy để tránh việc người dùng chỉ nhập photo sẽ hiện ra xml của hình ảnh rỗng
                                        message.setMessage("[photo]");
                                        //set imageUrl là filePath đã khai báo
                                        message.setImageUrl(filePath);
                                        //set msgToSend bằng rỗng
                                        binding.msgToSend.setText("");
                                        //Tương tự cách lấy lastMsg của voice
                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .push()
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("chats")
                                                        .child(receiverRoom)
                                                        .child("messages")
                                                        .push()
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                            }
                                        });

                                        //Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    //Hàm để xác định khi người dùng sử dụng app sẽ set presence thành online và lưu vào status
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    //hàm bấm nào nút trở về sẽ out
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    //Hàm đổi màu action bar
    public void changeBackgroundAcionbar() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1766EB"));
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    //Nút call ở bên góc phải của action bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.callnavigation, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

