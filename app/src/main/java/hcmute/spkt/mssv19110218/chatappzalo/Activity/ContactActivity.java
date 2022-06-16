package hcmute.spkt.mssv19110218.chatappzalo.Activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import hcmute.spkt.mssv19110218.chatappzalo.Adapters.ContactAdapter;
import hcmute.spkt.mssv19110218.chatappzalo.Adapters.UsersAdapter;
import hcmute.spkt.mssv19110218.chatappzalo.Models.Contact;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ActivityContactBinding;

public class ContactActivity extends AppCompatActivity {
    ActivityContactBinding binding; //Dùng để binding các view trong ContactActivity
    Contact contact; //khởi tạo contact của Contact Model
    ContactAdapter contactAdapter; //Khởi tạo contactAdapter của ContactAdapter
    ArrayList<Contact> contacts; //Khởi tạo ArrayList của contact
    FirebaseDatabase database; //Firebasedatabase được gán trong database
    DatabaseReference reference; //DatabaseReference được gán trong reference
    UsersAdapter usersAdapter; //Khởi tạo userAdapter của UserAdapter
    ArrayList<User> users; //Khởi tạo arrayList của user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set acction bar bằng toolbar được design trong xml
        setSupportActionBar(binding.toolbar);
        //Sự kiện click nút back sẽ quay lại mainActivity (parent của nó)
        binding.btnBack.setOnClickListener(v -> finish());
        //mapping với contacts
        contacts = new ArrayList<>();
        //mapping dữ liệu với database
        database = FirebaseDatabase.getInstance();
        //Lấy reference từ database có đường dẫn users
        reference = database.getReference("users");
        //Khởi tạo user bằng Arraylist
        users = new ArrayList<>();
        //Khởi tạo UserAdapter
        usersAdapter = new UsersAdapter(this, users);
        //Gọi hàm kiểm tra cấp quyền
        checkPermission();
    }

    private void checkPermission() {
        //Kiểm tra tình trạng
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            //Khi chưa được cấp quyền
            //Thực hiện yêu cầu xin cấp quyền
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        else
            //Khi đã được cấp quyền
            getContactList();
    }

    private void getContactList() {
        //Khởi tạo uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sắp xếp tên theo thứ tự mã ASCII
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        //Khởi tạo cursor
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri,null, null, null, sort);
        //Kiểm tra tình trạng
        if (cursor.getCount() > 0) {
            //Khi có nhiều hơn 0
            while (cursor.moveToNext()) {
                //Khi kéo màn hình
                //Lấy contactId
                @SuppressLint("Range") String id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //Lấy contactName
                @SuppressLint("Range") String name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //Khởi tạo uriPhone
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //Khởi tạo selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                //Khởi tọa phoneCursor
                @SuppressLint("Recycle") Cursor phoneCursor =
                        getContentResolver()
                                .query(uriPhone, null, selection, new String[]{id}, null);
                //Kiểm tra tình trạng
                if (phoneCursor.moveToNext()) {
                    //Khi kéo màn hình
                    @SuppressLint("Range") String number = phoneCursor
                            .getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Khởi tạo contact model
                    contact = new Contact();
                    //Set name của contact
                    contact.setName(name);
                    //Set lại phoneNo
                    contact.setPhoneNo(number);
                    //Thêm contact vào contact Model
                    contacts.add(contact);
                    //đóng phoneCursor
                    phoneCursor.close();
                }
            }
            //đóng phoneCursor
            cursor.close();
        }
        //set lại LinearLayoutManager
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Khởi tạo contact Adapter
        contactAdapter = new ContactAdapter(this, contacts);
        //Set lại recyclerView bằng contactAdapter
        binding.recyclerView.setAdapter(contactAdapter);
    }
}