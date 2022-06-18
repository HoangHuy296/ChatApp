package hcmute.spkt.mssv19110218.chatappzalo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

import hcmute.spkt.mssv19110218.chatappzalo.Models.Contact;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ContactboxBinding;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Context context; //Context chứa adapter
    ArrayList<Contact> contacts;    //Khởi tạo danh sách chứa contact

    public ContactAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    //* Tạo hàm constructor cho class
    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contactbox, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        //Khởi tạo biến database để nhận dữ liệu từ Firebasedatabase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Nhận dữ liệu từ Firebasedatabase với đường dẫn "users" được cung cấp trực tiếp
        DatabaseReference reference = database.getReference("users");

        //Lấy contact ở vị trí position
        Contact contact = contacts.get(position);
        //Gán nameContact bằng contact.getName() vào trong viewHolder
        holder.binding.nameContact.setText(contact.getName());
        //Gán phoneContact bằng contact.getPhoneNo() vào trong viewHolder
        holder.binding.phoneContact.setText(contact.getPhoneNo());

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot item) {
                //Khởi tạo biến phoneContact để nhận dữ  liệu contact từ phone
                String phoneContact = contact.getPhoneNo();
                //Khởi tạo biến kiểm tra isHasUser
                boolean isHasUser = false;
                //Chạy vòng lặp trong từ con của nút "users"
                for (DataSnapshot dataSnapshot : item.getChildren()) {
                    //Sử dụng model User để nhận dữ liệu từ Firebase
                    User user = dataSnapshot.getValue(User.class);
                    //Khởi tạo biến phoneAuth để nhận giá trị phoneNuber lấy xuống từ Firebase
                    String phoneAuth = Objects.requireNonNull(user).getPhoneNumber();
                    //Nếu giá trị của phone lấy xuống từ Firebase bằng với giá trị phone từ contact của điện thoại ->
                    if (phoneAuth.equals(phoneContact)) {
                        //Gán giá trị mới cho isHasUser  = true
                        isHasUser = true;
                        break;
                    }
                }
                //Ẩn button add friend
                holder.binding.btnAddFriend.setVisibility(View.GONE);
                //Hiện TextView added
                holder.binding.added.setVisibility(View.VISIBLE);
                //Nếu giá trị của isHasUser là true
                if (isHasUser)
                    //Gán giá trị cho TextView = "Added"
                    //Đã có tài khoản đăng kí với sđt này
                    holder.binding.added.setText("Added");
                //Ngược lại
                else
                    //Gán giá trị cho TextView = "No register"
                    //Chưa có tài khoản đăng kí với sđt này
                    holder.binding.added.setText("No register");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        //trả về chiếu dài của list contact
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ContactboxBinding binding; //tạo các view của ContactboxBinding
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContactboxBinding.bind(itemView);
        }
    }
}