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

    ActivityChatBinding binding; //D??ng ????? binding c??c view trong ChatActivity
    MessageAdapter adapter; //G??n MessageAdapter v??o bi???n adapter
    ArrayList<Message> messages; //G??n model Message v??o bi???n messages

    String senderRoom, receiverRoom; //Ph??ng c???a ng?????i g???i v?? ng?????i nh???n
    FirebaseDatabase database; //Firebasedatabase ???????c g??n trong database
    FirebaseStorage storage; //FirebaseStorage ???????c g??n trong storafe

    ProgressDialog dialog; //dialog c???a activity
    String senderUid; //Uid c???a ng?????i g???i
    String receiverUid; //Uid c???a ng?????i nh???n
    String name; //T??n c???a user
    String profile; //Chu???i string c???a image
    String token; //T???o bi???n token ????? l??u token c???a ng?????i d??ng
    String uid; //Uid c???a user
    String currentUserName; //kh???i t???o nameCurrentUser ??ang chat v???i ng?????i d??ng
    String mFileName = null; //file name c???a record

    private StorageReference mStorage; //Kh???i t???o storage
    private AudioRecorder audioRecord; //kh???i t???o audiorecord
    private File recordFile; //kh???i t???o recordfile


    //H??m oncreate ????? t???o c??c view cho ChatActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //@param inflater d??ng ????? map c??c view v??o ChatActivity
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //thay th??? thanh actionbar b???ng toolbar trong xml
        setSupportActionBar(binding.toolbar);

        database = FirebaseDatabase.getInstance(); //L???y FirebaseDatabase hi???n t???i
        storage = FirebaseStorage.getInstance(); //L???y FirebaseStorage hi???n t???i
        mStorage = FirebaseStorage.getInstance().getReference(); //???????c kh???i t???o t???i v??? tr?? l??u tr??? Firebase g???c
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath(); //tr??? l???i ???????ng d???n tuy???t ?????i c???a file
        mFileName += "/recorded_audio.3gp"; //th??m ??u??i c???a file name

//        //progress dialog c???a Upload Image
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("Uploading image...");
//        dialog.setCancelable(false);
        audioRecord = new AudioRecorder(); //kh???i t???o audioRecord b???ng new Controller c???a AudioRecorder

        messages = new ArrayList<>(); //kh???i t???o new arraylist cho message
        adapter = new MessageAdapter(this, messages); //kh???i t???o messageAdapter cho activity
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this)); //set l???i layoutManager v???i recyclerView
        binding.recyclerView.setAdapter(adapter); //Set adapter cho recyclerView b???ng MessageAdapter ???? khai b??o ??? tr??n

        profile = getIntent().getStringExtra("image"); //nh???n chu???i image t??? intent tr?????c g??n v??o profile
        name = getIntent().getStringExtra("name"); //nh???n chu???i name t??? intent tr?????c g??n v??o name
        uid = getIntent().getStringExtra("uid"); //nh???n chu???i uid t??? intent tr?????c g??n v??o uid
        token = getIntent().getStringExtra("token"); //nh???n token t??? intent tr?????c g??n v??o token
        currentUserName = getIntent().getStringExtra("currentUserName"); //nh???n currentUserName t??? intent tr?????c g??n v??o currentUser
        //Toast.makeText(this,token, Toast.LENGTH_SHORT).show();

        binding.name.setText(name); //set Text c???a textview Name v???i name ???? nh???n ??? tr??n
        Glide.with(ChatActivity.this)//M???t singleton ????? tr??nh b??y m???t giao di???n t??nh ????n gi???n ????? x??y d???ng c??c y??u c???u v???i RequestBuilder v?? duy tr?? Engine, BitmapPool, DiskCache v?? MemoryCache.
                .load(profile) //T????ng ??????ng v???i vi???c g???i asDrawable () v?? sau ???? l?? RequestBuilder.load (String).
                .placeholder(R.drawable.avatar) //hi???n th??? trong khi t??i nguy??n ??ang t???i thay th??? m???i l???nh g???i tr?????c ???? ?????n ph????ng th???c v???i id l?? avatar
                .into(binding.image); //?????t ImageView l?? n??i s??? ???????c t???i v??o b???ng c??ch binding ?????n image trong view, h???y m???i t???i hi???n c?? v??o ch??? ????? xem v?? gi???i ph??ng m???i t??i nguy??n m?? Glide c?? th??? ???? t???i tr?????c ???? v??o ch??? ????? xem ????? ch??ng c?? th??? ???????c s??? d???ng l???i.

        //?????t s??? ki???n click cho button back
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            } //k???t th??c
        });

        receiverUid = getIntent().getStringExtra("uid"); //l???y reveicerUid ????a v??o uid
        senderUid = FirebaseAuth.getInstance().getUid(); //getUid t??? FirebaseAuth g??n v??o senderUid

        //Nh???n tham chi???u ?????n locate li??n quan ?????n v??? tr?? n??y th??m h??m addValueEventListener ????? theo d??i thay ?????i ?????n locate n??y
        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            //B???n sao DataSnapshot ch???a d??? li???u t??? v??? tr?? C?? s??? d??? li???u Firebase. B???t k??? l??c n??o b???n ?????c d??? li???u C?? s??? d??? li???u, s??? nh???n ???????c d??? li???u d?????i d???ng ???nh ch???p d??? li???u.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //truy???n d??? li???u v??o snapshot v?? l??u v??o bi???n status v???i h??m getValue h??m String, r??ng bu???c c???n x??c ?????nh c??c getters c??ng khai
                    String status = snapshot.getValue(String.class);
                    assert status != null; //ki???m tra bi???n truy???n v??o c???a status
                    if (!status.isEmpty()) {
                        //n???u status r???ng th?? set status l?? offline
                        if (status.equals("Offline")) {
                            //???n status ??i
                            binding.status.setVisibility(View.GONE);
                        } else {
                            //n???u status kh??ng r???ng th?? set status l?? status
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE); //hi???n thanh status
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid; //senderRoom b???ng chu???i string c???a senderUid v?? ReceiverUid
        receiverRoom = receiverUid + senderUid; //ReceiverRoom b???ng chu???i string c???a ReceiverUid v?? senderUid

        //set dialog c???a sending image
        dialog = new ProgressDialog(this);
        dialog.setMessage("??ang t???i h??nh ???nh...");
        dialog.setCancelable(false);

        //tham chi???u ?????n v??? tr?? chat trong database
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                //th??m h??m addValueEventListener ????? theo d??i thay ?????i
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear messages cho data
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            //g??n message l???i b???ng c??ch getValue v?? truy???n v??o Message
                            Message message = snapshot1.getValue(Message.class);
                            //add message v??o messages
                            messages.add(message);
                        }
                        //Th??ng b??o cho b???t k??? quan s??t vi??n ???? ????ng k?? n??o r???ng t???p d??? li???u ???? thay ?????i.
                        //C?? hai l???p kh??c nhau v??? c??c s??? ki???n thay ?????i d??? li???u, thay ?????i m???c v?? thay ?????i c???u tr??c.
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Theo d??i textchange c???a msgToSend ????? b???t s??? ki???n
        binding.msgToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //khi textchange thay ?????i
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //ki???m tra n???u msgToSend r???ng
                if (binding.msgToSend.getText().toString().isEmpty()) {
                    //???n n??t btnSend
                    binding.btnsend.setVisibility(View.GONE);
                    //hi???n n??t btnPhoto
                    binding.btnphoto.setVisibility(View.VISIBLE);
                } else {
                    //hi???n n??t btnsend
                    binding.btnsend.setVisibility(View.VISIBLE);
                    //???n n??t btnphoto
                    binding.btnphoto.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //set s??? ki???n h??m onclick cho n??t btnsend
        binding.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //l???y d??? li???u t??? khung msgToSend
                String messageTxt = binding.msgToSend.getText().toString();

                //L???y d??? li???u date
                Date date = new Date();
                //l??u v??o message model v???i msgToSend, senderUid v?? Date
                Message message = new Message(messageTxt, senderUid, date.getTime());
                //setText l???i cho msgToSend b???ng r???ng khi ???? g???i
                binding.msgToSend.setText("");

                //Hashmap Object ????? l???y lastMsg
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                //put message v??o lastMsg
                lastMsgObj.put("lastMsg", message.getMessage());
                //put dateTime v??o LastMsgTime
                lastMsgObj.put("lastMsgTime", date.getTime());

                //l??u Hashmap lastMsg lastMsgTime v??o child c???a chats
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                //T????ng t??? h??m ??? receiverRoom
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
                            //Khi success s??? g???i notification v??? ??i???n tho???i b???ng token c???a user
                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification(currentUserName, message.getMessage(), token);
                            }
                        });
                    }
                });
            }
        });

        //set s??? ki???n onClick cho btnPhoto
        binding.btnphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //m??? intent m???i
                Intent intent = new Intent();
                //permission ???? set ????? v??o photo
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //setype cho intent
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

        //M???t handler cho ph??p x??? l?? c??c ?????i t?????ng c???a message v?? Runnable ???????c li??n k???t v???i MessageQueue
        final Handler handler = new Handler();
        //theo d??i msgToSend trong handler
        binding.msgToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //H??m sau khi nh???p msgToSend
            @Override
            public void afterTextChanged(Editable s) {
                //sau khi nh???p th?? setValue c???a presence trong status b???ng ??ang Nh???p ....
                database.getReference().child("presence").child(senderUid).setValue("??ang nh???p...");
                //X??a m???i tr???ng th??i v?? tin nh???n ???? g???i c?? ?????i t?????ng l?? m?? th??ng b??o. N???u m?? th??ng b??o r???ng, t???t c??? c??c l???nh g???i l???i v?? tin nh???n s??? b??? x??a.
                handler.removeCallbacksAndMessages(null);
                //setdelaytime
                handler.postDelayed(userStoppedTyping, 1000);
            }
            //khi user ng???ng type
            Runnable userStoppedTyping = new Runnable() {
                //set l???i status c???a sender l?? online
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });

        //setRecordView cho btnVoice
        binding.btnvoice.setRecordView(binding.recordView);
        //set h??m ??t h??n 1s cho RecordView
        binding.recordView.setLessThanSecondAllowed(false);
        //set s??? ki???n record cho recordView
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            //khi b???t ?????u
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                //g??n recordFile b???ng chu???i path v?? g??n v??o child
                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".3gp");
                try {
                    //g??n chu???i path v??o RecordFile v?? ch???y audioRecord
                    audioRecord.start(recordFile.getPath());
                } catch (IOException e) {
                    //b??o throwable cho lu???ng
                    e.printStackTrace();
                }
//                Toast.makeText(ChatActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();
                //khi b???t record th?? ???n v?? hi???n c??c button v?? view
                binding.msgToSend.setVisibility(View.GONE); //???n msgToSend
                binding.msgToSend.setText(""); //setText msgToSend b???ng r???ng
                binding.btnphoto.setVisibility(View.GONE); //???n n??t btnPhoto
            }

            //h??m oncancel khi ko mu???n g???i recordFile
            @Override
            public void onCancel() {
                //On Swipe To Cancel
                stopRecording(true);
                Log.d("RecordView", "onCancel");
                //hi???n msgToSend
                binding.msgToSend.setVisibility(View.VISIBLE);
                //Hi???n btnPhoto
                binding.btnphoto.setVisibility(View.VISIBLE);
            }

            //H??m onFinish c???a record
            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.
                //String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");
                //set false c???a StopRecord
                stopRecording(false);
                //g??n chu???i time ????? l???y lastMsg
                String time = getHumanTimeText(recordTime);
                //hi???n msgToSend
                binding.msgToSend.setVisibility(View.VISIBLE);
                //Hi???n btnPhoto
                binding.btnphoto.setVisibility(View.VISIBLE);
                //Up file record
                uploadRecord();
                //Log.d("RecordTime", time);
            }

            //H??m ??t h??n 1s
            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                //Hi???n msgToSend
                binding.msgToSend.setVisibility(View.VISIBLE);
                //Hi???n btnPhoto
                binding.btnphoto.setVisibility(View.VISIBLE);
            }
        });
        changeBackgroundAcionbar(); //thay ?????i background c???a ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false); //T???t tittle c???a ActionBar
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //h??m d???ng record
    private void stopRecording(boolean deleteFile) {
        //Khi d???ng audioRecord
        audioRecord.stop();
        //N???u khi d???ng m?? file record r???ng th?? x??a file
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    //H??m l???y th???i gian c???a LastMsg
    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        //ch???nh format c???a chu???i Strings
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    //H??m uploadRecord
    private void uploadRecord() {
        //L???y path Uri
        Uri selectedVoice = Uri.fromFile(new File(recordFile.getPath()));
        //l??u file audio v???i path v???a r???i v??o child c???a audio v?? l??u th??m ??u??i .3gp
        StorageReference reference = mStorage.child("Audio").child(UUID.randomUUID().toString() + ".3gp");

        //dialog khi g???i record
        dialog.setMessage("??ang t???i ??m thanh...");
        dialog.show();
        //Khi complete ???n dialog
        reference.putFile(selectedVoice).addOnCompleteListener(task -> {
            dialog.dismiss();
            //N???u task th??nh c??ng
            if (task.isSuccessful()) {
                //Truy xu???t kh??ng ?????ng b??? URL t???i xu???ng t???n t???i l??u d??i v???i m?? th??ng b??o c?? th??? thu h???i.
                //Ph???i c?? quy???n truy c???p firebase ????? xem ???????c file v???i h??m getDownloadUrl()
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    //g???n chu???i uri v??o chu???i filepath
                    String filePath = uri.toString();
                    //set messageTxt = [voice]
                    String messageTxt = "[voice]";
                    //kh???i t???o h??m date
                    Date date = new Date();
                    //l??u message v??o message data
                    Message message = new Message(messageTxt, senderUid, date.getTime());
                    //????a hashmap ????? l???y lastMsg
                    HashMap<String, Object> lastMsgObj = new HashMap<>();

                    //put lastMsg v??o database
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

    //H??m g???i th??ng b??o
    void sendNotification(String name, String message, String token) {
        try {
            //RequestQueue g???i y??u c???u ?????ng b???
            RequestQueue queue = Volley.newRequestQueue(this);
            //get API c???a notificatioon v??o url
            String url = "https://fcm.googleapis.com/fcm/send";

            //L???p n??y c?? th??? ??p bu???c c??c gi?? tr??? sang m???t ki???u kh??c khi ???????c y??u c???u.
            JSONObject data = new JSONObject();
            //put th??m tittle v?? body c???a message notification
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationData = new JSONObject();
            //th??m data v?? token c???a ng?????i nh???n
            notificationData.put("notification", data);
            notificationData.put("to", token);

            //JsonObjectRequest g???i notification cho 1 URL nh???t ?????nh
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
                //Th??m map key ??? Firebase ????? s??? d???ng app g???i noti
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAARnZaHzg:APA91bGHYY-gOuh4JzDv641jRHPQ_nlACvhWeKjO44Vixgj2-U5yHk1_x5bEXWwl-22UKQQ-vb1a3qgKHD41DN33UjYl2Fhq0z3etRvq-0avV_N0dfglkGgWecNXluzNfabAmk9qXdNr";
                    map.put("Content-Type", "application/json");
                    //Ch???ng th???c b???ng key ???????c c???p ??? firebase
                    map.put("Authorization", key);

                    return map;
                }
            };
            //Th??m Y??u c???u v??o h??ng ?????i g???i.
            queue.add(request);

        } catch (Exception ex) {

        }
    }

    //h??m ????? g???i h??nh ???nh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode = 25 ????? g???i h??nh ???nhq
        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    //kh???i t???o uri b???ng getdata c???a Image
                    Uri selectedImage = data.getData();
                    //kh???i t???o calender ????? l???y timeLastMsg
                    Calendar calendar = Calendar.getInstance();
                    //StorageReference ????? l??u h??nh ???nh b???ng storage
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    //Hi???n th??? dialog
                    dialog.show();
                    //put file l??n storage c???a firebase
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //????ng dialog
                            dialog.dismiss();
                            //Truy xu???t kh??ng ?????ng b??? URL t???i xu???ng t???n t???i l??u d??i v???i m?? th??ng b??o c?? th??? thu h???i.
                            //Ph???i c?? quy???n truy c???p firebase ????? xem ???????c file v???i h??m getDownloadUrl()
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //kh??i t???o filepath b???ng uri.toString
                                        String filePath = uri.toString();
                                        //g??n messageTxt b???ng image ????? get LastMsg c???a ph??to
                                        String messageTxt = "image";
                                        //Kh???i t???o Date ????? l???y lastMsgTime
                                        Date date = new Date();
                                        //L??u v??o message model v???i messagetxt, senderUid v?? lastMsgTime
                                        Message message = new Message(messageTxt, senderUid, date.getTime());
                                        //set message l???i l?? [photo]
                                        //g??n nh?? v???y ????? tr??nh vi???c ng?????i d??ng ch??? nh???p photo s??? hi???n ra xml c???a h??nh ???nh r???ng
                                        message.setMessage("[photo]");
                                        //set imageUrl l?? filePath ???? khai b??o
                                        message.setImageUrl(filePath);
                                        //set msgToSend b???ng r???ng
                                        binding.msgToSend.setText("");
                                        //T????ng t??? c??ch l???y lastMsg c???a voice
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

    //H??m ????? x??c ?????nh khi ng?????i d??ng s??? d???ng app s??? set presence th??nh online v?? l??u v??o status
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //l???y current Uid
        String currentId = FirebaseAuth.getInstance().getUid();
        //khi current Uid kh??ng d???ng app th?? set value cho currentId l?? offline
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }
    //h??m b???m n??o n??t tr??? v??? s??? out
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    //H??m ?????i m??u action bar
    public void changeBackgroundAcionbar() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1766EB"));
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    //N??t call ??? b??n g??c ph???i c???a action bar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.callnavigation, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

